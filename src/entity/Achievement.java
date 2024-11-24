package entity;

import java.sql.Connection;

public class Achievement {

    private String id;
    private int highScore;
    private int totalPlayTime;
    private int totalScore;
    private int currentPerfectStage;
    private double accuracy;
    private int maxCombo;
    private boolean flawlessFailure;
    private double highAccuracy; // 여기에 변수 선언

    public Achievement(String id, int highScore, int totalScore, int totalPlayTime, int currentPerfectStage, double accuracy,
                       int maxCombo, boolean flawlessFailure) {
        this.id = id;
        this.highScore = highScore;
        this.totalScore = totalScore;
        this.totalPlayTime = totalPlayTime;
        this.currentPerfectStage = currentPerfectStage;
        this.accuracy = accuracy;
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
    public Achievement(String id){
        this.id = id;
    }
    // Functions to get the status of each achievement.
    public String getID() { return id; }
    public int getHighScore() { return highScore; }
    public int getTotalScore() { return totalScore; }
    public int getTotalPlayTime() { return totalPlayTime; }
    public int getPerfectStage() { return currentPerfectStage; }
    public double getAccuracy() { return accuracy; }
    public int getHighMaxCombo() { return maxCombo; }
    public boolean getFlawlessFailure() { return flawlessFailure; }

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
    // Functions to store the status of each achievement.
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public void setTotalPlayTime(int totalPlayTime) { this.totalPlayTime += totalPlayTime; }
    public void setTotalScore(int totalScore) { this.totalScore += totalScore; }
    public void setHighMaxCombo(int maxCombo) { this.maxCombo = maxCombo; }
    public void setCurrentPerfectStage(int currentPerfectStage) { this.currentPerfectStage = currentPerfectStage; }
    public void setFlawlessFailure(boolean flawlessFailure) { this.flawlessFailure = flawlessFailure; }

}