package ca.cmpt276.iteration1.model;

/*
* The PlayedGame class is for storing played game's data.
* type for game name
* numberOfPlayers for #Players
* score for the total score of the whole team game
* achievement for the achievement that the team got according to their score.
* */

import androidx.annotation.NonNull;

import java.util.List;

public class PlayedGame {
    private final String type;
    private int numberOfPlayers;
    private int totalScore;
    private int achievementIndex;
    private String difficulty;
    private List playerScore;

    public PlayedGame(String type, int numberOfPlayers, int totalScore, int achievementIndex, String difficulty, List playerScore) {
        this.type = type;
        this.numberOfPlayers = numberOfPlayers;
        this.totalScore = totalScore;
        this.achievementIndex = achievementIndex;
        this.difficulty = difficulty;
        this.playerScore = playerScore;
    }

    public String getType() {
        return type;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getAchievement() {
        GameManager gameManager = GameManager.getInstance();
        return GameType.getAchievementName(achievementIndex, gameManager.getAchievementTheme());
    }

    public void editPlayedGame(int numberOfPlayers, int score, int achievementIndex){
        this.numberOfPlayers = numberOfPlayers;
        this.totalScore = score;
        this.achievementIndex = achievementIndex;
    }

    @NonNull
    @Override
    public String toString() {
        String output;
        output = "Score: " + totalScore + ", " + numberOfPlayers + "Players, " + getAchievement();
        return output;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
