package codman.seminho;

import android.Manifest;
import android.annotation.SuppressLint;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.graphics.Color;
import android.media.Ringtone;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.Query;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
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


import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.ProdId;
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
import codman.seminho.Adapters.AdapterCategories;
import codman.seminho.Calendar.OneDayDecorator;
import codman.seminho.Remind.AlarmReceiver;
import codman.seminho.Remind.AlarmUtil;
import codman.seminho.DataBase.DatabaseHelper;
import codman.seminho.Model.AlarmEvent;
import codman.seminho.Util.FirestoreHelper;


public class MainActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    final String TAG = "Seminho";
    final Calendar calendar = Calendar.getInstance();
    final CalendarDay curDate = new CalendarDay();
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd ");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH : mm ");
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int RINGTONES_REQUEST_CODE = 124;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;

    // variables Firebase
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private boolean sIn = false;
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


    /*
    google SignIn
     */
    private void signIn() {
        //  Log.d(TAG, "Sign In");
        Toast.makeText(this, "Wait", Toast.LENGTH_SHORT).show();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START auth_with_google]
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Log.d(TAG, "Sign Out complete");
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                        Log.d(TAG, "Sign OUT complete");
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // updateUI(null);
                    }
                });
    }


    private void readFromFireStore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        builder.setTitle(R.string.actionImportServerData)
                .setCancelable(true)
                .setPositiveButton(R.string.buttonDownload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this,"Fly to server " , Toast.LENGTH_SHORT).show();
                        // getFileFromUrl(et.getText().toString());
                        FirestoreHelper mFirestoreHelper = new FirestoreHelper(MainActivity.this, mAuth.getCurrentUser().getEmail());

                        mFirestoreHelper.readFromFirestore();
                        // ArrayList<AlarmEvent> list=dbHelper.getEvents();
                        //for(AlarmEvent ae:list)
                        {
                            // mFirestoreHelper.addToFirestore(ae);
                        }
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

    private void addToServer() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        builder.setTitle(R.string.actionExportToServer)
                .setCancelable(true)
                .setPositiveButton(R.string.buttonUpload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(MainActivity.this, "Fly to server ", Toast.LENGTH_SHORT).show();
                        // getFileFromUrl(et.getText().toString());
                        FirestoreHelper mFirestoreHelper = new FirestoreHelper(MainActivity.this, mAuth.getCurrentUser().getEmail());
                        ArrayList<AlarmEvent> list = dbHelper.getEvents();
                        for (AlarmEvent ae : list) {
                            mFirestoreHelper.addToFirestore(ae);
                        }
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


        //FirestoreHelper mFirestoreHelper= new FirestoreHelper(this,mAuth.getCurrentUser().getUid());


    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Log.d(TAG, "signInWithCredential:success"+user.getEmail());
                            Snackbar.make(findViewById(R.id.root), "Authentication success.", Snackbar.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this,"Login succes",Toast.LENGTH_SHORT).show();
                            //Intent intent= new Intent(MainActivity.this,MainActivity.class);
                            //intent.putExtra("user",user.getDisplayName());
                            //startActivity(intent);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.root), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //  hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
    ///


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeNumber = preferences.getInt(TAG + "theme", 0);
        ringtone = Uri.parse(preferences.getString(TAG + "ringtone", "content://settings/system/notification_sound"));
        advance = preferences.getLong(TAG + "advance", 0);
        // Log.d(TAG, "Main onCreate advance=" + advance);
        pathURL = preferences.getString(TAG + "pathURL", getResources().getString(R.string.pathURL));

        // Boolean notif = preferences.getBoolean("notification", false);
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
                sv.scrollTo(0, 0);
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
                                    }


                                });

                            }
                        },
                        0, 60000);


        //  restartNotify();

        FirebaseFirestore.setLoggingEnabled(true);
        //Google Sign_iN
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

    }


    private void restartNotify() {


        AlarmEvent ae = dbHelper.getNextEvent(advance);
        if (ae != null) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            AlarmUtil.setAlarm(this, alarmIntent, (int) ae.getId(), ringtone, ae.getStartTime(), advance);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //  FirebaseUser currentUser = mAuth.getCurrentUser();
        // Log.d(TAG,"onStart getCurrentUser : "+currentUser);
        //updateUI(currentUser);
    }

    private void createList() {
        final ArrayList<AlarmEvent> events = dbHelper.getFutureEventsForListView();
        final Calendar cal = Calendar.getInstance();

        AlarmEvent ae = new AlarmEvent();

        events.add(ae);
        ArrayAdapter<AlarmEvent> adapter = new ArrayAdapter<AlarmEvent>(this,
                R.layout.item_list, R.id.tvTitleLV, events) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitleLV);
                TextView tvData = (TextView) view.findViewById(R.id.tvDataLV);

                TextView tvTime = (TextView) view.findViewById(R.id.tvTimeLV);
                if ((events.size() - 1) > position) {

                    cal.setTimeInMillis(events.get(position).getStartTime());

                    String date = dateFormat.format(new Date(cal.getTimeInMillis()));
                    String time = timeFormat.format(new Date(cal.getTimeInMillis()));
                    tvData.setText(date);
                    tvTime.setText(time);
                    tvTitle.setText(events.get(position).getTitle());
                } else {

                    LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(  LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(lp);

                    tvTitle.setWidth(getWindowManager().getDefaultDisplay().getWidth());

                    tvTitle.setTextSize(20);
                    tvTitle.setText(dbHelper.getCountFutureEvents() + " " + getResources().getString(R.string.nextEvents));

                }
                return view;
            }
        };

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == events.size() - 1) {
                    Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                    intent.putExtra("ID", (int) events.get(position).getId());
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(MainActivity.this, PagesActivity.class);
                    intent.putExtra("ID", (int) events.get(position).getId());
                    intent.putExtra("MS", events.get(position).getStartTime());
                    startActivity(intent);
                }
            }
        });
        lv.setAdapter(adapter);
        getListViewSize(lv, adapter);
        if (start) {
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

    }


    private void showCurrentEvent() {

        Calendar cal = Calendar.getInstance();

        AlarmEvent ae = dbHelper.getFirstEvent(System.currentTimeMillis());
        int days;
        int hours;
        int minutes;
        // Log.d(TAG,"showCurrentEvents = "+ae);
        if (ae != null) {
            long t = ae.getStartTime();
            long res = t - System.currentTimeMillis();
            //Log.d(TAG, "Res :" + res);

            days = (int) (res / 86400000);
            hours = (int) ((res % 86400000) / 3600000);
            minutes = (int) ((res % 3600000) / 60000);

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

            if (days == 0 && hours == 0 && minutes == 0)
                tv1.setText(getResources().getString(R.string.now) + " " + ae.getTitle());
            else
                tv1.setText(time + "  " + getResources().getString(R.string.till) + " " + ae.getTitle());
        } else {
            ae = dbHelper.getFirstEvent(System.currentTimeMillis() - 900000);
            if (ae != null) {
                tv1.setText(getResources().getString(R.string.now) + " " + ae.getTitle());
            } else
                tv1.setText(getResources().getString(R.string.noNextEvent));
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        widget.removeDecorators();
        decorateCalendar(date);
        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
        if (dbHelper.getCountEvents(cal) > 0) {
            Intent intent = new Intent(MainActivity.this, EventsActivity.class);
            intent.putExtra("day", date.getDay());
            intent.putExtra("month", date.getMonth());
            intent.putExtra("year", date.getYear());
            startActivity(intent);
        } else
            Toast.makeText(this, getResources().getString(R.string.noEvents), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        decorateCalendar(null);
    }


    public void decorateCalendar(CalendarDay date) {


        calendarView.removeDecorators();
        ArrayList<OneDayDecorator> decors = new ArrayList<>();
        for (int i = 0, count = 0; i < 31; i++) {
            //CalendarDay day = new CalendarDay(2018, date.getMonth(), i + 1);
            CalendarDay day = new CalendarDay(2018, calendarView.getCurrentDate().getMonth(), i + 1);
            // if(date!=null&&date.getDay()!=(i+1))
            decors.add(new OneDayDecorator(day, 0, 0, 0, 0));
        }
        calendarView.addDecorators(decors);
        Calendar curDay = Calendar.getInstance();
        for (int i = 0, count = 0; i < 31; i++) {

            CalendarDay day = new CalendarDay(2018, calendarView.getCurrentDate().getMonth(), i + 1);
            count = dbHelper.getCountEvents(day.getCalendar());

            int color = 1;


            curDay.set(curDay.get(Calendar.YEAR), curDay.get(Calendar.MONTH), curDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            long curTime = curDay.getTimeInMillis() - 1000;//curDay.getTimeInMillis();
            long curEventTime = day.getCalendar().getTimeInMillis();

            if (curEventTime < curTime)
                color = getResources().getColor(R.color.colorLastDate);
            else
                color = getResources().getColor(R.color.colorNextDate);
            if (themeNumber == 1 && ((date != null && date.getDay() != (i + 1)) || date == null))
                decors.add(new OneDayDecorator(day, count, color, getResources().getColor(R.color.colorPrimary_blue), getResources().getColor(R.color.colorContent_blue)));
            else if (date == null || (date != null && (date.getDay() != (i + 1))))
                decors.add(new OneDayDecorator(day, count, color, getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorContent)));
        }
        calendarView.addDecorators(decors);

    }


    @Override
    public void onResume() {

        super.onResume();
        decorateCalendar(null);
        createList();
        showCurrentEvent();
        calendarView.clearSelection();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.getItem(7).getSubMenu().getItem(0);
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
                    //Log.d(TAG, "SEND SelectDate" + calendarView.getSelectedDate());
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
            case R.id.actionAuth: {
                signIn();
                return true;
            }

            case R.id.actionLogOut: {
                signOut();
                return true;
            }

            case R.id.actionExportToServer: {

                if (mAuth.getCurrentUser() != null)
                    addToServer();
                else
                    Toast.makeText(this, getResources().getString(R.string.pleaseLogin), Toast.LENGTH_SHORT).show();

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
                    //Toast.makeText(this, "Input URL :" + this.getApplicationInfo().dataDir, Toast.LENGTH_SHORT).show();
                } else {

                    requestPermissionWithRationale();
                }

                return true;
            case R.id.actionImportServerData:
                if (hasPermissions()) {
                    //getFileFromUrl();


                    if (mAuth.getCurrentUser() != null) {
                        readFromFireStore();

                        decorateCalendar(null);
                    } else
                        Toast.makeText(this, getResources().getString(R.string.pleaseLogin), Toast.LENGTH_SHORT).show();

                    //Toast.makeText(this, "GetData Server :", Toast.LENGTH_SHORT).show();
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
                // Toast.makeText(this, "Advanced", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.actionEditCategories:
                editCategories();
                // Toast.makeText(this, "EditCategories", Toast.LENGTH_SHORT).show();
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


    private void editCategories() {

        View view = getLayoutInflater().inflate(R.layout.edit_categories, null);
        RecyclerView rv = view.findViewById(R.id.rvCategories);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);


        // String[] categories= getResources().getStringArray(R.array.categories);
        final ArrayList<String> cats = dbHelper.getCategories();
        if (cats.size() == 0) {
            String[] categories = getResources().getStringArray(R.array.categories);
            for (String c : categories) {
                dbHelper.putCategory(c);
                cats.add(c);
            }

        }

        // Log.d(TAG, "Count =" + cats.size());

        final AdapterCategories adapter = new AdapterCategories(this, getLayoutInflater(), cats);
        rv.setAdapter(adapter);
        //AlertDialog.Builder builder= new AlertDialog.Builder(this);
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.categories))
                .setView(view)
                .setCancelable(true)
                .setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.buttonAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.newCategory), Toast.LENGTH_SHORT).show();
                        final View view = getLayoutInflater().inflate(R.layout.dialog_category, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setCancelable(true).setView(view).setTitle(R.string.newCategory);
                        builder.setPositiveButton(R.string.addNewCategory, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                EditText et = view.findViewById(R.id.etCategory);
                                long res = dbHelper.putCategory(et.getText().toString());
                                cats.add(0, et.getText().toString());
                                //cats.remove(position+1);
                                adapter.notifyDataSetChanged();
                                //  Toast.makeText(MainActivity.this,"Save res="+res,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).setNeutralButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.buttonCancel), Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                        dialog.cancel();
                        builder.create().show();
                    }
                });
        adb.create().show();

    }

    private void actionImportURL() {
        final View viewDialog = getLayoutInflater().inflate(R.layout.dialog_download, null);
        final EditText et = viewDialog.findViewById(R.id.etPathURL);
        et.setHint("INPUT URL");
        et.setText("");
        //et.setText(pathURL);
        // AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
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
        seekBar.setProgress((int) MainActivity.this.advance / 60000);
        final TextView tvAdvance = view.findViewById(R.id.tvAdvance);
        // final Button btnOk = view.findViewById(R.id.btnOk);
        // final Button btnCancel = view.findViewById(R.id.btnCancel);

        builder.setTitle(getResources().getString(R.string.pleaseSelectAdvance));
        builder.setView(view);
        builder.setCancelable(true).setNegativeButton(R.string.buttonCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton(R.string.buttonOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MainActivity.this.advance = (seekBar.getProgress() * 60000);

                Toast.makeText(MainActivity.this, getResources().getString(R.string.advanceResult)
                        + " " + advance, Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(TAG + "advance", MainActivity.this.advance);
                editor.commit();

                //Set value in menuItem
                toolbar.getMenu().
                        getItem(7).
                        getSubMenu().
                        getItem(0).
                        setTitle(getResources().getString(R.string.actionAdvance) + " : " + (MainActivity.this.advance / 60000) + " min");

                dialog.dismiss();
                restartNotify();
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

    private void selectSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
      if(ringtone==null)
       ringtone = RingtoneManager.getActualDefaultRingtoneUri(
                getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
        startActivityForResult(intent, RINGTONES_REQUEST_CODE);
    }


    class ImportTask extends AsyncTask<String, Void, Void> {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("IMporting ...");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog
                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            parseCalendar(params[0], progressDialog);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            decorateCalendar(null);
            createList();
        }
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

        // final String p="";
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                dialog.dismiss();

                ImportTask iT = new ImportTask();
                iT.execute(files);

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
                    // Log.d(TAG, "GET PATH ===" + file.getAbsolutePath());
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
                    // Log.d(TAG, "EXCEPTION EXPORT :" + ex.getMessage() + "||||||" + ex.getLocalizedMessage());
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
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }


        if (requestCode == PERMISSION_REQUEST_CODE) {
            // makeFolder();
            return;
        }
        if (requestCode == RINGTONES_REQUEST_CODE) {

            if (data != null) {
                this.ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                if (ringtone != null) {
                    editor.putString(TAG + "ringtone", ringtone.toString());
                    editor.commit();

                    Ringtone r = RingtoneManager.getRingtone(this, ringtone);


                    Toast.makeText(this, getResources().getString(R.string.installed) + " : " + r.getTitle(this), Toast.LENGTH_SHORT).show();
                } else {
                    // Log.d(TAG,"NONE");
                    editor.putString(TAG + "ringtone", "none");
                    editor.commit();

                }
                restartNotify();
            }
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
                startDate.setTimeInMillis(events.get(i).getStartTime());
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

//                VAlarm vAlarm = new VAlarm(new Dur(-1000 * 60 * 60));
//                vAlarm.getProperties().add(new Description(events.get(i).getCategory()));
//                vAlarm.getProperties().add(Action.DISPLAY);
//                vAlarm.getProperties().add(new Summary(events.get(i).getAlarmName()));
//
//                // vAlarm.getDescription().setValue(events.get(i).getAlarmName());
//                vEvent.getAlarms().add(vAlarm);


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


    private void parseCalendar(String path, ProgressDialog pDialog) {


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
                // Log.d(TAG,"----------------DeltaTZ :"+deltaTZ);//.getProperty(Property.TZOFFSETFROM).getValue());
            } catch (Exception e) {
                //  e.printStackTrace();
            }


            ComponentList listEvent = calendar.getComponents(Component.VEVENT);
            //int delta=100/listEvent.size();
            //int step=0;
            for (int i = 0; i < listEvent.size(); i++) {//Object elem : listEvent) {
                if (i < pDialog.getMax())
                    pDialog.setProgress(i);
                AlarmEvent ae = new AlarmEvent();

                VEvent vEvent = (VEvent) listEvent.get(i);
                if (vEvent.getLastModified() != null) {
                    ae.setLastModified(vEvent.getLastModified().getDate().getTime());
                    // Log.d(TAG,"Last Modified = "+vEvent.getLastModified().getDate());
                } else {
                    // Log.d(TAG,"Not Last Modified = ");
                }

                if (vEvent.getSummary() != null) {
                    ae.setTitle(vEvent.getSummary().getValue());
                }
                if (vEvent.getDescription() != null)
                    ae.setContent(vEvent.getDescription().getValue());
                if (vEvent.getStartDate() != null)
                    ae.setStartTime(vEvent.getStartDate().getDate().getTime() - deltaTZ);
                if (vEvent.getEndDate() != null)
                    ae.setFinishTime(vEvent.getEndDate().getDate().getTime() - deltaTZ);
                if (vEvent.getUid() != null)
                    ae.setUID(vEvent.getUid().getValue());

                int res = dbHelper.replaceAlarmEvent(ae);
                // Log.d(TAG, "------Import:   " +ae + "\tRES=" + res);
                if (res > 0) {
                    //Log.d(TAG,"parse OK");
                }

                //  Thread.sleep(2000);
            }
            // decorateCalendar();
        } catch (IOException e) {
            //Log.d(TAG, "IOEXception " + e.getMessage());
            // e.printStackTrace();
        } catch (ParserException e) {
            // Log.d(TAG, "ParseEXception " + e.getMessage());
            // e.printStackTrace();
        } catch (Exception e) {
            // Log.d(TAG, "AllCreateCalendarEXception " + e.getMessage() + " |" + e.getLocalizedMessage());
            // e.printStackTrace();

        }

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
                        // Log.d(TAG, "ERRRRRRRRRRRRRRRor");
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
                        // Log.d(TAG, "Download READ :" + downloadedSize);
                    }

                    fos.close();
                    inputStream.close();


                    parseCalendar(file.getAbsolutePath(), progressDialog);
                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                    Log.d(TAG, "Not URL");
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                    Log.d(TAG, "Not URL Ioexception");
                    progressDialog.dismiss();
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

                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }

                progressDialog.hide();
                file.delete();
                decorateCalendar(null);
                createList();
            }
        }.execute(url);
    }
}


