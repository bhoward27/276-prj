package cmpt276.assignment.iteration1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity for showing the animated intro preceding an automatic change to the Main Menu Activity. Also includes a button to skip the intro
 * and go straight to the aforementioned Activity.
 * @Author Michael Mora
 */
public class IntroScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        setUpAnimatedIntroText();
        setUpSkipButton();
        beginAutoSkipTimer();
    }

    private void setUpAnimatedIntroText(){
        TextView animatedText = findViewById(R.id.gameTitle);
         Animation spinAnimation = AnimationUtils.loadAnimation(this, R.anim.animations);
        animatedText.startAnimation(spinAnimation);
    }

    private void setUpSkipButton(){
        Button btnSkip = findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });
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

    private void goToMainMenu(){
        Intent intent = new Intent(IntroScreenActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
}
