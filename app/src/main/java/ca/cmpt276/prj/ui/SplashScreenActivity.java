package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.PrefsManager;

import static ca.cmpt276.prj.model.Constants.PREFS;

/**
 * Activity for showing the animated intro preceding an automatic change to the Main Menu Activity. Also includes a button to skip the intro
 * and go straight to the aforementioned Activity.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // instantly instantiate SharedPreferences singleton for global use over program
        PrefsManager.instantiate(getSharedPreferences(PREFS, Context.MODE_PRIVATE));

        setUpAnimatedIntroText();
        setUpSkipButton();
        beginAutoSkipTimer();
    }


    private void setUpAnimatedIntroText(){
        TextView animatedText = findViewById(R.id.textIntroTitle);
        Animation spinAnimation = AnimationUtils.loadAnimation(this, R.anim.animations);
        animatedText.startAnimation(spinAnimation);
    }

    private void beginAutoSkipTimer(){
        new CountDownTimer(7000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                goToMainMenu();
            }
        }.start();
    }

    private void setUpSkipButton() {
        Button btnMainMenu = findViewById(R.id.btnMainMenu);
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });
    }

    private void goToMainMenu(){
        Intent intent = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
}
