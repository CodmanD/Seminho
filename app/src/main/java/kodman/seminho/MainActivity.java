package kodman.seminho;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;


import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.seminho.Calendar.OneDayDecorator;
import kodman.seminho.Remind.AlarmReceiver;
import kodman.seminho.Remind.AlarmUtil;
import kodman.seminho.DataBase.DatabaseHelper;
import kodman.seminho.Model.AlarmEvent;


public class MainActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    final String TAG = "Seminho";
    final Calendar calendar = Calendar.getInstance();
    final CalendarDay curDate = new CalendarDay();
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int RINGTONES_REQUEST_CODE = 124;
    private FilePickerDialog dialog;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.calendarView)
    com.prolificinteractive.materialcalendarview.MaterialCalendarView calendarView;
    @BindView(R.id.textView1)
    TextView tv1;

    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.sV)
    ScrollView sv;

    Uri ringtone;
    long advance;
    String pathURL;
    boolean start = true;

    int themeNumber = 0;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeNumber = preferences.getInt(TAG + "theme", 0);
        ringtone = Uri.parse(preferences.getString(TAG + "ringtone", "content://settings/system/notification_sound"));
        advance = preferences.getLong(TAG + "advance", 0);
        pathURL = preferences.getString(TAG + "pathURL", getResources().getString(R.string.pathURL));
        // Log.d(TAG,"Theme = "+themeNumber+" ringtone"+ringtone+" Advance = "+advance);
        if (themeNumber == 1) {
            setTheme(R.style.AppThemeBlue);
        }

        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        dbHelper = DatabaseHelper.getInstance(this);

        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        String title = FORMATTER.format(new Date(System.currentTimeMillis()));
        getSupportActionBar().setTitle(title);


        lv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.d(TAG, "LayoutChangeLisyener");

                // if(start)
                {
                    Log.d(TAG, "ScrollTo");
                    sv.scrollTo(0, 0);
                    start = false;
                }
            }
        });

        new java.util.Timer().schedule
                (
                        new TimerTask() {
                            public void run() {
                                if (toolbar == null) return;
                                tv1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  Log.d(TAG,"Timer Tick");
                                        showCurrentEvent();
//                                        if(start)
//                                        {
//                                            Log.d(TAG,"ScrollTo");
//                                            sv.scrollTo(0,0);
//                                            start=false;
//                                        }

                                    }


                                });

                            }
                        },
                        0, 60000);


        restartNotify();
    }


    private void restartNotify() {

        AlarmEvent ae = dbHelper.getNextEvent(advance);
        if (ae != null) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            AlarmUtil.setAlarm(this, alarmIntent, (int) ae.getId(), ringtone, ae.getTimeAlarm(), advance);
        }

    }


    private void createList() {
        final ArrayList<AlarmEvent> events = dbHelper.getEventForMain();
        String[] arr = new String[events.size()];
       final Calendar cal = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            AlarmEvent ae = events.get(i);
            cal.setTimeInMillis(ae.getTimeAlarm());
            arr[i] = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR) +
                    " | " + ae.getTitle() + " | " + cal.get(Calendar.HOUR) + " : " + cal.get(Calendar.MINUTE) +
                    " " + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        }
       /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arr) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
*/
        ArrayAdapter<AlarmEvent> adapter = new ArrayAdapter<AlarmEvent>(this,
                R.layout.item_list,R.id.tvTitleLV, events) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView tvData = (TextView) view.findViewById(R.id.tvDataLV);
                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitleLV);
                TextView tvTime = (TextView) view.findViewById(R.id.tvTimeLV);
                cal.setTimeInMillis(events.get(position).getTimeAlarm());
                tvData.setText( cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
                tvTitle.setText( events.get(position).getTitle());
                tvTime.setText( cal.get(Calendar.HOUR) + " : " + cal.get(Calendar.MINUTE) +
                        " " + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
              //  textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PagesActivity.class);
                //intent.putExtra("ID", Integer.parseInt(id.getText().toString()));
                Log.d(TAG, "===============AE = " + events.get(position).getId() + " | " + events.get(position).getTitle());
                intent.putExtra("ID", (int) events.get(position).getId());
                Log.d(TAG, "GET ID=" + intent.getIntExtra("ID", -1));
                intent.putExtra("MS", events.get(position).getTimeAlarm());
                startActivity(intent);
            }
        });
        lv.setAdapter(adapter);
        getListViewSize(lv, adapter);
        if (start) {
            Log.d(TAG, "ScrollTo");
            sv.scrollTo(0, 0);
            start = false;
        }
    }

    public static void getListViewSize(ListView myListView, ListAdapter adapter) {
        //ListAdapter myListAdapter = myListView.getAdapter();
        if (adapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;

        for (int size = 0; size < adapter.getCount(); size++) {
            View listItem = adapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (adapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.d("height of listItem:", String.valueOf(totalHeight));

    }



/*
    private void showCurrentEvent() {

        Calendar cal = Calendar.getInstance();

        AlarmEvent[] aes = dbHelper.getCurrentEvents();

        if (aes != null) {

            if(aes[0].getTimeAlarm()<cal.getTimeInMillis())
            {
                tv1.setText("  now " + aes[0].getTitle());
            }
            else
            {
                long t = aes[0].getTimeAlarm();
                long res = t - System.currentTimeMillis();

                int days = (int) res / 86400000;
                int hours = (int) ((res % 86400000) / 3600000);
                int minutes = (int) ((res % 3600000) / 60000);

            // Log.d(TAG, "TTIME :" + days + "/" + hours + "/" + minutes);

            Resources r = getResources();
            String d = "";
            if (days == 1)
                d = r.getString(R.string.day);
            if (days > 1)
                d = r.getString(R.string.days);
            String h = "";
            if (hours == 1)
                h = r.getString(R.string.hour);
            if (hours > 1)
                h = r.getString(R.string.hours);
            String m = getString(R.string.zeroMinutes);
            if (minutes == 1)
                m = r.getString(R.string.minute);
            if (minutes > 1)
                m = r.getString(R.string.minutes);

            String time = (days > 0 ? days + " " + d + " " : d) +
                    (hours > 0 ? hours + " " + h + " " : h) +
                    (minutes > 0 ? minutes + " " + m + " " : m);

            tv1.setText(time + "  till " + aes[0].getTitle());
            }
        }else
            tv1.setText("no next event");
    }
    */

    private void showCurrentEvent() {

        Calendar cal = Calendar.getInstance();

        AlarmEvent ae = dbHelper.getFirstEvent(System.currentTimeMillis());

        // Log.d(TAG,"showCurrentEvents = "+ae);
        if (ae != null) {
            long t = ae.getTimeAlarm();
            long res = t - System.currentTimeMillis();
            //Log.d(TAG, "Res :" + res);

            int days = (int) (res / 86400000);
            int hours = (int) ((res % 86400000) / 3600000);
            int minutes = (int) ((res % 3600000) / 60000);

            //  Log.d(TAG, "TTIME :" + days + "/" + hours + "/" + minutes);

            Resources r = getResources();
            String d = "";
            if (days == 1)
                d = r.getString(R.string.day);
            if (days > 1)
                d = r.getString(R.string.days);
            String h = "";
            if (hours == 1)
                h = r.getString(R.string.hour);
            if (hours > 1)
                h = r.getString(R.string.hours);
            String m = getString(R.string.zeroMinutes);
            if (minutes == 1)
                m = r.getString(R.string.minute);
            if (minutes > 1)
                m = r.getString(R.string.minutes);

            String time = (days > 0 ? days + " " + d + " " : d) +
                    (hours > 0 ? hours + " " + h + " " : h) +
                    (minutes > 0 ? minutes + " " + m + " " : m);

            tv1.setText(time + "  till " + ae.getTitle());
        } else {
            ae = dbHelper.getFirstEvent(System.currentTimeMillis() - 900000);
            if (ae != null) {
                tv1.setText(" now " + ae.getTitle());
            } else
                tv1.setText("no next event");
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        //tvDate.setText(getSelectedDatesString());
        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
        if (dbHelper.getItemsEvents(cal) > 0) {
            Intent intent = new Intent(MainActivity.this, EventsActivity.class);
            intent.putExtra("day", date.getDay());
            intent.putExtra("month", date.getMonth());
            intent.putExtra("year", date.getYear());
            startActivity(intent);
        } else
            Toast.makeText(this, "Not Events", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        decorateCalendar();
    }


    private void decorateCalendar() {

        calendarView.removeDecorators();
        ArrayList<OneDayDecorator> decors = new ArrayList<>();
        for (int i = 0, count = 0; i < 31; i++) {
            //CalendarDay day = new CalendarDay(2018, date.getMonth(), i + 1);
            CalendarDay day = new CalendarDay(2018, calendarView.getCurrentDate().getMonth(), i + 1);
            decors.add(new OneDayDecorator(day, 0, 0));
        }
        calendarView.addDecorators(decors);
        Calendar curDay = Calendar.getInstance();
        for (int i = 0, count = 0; i < 31; i++) {

            CalendarDay day = new CalendarDay(2018, calendarView.getCurrentDate().getMonth(), i + 1);
            count = dbHelper.getItemsEvents(day.getCalendar());

            int color = 1;


            curDay.set(curDay.get(Calendar.YEAR), curDay.get(Calendar.MONTH), curDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            long curTime = curDay.getTimeInMillis() - 1000;//curDay.getTimeInMillis();
            long curEventTime = day.getCalendar().getTimeInMillis();

            if (curEventTime < curTime)
                color = getResources().getColor(R.color.colorLastDate);
            else
                color = getResources().getColor(R.color.colorNextDate);
            decors.add(new OneDayDecorator(day, count, color));
        }
        calendarView.addDecorators(decors);

    }


    @Override
    public void onResume() {

        super.onResume();
        decorateCalendar();
        createList();
        showCurrentEvent();
        calendarView.clearSelection();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.getItem(4).getSubMenu().getItem(0);
        item.setTitle(getResources().getString(R.string.actionAdvance) + " : " + (advance / 60000) + " min");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionNewEvent: {

                //Toast.makeText(this, "NEW EVENT", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, PagesActivity.class);
                if (calendarView.getSelectedDate() != null) {
                    Log.d(TAG, "SEND SelectDate" + calendarView.getSelectedDate());
                    intent.putExtra("selectedDate", calendarView.getSelectedDate());
                }

                startActivity(intent);
                return true;
            }
            case R.id.actionChangeTheme: {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(TAG + "theme", themeNumber == 0 ? 1 : 0);
                editor.commit();
                this.finish();
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);

                return true;
            }
            case R.id.actionImport:
                if (hasPermissions()) {
                    pickFile();
                    //Toast.makeText(this,"Input URL",Toast.LENGTH_SHORT).show();
                } else {

                    requestPermissionWithRationale();
                }

                return true;
            case R.id.actionImportURL:
                if (hasPermissions()) {
                    //getFileFromUrl();

                    actionImportURL();
                    Toast.makeText(this, "Input URL :" + this.getApplicationInfo().dataDir, Toast.LENGTH_SHORT).show();
                } else {

                    requestPermissionWithRationale();
                }

                return true;
            case R.id.actionExport:
                if (hasPermissions()) {

                    pickFolder();

                } else {

                    requestPermissionWithRationale();
                }


                return true;
            case R.id.actionSettings:
                // Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.actionSelectSound:
                selectSound();
                // Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.actionAdvance:
                setAdvance();
                Toast.makeText(this, "Advanced", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.actionAbout:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Seminho " + getResources().getString(R.string.aboutVersion))
                        .setMessage(R.string.aboutCompany)
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionImportURL() {
        final View viewDialog = getLayoutInflater().inflate(R.layout.dialog_download, null);
        final EditText et = viewDialog.findViewById(R.id.etPathURL);
        et.setText(pathURL);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle(R.string.actionImportFromUrl)
                .setCancelable(true)
                .setView(viewDialog)
                .setPositiveButton(R.string.buttonDownload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.buttonDownload)
                                + ":" + et.getText(), Toast.LENGTH_SHORT).show();
                        // getFileFromUrl(et.getText().toString());
                        downloadFile(et.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
        builder.create().show();

    }

    private void setAdvance() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog_advance, null);
        final SeekBar seekBar = view.findViewById(R.id.seekBar);
        final TextView tvAdvance = view.findViewById(R.id.tvAdvance);
        final Button btnOk = view.findViewById(R.id.btnOk);
        final Button btnCancel = view.findViewById(R.id.btnCancel);

        builder.setTitle("Please Select Advance ");
        builder.setView(view);
        builder.setCancelable(true);

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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.advanceResult)
                        + " " + tvAdvance.getText(), Toast.LENGTH_SHORT).show();
                MainActivity.this.advance = Long.parseLong(tvAdvance.getText().toString().substring(0, 1)) * 60000;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(TAG + "advance", MainActivity.this.advance);
                editor.commit();

                //Set value in menuItem
                toolbar.getMenu().
                        getItem(4).
                        getSubMenu().
                        getItem(0).
                        setTitle(getResources().getString(R.string.actionAdvance) + " : " + (MainActivity.this.advance / 60000) + " min");

                popDialog.dismiss();
                restartNotify();
            }
        });
        popDialog.show();
    }

    private void selectSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        Uri rington = RingtoneManager.getActualDefaultRingtoneUri(
                getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, rington);
        startActivityForResult(intent, RINGTONES_REQUEST_CODE);
    }


    //
    private void pickFile() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"ics"};

        dialog = new FilePickerDialog(MainActivity.this, properties);


        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                dialog.dismiss();
                parseCalendar(files[0]);
               // Toast.makeText(MainActivity.this, "Import file :" + files[0], Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }


    private void pickFolder() {

        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(MainActivity.this, properties);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                try {
                    File file = new File(files[0] + "/seminho.ics");
                    Log.d(TAG, "GET PATH ===" + file.getAbsolutePath());
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    FileOutputStream fout = new FileOutputStream(file);
                    CalendarOutputter out = new CalendarOutputter();
                    net.fortuna.ical4j.model.Calendar iCal = createICal();
                    out.output(iCal, fout);
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.exportCompleted) + " /Seminho.ics", Toast.LENGTH_SHORT).show();

                } catch (Exception ex) {
                    Log.d(TAG, "EXCEPTION EXPORT :" + ex.getMessage() + "||||||" + ex.getLocalizedMessage());
                    Toast.makeText(MainActivity.this, "Export Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }


    ////////////////Permissions
    @SuppressLint("WrongConstant")
    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            //  makeFolder();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }


    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(MainActivity.this.findViewById(R.id.root), "Storage permission isn't granted", Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();

    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // makeFolder();
            return;
        }
        if (requestCode == RINGTONES_REQUEST_CODE) {

            this.ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(TAG + "ringtone", ringtone.toString());
            editor.commit();


            Toast.makeText(this, ringtone.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "--------------------RINGTONES :" + ringtone.getAuthority());

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";

            Snackbar.make(MainActivity.this.findViewById(R.id.root), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();

        } else {
            requestPerms();
        }
    }
///////////End Permissiom

    ///////CREATED CALENDAR Ical4j
    private net.fortuna.ical4j.model.Calendar createICal() {

        //net.fortuna.ical4j.parsing.relaxed=true;
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        java.util.TimeZone tzDefault = java.util.TimeZone.getDefault();
        TimeZone timezone = registry.getTimeZone(tzDefault.getID());

        VTimeZone tz = timezone.getVTimeZone();
        //Log.d(TAG, "TIMEZONE ============" + tzDefault.getID());

        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();

        icsCalendar.getComponents().add(tz);
        icsCalendar.getProperties().add(new ProdId("-//Seminho"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        // Log.d(TAG, "Create Empty ICaL ============" + tzDefault.getID());
        ArrayList<AlarmEvent> events = dbHelper.getEvents();

        //  Log.d(TAG, "Create  ICaL  GET EVENTS============" + events.size());

        for (int i = 0; i < events.size(); i++) {

            //    Log.d(TAG, "WRITE TO CALENDAR :" + events.get(i));

            try {
                java.util.Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(events.get(i).getTimeAlarm());
                startDate.setTimeZone(timezone);
                startDate.set(java.util.Calendar.MONTH, startDate.get(java.util.Calendar.MONTH));
                startDate.set(java.util.Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
                startDate.set(java.util.Calendar.YEAR, startDate.get(java.util.Calendar.YEAR));
                startDate.set(java.util.Calendar.HOUR_OF_DAY, startDate.get(Calendar.HOUR_OF_DAY));
                startDate.set(java.util.Calendar.MINUTE, startDate.get(Calendar.MINUTE));
                startDate.set(java.util.Calendar.SECOND, startDate.get(Calendar.SECOND));
                //    Log.d(TAG, "Create new VEVENT get Calendar ============" + tzDefault.getID());


                String eventName = events.get(i).getTitle();
                DateTime start = new DateTime(startDate.getTime());
                //  DateTime end = new DateTime(endDate.getTime());
                //String[] ps = new String[]{events.get(i).getTitle(), events.get(i).getTitle()};

                VEvent vEvent = new VEvent(start, new Dur(0, 0, 15, 0), eventName);


                //setLastModified
                //DateTime lastModified = new DateTime(events.get(i).getLastModified());
                //vEvent.getProperties(Property.LAST_MODIFIED).add(lastModified);
                vEvent.getProperties()
                        .add(new LastModified(new DateTime(events.get(i).getLastModified())));

                vEvent.getProperties().add(new Uid(events.get(i).getUID()));
                //      Log.d(TAG, "Create new UID  generate ============" );

                //Add VAlarm

                VAlarm vAlarm = new VAlarm(new Dur(-1000 * 60 * 60));
                vAlarm.getProperties().add(new Description(events.get(i).getCategory()));
                vAlarm.getProperties().add(Action.DISPLAY);
                vAlarm.getProperties().add(new Summary(events.get(i).getAlarmName()));

                // vAlarm.getDescription().setValue(events.get(i).getAlarmName());
                vEvent.getAlarms().add(vAlarm);


                vEvent.getProperties().add(tz.getTimeZoneId());
                // Description description=new Description();
                vEvent.getProperties().add(new Description(events.get(i).getContent()));

                icsCalendar.getComponents().add(vEvent);
            } catch (Exception e) {
                 Log.d(TAG, "Exception CREATE VEVENT  i=" + i + " Message = " + e.getMessage() + " Excep = " + e.getClass());
            }
        }

        //Log.d(TAG, "ICalendar =========" + icsCalendar);
        return icsCalendar;
    }


    private void parseCalendar(String path) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing ...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog
                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        Log.d(TAG,"SHOW PROGRESS");

        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(path);
        } catch (IOException ioe) {
            //Log.d(TAG, "EXCEPTION InputStream");
            return;
        }


        CalendarBuilder builder = new CalendarBuilder();
        try {
            java.util.TimeZone tzDefault = java.util.TimeZone.getDefault();
            long deltaTZ = 0;
            net.fortuna.ical4j.model.Calendar calendar = builder.build(fin);

            try {
                Component c = calendar.getComponent(Component.VTIMEZONE);

                PropertyList pList = c.getProperties();
                // ComponentList cList.getComponents();
                TimeZoneRegistry registry = builder.getRegistry();
                TimeZone tZvEvent = registry.getTimeZone(((Property) pList.get(0)).getValue());
                //TimeZone tZone= registry.getTimeZone();
                deltaTZ = tzDefault.getRawOffset() - tZvEvent.getRawOffset();
                // Log.d(TAG,"----------------TZDefaault :"+tzDefault.getRawOffset());//.getProperty(Property.TZOFFSETFROM).getValue());
            } catch (Exception e) {
                //  e.printStackTrace();
            }


            ComponentList listEvent = calendar.getComponents(Component.VEVENT);
            for (Object elem : listEvent) {
                progressDialog.setProgress(10);
                AlarmEvent ae = new AlarmEvent();

                VEvent vEvent = (VEvent) elem;
                try {
                    ae.setLastModified(vEvent.getLastModified().getDate().getTime());
                }catch(Exception e)
                {
                    e.printStackTrace();
                }

                try {
                    ae.setTitle(vEvent.getSummary().getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    ae.setContent(vEvent.getDescription().getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // Log.d(TAG,"Tinezone = "+vEvent.getStartDate().getTimeZone().);
                    ae.setTimeAlarm(vEvent.getStartDate().getDate().getTime() - deltaTZ);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ae.setUID(vEvent.getUid().getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                   // continue;
                }
                int res = dbHelper.replaceAlarmEvent(ae);
                Log.d(TAG, "------Import:   " +ae + "RES=" + res);
                if (res > 0) {
                    //Log.d(TAG,"parse OK");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, R.string.importCompleted, Toast.LENGTH_SHORT).show();
                        //showEvent(new CalendarDay(calendarView.getCurrentDate().getCalendar()));
                        decorateCalendar();
                        createList();
                    }
                });
                Thread.sleep(2000);
            }
            // decorateCalendar();
        } catch (IOException e) {
            Log.d(TAG, "IOEXception " + e.getMessage());
            // e.printStackTrace();
        } catch (ParserException e) {
            Log.d(TAG, "ParseEXception " + e.getMessage());
            // e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "AllCreateCalendarEXception " + e.getMessage() + " |" + e.getLocalizedMessage());
            // e.printStackTrace();
        }
        progressDialog.dismiss();
        //dbHelper.lookDB();
        Toast.makeText(MainActivity.this, "Import file :" + path, Toast.LENGTH_SHORT).show();
    }

    private void downloadFile(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Downloading ...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;

                File file = null;
                FileOutputStream fos = null;

                try {
                    //Log.d(TAG,"Download "+params[0]);
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    //Log.d(TAG,"Download connect");
                    //file = File.createTempFile("Mustachify", "download");
                    file = new File(MainActivity.this.getApplicationInfo().dataDir + "/test.ics");
                    //Log.d(TAG,"---------------Download New FILE");
                    // File.createTempFile("Mustachify", "download");
                    if (!file.createNewFile()) {
                        //Toast.makeText(this,"Error Create file",Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        Log.d(TAG, "ERRRRRRRRRRRRRRRor");
                        return null;
                    }
                    //Log.d(TAG,"Download FILE create OK");
                    fos = new FileOutputStream(file);
                    inputStream = urlConnection.getInputStream();

                    totalSize = urlConnection.getContentLength();
                    downloadedSize = 0;

                    buffer = new byte[1024];
                    bufferLength = 0;

                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                        Log.d(TAG, "Download READ :" + downloadedSize);
                    }

                    fos.close();
                    inputStream.close();


                    parseCalendar(file.getAbsolutePath());
                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }

                return null;
            }

            // обновляем progressDialog
            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            ;

            @Override
            protected void onPostExecute(File file) {
                // отображаем сообщение, если возникла ошибка
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                // закрываем прогресс и удаляем временный файл
                progressDialog.hide();
                file.delete();
            }
        }.execute(url);
    }
}


