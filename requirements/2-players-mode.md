# ZARA

## Team Introduction
Hello

- Leader
    - `Sungyoon Chang`
- Members
    - `Jiwon Moon`
    - `Minkyeong Ji`
    - `Dongbin Shin`
    - `Jiwon Seong`
    - `Hojune Moon`
    - `Hyojong Yu`

## Team Requirements
### 2-players-mode
- Add Player
- Control Settings
- Screen split
- Modify Game Logic
- Display Scores and Lives
- Split sound channel
- Network Play (Optional)


## Detailed Requirements

- Control Settings
  - Differentiate controls for the first and second players.
  - For example, the first player can use arrow keys, and the second player can use WASD keys.
- Screen split
  - Allow both players to play simultaneously by splitting the ui.
  - In the case of ui splitting, divide the game area for each player.
- Modify Game Logic
  - Adjust game over conditions, victory conditions, etc., to accommodate two players.
  - For example, the game should be over when both players run out of lives.
- Split sound channel
  - Split sound channel to Left and Right
  - This allows for simultaneous sounding effects to be processed.
- Network Play (Optional):
  - Allow remote play over a network, not just local play.
  - Implement server-client communication for this purpose.
