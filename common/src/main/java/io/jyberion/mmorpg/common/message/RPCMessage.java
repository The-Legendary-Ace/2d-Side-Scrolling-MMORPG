package io.jyberion.mmorpg.common.message;

import java.io.Serializable;

public class RPCMessage implements Message, Serializable {
    private String methodName;
    private Object[] args;

    public RPCMessage(String methodName, Object... args) {
        this.methodName = methodName;
        this.args = args;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public MessageType getType() {
        return MessageType.RPC_CALL;
    }
}
