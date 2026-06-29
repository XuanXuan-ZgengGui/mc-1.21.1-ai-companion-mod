# Release Notes - v0.2.0

## AI Companion Entity & LAN Auto-Open

### New Features

- **AI Model Connection in Join-Game Mode**: The AI companion `XuanXuan-ZhengGui` now connects to the configured model and can reply to chat messages.
- **Autonomous Movement**: AI companion follows the player smoothly, teleports when too far away, and faces the player when nearby.
- **Survival Logic**: Companion stays at full health, ignores fall damage, and never runs out of air.
- **Smart Chat Replies**: When the player mentions the companion name, model name, or keywords like "玄玄" / "ai", the companion replies after a short delay.
- **Auto Open to LAN**: Selecting "加入游戏" automatically opens the world to LAN so other players can join.

### Improvements

- Better follow behavior with yaw/head-yaw synchronization.
- Chat interception works for both `@modelName` and normal chat in join-game mode.

### Compatibility

- Minecraft 1.21.1
- Fabric Loader >= 0.16.9
- Fabric API 0.102.1+1.21.1
- Java >= 21
