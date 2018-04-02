package codman.seminho.Remind;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.URISyntaxException;

import codman.seminho.Model.AlarmEvent;
import codman.seminho.DataBase.DatabaseHelper;


public class AlarmReceiver extends BroadcastReceiver {
    final String TAG = "Seminho";

    @Override
    public void onReceive(Context context, Intent intent) {



        int id = intent.getIntExtra("NOTIFICATION_ID", -1);


        long advance=intent.getLongExtra("advance",0);
        long delay=intent.getLongExtra("delay",0);
        boolean isDelay=intent.getBooleanExtra("isDelay",false);
        Uri ringtone= intent.getParcelableExtra("ringtone");
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        AlarmEvent ae = database.select(id);

        Log.d(TAG,"onReceive"+"  "+ae.getTitle()+" |ref Id= "+id);
        if(isDelay)
        {
           // NotificationUtil.createNotification(context, ae,ringtone);
        }



       // Log.d(TAG, "OnReceive BroadCast ID=" + id+" Ringtone "+ringtone);

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
            AlarmUtil.setAlarm(context, intent, (int) ae.getId(),ringtone, ae.getStartTime(),advance);
        database.close();

    }
}