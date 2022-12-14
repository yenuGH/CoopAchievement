package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;

public class AchievementCelebrationActivity extends AppCompatActivity {

    private static final String GAME_PLAYED_INDEX_TAG = "GamePlayedIndex";
    private static final String GAME_PLAYED_TYPE_TAG = "GameType";
    private final int INVALID_GAME_PLAYED_INDEX = -1;

    private GameManager gameManager;
    private int gamePlayedIndex;
    private String gameTypeString;
    private GameType gameType;

    private PlayedGame playedGame;
    private int playerCount;
    private int gameScore;
    private String difficulty;
    private String currentLevelName;
    private String nextLevelName;
    private int pointsToNextLevel;

    private TextView tvGameScore;
    private TextView tvNextLevelName;
    private ImageView ivGameSelfie;

    public static Intent makeIntent(Context context, String gameType, int gamePlayedIndex){
        Intent intent = new Intent(context, AchievementCelebrationActivity.class);
        intent.putExtra(GAME_PLAYED_INDEX_TAG, gamePlayedIndex);
        intent.putExtra(GAME_PLAYED_TYPE_TAG, gameType);
        return intent;
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();
        gamePlayedIndex = intent.getIntExtra(GAME_PLAYED_INDEX_TAG, INVALID_GAME_PLAYED_INDEX);
        gameTypeString = intent.getStringExtra(GAME_PLAYED_TYPE_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_celebration);


        extractIntentExtras();
        getGameInfo();
        setGameInfo();
        setPlayAnimationButton();
        setSelectThemeSpinner();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.game_info);
        actionBar.setDisplayHomeAsUpEnabled(true);

        playAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return true;
    }

    private void getGameInfo(){
        gameManager = GameManager.getInstance();
        playedGame = gameManager.getSpecificPlayedGames(gameTypeString).get(gamePlayedIndex);
        gameType = gameManager.getGameTypeFromString(gameTypeString);

        playerCount = playedGame.getNumberOfPlayers();
        gameScore = playedGame.getTotalScore();

        difficulty = playedGame.getDifficulty();
        currentLevelName = playedGame.getAchievement();

        ArrayList<String> achievementLevelScoreRequirements = gameType.getAchievementLevelScoreRequirements(playerCount, difficulty);
        int nextLevelScore = 0;
        // Iterate through each achievement level, use regex to grab numerical values
        for (String achievementLevelScore : achievementLevelScoreRequirements){
            // With the numerical value, if it is greater than the current game score, it is the score of the next level
            int achievementScore = Integer.parseInt(achievementLevelScore.replaceAll("[^0-9]", ""));
            if (achievementScore > gameScore){
                nextLevelScore = achievementScore;
                break;
            }
        }

        // If nextLevelScore == 0, gameScore is on the dot of max level
        if (nextLevelScore != 0){
            pointsToNextLevel = nextLevelScore - gameScore;
            nextLevelName = gameType.getAchievementLevel(nextLevelScore, playerCount, difficulty);
        }
        else {
            pointsToNextLevel = 0;
            // + 1 to get max level name (as we are on the dot of it, but max level must be greater than
            nextLevelName = gameType.getAchievementLevel(gameScore + 1, playerCount, difficulty);
        }

    }

    private void setGameInfo(){
        TextView tvNumberOfPlayers = findViewById(R.id.tvNumberOfPlayers);
        TextView tvNextLevelGap = findViewById(R.id.tvNextLevelGap);

        tvGameScore = findViewById(R.id.tvGameScore);
        tvNextLevelName = findViewById(R.id.tvNextLevelName);
        ivGameSelfie = findViewById(R.id.ivGameSelfie);

        ivGameSelfie.setImageBitmap(getBitmapFromPath(playedGame.getPicturePath(), AchievementCelebrationActivity.this.getResources()));

        tvNumberOfPlayers.setText(getString(R.string.ac_players_display, playerCount));
        tvGameScore.setText(getString(R.string.ac_score_display, currentLevelName, gameScore));


        tvNextLevelGap.setText(getString(R.string.ac_next_level_gap_display, pointsToNextLevel));
        tvNextLevelName.setText(getString(R.string.ac_next_level_title_display, nextLevelName, (gameScore + pointsToNextLevel)));
    }

    private Bitmap getBitmapFromPath(String imagePath, Resources resources){
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        if(imageBitmap == null){
            imageBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        }
        return imageBitmap;
    }

    private void setPlayAnimationButton(){
        Button btnPlayAnimation = findViewById(R.id.btnPlayAnimation);
        btnPlayAnimation.setOnClickListener(view -> {
            playAnimation();
        });
    }

    private void playAnimation(){
        // Animate the star to signify the achievement
        ImageView starImage = findViewById(R.id.ivAchievementAnimation);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        starImage.startAnimation(animation);
        starImage.setVisibility(View.VISIBLE);

        // Store a reference to the activity so we can end the activity after an animation finishes
        Activity thisActivity = this;

        // End the activity after the animation finished https://stackoverflow.com/questions/7606498/end-animation-event-android
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Play a sound
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.achievement_jingle);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void setSelectThemeSpinner(){
        // https://www.youtube.com/watch?v=on_OrrX7Nw4
        Spinner spnrSelectTheme = findViewById(R.id.spnrSelectTheme);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                AchievementCelebrationActivity.this,
                R.array.theme_names,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSelectTheme.setAdapter(arrayAdapter);

        // https://stackoverflow.com/questions/10634180/how-to-set-spinner-default-by-its-value-instead-of-position
        // Set the default choice to the app's selected theme
        spnrSelectTheme.setSelection(gameManager.getAchievementTheme());

        spnrSelectTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // i is the position of the selected item
                gameManager.setGameTheme(i);

                // We need to redefine current level name and next level name
                getGameInfo();
                setGameInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}








