package kodman.seminho.Remind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import kodman.seminho.Remind.AlarmReceiver;

import java.util.Calendar;

import kodman.seminho.Model.AlarmEvent;
import kodman.seminho.DataBase.DatabaseHelper;

public class AlarmUtil {

    final static String TAG = "Seminho";

   // public static void setAlarm(Context context, Intent intent, int notificationId, Calendar calendar) {
   public static void setAlarm(Context context, Intent intent, int notificationId,Uri ringtone, long ms,long advance) {
        intent.putExtra("NOTIFICATION_ID", notificationId);
       intent.putExtra("ringtone", ringtone);
       intent.putExtra("advance",advance);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ms-advance, pendingIntent);
           // Log.d(TAG,"setALARM  -seExactAndAllowWhileIdle");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ms-advance, pendingIntent);
            //Log.d(TAG,"setALARM  -seExact");
        } else {
           // alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, ms-advance, pendingIntent);
           // Log.d(TAG,"setALARM  -set");
        }
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

//    /*
//    public static void setNextAlarm(Context context, AlarmEvent ae, DatabaseHelper database) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(ae.getTimeAlarm());
//
//        Calendar now = Calendar.getInstance();
//        calendar.set(Calendar.SECOND, 0);
///*
//        switch (reminder.getRepeatType()) {
//            case Reminder.HOURLY:
//                while (calendar.before(now)) {
//                    calendar.add(Calendar.HOUR, reminder.getInterval());
//                }
//                break;
//            case Reminder.DAILY:
//                while (calendar.before(now)) {
//                    calendar.add(Calendar.DATE, reminder.getInterval());
//                }
//                break;
//            case Reminder.WEEKLY:
//                while (calendar.before(now)) {
//                    calendar.add(Calendar.WEEK_OF_YEAR, reminder.getInterval());
//                }
//                break;
//            case Reminder.MONTHLY:
//                while(calendar.before(now)) {
//                    calendar.add(Calendar.MONTH, reminder.getInterval());
//                }
//                break;
//            case Reminder.YEARLY:
//                while(calendar.before(now)) {
//                    calendar.add(Calendar.YEAR, reminder.getInterval());
//                }
//                break;
//            case Reminder.SPECIFIC_DAYS:
//                Calendar weekCalendar = (Calendar) calendar.clone();
//                weekCalendar.add(Calendar.DATE, 1);
//                for (int i = 0; i < 7; i++) {
//                    int position = (i + (weekCalendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7;
//                    if (reminder.getDaysOfWeek()[position]) {
//                        calendar.add(Calendar.DATE, i + 1);
//                        break;
//                    }
//                }
//                break;
//        }
//

   //     reminder.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(calendar));
  //      database.addNotification(reminder);

   //    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
    //    setAlarm(context, alarmIntent, (int)ae.getId(), calendar);
   // }

}