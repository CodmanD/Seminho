package kodman.seminho;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//Activity for SplashScreen
public class SplashActivity extends AppCompatActivity {
    final String TAG = "Seminho";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = preferences.getInt(TAG + "theme", 0);
        if (theme == 1) super.setTheme(R.style.AppThemeBlue);
        //Log.d(TAG, "N =" + theme);
        //   if(theme==0)
        setContentView(R.layout.activity_splash);
//else
//        {
//            super.setTheme(R.style.AppThemeBlue);
//            setContentView(R.layout.activity_splash_blue);
//        }
        super.onCreate(savedInstanceState);
        // deferred action
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}
