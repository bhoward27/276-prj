package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.PrefsManager;

import static ca.cmpt276.prj.model.Constants.PREFS;

/**
 * Activity for showing the animated intro preceding an automatic change to the Main Menu Activity. Also includes a button to skip the intro
 * and go straight to the aforementioned Activity.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private boolean skipButtonPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // instantly instantiate SharedPreferences singleton for global use over program
        PrefsManager.instantiate(getSharedPreferences(PREFS, Context.MODE_PRIVATE));

        setUpAnimatedIntro();
        setUpSkipButton();
        beginAutoSkipTimer();
    }


    private void setUpAnimatedIntro(){
        ImageView animatedMagnifyingGlass = findViewById(R.id.magnifyingGlass);
        TextView animatedText = findViewById(R.id.textIntroTitle);
        Animation translateRightAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_right_animation);
        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_animation);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);
        animatedMagnifyingGlass.startAnimation(translateRightAnimation);
        new CountDownTimer(2000, 1000){
            //Calling cancel stops function from redundantly calling goToMainMenu() if the skip button was pressed.
            //Otherwise, a user that uses the skip button and goes to a new activity from the Main Menu before this timer ends
            //would be forced back to the Main Menu when the timer ends.
            @Override
            public void onTick(long millisUntilFinished) {
                if(skipButtonPressed) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                //Wait until translateRightAnimation is finished via CounDownTimer before starting zoomAnimation.
                animatedMagnifyingGlass.startAnimation(fadeOutAnimation);
            }
        }.start();

        new CountDownTimer(4000, 1000){
            //Calling cancel stops function from redundantly calling goToMainMenu() if the skip button was pressed.
            //Otherwise, a user that uses the skip button and goes to a new activity from the Main Menu before this timer ends
            //would be forced back to the Main Menu when the timer ends.
            @Override
            public void onTick(long millisUntilFinished) {
                if(skipButtonPressed) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                //Wait until fadeOutAnimation is finished via CounDownTimer before starting zoomAnimation.
                animatedMagnifyingGlass.setImageResource(0);
                animatedText.startAnimation(zoomInAnimation);
            }
        }.start();
    }

    private void beginAutoSkipTimer(){
        new CountDownTimer(10000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(skipButtonPressed) {
                    cancel();
                }
            }

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
                skipButtonPressed = true;
                goToMainMenu();
            }
        });
    }

    private void goToMainMenu(){
        Intent intent = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();//Once the Splash Screen is left, pressing the back button on the Main Menu should NOT return to this activity.
    }
}
