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
        TextView animatedIntroTitle = findViewById(R.id.textIntroTitle);
        Animation mangifyingGlassAnimation = AnimationUtils.loadAnimation(this, R.anim.magnifying_glass_animation);
        Animation introTitleAnimation = AnimationUtils.loadAnimation(this, R.anim.title_text_animation);
        animatedMagnifyingGlass.startAnimation(mangifyingGlassAnimation);

        //Code to use setAnimationListener adapted from RightHandedMonkey @ https://stackoverflow.com/questions/5731019/android-animation-one-after-another
        mangifyingGlassAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation currentAnimation) {//Wait until animatedMagnifyingGlass' animation is finished before animating animatedText.
                animatedMagnifyingGlass.setImageResource(0);
                animatedIntroTitle.startAnimation(introTitleAnimation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

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
        finish();//Once Splash Screen is left, pressing back button on Main Menu should NOT return to this activity; finish() ensures Splash Screen cannot be returned to during runtime.-
    }
}
