package codman.seminho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.util.UidGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import codman.seminho.Remind.AlarmReceiver;
import codman.seminho.Remind.AlarmUtil;
import codman.seminho.Model.AlarmEvent;
import codman.seminho.DataBase.DatabaseHelper;

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
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    String user="dd";


    private void addToFirestore()
    {
        Log.d(TAG,"TOOOOOOOOOOOO Firestore");
        Map<String,Object> event = new HashMap<>();
        event.put("owner", user);
        event.put("title", currentAE.getTitle());
        event.put("category",currentAE.getCategory());
        event.put("description",currentAE.getContent());
        //event.put("uid",currentAE.getUID());
        event.put("lastModified",currentAE.getLastModified());
        event.put("startTime", currentAE.getStartTime());
        event.put("finishTime", currentAE.getFinishTime());

        mFirestore.collection("events").document(currentAE.getUID())
                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void updateFirestore()
    {

// Remove the 'capital' field from the document




                Log.d(TAG,"Update Firestore");
        Map<String,Object> event = new HashMap<>();
        event.put("owner", user);
        event.put("title", currentAE.getTitle());
        event.put("category",currentAE.getCategory());
        event.put("description",currentAE.getContent());
        //event.put("uid",currentAE.getUID());
        event.put("lastModified",currentAE.getLastModified());
        event.put("startTime", currentAE.getStartTime());
        event.put("finishTime", currentAE.getFinishTime());

        mFirestore.collection("events").document(currentAE.getUID()).update(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully update!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    private void deleteFromFirestore()
    {
        Log.d(TAG,"Delete event from Firestore");

        mFirestore.collection("events").document(currentAE.getUID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(PagesActivity.this,getResources().getString(R.string.deleteFromFirestore),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


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

        ArrayList<String> cats= DatabaseHelper.getInstance(this).getCategories();
        if(cats.size()==0)
        {
            categories = getResources().getStringArray(R.array.categories);
        }
        else
        {
            categories= new String[cats.size()];
            for(int i=0;i<cats.size();i++)
                categories[i]=cats.get(i);
        }

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

            calendar.setTimeInMillis(currentAE.getStartTime());
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
                    //EditText etAlarmName = pager.findViewById(R.id.etAlarmName);
                    EditText etDay = pager.findViewById(R.id.etDay);
                    EditText etMonth = pager.findViewById(R.id.etMonth);
                    EditText etYear = pager.findViewById(R.id.etYear);
                    EditText etStartHours = pager.findViewById(R.id.etStartHours);
                    EditText etStartMin = pager.findViewById(R.id.etStartMin);
                    EditText etFinishHours = pager.findViewById(R.id.etFinishHours);
                    EditText etFinishMin = pager.findViewById(R.id.etFinishMin);
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
                                Integer.parseInt(etStartHours.getText().toString()),
                                Integer.parseInt(etStartMin.getText().toString()));
                        currentAE.setStartTime(calendar.getTimeInMillis());
                    } catch (Exception ex) {
                        //Log.d(TAG, "ADD Exception curTime-----------Update Event");
                        Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        calendar.set(Integer.parseInt(etYear.getText().toString()),
                                Integer.parseInt(etMonth.getText().toString()) - 1
                                , Integer.parseInt(etDay.getText().toString()),
                                Integer.parseInt(etFinishHours.getText().toString()),
                                Integer.parseInt(etFinishMin.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        //Toast.makeText(this, R.string.ErrorAdding, Toast.LENGTH_SHORT).show();
                        currentAE.setFinishTime(currentAE.getStartTime()+900000);
                    }

                    if(currentAE.getStartTime()>currentAE.getFinishTime())
                        {
                            Log.d(TAG,"default endTime");
                            currentAE.setFinishTime(currentAE.getStartTime()+900000);
                        }




                    currentAE.setTitle(etTitle.getText().toString());
                    currentAE.setContent(etContent.getText().toString());
                    //currentAE.setAlarmName(etAlarmName.getText().toString());
                    currentAE.setCategory(categories[spCategory.getSelectedItemPosition()]);

                    Log.d(TAG,"Category = "+currentAE.getCategory());


                    currentAE.setLastModified(System.currentTimeMillis());

                    if (this.STATUS == STATUS_ADD) {

                        if (DatabaseHelper.getInstance(this).addEventToDB(currentAE) >= 0) {
                            // Log.d(TAG, "ADD to Base----------ADD Event");
                           // addToFirestore();
                            Toast.makeText(this, getResources().getString(R.string.add) + " " + currentAE.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        if (DatabaseHelper.getInstance(this).updateEventToDB(currentAE)) {
                          //  updateFirestore();
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

                   // deleteFromFirestore();
                    int pos = pager.getCurrentItem();

                    calendar.setTimeInMillis(currentAE.getStartTime());
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

                    Toast.makeText(this, getResources().getString(R.string.removeEvent), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, getResources().getString(R.string.removeEventError), Toast.LENGTH_SHORT).show();

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
            AlarmUtil.setAlarm(this, alarmIntent, (int) ae.getId(), ringtone, ae.getStartTime(), advance);
        }

    }
}
