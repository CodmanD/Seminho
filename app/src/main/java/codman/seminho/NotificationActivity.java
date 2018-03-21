package codman.seminho;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static codman.seminho.Remind.NotificationUtil.ANDROID_CHANNEL_ID;

/**
 * Created by DI1 on 20.03.2018.
 */

public class NotificationActivity extends AppCompatActivity {
    private final String TAG = "Seminho";
    long advance=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int themeNumber = preferences.getInt(TAG + "theme", 0);
        // ringtone = Uri.parse(preferences.getString(TAG + "ringtone", "content://settings/system/notification_sound"));
        advance = preferences.getLong(TAG + "advance", 0);

        // pathURL = preferences.getString(TAG + "pathURL", getResources().getString(R.string.pathURL));

        // Boolean notif = preferences.getBoolean("notification", false);
        // Log.d(TAG,"Theme = "+themeNumber+" ringtone"+ringtone+" Advance = "+advance);
        if (themeNumber == 1) {
            setTheme(R.style.AppThemeBlue);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        showDialog();

    }

    public void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog_advance, null);

        final SeekBar seekBar = view.findViewById(R.id.seekBar);
       // Log.d(TAG,"View = "+view+"  |||  sekbar = "+seekBar);
        seekBar.setProgress((int) this.advance / 60000);
        final TextView tvAdvance = view.findViewById(R.id.tvAdvance);
        tvAdvance.setText(String.valueOf(seekBar.getProgress()) + " min");
        // final Button btnOk = view.findViewById(R.id.btnOk);
        // final Button btnCancel = view.findViewById(R.id.btnCancel);


        builder.setTitle("Please Select Advance ")
                .setView(view)
                .setCancelable(true).setNegativeButton(R.string.deleteNotification, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                NotificationManager nManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);


                Intent intent= getIntent();

               long id=intent.getLongExtra("NOTIFICATION_ID",-1);
                String uid=intent.getStringExtra("EVENT_UID");
                Toast.makeText(NotificationActivity.this,"Del notif id="+id+" | "+uid,Toast.LENGTH_SHORT).show();
               if(id>0)
                nManager.cancel((int)id);
               else
                   {
                       Toast.makeText(NotificationActivity.this," Not Del notif id="+id,Toast.LENGTH_SHORT).show();
                   }

            }
        }).setPositiveButton(R.string.saveAdvance,new  DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                advance = (seekBar.getProgress() * 60000);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                Log.d(TAG,"Notif = "+advance);
                editor.putLong(TAG + "advance", advance);
                editor.commit();

                Intent intent= new Intent(NotificationActivity.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(NotificationActivity.this,"Wait",Toast.LENGTH_SHORT).show();
               // dialog.cancel();
        }
        })
                .setNeutralButton(R.string.showEvent, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent= getIntent();

                        long id=intent.getLongExtra("NOTIFICATION_ID",-1);
                        long ms=intent.getLongExtra("EVENT_MS",-1);
                        String uid=intent.getStringExtra("EVENT_UID");

                        dialog.dismiss();
                        intent=new Intent(NotificationActivity.this,PagesActivity.class);
                        intent.putExtra("ID",(int)id);
                        //intent.putExtra("MS",id);
                        startActivity(intent);
                        // restartNotify();
                    }
                })
        ;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                tvAdvance.setText(String.valueOf(progress) + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//            }
        final AlertDialog popDialog = builder.create();


        popDialog.show();
    }
}
