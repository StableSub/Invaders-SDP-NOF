# COIN

## Team Introduction
> Our team will develop an in-game currency system and shop, allowing players to upgrade their ships.

저희 팀은 게임 내 화폐시스템 및 상점을 개발해 플레이어의 함선을 업그레이드 할 수 있도록 할 것입니다.


- Team leader
    - `Jinbum Shin` : Development & PM
- Team member
    - `Dohee Kwon` : Development
    - `Kyusik Kim` : Development
    - `Junghyun Choe` : Development
    - `Seunggo Kim` : Development
    - `Gibeom Kim` : Development
    - `Seungmin Jung` : Development

## Team Requirements
> Implementation of Currency and Shop & In-Shop Upgrades

화폐 및 상점 & 상점 내 업그레이드 구현

## Detailed Requirements
> The implementation involves creating a Wallet object in the core class to manage coins and upgrade statuses. For teams that need coin deposit/withdrawal/inquiry functions, you can use the Wallet to handle deposits, withdrawals, and inquiries.
>
구현은 core클래스에 Wallet객체를 생성해 코인 및 업그레이드 상태 등을 관리합니다.
코인 입금/출금/조회 기능이 필요한 다른 팀의 경우, Wallet을 이용해 입출금 및 조회를 진행하시면 될 것 같습니다.

> Coins will be awarded in proportion to the game score (10% of the score), and the required coins for upgrades will increase in a pattern like 1000-2000-4000.

코인은 게임 점수에 비례해 지급할 예정이며(점수10%), 1000-2000-4000 이런식으로 업그레이드 당 필요 코인이 증가하는 식으로 벨류를 잡았습니다.

> Regarding the application of shop upgrades, our team plans to adjust values like speedY when creating the ship in the GameScreen.initialize() section during development.

상점 업그레이드 적용과 관련해 저희 팀에서 GameScreen.initialize()부분에서 ship생성 시 속도 등의 수치를 조정하며 개발할 것 같습니다.

### Shop-list
> We plan to offer the following upgrades in the shop:
> - Increased bullet speedY
> - Increased firing rate
> - Extra life
> - Increased coin acquisition

상점에서 다음과 같은 업그레이드를 제공할 예정입니다.
- 총알 속도 증가
- 발사 빈도 증가
- 추가 라이프
- 획득 코인 증가


## Dependency
> Sound Effects: A sound effect will be played when coins are used in the shop.

사운드 효과: 상점 내 코인 사용시 효과음 재생