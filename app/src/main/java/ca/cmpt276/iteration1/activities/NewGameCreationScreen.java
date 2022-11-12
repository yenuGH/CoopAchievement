package ca.cmpt276.iteration1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.model.GameManager;
import ca.cmpt276.iteration1.model.GameType;
import ca.cmpt276.iteration1.model.PlayedGame;

/*
 * Takes in an intent from previous activity and creates a new game of the "GameType"
 * using the inputs "number of players" and "GameScore" then adds it to the "Played Games"
 * array list in the GameManager
 *
 * */
public class NewGameCreationScreen extends AppCompatActivity {


    private String gameTypeString;
    private GameType gameType;
    private GameManager gm;

    // For when we are editing an existing played game
    private final int POSITION_NON_EXISTENT = -1;
    private int gamePlayedPosition;
    private PlayedGame playedGame;

    EditText gameScore;
    EditText numberOfPlayers;

    // When it takes in a string, we are creating a new game based on the type
    public static Intent makeIntent(Context context, String gameTypeString){
        Intent intent = new Intent(context, NewGameCreationScreen.class);
        intent.putExtra("GameType", gameTypeString);
        return intent;
    }

    // When it receives a string and position, we are editing an existing game
    public static Intent makeIntent(Context context, String gameTypeString, int position){
        Intent intent = new Intent(context, NewGameCreationScreen.class);
        intent.putExtra("GameType", gameTypeString);
        intent.putExtra("GamePlayedPosition", position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_creation_screen);


        // action bar setup
        ActionBar ab = getSupportActionBar();
        MenuInflater menuInflater = getMenuInflater();
        ab.setDisplayHomeAsUpEnabled(true);

        // edittext input field setup
        gameScore = findViewById(R.id.etGameScore);
        gameScore.addTextChangedListener(inputTextWatcher);
        numberOfPlayers = findViewById(R.id.etNumberOfPlayers);
        numberOfPlayers.addTextChangedListener(inputTextWatcher);

        gm = GameManager.getInstance();
        gameType = gm.getGameTypeFromString(gameTypeString);
        extractIntentExtras();
    }

    private void extractIntentExtras(){
        Intent intent = getIntent();
        this.gameTypeString = intent.getStringExtra("GameType");
        this.gamePlayedPosition = intent.getIntExtra("GamePlayedPosition", POSITION_NON_EXISTENT);
        this.gameType = gm.getGameTypeFromString(gameTypeString);

        // Creating a new game
        if (this.gamePlayedPosition == POSITION_NON_EXISTENT){
            setTitle(getString(R.string.add_new_game));
        }
        // Editing an existing game
        else {
            setTitle(getString(R.string.edit_existing_game));
            setPlayedGameInfo();
        }
    }

    private void setPlayedGameInfo(){
        ArrayList<PlayedGame> playedGames = gm.getSpecificPlayedGames(gameTypeString);
        this.playedGame = playedGames.get(gamePlayedPosition);

        gameScore.setText(String.valueOf(playedGame.getScore()));
        numberOfPlayers.setText(String.valueOf(playedGame.getNumberOfPlayers()));
    }

    private void displayError(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private final TextWatcher inputTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            TextView displayAchievementLevel = findViewById(R.id.tvGameAchievementLevel);

            try {
                int players = Integer.parseInt(numberOfPlayers.getText().toString());
                int score = Integer.parseInt(gameScore.getText().toString());

                if (players == 0) {
                    displayError(getString(R.string.player_number_error));
                    throw new NumberFormatException();
                }
                GameType gameType = gm.getGameTypeFromString(gameTypeString);
                String achievementLevel = getString(R.string.achievement_level) + " " + gameType.getAchievementLevel(score, players);

                displayAchievementLevel.setText(achievementLevel);
            }
            catch (NumberFormatException numberFormatException) {
                displayAchievementLevel.setText(R.string.game_achievement_level_calculating);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu
        getMenuInflater().inflate(R.menu.menu_add_type_appbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSave:
                try {
                    EditText etGameScore = findViewById(R.id.etGameScore);
                    EditText etNumberOfPlayers = findViewById(R.id.etNumberOfPlayers);

                    if (etNumberOfPlayers == null || etGameScore == null) {
                        throw new IllegalArgumentException("Edit Text fields need to be filled");
                    }

                    int gameScore = Integer.parseInt(etGameScore.getText().toString());
                    int numberOfPlayers = Integer.parseInt(etNumberOfPlayers.getText().toString());

                    if (gameScore < 0 || numberOfPlayers <= 0) {
                        throw new IllegalArgumentException("Edit Text fields need to have positive values");
                    }

                    // Creating a new game
                    if (gamePlayedPosition == POSITION_NON_EXISTENT){
                        PlayedGame currGame = new PlayedGame(gameTypeString, numberOfPlayers, gameScore, gameType.getAchievementIndex(gameScore,numberOfPlayers));
                        gm.addPlayedGame(currGame);

                        String res = gameTypeString + getString(R.string.game_saved_toast);
                        Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
                    }
                    // Editing an existing game
                    else {
                        playedGame.editPlayedGame(numberOfPlayers, gameScore, gameType.getAchievementIndex(gameScore,numberOfPlayers));
                        Toast.makeText(this, getString(R.string.save_changes_to_existing_game), Toast.LENGTH_SHORT).show();
                    }

                    finish();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(this, R.string.invalid_game, Toast.LENGTH_SHORT).show();
                    break;
                }
            case android.R.id.home:
                this.finish();
                return true;
        }


        return true;
    }

}