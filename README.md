# MMORPG Centralized API and Game Servers
=============================================================================

This project is an open-source MMORPG server, aimed at providing a centralized API Server and various game servers for managing multiplayer functionality in a massive online game. The project includes Chat, Login, World, and Channel Servers that are connected through a centralized API server.

This server software is designed to give developers a foundation to build on, experiment with, and create MMORPG features, while providing a learning opportunity in network programming, server management, and multiplayer game design.

This project is not intended for playing any proprietary games or competing with any official game servers. It is purely for learning, development, and building open-source server infrastructure.

# Client
The project currently does not include a game client of its own. Instead, it is designed to be adaptable for integration with custom or existing MMORPG game clients.

# State of Development
The project is in active development and is progressing towards a fully functioning MMORPG server system. Many features of typical online role-playing games have been implemented, but it remains a work in progress with more features to be added.

# Features currently implemented:
API Server:
Acts as a command relay between the game servers (Chat, Login, World, Channel).
Receives admin commands and forwards them to appropriate servers for execution.
Game Servers:
Chat Server: Handles chat features like global announcements, player messaging, and guild chats.
Login Server: Manages player logins, authentication, and session management.
World Server: Oversees world events, NPC interactions, and player interactions.
Channel Server: Handles game instances, player actions, and balancing between different channels.
Features in progress:
Combat and Skills System
Player vs. Environment (PvE) combat.
Basic skill systems and leveling.
Player Guilds
Implementation of player guilds, including chat, events, and coordination.
Quests and NPC Dialogues
In-game quest management.
NPC interactions, shops, and quest dialogues.
Requirements
The MMORPG server system is developed using Java 17 and Netty for networking, and it requires MySQL for persistent storage. To run the system, the following tools are needed:

Java Development Kit (JDK) 17+
Netty (for non-blocking I/O and networking)
MySQL or compatible database server (e.g., MariaDB)

# Contribution
We welcome contributions to this project! Please check out the file CONTRIBUTING.md for details on how you can help with development, testing, documentation, and more.

# Links
GitHub: https://github.com/The-Legendary-Ace/2d-Side-Scrolling-MMORPG
Wiki: https://github.com/The-Legendary-Ace/2d-Side-Scrolling-MMORPG/wiki
Forum: *Undecided*
Discord: Coming Soon
