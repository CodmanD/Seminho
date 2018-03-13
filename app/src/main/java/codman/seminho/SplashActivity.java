package codman.seminho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//Activity for SplashScreen
public class SplashActivity extends AppCompatActivity  implements View.OnClickListener{
    final String TAG = "Seminho";

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = preferences.getInt(TAG + "theme", 0);
        if (theme == 1) super.setTheme(R.style.AppThemeBlue);
        //Log.d(TAG, "N =" + theme);
        //   if(theme==0)
        setContentView(R.layout.activity_splash);

        ImageView ivSignIn=this.findViewById(R.id.ivSignIn);
        ivSignIn.setOnClickListener(this);
//else
//        {
//            super.setTheme(R.style.AppThemeBlue);
//            setContentView(R.layout.activity_splash_blue);
//        }
        super.onCreate(savedInstanceState);
        // deferred action
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
        */
    }

    public void onClick()
    {

    }
}
