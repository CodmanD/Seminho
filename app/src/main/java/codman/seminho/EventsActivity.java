package codman.seminho;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import codman.seminho.Model.AlarmEvent;
import codman.seminho.DataBase.DatabaseHelper;


public class EventsActivity extends AppCompatActivity {

    final String TAG = "Seminho";
    @BindView(R.id.toolbarAE)
    Toolbar toolbar;
    Calendar calendar;
    RecyclerView rv;
    int themeNumber = 0;
    ArrayList<AlarmEvent> events;
    String head;
    int year,month,day;
    int id;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            themeNumber = preferences.getInt(TAG + "theme", 0);
            //   Log.d(TAG, "ChangeTheme =" + themeNumber);

            if (themeNumber == 1) {
                setTheme(R.style.AppThemeBlue);
            }
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_events);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EventsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            rv = (RecyclerView) findViewById(R.id.rv);

            rv.setLayoutManager(mLayoutManager);

            Intent intent = getIntent();
            Bundle data = intent.getExtras();
            id=data.getInt("ID",0);
           // Log.d(TAG,"EventsActivity ID="+id);

            calendar = Calendar.getInstance();
            if(id==0)
            {
                day = data.getInt("day");
                month = data.getInt("month");
                year = data.getInt("year");
                calendar.set(year, month, day);
                //head=day + "." + month + "." + year;
               // head=FORMATTER.format(new Date(calendar.getTimeInMillis()));
            }
           else
               {

                   //head=calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH)+1) + "." + calendar.get(Calendar.YEAR);
               }
            head=FORMATTER.format(new Date(calendar.getTimeInMillis()));
           // toolbar.setTitle("" + day + "." + month + "." + year);

           // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));



        } catch (Exception e) {
         // Log.d(TAG,"Exception "+e.getMessage());
        }

        // TextView tv=this.findViewById(R.id.tvDate);
       // tv.setText(day+"."+(month+1)+"."+year);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_events, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(EventsActivity.this, PagesActivity.class);

        CalendarDay date=new CalendarDay(year,month,day);

           // Log.d(TAG, "SEND SelectDate" +new CalendarDay());
            intent.putExtra("selectedDate", date);


        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
if(id==0)
{
    events =  DatabaseHelper.getInstance(this).getEvents(calendar);
   // Log.d(TAG,"EventsActivity ID=0 events="+events.size());
}
else
{

    events =  DatabaseHelper.getInstance(this).getFutureEvents();
   // Log.d(TAG,"EventsActivity events="+events.size()+" ID+"+id);
}

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events);
        rv.setAdapter(adapter);
        Log.d(TAG,"EventsActivity events="+events.size()+" ID+"+id+events.get(0).getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
       // toolbar.setTitle((Html.fromHtml("<font color=\"#999999\">" + "text" + "</font>")));
        toolbar.setTitle(head);
        if (events.size() <= 0) {
            Intent intent = new Intent(EventsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<AlarmEvent> events;

        public RecyclerViewAdapter(List<AlarmEvent> events) {
            this.events = events;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Log.d(TAG,"onCreateViewHolder");
            // View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false);

            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            AlarmEvent event = events.get(i);
             Log.d(TAG,"onBindViewHolder event = "+event.getTitle());
            viewHolder.title.setText(event.getTitle());
            //viewHolder.alarmName.setText(event.getAlarmName());
            viewHolder.category.setText(event.getCategory());


            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String date = format.format(new Date(event.getStartTime()));
            viewHolder.date.setText(date);
            viewHolder.id=(int)event.getId();

        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            //private TextView alarmName;
            private TextView category;
            private TextView date;
            //private TextView id;
            private int id;

            public ViewHolder(View itemView) {
                super(itemView);
                //this.id = (TextView) itemView.findViewById(R.id.tvItemID);
                this.title = (TextView) itemView.findViewById(R.id.tvItemTitle);
               // this.alarmName = (TextView) itemView.findViewById(R.id.tvItemAlarmName);
                this.category = (TextView) itemView.findViewById(R.id.tvItemCategory);
                this.date = (TextView) itemView.findViewById(R.id.tvItemDate);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    //    Toast.makeText(EventsActivity.this, "CLICK id = " + id.getText(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EventsActivity.this, PagesActivity.class);
                        //intent.putExtra("ID", Integer.parseInt(id.getText().toString()));
                        intent.putExtra("ID", id);
                        intent.putExtra("MS", calendar.getTimeInMillis());
                        startActivity(intent);
                    }
                });
            }
        }
    }

}
