package database;

import engine.manager.FileManager;
import entity.Achievement;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AchievementManager {

    /** Created AchievementManager class to easily manage achievement-related aspects */

    private DatabaseManager db;
    private static final Logger LOGGER = Logger.getLogger(AchievementManager.class.getName());

    private Achievement achievement;

    private int highScore;

    // Variables related to Perfect Achievement
    private static int currentPerfectLevel;
    private final int MAX_PERFECT_STAGE = 7;

    // Variables related to Accuracy Achievement
    private int highMaxCombo;
    private double highAccuracy;
    private final int[] ACCURACY_GOALS = {50, 70, 90, 100};
    private final int[] ACCURACY_REWARDS = {100, 200, 300, 500};

    // Variables related to Flawless Failure Achievement
    private boolean checkFlawlessFailure;

    // Coin rewards update
    private int coinReward = 0;

    private final int[] COMBO_COIN_REWARD = {500, 1500, 2000, 2500};
    private final int[] PERFECT_COIN_REWARD = {200, 400, 800, 2000, 3000, 4000, 5000};
    private final int FLAWLESS_FAILURE_COIN = 1000;
    private final int PLAY_TIME_COIN = 1000;

    // Variables needed for each achievement are loaded through a file.
    public AchievementManager(Achievement achievement) throws IOException {
        this.achievement = achievement;
        this.highScore = achievement.getHighScore();
        this.currentPerfectLevel = achievement.getPerfectStage();
        this.highMaxCombo = achievement.getHighMaxCombo();
        this.highAccuracy = achievement.getHighAccuracy();
        this.checkFlawlessFailure = achievement.getFlawlessFailure();
    }

    public int getAchievementReward() {
        return coinReward;
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            achievement.setHighScore(score);
        }
    }

    public void updateTotalPlayTime(int playTime) {
        if (achievement.getTotalPlayTime() < 600 && achievement.getTotalPlayTime() + playTime >= 600) {
            coinReward += PLAY_TIME_COIN;
        }
        achievement.setTotalPlayTime(playTime);
    }

    public void updateTotalScore(int score) {
        achievement.setTotalScore(score);
    }

    /**
     * Updates the maximum combo achievement.
     */
    public void updateMaxCombo(int maxCombo) {
        if (highMaxCombo >= maxCombo) {
            return;
        }
        int maxComboGoal = 10;
        if (highMaxCombo < 10) {
            maxComboGoal = 10;
        } else if (highMaxCombo < 15) {
            maxComboGoal = 15;
        } else if (highMaxCombo < 20) {
            maxComboGoal = 20;
        } else if (highMaxCombo < 25) {
            maxComboGoal = 25;
        }
        int rewardIndex = highMaxCombo / 5 - 1 <= 9 ? 0 : highMaxCombo / 5 - 1;
        highMaxCombo = maxCombo;
        if (highMaxCombo < maxComboGoal) {
            achievement.setHighMaxCombo(highMaxCombo);
            return;
        }
        if (highMaxCombo >= 25) {
            for (int i = rewardIndex; i < 4; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 20) {
            for (int i = rewardIndex; i < 3; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 15) {
            for (int i = rewardIndex; i < 2; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 10) {
            coinReward += COMBO_COIN_REWARD[0];
        }
        achievement.setHighMaxCombo(highMaxCombo);
    }

    /**
     * Updates the accuracy achievement.
     */
    public void updateAccuracy(double accuracy) {
        // 이미 달성된 목표 초기화
        for (int i = 0; i < ACCURACY_GOALS.length; i++) {
            if (highAccuracy >= ACCURACY_GOALS[i]) {
                achievement.getAchievedAccuracyGoals()[i] = true; // 목표 달성 상태를 true로 설정
            }
        }

        // 새로운 명중률이 기존보다 높을 경우 업데이트
        if (accuracy > highAccuracy) {
            highAccuracy = accuracy; // 최고 명중률 갱신g
            achievement.setHighAccuracy(highAccuracy); // 업적 객체에 설정

            // 새로운 목표 달성 확인 및 보상 지급
            for (int i = 0; i < ACCURACY_GOALS.length; i++) {
                if (!achievement.getAchievedAccuracyGoals()[i] && accuracy >= ACCURACY_GOALS[i]) {
                    achievement.getAchievedAccuracyGoals()[i] = true; // 목표 달성 상태 업데이트
                    coinReward += ACCURACY_REWARDS[i]; // 해당 목표에 대한 보상 지급
                }
            }
        }
    }
    /**
     * Check if the perfect achievement has been reached.
     */
    public void updatePerfect(final int MAX_LIVES, int checkLives, int gameLevel) {
        if (checkLives >= MAX_LIVES && currentPerfectLevel < MAX_PERFECT_STAGE && gameLevel > currentPerfectLevel) {
            currentPerfectLevel += 1;
            coinReward += PERFECT_COIN_REWARD[currentPerfectLevel - 1];
            achievement.setCurrentPerfectStage(currentPerfectLevel);
        }
    }

    public void updateFlawlessFailure(double accuracy) {
        if (!checkFlawlessFailure && accuracy <= 0) {
            checkFlawlessFailure = true;
            coinReward += FLAWLESS_FAILURE_COIN;
            achievement.setFlawlessFailure(true);
        }
    }

    public void updateAllAchievements() {
        String sql = "UPDATE user_ach SET HighScore = ?, TotalScore = ?, TotalPlaytime = ?, " +
                "PerfectStage = ?, highAccuracy = ?, MaxCombo = ?, FlawlessFailure = ? WHERE id = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, achievement.getHighScore());
            pstmt.setInt(2, achievement.getTotalScore());
            pstmt.setInt(3, achievement.getTotalPlayTime());
            pstmt.setInt(4, achievement.getPerfectStage());
            pstmt.setDouble(5, achievement.getHighAccuracy());
            pstmt.setInt(6, achievement.getHighMaxCombo());
            pstmt.setBoolean(7, checkFlawlessFailure);
            pstmt.setString(8, achievement.getID());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.SEVERE, "Achievement updated successfully!");
            } else {
                LOGGER.log(Level.SEVERE, "No records updated. Check the ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlaying(int maxCombo, int playtime, int maxLives, int livesRemaining, int level) {
        updateTotalPlayTime(playtime);
        updatePerfect(maxLives, livesRemaining, level);
        updateMaxCombo(maxCombo);
    }

    public void updatePlayed(double accuracy, int score) {
        updateHighScore(score);
        updateTotalScore(score);
        updateFlawlessFailure(accuracy);
        updateAccuracy(accuracy);
    }
}