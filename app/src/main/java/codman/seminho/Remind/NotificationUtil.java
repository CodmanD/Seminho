package codman.seminho.Remind;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import codman.seminho.NotificationActivity;
import codman.seminho.R;


import java.net.URISyntaxException;

import codman.seminho.Model.AlarmEvent;
import codman.seminho.MainActivity;

public class NotificationUtil extends ContextWrapper {

    final static String TAG = "Seminho";

    private static NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "kodman.seminho.ANDROID";
    public static final String IOS_CHANNEL_ID = "kodman.seminho.IOS";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    public static final String IOS_CHANNEL_NAME = "IOS CHANNEL";

    private static Context context;

    private static Uri ringtone;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationUtil(Context base) {
        super(base);
        createChannels();
       // Log.d(TAG, "Create Notif CHANNEL");
    }

    private static NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public static void createNotification(Context context, AlarmEvent ae,Uri ringtone) throws URISyntaxException {
        NotificationUtil.context = context;
        NotificationUtil.ringtone=ringtone;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);
        }


       // Log.d(TAG, "createNotificattion :" + ringtone);
        // Create intent for notification onClick behaviour
       // Intent viewIntent = new Intent(context, MainActivity.class);
        Intent viewIntent = new Intent(context, NotificationActivity.class);
        viewIntent.putExtra("EVENT_UID", ae.getUID());
        viewIntent.putExtra("NOTIFICATION_ID", ae.getId());
       // viewIntent.putExtra("EVENT_MS", ae.getStartTime());
       // Log.d(TAG,"put Extra id="+ae.getId());
        viewIntent.putExtra("NOTIFICATION_DISMISS", true);
        // PendingIntent pending = PendingIntent.getActivity(context, (int)ae.getId(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       // PendingIntent pending = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pending = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Create intent for notification snooze click behaviour
        //Intent intent = new Intent(context, MainActivity.class);
       // Intent intent = new Intent(context, NotificationActivity.class);
        //SharedPreferences sPref= PreferenceManager.getDefaultSharedPreferences(context);
        //sPref.edit().putBoolean("notification",true);
        //sPref.edit().commit();
        //  snoozeIntent.putExtra("NOTIFICATION_ID", reminder.getId());
        //PendingIntent pendingSnooze = PendingIntent.getBroadcast(context, (int) ae.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // int imageResId = context.getResources().getIdentifier(reminder.getIcon(), "drawable", context.getPackageName());

        // String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher))
                //.setColor(Color.parseColor(reminder.getColour()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(ae.getContent()))
                .setChannelId(ANDROID_CHANNEL_ID)
                .setGroup("SeminhoNotification")
                .setContentTitle(ae.getTitle())
                .setContentText(ae.getContent())
                .setTicker(ae.getTitle())
                .setContentIntent(pending);


        //PendingIntent pendingDismiss = PendingIntent.getBroadcast(context, reminder.getId(), swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setDeleteIntent(pendingDismiss);

/*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getBoolean("checkBoxNagging", false)) {
            Intent swipeIntent = new Intent(context, DismissReceiver.class);
            swipeIntent.putExtra("NOTIFICATION_ID", reminder.getId());
            PendingIntent pendingDismiss = PendingIntent.getBroadcast(context, reminder.getId(), swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDeleteIntent(pendingDismiss);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, sharedPreferences.getInt("nagMinutes", context.getResources().getInteger(R.integer.default_nag_minutes)));
            calendar.add(Calendar.SECOND, sharedPreferences.getInt("nagSeconds", context.getResources().getInteger(R.integer.default_nag_seconds)));
            Intent alarmIntent = new Intent(context, NagReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, reminder.getId(), calendar);
        }
*/
/*
        String soundUri = sharedPreferences.getString("NotificationSound", "content://settings/system/notification_sound");
        if (soundUri.length() != 0) {
            builder.setSound(Uri.parse(soundUri));
        }
        if (sharedPreferences.getBoolean("checkBoxLED", true)) {
            builder.setLights(Color.BLUE, 700, 1500);
        }
        if (sharedPreferences.getBoolean("checkBoxOngoing", false)) {
            builder.setOngoing(true);
        }
        if (sharedPreferences.getBoolean("checkBoxVibrate", true)) {
            long[] pattern = {0, 300, 0};
            builder.setVibrate(pattern);
        }
        if (sharedPreferences.getBoolean("checkBoxMarkAsDone", false)) {
            Intent intent = new Intent(context, DismissReceiver.class);
            intent.putExtra("NOTIFICATION_ID", reminder.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.mark_as_done), pendingIntent);
        }
        if (sharedPreferences.getBoolean("checkBoxSnooze", false)) {
            builder.addAction(R.drawable.ic_snooze_white_24dp, context.getString(R.string.snooze), pendingSnooze);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
*/
        // mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            //builder.setSound(Uri.parse("content://settings/system/notification_sound"));
           if(!ringtone.toString().equals("none"))
           {
               builder.setSound(ringtone);
               //Log.d(TAG, "----------------------- MUSIC "+builder.toString()+"Ringtone = "+ringtone);
           }
           else
               builder.setSound(null);
           //Log.d(TAG, "-----------------------NONE MUSIC");

        } catch (Exception e) {
            //Log.d(TAG, "-----------------------EXCEPTION MUSIC");
           // e.printStackTrace();
        }

        getManager().notify((int) ae.getId(), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        // nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        getManager().cancel(notificationId);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        //Log.d(TAG, " createChannels");

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);

        // create ios channel
        NotificationChannel iosChannel = new NotificationChannel(IOS_CHANNEL_ID,
                IOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        iosChannel.enableLights(true);
        iosChannel.enableVibration(true);
        iosChannel.setLightColor(Color.GRAY);
        iosChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(iosChannel);
    }
}