package entity;

public class Achievement {

    private String id;
    private int highScore, totalPlayTime, totalScore, maxCombo, currentPerfectStage;
    private boolean flawlessFailure;
    private double highAccuracy;
    private boolean[] achievedAccuracyGoals; // 명중률 목표 달성 여부를 추적하는 배열

    // Primary constructor
    public Achievement(String id, int highScore, int totalScore, int totalPlayTime, int currentPerfectStage,
                       int maxCombo, boolean flawlessFailure, double highAccuracy) {
        this.id = id;
        this.highScore = highScore;
        this.totalScore = totalScore;
        this.totalPlayTime = totalPlayTime;
        this.currentPerfectStage = currentPerfectStage;
        this.maxCombo = maxCombo;
        this.flawlessFailure = flawlessFailure;
        this.highAccuracy = highAccuracy;
        this.achievedAccuracyGoals = new boolean[]{false, false, false, false}; // 초기화
    }

    // Secondary constructor with only ID, initializing other fields to default values
    public Achievement(String id) {
        this(id, 0, 0, 0, 0, 0, false, 0.0);
    }

    // Getters
    public String getID() { return id; }
    public int getHighScore() { return highScore; }
    public int getTotalScore() { return totalScore; }
    public int getTotalPlayTime() { return totalPlayTime; }
    public int getHighMaxCombo() { return maxCombo; }
    public int getPerfectStage() { return currentPerfectStage; }
    public boolean getFlawlessFailure() { return flawlessFailure; }
    public double getHighAccuracy() { return highAccuracy; }
    public boolean[] getAchievedAccuracyGoals() { return achievedAccuracyGoals; } // Getter for accuracy goals

    // Setters
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public void setTotalPlayTime(int totalPlayTime) { this.totalPlayTime += totalPlayTime; }
    public void setTotalScore(int totalScore) { this.totalScore += totalScore; }
    public void setHighMaxCombo(int maxCombo) { this.maxCombo = maxCombo; }
    public void setCurrentPerfectStage(int currentPerfectStage) { this.currentPerfectStage = currentPerfectStage; }
    public void setFlawlessFailure(boolean flawlessFailure) { this.flawlessFailure = flawlessFailure; }
    public void setHighAccuracy(double highAccuracy) { this.highAccuracy = highAccuracy; }
    public void setAchievedAccuracyGoals(boolean[] achievedAccuracyGoals) {
        this.achievedAccuracyGoals = achievedAccuracyGoals;
    } // Setter for accuracy goals
}