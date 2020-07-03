package cmpt276.assignment.iteration1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Activity for showing the Game's Main Menu, where players can click buttons to go to a variety of Activities
 * @Author Michael Mora
 */
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setUpPlayButton();
        setUpHelpButton();
        setUpOptionsButton();
    }

    private void setUpPlayButton(){
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HelpSreenActivity.class);//Change when Activity for the actual game is added
                startActivity(intent);
            }
        });
    }

    private void setUpHelpButton(){
        Button btnHelp = findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HelpSreenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpOptionsButton(){
        Button btnOptions = findViewById(R.id.btnOptions);
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HelpSreenActivity.class);//Change when Activity for the options is added
                startActivity(intent);
            }
        });
    }

    private void setUpHighScoresButton(){
        Button btnHighScores = findViewById(R.id.btnHighScores);
        btnHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainMenuActivity.class);//Change when Activity for High Scores is added
                startActivity(intent);
            }
        });
    }
}
