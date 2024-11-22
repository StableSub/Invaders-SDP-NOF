package entity;

public class Achievement {

    private int totalPlayTime;
    private int totalScore;
    private int maxCombo;
    private int currentPerfectStage;
    private boolean flawlessFailure;
    private double highAccuracy; // 여기에 변수 선언

    public Achievement(int totalPlayTime, int totalScore, int maxCombo, int currentPerfectStage,
                       boolean flawlessFailure, double highAccuracy) {
        this.totalPlayTime = totalPlayTime;
        this.totalScore = totalScore;
        this.maxCombo = maxCombo;
        this.currentPerfectStage = currentPerfectStage;
        this.flawlessFailure = flawlessFailure;
        this.highAccuracy = highAccuracy; // 변수 초기화
    }

    // Getters
    public int getTotalPlayTime() {
        return totalPlayTime;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getHighmaxCombo() {
        return maxCombo;
    }

    public int getPerfectStage() {
        return currentPerfectStage;
    }

    public boolean getFlawlessFailure() {
        return flawlessFailure;
    }

    public double getHighAccuracy() { // Getter for highAccuracy
        return highAccuracy;
    }

    // Setters
    public void setTotalPlayTime(int totalPlayTime) {
        this.totalPlayTime += totalPlayTime;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore += totalScore;
    }

    public void setHighAccuracy(double highAccuracy) { // Setter for highAccuracy
        this.highAccuracy = highAccuracy;
    }

    public void setHighMaxcombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    public void setCurrentPerfectStage(int currentPerfectStage) {
        this.currentPerfectStage = currentPerfectStage;
    }

    public void setFlawlessFailure(boolean flawlessFailure) {
        this.flawlessFailure = flawlessFailure;
    }
}