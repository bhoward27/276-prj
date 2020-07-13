package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ca.cmpt276.prj.R;

public class OptionsActivity extends AppCompatActivity {
    private static final String PICTURE_PREF_NAME = "Picture types";
    private static final String PREFS_NAME = "AppPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        getSupportActionBar().setTitle(getString(R.string.title_options_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createRadioButton();

        String savedValue = getTypePictureInstalled(this);
        Toast.makeText(this,"Saved value: " + savedValue, Toast.LENGTH_SHORT).show();
    }


    private void createRadioButton(){
        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);

        String[] strPic = getResources().getStringArray(R.array.str_pic_types);

        //Create the radio buttons:
        for(int i = 0; i < strPic.length; i++){
            final String strType = strPic[i];

            RadioButton button = new RadioButton(this);
            button.setText(strType);

            //Set on-click callbacks
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(OptionsActivity.this, "You clicked " + strType, Toast.LENGTH_SHORT).show();

                    saveStrTypeInstalled(strType);
                }
            });

            //Add to radio group:
            group.addView(button);

            //Select default button:
            if(strType.equals(getTypePictureInstalled(this))){
                button.setChecked(true);
            }

        }
    }

    private void saveStrTypeInstalled(String strType) {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();//Write the value
        editor.putString(PICTURE_PREF_NAME, strType);
        editor.apply();
    }

    static public String getTypePictureInstalled(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String defaultValue = context.getResources().getString(R.string.default_picture_types);
        //TODO: change default value.
        return prefs.getString(PICTURE_PREF_NAME, defaultValue);
    }
}
