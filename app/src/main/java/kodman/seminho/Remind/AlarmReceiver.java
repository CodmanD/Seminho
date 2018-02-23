package kodman.seminho.Remind;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.URISyntaxException;

import kodman.seminho.Model.AlarmEvent;
import kodman.seminho.DataBase.DatabaseHelper;


public class AlarmReceiver extends BroadcastReceiver {
    final String TAG = "Seminho";

    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper database = DatabaseHelper.getInstance(context);
        int id = intent.getIntExtra("NOTIFICATION_ID", 0);
        long advance=intent.getLongExtra("advance",0);
        Uri ringtone= intent.getParcelableExtra("ringtone");
       // Log.d(TAG, "OnReceive BroadCast ID=" + id+" Ringtone "+ringtone);
        AlarmEvent ae = database.select(id);
        try
        {
            NotificationUtil.createNotification(context, ae,ringtone);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
        //ae = database.getFirstEvent();
        ae = database.getNextEvent(advance);
        if (ae != null)
            AlarmUtil.setAlarm(context, intent, (int) ae.getId(),ringtone, ae.getTimeAlarm(),advance);
        database.close();

    }
}