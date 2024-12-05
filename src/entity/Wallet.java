package entity;

import database.DatabaseManager;
import engine.core.Core;

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

    //shot frequency level
    private int shot_lv;

    //additional lives level
    private int lives_lv;

    //coin gain level
    private int coin_lv;

    public Wallet()
    {
        this.coin = 0;
        this.bullet_lv = 1;
        this.shot_lv = 1;
        this.lives_lv = 1;
        this.coin_lv = 1;
        writeWallet();
    }

    public Wallet(String id, int coin, int bullet_lv, int shot_lv, int lives_lv, int coin_lv)
    {
        this.id = id;
        this.coin = coin;
        this.bullet_lv = bullet_lv;
        this.shot_lv = shot_lv;
        this.lives_lv = lives_lv;
        this.coin_lv = coin_lv;
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

    public void setBullet_lv(int bullet_lv)
    {
        this.bullet_lv = bullet_lv;
        writeWallet();
        logger.info("Upgrade Bullet Speed " + (bullet_lv-1) + "to " + bullet_lv);
    }

    public void setShot_lv(int shot_lv)
    {
        this.shot_lv = shot_lv;
        writeWallet();
        logger.info("Upgrade Shop Frequency  " + (shot_lv-1) + "to " + shot_lv);
    }

    public void setLives_lv(int lives_lv)
    {
        this.lives_lv = lives_lv;
        writeWallet();
        logger.info("Upgrade Additional Lives " + (lives_lv-1) + "to " + lives_lv);
    }

    public void setCoin_lv(int coin_lv)
    {
        this.coin_lv = coin_lv;
        writeWallet();
        logger.info("Upgrade Gain Coin " + (coin_lv-1) + "to " + coin_lv);
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

    public boolean withdraw(int amount)
    {
        if(amount <= 0) return false;
        if(coin - amount < 0)
        {
            logger.info("Insufficient coin");
            return false;
        }
        coin -= amount;
        writeWallet();
        logger.info("Withdraw completed. Your coin: " + this.coin);
        return true;
    }

    private void writeWallet() {
        String sql = "UPDATE user_wallet SET Coin = ?, BulletSpeed = ?, ShotInterval = ?, " +
                "AdditionalLife = ?, CoinGain = ? WHERE id = ?";
        try (Connection conn = new DatabaseManager().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 매개변수 설정
            pstmt.setInt(1, coin);
            pstmt.setInt(2, bullet_lv);
            pstmt.setInt(3, shot_lv);
            pstmt.setInt(4, lives_lv);
            pstmt.setDouble(5, coin_lv);
            pstmt.setString(6, id);
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
}