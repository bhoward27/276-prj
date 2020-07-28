package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.ImageNameMatrix;
import ca.cmpt276.prj.model.OptionSet;

import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

/**
 * Activity for showing the Game's Main Menu, where players can click buttons to go to a variety of Activities
 */
public class MainMenuActivity extends AppCompatActivity {
    OptionSet options;

    //  ***This method should be deleted before merging this branch.***
    private void testFiles() {
        /*
        CITATION: This IO / File related code is based off of stuff from the following links:
            http://www.lucazanini.eu/en/2016/android/saving-reading-files-internal-storage/
            https://developer.android.com/reference/android/content/Context.html#getDir(java.lang.String,%20int)
            https://developer.android.com/reference/java/io/File
            https://developer.android.com/reference/android/content/Context#MODE_PRIVATE
            https://developer.android.com/reference/android/content/Context#MODE_APPEND
         */
        final String FOLDER_NAME = "FLICKR_USER_IMAGES";
        final String FILE_NAME = "test_file.txt";
        String stars = "\n*******\n";
        System.out.println(stars + "Calling getDir()..." + stars);
        File file = getDir(FOLDER_NAME, Context.MODE_PRIVATE);
        //System.out.println(stars + "Done." + stars);

        try {
            FileOutputStream fout = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            System.out.println("File found!");

            String desiredOutput = "Testing 1, 2, 3...";
            fout.write(desiredOutput.getBytes());
            fout.close();


            final int NUM_BYTES = 1024;
            byte[] bytes = new byte[NUM_BYTES];
            FileInputStream fin = openFileInput(FILE_NAME);
            fin.read(bytes);
            fin.close();

            String output = new String(bytes);
            System.out.println("Output string: " + output);
        }
        catch (IOException e) {
            Log.e("ERROR", e.toString());
        }

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        options = OptionSet.getInstance();
        testFiles();

        setUpImages();
        setUpButtons();

    }

    private void setUpImages() {//Images change depending on the currently selected deck in the Options screen
        List<ImageView> views = new ArrayList<>();
        views.add(findViewById(R.id.img_1));
        views.add(findViewById(R.id.img_2));
        views.add(findViewById(R.id.img_3));
        views.add(findViewById(R.id.img_4));
        views.add(findViewById(R.id.img_5));
        views.add(findViewById(R.id.img_6));
        views.add(findViewById(R.id.img_7));
        // randomize images? we can remove this, just an idea
        Collections.shuffle(views);

        String imageSetPrefix = options.getImageSetPrefix();

        for (ImageView view : views) {
            int resourceID = getResources().getIdentifier(imageSetPrefix + RESOURCE_DIVIDER
                            + views.indexOf(view), IMAGE_FOLDER_NAME, getPackageName());
            view.setImageResource(resourceID);
        }
    }

    private void setUpButtons() {

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);//Change when Activity for the actual game is added
            startActivity(intent);
        });

        Button btnOpenHelp = findViewById(R.id.btnOpenHelp);
        btnOpenHelp.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        Button btnOpenOptions = findViewById(R.id.btnOpenOptions);
        btnOpenOptions.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
            startActivity(intent);
        });

        Button btnOpenHighScores = findViewById(R.id.btnOpenHighScores);
        btnOpenHighScores.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, HighScoresActivity.class);
            startActivity(intent);
        });

        Button btnOpenPhotoGallery = findViewById(R.id.btnOpenPhotoGallery);
        btnOpenPhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, PhotoGalleryActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume() {
        setUpImages();
        super.onResume();

    }
}
