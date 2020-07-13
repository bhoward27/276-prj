package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ca.cmpt276.prj.R;

import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;

/**
 * Activity for showing the Game's Main Menu, where players can click buttons to go to a variety of Activities
 */
public class MainMenuActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setUpImages();
        setUpButtons();

    }

    private void setUpImages(){//Images change depending on the currently selected deck
        ImageView caveOrBear = (ImageView)findViewById(R.id.img_cave_or_bear);
        ImageView desertOrEagle = (ImageView)findViewById(R.id.img_desert_or_eagle);
        ImageView forestOrLeopard = (ImageView)findViewById(R.id.img_forest_or_leopard);
        ImageView oceanOrLion = (ImageView)findViewById(R.id.img_ocean_or_lion);
        ImageView stormOrShark = (ImageView)findViewById(R.id.img_storm_or_shark);
        //ImageView sunriseOrSnake = (ImageView)findViewById(R.id.img_sunrise_or_snake);
        ImageView volcanoOrSpider = (ImageView)findViewById(R.id.img_volcano_spider);

        int imageSet = 0;//Test code; when Options is implemented this may be changed.

        if(imageSet == LANDSCAPE_SET){
            caveOrBear.setImageResource(R.drawable.landscape_cave);
            desertOrEagle.setImageResource(R.drawable.landscape_desert);
            forestOrLeopard.setImageResource(R.drawable.landscape_forest);
            oceanOrLion.setImageResource(R.drawable.landscape_ocean);
            stormOrShark.setImageResource(R.drawable.landscape_storm);
            //sunriseOrSnake.setImageResource(R.drawable.landscape_sunrise);
            volcanoOrSpider.setImageResource(R.drawable.landscape_volcano);
        }else{
            caveOrBear.setImageResource(R.drawable.predator_bear);
            desertOrEagle.setImageResource(R.drawable.predator_eagle);
            forestOrLeopard.setImageResource(R.drawable.predator_leopard);
            oceanOrLion.setImageResource(R.drawable.predator_lion);
            stormOrShark.setImageResource(R.drawable.predator_shark);
            //sunriseOrSnake.setImageResource(R.drawable.predator_snake);
            volcanoOrSpider.setImageResource(R.drawable.predator_spider);
        }
    }
    private void setUpButtons() {

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);//Change when Activity for the actual game is added
                startActivity(intent);
            }
        });

        Button btnOpenHelp = findViewById(R.id.btnOpenHelp);
        btnOpenHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        Button btnOpenOptions = findViewById(R.id.btnOpenOptions);
        btnOpenOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        Button btnOpenHighScores = findViewById(R.id.btnOpenHighScores);
        btnOpenHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HighScoresActivity.class);
                startActivity(intent);
            }
        });
    }
}
