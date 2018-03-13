package codman.seminho.Model;

import java.util.Calendar;

public class AlarmEvent {

    final static String TAG = "Seminho";

    long id = -1;
    String title;
    String content;
    String category;
    String uID ;

    long startTime;
    long finishTime;
    long lastModified;

//    String alarmName;



    public AlarmEvent() {
        this.title = "";
        this.content = "";
        this.startTime = System.currentTimeMillis();
        this.lastModified=System.currentTimeMillis();
        this.finishTime=System.currentTimeMillis();
        this.category = "event";
      //  this.alarmName = "";
        this.uID="";
    }
/*
    public AlarmEvent(String title, String content, String category, String alarmName, long startTime, String uID) {
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.category = category;
        this.alarmName = alarmName;
        this.uID = uID;
    }
*/
    public AlarmEvent(String title, String content, String category, long startTime,long finishTime, String uID) {
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.category = category;
       // this.alarmName = alarmName;

        this.uID = uID;
    }



    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
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

    //public void setAlarmName(String alarmName) {
    //    this.alarmName = alarmName;
   // }


    public void setStartTime(long startTime) {
        this.startTime =startTime;
    }
    public void setLastModified(long  lastModified) {
        this.lastModified= lastModified;
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

    //public String getAlarmName() {
    //    return this.alarmName;
    //}


    public long getStartTime() {
        return this.startTime;
    }
    public long getLastModified() {
        return this.lastModified;
    }


    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.startTime);

        return this.id + " | " + this.title + " | " + this.category + " | " +  "|" + this.startTime + " | " +
                cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " | " + this.uID;
    }


}
