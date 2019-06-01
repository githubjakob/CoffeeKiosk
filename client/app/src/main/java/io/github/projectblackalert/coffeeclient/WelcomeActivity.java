package io.github.projectblackalert.coffeeclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();

        Button continueToMainActivity = findViewById(R.id.continueToMainActivity);
        continueToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstStartup", false);
                editor.commit();

                finish();
            }
        });
    }
}
