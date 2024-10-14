package io.jyberion.mmorpg.world;

import io.jyberion.mmorpg.common.message.ChannelAvailabilityRequest;
import io.jyberion.mmorpg.common.message.ChannelAvailabilityResponse;
import io.jyberion.mmorpg.common.model.ChannelInfo;
import io.jyberion.mmorpg.common.model.ChannelStatus;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldServerChannelListener implements MessageListener {

    private Session session;
    private final Map<String, ChannelInfo> connectedChannels = new ConcurrentHashMap<>();

    public WorldServerChannelListener(Session session) {
        this.session = session;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                if (objMessage.getObject() instanceof ChannelAvailabilityRequest) {
                    // Fetch available channels for this world server
                    List<ChannelInfo> availableChannels = getAvailableChannels();

                    // Create response with available channels
                    ChannelAvailabilityResponse response = new ChannelAvailabilityResponse(availableChannels);

                    // Send response to the reply destination
                    Destination replyDestination = objMessage.getJMSReplyTo();
                    if (replyDestination != null) {
                        MessageProducer replyProducer = session.createProducer(replyDestination);
                        ObjectMessage replyMessage = session.createObjectMessage(response);
                        replyProducer.send(replyMessage);
                    }
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch available channels
    private List<ChannelInfo> getAvailableChannels() {
        List<ChannelInfo> availableChannels = new ArrayList<>();
        for (ChannelInfo channelInfo : connectedChannels.values()) {
            if (channelInfo.getStatus() == ChannelStatus.ONLINE) { // Only add online channels
                availableChannels.add(channelInfo);
            }
        }
        return availableChannels;
    }

    // Method to add a channel to the list of connected channels
    public void addChannel(ChannelInfo channelInfo) {
        connectedChannels.put(channelInfo.getChannelName(), channelInfo);
    }

    // Method to remove a channel from the list of connected channels
    public void removeChannel(String channelName) {
        connectedChannels.remove(channelName);
    }

    // Method to update the status of a channel (e.g., set it to offline)
    public void updateChannelStatus(String channelName, ChannelStatus status) {
        ChannelInfo channelInfo = connectedChannels.get(channelName);
        if (channelInfo != null) {
            channelInfo.setStatus(status);
            channelInfo.updateHeartbeat();
        }
    }
}
