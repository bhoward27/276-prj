package cmpt276.assignment.iteration1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Michael Mora's Main Activity solely intended for using buttons to go to certain activities he has created,
 * Any MainActivity file in the final product likely should not have said buttons.
 * @Author Michael Mora
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  setUpHelpButton();
        setUpMenuButton();
    }


    private void setUpMenuButton(){
        Button btnMainMenu = findViewById(R.id.menu);
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IntroScreenActivity.class);
                startActivity(intent);
            }
        });
    }
//    private void setUpHelpButton(){
//        Button btnHelp = findViewById(R.id.btnHighScores);
//        btnHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, HelpSreenActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void setUpHighScoresButton(){
//        Button btnHighScores = findViewById(R.id.btnHighScores);
//        btnHighScores.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, HelpSreenActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
}
