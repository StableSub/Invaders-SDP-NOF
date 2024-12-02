package entity;

import database.DatabaseManager;
import engine.core.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Wallet {
    private static Logger logger = Core.getLogger();
    private int coin;

    private String id;
    //bullet speed level
    private int bullet_lv;
    private int bullet_bl;

    //shot frequency level
    private int shot_lv;
    private int shot_bl;

    //additional lives level
    private int lives_lv;
    private int lives_bl;

    //coin gain level
    private int coin_lv;
    private int coin_bl;

    public Wallet()
    {
        this.coin = 0;
        this.bullet_lv = 1;
        this.shot_lv = 1;
        this.lives_lv = 1;
        this.coin_lv = 1;
        this.bullet_bl = 0;
        this.shot_bl = 0;
        this.lives_bl = 0;
        this.coin_bl = 0;
        writeWallet();
    }

    public Wallet(String id, int coin, int bullet_lv, int shot_lv, int lives_lv, int coin_lv, int bullet_bl, int shot_bl, int lives_bl, int coin_bl)
    {
        this.id = id;
        this.coin = coin;
        this.bullet_lv = bullet_lv;
        this.shot_lv = shot_lv;
        this.lives_lv = lives_lv;
        this.coin_lv = coin_lv;
        this.bullet_bl = bullet_bl;
        this.shot_bl = shot_bl;
        this.lives_bl = lives_bl;
        this.coin_bl = coin_bl;
    }

    public int getCoin()
    {
        return coin;
    }

    public int getBullet_lv()
    {
        return bullet_lv;
    }

    public int getShot_lv()
    {
        return shot_lv;
    }

    public int getLives_lv()
    {
        return lives_lv;
    }

    public int getCoin_lv()
    {
        return coin_lv;
    }

    public int getBullet_bl() {return bullet_bl;}

    public int getShot_bl() {return shot_bl;}

    public int getLives_bl() {return lives_bl;}

    public int getCoin_bl() {return coin_bl;}

    public void setBullet_lv(int bullet_lv) {
        if (this.bullet_lv >= getMaxLevel(0)) {
            logger.warning("Upgrade failed: Bullet speed level cannot exceed max level (" + getMaxLevel(0) + ")");
            logger.warning("Withdraw blocked: Bullet level has reached or exceeded the max level.");
            blockWithdrawForBullet();
            return;
        };
        this.bullet_lv = bullet_lv;
        writeWallet();
        logger.info("Upgrade Bullet Speed from " + (bullet_lv - 1) + " to " + bullet_lv);
    }

    public void setShot_lv(int shot_lv) {
        if (this.shot_lv >= getMaxLevel(1)) {
            logger.warning("Upgrade failed: Shot frequency level cannot exceed max level (" + getMaxLevel(1) + ")");
            logger.warning("Withdraw blocked: Shot level has reached or exceeded the max level.");
            blockWithdrawForShot();
            return;
        }
        this.shot_lv = shot_lv;
        writeWallet();
        logger.info("Upgrade Shop Frequency from " + (shot_lv - 1) + " to " + shot_lv);
    }

    public void setLives_lv(int lives_lv) {
        if (this.lives_lv >= getMaxLevel(2)) {
            logger.warning("Upgrade failed: Additional lives level cannot exceed max level (" + getMaxLevel(2) + ")");
            logger.warning("Withdraw blocked: Lives level has reached or exceeded the max level.");
            blockWithdrawForLives();
            return;
        }
        this.lives_lv = lives_lv;
        writeWallet();
        logger.info("Upgrade Additional Lives from " + (lives_lv - 1) + " to " + lives_lv);
    }

    public void setCoin_lv(int coin_lv) {
        if (this.coin_lv >= getMaxLevel(3)) {
            logger.warning("Upgrade failed: Coin gain level cannot exceed max level (" + getMaxLevel(3) + ")");
            logger.warning("Withdraw blocked: Coin level has reached or exceeded the max level.");
            blockWithdrawForCoin();
            return;
        }
        this.coin_lv = coin_lv;
        writeWallet();
        logger.info("Upgrade Gain Coin from " + (coin_lv - 1) + " to " + coin_lv);
    }

    // Bullet 항목 출금 차단
    private boolean blockWithdrawForBullet() {
        if (this.bullet_lv >= getMaxLevel(0)) {
            logger.warning("Withdraw blocked: Bullet speed level has reached or exceeded the max level.");
            return true;
        }
        return false;
    }

    // Shot 항목 출금 차단
    private boolean blockWithdrawForShot() {
        if (this.shot_lv >= getMaxLevel(1)) {
            logger.warning("Withdraw blocked: Shot frequency level has reached or exceeded the max level.");
            return true;
        }
        return false;
    }

    // Lives 항목 출금 차단
    private boolean blockWithdrawForLives() {
        if (this.lives_lv >= getMaxLevel(2)) {
            logger.warning("Withdraw blocked: Additional lives level has reached or exceeded the max level.");
            return true;
        }
        return false;
    }

    // Coin 항목 출금 차단
    private boolean blockWithdrawForCoin() {
        if (this.coin_lv >= getMaxLevel(3)) {
            logger.warning("Withdraw blocked: Coin gain level has reached or exceeded the max level.");
            return true;
        }
        return false;
    }



    public void setBullet_bl(int bullet_bl)
    {
        this.bullet_bl = bullet_bl;
        writeWallet();
        logger.info("Break Limit of Bullet Speed " + (bullet_bl-1) + "to " + bullet_bl);
    }

    public void setShot_bl(int shot_bl)
    {
        this.shot_bl = shot_bl;
        writeWallet();
        logger.info("Break Limit of Shop Frequency  " + (shot_bl-1) + "to " + shot_bl);
    }

    public void setLives_bl(int lives_bl)
    {
        this.lives_bl = lives_bl;
        writeWallet();
        logger.info("Break Limit of Additional Lives " + (lives_bl-1) + "to " + lives_bl);
    }

    public void setCoin_bl(int coin_bl)
    {
        this.coin_bl = coin_bl;
        writeWallet();
        logger.info("Break Limit of Gain Coin " + (coin_bl-1) + "to " + coin_bl);
    }



    public boolean deposit(int amount)
    {
        if(amount <= 0) return false;
        if(coin > Integer.MAX_VALUE -amount)
        {
            coin = Integer.MAX_VALUE;
            return true;
        }
        coin += amount;
        writeWallet();
        logger.info("Deposit completed. Your coin: " + this.coin);
        return true;
    }

    public boolean withdraw(int amount) {
        // 항목별 출금 차단 조건 확인

        if (amount <= 0) return false;

        // 코인 수량이 충분한지 확인
        if (coin - amount < 0) {
            logger.info("Insufficient coin");
            return false; // 코인이 부족하면 출금하지 않음
        }
        else {
            coin -= amount;
            writeWallet();
            logger.info("Withdraw completed. Your coin: " + this.coin);
            return true;
        }
    }

    private void writeWallet() {
        String sql = "UPDATE user_wallet SET Coin = ?, BulletSpeed = ?, ShotInterval = ?, " +
                "AdditionalLife = ?, CoinGain = ?, BulletSpeedBL = ?, ShotIntervalBL = ?, AdditionalLifeBL = ?, CoinGainBL = ? WHERE id = ?";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 매개변수 설정
            pstmt.setInt(1, coin);
            pstmt.setInt(2, bullet_lv);
            pstmt.setInt(3, shot_lv);
            pstmt.setInt(4, lives_lv);
            pstmt.setDouble(5, coin_lv);
            pstmt.setInt(6, bullet_bl);
            pstmt.setInt(7, shot_bl);
            pstmt.setInt(8, lives_bl);
            pstmt.setDouble(9, coin_bl);
            pstmt.setString(10, id);
            // SQL 실행
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.SEVERE, "Wallet updated successfully!");
            } else {
                logger.log(Level.SEVERE, "No records updated. Check the ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 기존의 필드와 메서드는 생략

    public int getMaxLevel(int index) {
        switch (index) {
            case 0:
                return 5 + getBullet_bl();
            case 1:
                return 5 + getShot_bl();
            case 2:
                return 5 + getLives_bl();
            case 3:
                return 5 + getCoin_bl();
            default:
                return 5; // 기본값
        }
    }


}