package kodman.seminho.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import kodman.seminho.Model.AlarmEvent;


import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = "Seminho";

    private static DatabaseHelper instance;
    private Context context;

    //  private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "RECURRENCE_DB";

    private static final String ALARMEVENT_TABLE = "NOTIFICATIONS";
    private static final String COL_ID = "ID";
    private static final String COL_UID = "UID";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_CONTENT = "CONTENT";
    private static final String COL_CATEGORY = "CATEGORY";
    private static final String COL_ALARMNAME = "ALARMNAME";
    private static final String COL_DAY = "DAY";
    private static final String COL_MONTH = "MONTH";
    private static final String COL_YEAR = "YEAR";
    private static final String COL_DATE_AND_TIME = "DATE_AND_TIME";
    private static final String COL_LAST_MODIFIED = "LAST_MODIFIED";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor is private to prevent direct instantiation
     * Call made to static method getInstance() instead
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + ALARMEVENT_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_UID + " TEXT, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_ALARMNAME + " TEXT, "
                + COL_DAY + " TEXT, "
                + COL_MONTH + " TEXT, "
                + COL_YEAR + " TEXT, "
                + COL_LAST_MODIFIED + " INTEGER, "
                + COL_DATE_AND_TIME + " INTEGER, CONSTRAINT unique_UID UNIQUE (" + COL_UID + "))"
        );
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }


    public Cursor getCursor() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE;
        return database.rawQuery(query, null);
    }

    public long addEventToDB(AlarmEvent ae) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_UID, ae.getUID());
        values.put(COL_TITLE, ae.getTitle());
        values.put(COL_CONTENT, ae.getContent());
        values.put(COL_CATEGORY, ae.getCategory());
        values.put(COL_ALARMNAME, ae.getAlarmName());
        values.put(COL_DATE_AND_TIME, ae.getTimeAlarm());
        values.put(COL_LAST_MODIFIED, ae.getLastModified());

        Log.d(TAG, " ADD to  DB=" + values);


        long res = database.insert(DatabaseHelper.ALARMEVENT_TABLE, null, values);

        database.close();
        Log.d(TAG, " ADD to  RES=" + res);
        return res;

    }

    public boolean removeEventFromDB(long id) {

        SQLiteDatabase database = this.getWritableDatabase();
        int res = database.delete(DatabaseHelper.ALARMEVENT_TABLE, COL_ID + "=" + id, null);
        database.close();
        return res >= 0 ? true : false;

    }


    public void dropTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DROP TABLE " + ALARMEVENT_TABLE;
        database.execSQL(query);
        database.close();
    }

    public void clearDB() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + ALARMEVENT_TABLE);
        database.close();
    }


    public void lookDB() {
        Log.d(TAG, " LOOK DataBase ");
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                AlarmEvent ae = new AlarmEvent();
                ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
                ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
                ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
                //ae.setTimeAlarm(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME))));
                ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                Log.d(TAG, "AE:" + ae);

            } while (cursor.moveToNext());
        cursor.close();
        database.close();
    }


    public ArrayList<AlarmEvent> getEvents() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<AlarmEvent> events = new ArrayList<>();
        if (cursor.moveToFirst())
            do {
                AlarmEvent ae = new AlarmEvent();
                ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
                ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
                ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
                ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                //Log.d(TAG,"GET EVENTS AE: "+ae.toString());
                events.add(ae);
            } while (cursor.moveToNext());

        cursor.close();
        database.close();
        // Log.d(TAG,"GET EVENTS eventsList: "+events);
        return events;
    }


    public int getItemsEvents(Calendar calendar) {
        calendar.setTimeZone(TimeZone.getDefault());
        CalendarDay date = new CalendarDay(calendar);
        Calendar dateStart = Calendar.getInstance();
        dateStart.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(date.getYear(), date.getMonth(), date.getDay() + 1, 0, 0, 0);

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " BETWEEN " + dateStart.getTimeInMillis() +
                " AND " + dateEnd.getTimeInMillis();
        Cursor cursor = database.rawQuery(query, null);
        return cursor.getCount();
    }

    public ArrayList<AlarmEvent> getEvents(Calendar calendar) {

        CalendarDay date = new CalendarDay(calendar);
        Calendar dateStart = Calendar.getInstance();
        dateStart.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(date.getYear(), date.getMonth(), date.getDay() + 1, 0, 0, 0);

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " BETWEEN " + dateStart.getTimeInMillis() +
                " AND " + dateEnd.getTimeInMillis();
        //Log.d(TAG,query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());
        ArrayList<AlarmEvent> events = new ArrayList<>();
        if (cursor.moveToFirst())
            do {
                AlarmEvent ae = new AlarmEvent();
                ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
                ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
                ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
                //Log.d(TAG,"GET EVENTS AE: "+ae.toString());
                events.add(ae);
            } while (cursor.moveToNext());
        cursor.close();
        database.close();

        //Log.d(TAG,"GET EVENTS eventsList: "+events);
        return events;
    }


    public int replaceAlarmEvent(AlarmEvent ae) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_UID + " = '" + ae.getUID() + "'";
        Cursor cursor = database.rawQuery(query, null);
        Log.d(TAG, "CURSOR = " + cursor);
        if (cursor.moveToFirst()) {
            values.put(COL_TITLE, ae.getTitle());
            values.put(COL_CONTENT, ae.getContent());
            values.put(COL_CATEGORY, ae.getCategory());
            values.put(COL_ALARMNAME, ae.getAlarmName());
            values.put(COL_DATE_AND_TIME, ae.getTimeAlarm());
            values.put(COL_LAST_MODIFIED, ae.getLastModified());



            Log.d(TAG,"InDB LastModified="+cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED))+" : "+ae.getLastModified());
            int updCount = database.update(ALARMEVENT_TABLE, values,
                    COL_UID + " = '" + ae.getUID() + "' AND "+COL_LAST_MODIFIED+" < "+ae.getLastModified(),
                    null);

            cursor.close();
            database.close();
            return updCount;

        } else {
            values.put(COL_TITLE, ae.getTitle());
            values.put(COL_UID, ae.getUID());
            values.put(COL_CONTENT, ae.getContent());
            values.put(COL_CATEGORY, ae.getCategory());
            values.put(COL_ALARMNAME, ae.getAlarmName());
            values.put(COL_DATE_AND_TIME, ae.getTimeAlarm());
            values.put(COL_LAST_MODIFIED, ae.getLastModified());

            long res = database.insert(DatabaseHelper.ALARMEVENT_TABLE, null, values);

            cursor.close();
            database.close();
            return (int) res;
        }

    }

    public long getTimeFirstEvent() {

        // Calendar date=Calendar.getInstance();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + COL_DATE_AND_TIME + " FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " >= " + System.currentTimeMillis() +
                " ORDER BY " + COL_DATE_AND_TIME + " LIMIT 1";

        // Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());

        if (cursor.moveToFirst())

        {

            long res = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME));
            cursor.close();
            database.close();

            return res;
            // Log.d(TAG, "GET EVENTS AE: " + ae.toString());
        }
        cursor.close();
        database.close();

        return -1;
    }


    public AlarmEvent[] getCurrentEvents() {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE +
                " WHERE " + COL_DATE_AND_TIME + " >= " + (System.currentTimeMillis()-900000) +
                " ORDER BY " + COL_DATE_AND_TIME + " LIMIT 2";

        // Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());
        AlarmEvent[] aes= new AlarmEvent[2];

        if (cursor.moveToFirst())
        {

            AlarmEvent ae = new AlarmEvent();
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
            ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            aes[0]=ae;
            if (cursor.moveToNext()) {
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            aes[1]=ae;
            }
            cursor.close();
            database.close();
            return aes;
            // Log.d(TAG, "GET EVENTS AE: " + ae.toString());
        }
        cursor.close();
        database.close();

        return null;
    }



    public AlarmEvent getFirstEvent(long time) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " >= " + time +
                " ORDER BY " + COL_DATE_AND_TIME + " LIMIT 1";

        // Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());

        if (cursor.moveToFirst()) {
            AlarmEvent ae = new AlarmEvent();
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
            cursor.close();
            database.close();

            return ae;
            // Log.d(TAG, "GET EVENTS AE: " + ae.toString());
        }
        cursor.close();
        database.close();
        return null;
    }

    public AlarmEvent getNextEvent(long advance) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " >= "
                + (System.currentTimeMillis()+advance) +
                " ORDER BY " + COL_DATE_AND_TIME + " LIMIT 1";

        // Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());

        if (cursor.moveToFirst()) {
            AlarmEvent ae = new AlarmEvent();
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
            cursor.close();
            database.close();

            return ae;
            // Log.d(TAG, "GET EVENTS AE: " + ae.toString());
        }
        cursor.close();
        database.close();
        return null;
    }

    public AlarmEvent getNext(long time) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " >= "
                + time +
                " ORDER BY " + COL_DATE_AND_TIME + " LIMIT 1";

         Cursor cursor = database.rawQuery(query, null);


        if (cursor.moveToFirst()) {
            AlarmEvent ae = new AlarmEvent();
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
            cursor.close();
            database.close();
            Log.d(TAG, "GET EVENTS AE: " + ae.toString());
            return ae;

        }
        cursor.close();
        database.close();
        return null;
    }

    public AlarmEvent getPrev(long time) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " <= "
                + time +
                " ORDER BY " + COL_DATE_AND_TIME + " DESC LIMIT 1";

        // Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());

        if (cursor.moveToFirst()) {
            AlarmEvent ae = new AlarmEvent();
            ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
            ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
            ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
            ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
            ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
            cursor.close();
            database.close();
            Log.d(TAG, "GET EVENTS AE: " + ae.toString());
            return ae;

        }
        cursor.close();
        database.close();
        return null;
    }



    public ArrayList<AlarmEvent> getEventForMain() {

 SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + ALARMEVENT_TABLE + " WHERE " + COL_DATE_AND_TIME + " >= " + System.currentTimeMillis() +
                " ORDER BY " + COL_DATE_AND_TIME;

        // Log.d(TAG,query);

        Cursor cursor = database.rawQuery(query, null);
        //Log.d(TAG,"GET FROM DB Cursor ="+cursor.getCount());
        ArrayList<AlarmEvent> events = new ArrayList<>();
        if (cursor.moveToFirst())
            do {
                AlarmEvent ae = new AlarmEvent();
                ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                ae.setAlarmName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARMNAME)));
                ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
                ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                // Log.d(TAG, "GET EVENTS AE: " + ae.toString());

                events.add(ae);
            } while (cursor.moveToNext());
        cursor.close();
        database.close();

        return events;
    }


    public AlarmEvent select(long id) {

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(ALARMEVENT_TABLE, null, COL_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        // Log.d(TAG,"Count"+cursor.getCount());
        cursor.moveToFirst();


        AlarmEvent ae = new AlarmEvent();
        ae.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        ae.setUID(cursor.getString(cursor.getColumnIndexOrThrow(COL_UID)));
        ae.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        ae.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
        ae.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
        ae.setLastModified(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_MODIFIED)));
        ae.setTimeAlarm(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
        // Log.d(TAG,"getAlarmEvent = "+ae);
        cursor.close();
        database.close();
        return ae;

    }


    public boolean updateEventToDB(AlarmEvent ae) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TITLE, ae.getTitle());
        values.put(COL_CONTENT, ae.getContent());
        values.put(COL_CATEGORY, ae.getCategory());
        values.put(COL_ALARMNAME, ae.getAlarmName());
        values.put(COL_LAST_MODIFIED, ae.getLastModified());
        values.put(COL_DATE_AND_TIME, ae.getTimeAlarm());
        values.put(COL_UID, ae.getUID());

        Log.d(TAG, "---------------UPDATE : " + ae.getLastModified());
        int updCount = database.update(ALARMEVENT_TABLE, values, "id = ?",
                new String[]{String.valueOf(ae.getId())});
        return true;
    }


}