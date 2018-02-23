package kodman.seminho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.util.UidGenerator;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.seminho.Remind.AlarmReceiver;
import kodman.seminho.Remind.AlarmUtil;
import kodman.seminho.Model.AlarmEvent;
import kodman.seminho.DataBase.DatabaseHelper;

public class PagesActivity extends AppCompatActivity {

    final String TAG = "Seminho";

    final short STATUS_ADD = 1;
    final short STATUS_EDIT = 2;
    short STATUS = 0;
    // static final int PAGE_COUNT = 10;

    private DatabaseHelper dbHelper;


    ArrayList<AlarmEvent> events;// = new ArrayList<>(10);
    @BindView(R.id.viewPager)
    ViewPager pager;
    PagerAdapter pagerAdapter;

    AlarmEvent currentAE;
    Uri ringtone;
    long advance;

    String[] categories;//= {"Event", "Date", "Business", "Private"};
    @BindView(R.id.toolbarEvent)
    Toolbar toolbar;
    int themeNumber = 0;
    @BindView(R.id.etTitle)
    EditText etTitle;

    Calendar calendar;
    CalendarDay selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        themeNumber = preferences.getInt(TAG + "theme", 0);
        ringtone = Uri.parse(preferences.getString(TAG + "ringtone", "content://settings/system/notification_sound"));
        advance = preferences.getLong(TAG + "advance", 0);
       //Log.d(TAG, "----------PagesActivity ringtone " + ringtone + " Advance = " + advance);
        if (themeNumber == 1) {
            setTheme(R.style.AppThemeBlue);
            // Log.d(TAG,"ChangeTheme");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        ButterKnife.bind(this);

        categories = getResources().getStringArray(R.array.categories);

        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", -1);
        //Log.d(TAG, "Pages ID=" + id);
        long date = (long) intent.getIntExtra("MS", -1);

        dbHelper = DatabaseHelper.getInstance(this);
        calendar = Calendar.getInstance();

        if (id > 0) {
            this.STATUS = STATUS_EDIT;

            currentAE = dbHelper.select(id);
            etTitle.setText(currentAE.getTitle());
            // Log.d(TAG, "GetCurrentAE FROM DB");

            calendar.setTimeInMillis(currentAE.getTimeAlarm());
            events = dbHelper.getEvents(calendar);

        } else {
            this.STATUS = STATUS_ADD;
            events = new ArrayList<>();
            AlarmEvent ae = new AlarmEvent();
            events.add(ae);

            selectedDate = intent.getParcelableExtra("selectedDate");

        }


        setSupportActionBar(toolbar);


        FragmentManager fm = getSupportFragmentManager();
        pagerAdapter = new MyFragmentPagerAdapter(fm);

        pager.setAdapter(pagerAdapter);
        int pos = getCurrentPosition(id);
        pager.setCurrentItem(pos);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (currentAE != null && events.size() - 1 >= position) {
                    currentAE=events.get(position);
                    etTitle.setText(currentAE.getTitle());
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        //  Log.d(TAG, "Create PageActivity  curPosition" + pager.getCurrentItem());
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAdd: {
                try {
                    EditText etContent = pager.findViewById(R.id.etContent);
                    EditText etAlarmName = pager.findViewById(R.id.etAlarmName);
                    EditText etDay = pager.findViewById(R.id.etDay);
                    EditText etMonth = pager.findViewById(R.id.etMonth);
                    EditText etYear = pager.findViewById(R.id.etYear);
                    EditText etHours = pager.findViewById(R.id.etHours);
                    EditText etMin = pager.findViewById(R.id.etMin);
                    Spinner spCategory = pager.findViewById(R.id.spinnerCategories);

                    Calendar calendar = Calendar.getInstance();
                    if (currentAE == null) {
                        currentAE = new AlarmEvent();
                        try {
                            UidGenerator ug = new UidGenerator(null, "uidGen");
                            String uid = ug.generateUid().getValue();
                            currentAE.setUID(uid);
                            //Log.d(TAG, "ADD UG=" + currentAE.getUID());
                            //   Log.d(TAG, "ADD curAE=" + Integer.parseInt(currentAE.getYear()) + "/" + Integer.parseInt(currentAE.getMonth())
                            //          + "/" + Integer.parseInt(currentAE.getDay()));

                        } catch (Exception ex) {
                            //Log.d(TAG, "ADD Exception curTime------------New Event :" + ex.getMessage());
                            Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    try {
                        calendar.set(Integer.parseInt(etYear.getText().toString()),
                                Integer.parseInt(etMonth.getText().toString()) - 1
                                , Integer.parseInt(etDay.getText().toString()),
                                Integer.parseInt(etHours.getText().toString()),
                                Integer.parseInt(etMin.getText().toString()));

                    } catch (Exception ex) {
                        //Log.d(TAG, "ADD Exception curTime-----------Update Event");
                        Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    currentAE.setTitle(etTitle.getText().toString());
                    currentAE.setContent(etContent.getText().toString());
                    currentAE.setAlarmName(etAlarmName.getText().toString());
                    currentAE.setCategory(categories[spCategory.getSelectedItemPosition()]);
                    currentAE.setTimeAlarm(calendar.getTimeInMillis());
                    currentAE.setLastModified(System.currentTimeMillis());

                    if (this.STATUS == STATUS_ADD) {

                        if (DatabaseHelper.getInstance(this).addEventToDB(currentAE) >= 0) {
                            // Log.d(TAG, "ADD to Base----------ADD Event");
                            Toast.makeText(this, getResources().getString(R.string.add) + " " + currentAE.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        if (DatabaseHelper.getInstance(this).updateEventToDB(currentAE)) {
                            Toast.makeText(this, getResources().getString(R.string.update) + " " + currentAE.getTitle(), Toast.LENGTH_SHORT).show();
                            // Log.d(TAG, "UPADTE to Base-----------" + currentAE);
                        }

                    }

                } catch (Exception exc) {
                    Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                }
                restartNotify();
            }

            // DatabaseHelper.getInstance(this).lookDB();
            break;
            case R.id.tvPrev:
                if (this.STATUS == STATUS_EDIT) {

                    int prevPos = pager.getCurrentItem() - 1;
                    // Log.d(TAG, "PrevPos=" + prevPos + "  cuRAE" + currentAE);

                    AlarmEvent ae = getPrev(currentAE.getId());
                    if (ae != null)
                        currentAE = ae;
                    //Log.d(TAG, "  cuRAE" + currentAE);
                    pager.setCurrentItem(prevPos);
                    pagerAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.tvNext:
                if (this.STATUS == STATUS_EDIT) {
                    int nextPos = pager.getCurrentItem() + 1;
                    //Log.d(TAG, "NextPos=" + nextPos + "  cuRAE" + currentAE);

                    AlarmEvent ae = getNext(currentAE.getId());
                    if (ae != null)
                        currentAE = ae;

                    //  Log.d(TAG, "CurAE =" + currentAE);
                    pager.setCurrentItem(nextPos);
                } else {
                    currentAE = new AlarmEvent();
                    try {
                        UidGenerator ug = new UidGenerator(null, "uidGen");
                        String uid = ug.generateUid().getValue();
                        currentAE.setUID(uid);
                        //Log.d(TAG, "ADD UG=" + currentAE.getUID());
                        //   Log.d(TAG, "ADD curAE=" + Integer.parseInt(currentAE.getYear()) + "/" + Integer.parseInt(currentAE.getMonth())
                        //          + "/" + Integer.parseInt(currentAE.getDay()));

                    } catch (Exception ex) {
                        //Log.d(TAG, "ADD Exception curTime------------New Event :" + ex.getMessage());
                        Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    events.add(currentAE);
                    etTitle.setText(currentAE.getTitle());
                }

                pagerAdapter.notifyDataSetChanged();

                break;
            case R.id.tvDel:
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
                if (currentAE != null && dbHelper.removeEventFromDB(currentAE.getId())) {
                    int pos = pager.getCurrentItem();

                    calendar.setTimeInMillis(currentAE.getTimeAlarm());
                    events = dbHelper.getEvents(calendar);

                    ((MyFragmentPagerAdapter) pagerAdapter).notifyChangeInPosition(pos);
                    pagerAdapter.notifyDataSetChanged();
                    if (pos >= events.size()) {
                        pos = events.size() - 1;

                        try {
                            currentAE = events.get(pos);
                            etTitle.setText(currentAE.getTitle());

                        } catch (Exception ex) {
                            etTitle.setText("");
                            //Log.d(TAG, "EXCEPTION pos=" + pos + "||count=" + events.size());
                        }
                    } else {
                        currentAE = events.get(pos);
                        etTitle.setText(currentAE.getTitle());

                    }

                    pager.setCurrentItem(pos);

                    Toast.makeText(this, "Remove Event ", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Remove Error", Toast.LENGTH_SHORT).show();

                dbHelper.close();
                break;
        }

    }


    private AlarmEvent getNext(long id) {
        for (int i = 0; i < events.size() - 1; i++) {
            if (id == events.get(i).getId()) {
                return events.get(i + 1);
            }
        }
        return null;
    }

    private AlarmEvent getPrev(long id) {
        for (int i = 1; i < events.size(); i++) {
            if (id == events.get(i).getId()) {
                return events.get(i - 1);
            }
        }
        return null;
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        private long baseId = 0;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = PageFragment.newInstance(position, currentAE != null ? events.get(position).getId() : -1);
            Bundle b = new Bundle();
            b.putParcelable("sd", selectedDate);
            f.getArguments().putParcelable("sd", selectedDate);

            return f;
        }

        @Override
        public int getCount() {
            return events.size();
        }


        @Override
        public int getItemPosition(Object object) {

            return PagerAdapter.POSITION_NONE;
        }


        @Override
        public long getItemId(int position) {

            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {

            baseId += getCount() + n;
            //Log.d(TAG, "baseId = " + baseId + "  n = " + n + "   getCount" + getCount());

        }
    }


    private int getCurrentPosition(long id) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId() == id) {
                // Log.d(TAG, "------------------set position" + i);
                return i;
            }
        }
        return -1;
    }


    private int getCategoryPosition(String category) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category))
                return i;
        }
        return -1;
    }


    private void restartNotify() {

        //AlarmEvent ae = DatabaseHelper.getInstance(this).getFirstEvent();
        AlarmEvent ae = dbHelper.getNextEvent(advance);
        if (ae != null) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.setAlarm(this, alarmIntent, (int) ae.getId(), ringtone, ae.getTimeAlarm(), advance);
        }

    }
}
