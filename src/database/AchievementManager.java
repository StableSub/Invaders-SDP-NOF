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

    // Variables related to Flawless Failure Achievement
    private boolean checkFlawlessFailure;

    // Coin rewards update
    private int coinReward = 0;

    private final int[] COMBO_COIN_REWARD = {500, 1500, 2000, 2500};
    private final int[] PERFECT_COIN_REWARD = {200, 400, 800, 2000, 3000, 4000, 5000};
    private final int FLAWLESS_FAILURE_COIN = 1000;
    private final int PLAY_TIME_COIN = 1000;

    private boolean checkPlayTimeAch;

    // Variables needed for each achievement are loaded through a file.
    public AchievementManager(Achievement achievement) throws IOException {
        this.achievement = achievement;
        this.highScore = achievement.getHighScore();
        this.currentPerfectLevel = achievement.getPerfectStage();
        this.highMaxCombo = achievement.getHighMaxCombo();
        this.checkFlawlessFailure = achievement.getFlawlessFailure();
        this.highAccuracy = achievement.getHighAccuracy();
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
        // When an accuracy achievement is reached, all lower achievements are achieved together.
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
        // Save the updated achievement.
        achievement.setHighMaxCombo(highMaxCombo);
    }

    /**
     * Updates the accuracy achievement.
     */
    public void updateAccuracy(double accuracy) {
        // 현재 높은 명중률(highAccuracy)보다 낮거나 같으면 아무 작업도 하지 않음
        if (highAccuracy >= accuracy) {
            return;
        }

        // 명중률 목표를 설정
        int[] accuracyGoals = {50, 70, 90, 100}; // 업적 목표
        int[] rewards = {100, 200, 300, 500}; // 각 목표 달성 보상
        boolean[] achievedGoals = new boolean[accuracyGoals.length]; // 목표 달성 여부 추적

        // 이전에 달성한 업적을 유지
        for (int i = 0; i < accuracyGoals.length; i++) {
            if (highAccuracy >= accuracyGoals[i]) {
                achievedGoals[i] = true;
            }
        }

        // 명중률 갱신
        highAccuracy = accuracy;

        // 새로운 업적 달성 및 보상 계산
        int totalReward = 0;
        for (int i = 0; i < accuracyGoals.length; i++) {
            if (!achievedGoals[i] && highAccuracy >= accuracyGoals[i]) {
                achievedGoals[i] = true; // 목표 달성
                totalReward += rewards[i]; // 보상 추가
            }
        }

        // 보상 지급
        coinReward += totalReward;

        // 업적 상태 업데이트
        achievement.setHighAccuracy(highAccuracy);
    }

    /**
     * Updates the perfect achievement.
     */
    public void updatePerfect(final int MAX_LIVES, int checkLives, int gameLevel) {
        if (checkLives >= MAX_LIVES && currentPerfectLevel < MAX_PERFECT_STAGE && gameLevel > currentPerfectLevel) {
            // Check if the current perfect stage has not exceeded the total stages.
            currentPerfectLevel += 1;
            coinReward += PERFECT_COIN_REWARD[currentPerfectLevel - 1];
            achievement.setCurrentPerfectStage(currentPerfectLevel);
        }
    }

    /**
     * Updates the flawless failure achievement.
     */
    public void updateFlawlessFailure(double accuracy) {
        if (!checkFlawlessFailure && accuracy <= 0) {
            checkFlawlessFailure = true;
            coinReward += FLAWLESS_FAILURE_COIN;
            achievement.setFlawlessFailure(true);
        }
    }

    public void updateAllAchievements() {
        String sql = "UPDATE user_ach SET HighScore = ?, TotalScore = ?, TotalPlaytime = ?, " +
                "PerfectStage = ?, Accuracy = ?, MaxCombo = ?, FlawlessFailure = ? WHERE id = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 매개변수 설정
            pstmt.setInt(1, achievement.getHighScore());
            pstmt.setInt(2, achievement.getTotalScore());
            pstmt.setInt(3, achievement.getTotalPlayTime());
            pstmt.setInt(4, achievement.getPerfectStage());
            pstmt.setDouble(5, achievement.getAccuracy());
            pstmt.setInt(6, achievement.getHighMaxCombo());
            pstmt.setBoolean(7, checkFlawlessFailure);
            pstmt.setString(8, achievement.getID());
            // SQL 실행
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.SEVERE, "Achievement updated successfully!");
            } else {
                LOGGER.log(Level.SEVERE, "No records updated. Check the ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    /**
     * Saves all achievements.
     */
    public void updateAllAchievements() throws IOException {
        FileManager.getInstance().saveAchievement(achievement);
    }

    public void updatePlaying(int maxCombo, int playtime, int max_lives, int LivesRemaining, int level) throws IOException {
    public void updatePlaying(int maxCombo ,int playtime, int max_lives, int LivesRemaining, int level) {
        updateTotalPlayTime(playtime);
        updatePerfect(max_lives, LivesRemaining, level);
        updateMaxCombo(maxCombo);
    }

    public void updatePlayed(double accuracy, int score) throws IOException {
    public void updatePlayed(double accuracy, int score) {
        updateHighScore(score);
        updateTotalScore(score);
        updateFlawlessFailure(accuracy);
        updateAccuracy(accuracy);
    }
}