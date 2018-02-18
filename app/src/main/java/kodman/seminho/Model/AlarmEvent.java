package kodman.seminho.Model;

import java.util.Calendar;

public class AlarmEvent {

    final static String TAG = "Seminho";
    String title;
    String content;
    long timeAlarm;
    String category;
    String alarmName;
    long id = -1;
    String uID ;

    public AlarmEvent() {
        this.title = "";
        this.content = "";
        this.timeAlarm = System.currentTimeMillis();
        this.category = "event";
        this.alarmName = "";
        this.uID="";
    }

    public AlarmEvent(String title, String content, String category, String alarmName, long timeAlarm, String uID) {
        this.title = title;
        this.content = content;
        this.timeAlarm = timeAlarm;
        this.category = category;
        this.alarmName = alarmName;
        this.uID = uID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }


    public void setTimeAlarm(long timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public long getId() {
        return this.id;
    }

    public String getUID() {
        return this.uID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return this.content;
    }

    public String getCategory() {
        return this.category;
    }

    public String getAlarmName() {
        return this.alarmName;
    }


    public long getTimeAlarm() {
        return this.timeAlarm;
    }


    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.timeAlarm);

        return this.id + " | " + this.title + " | " + this.category + " | " + this.alarmName + "|" + this.timeAlarm + " | " +
                cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " | " + this.uID;
    }


}
