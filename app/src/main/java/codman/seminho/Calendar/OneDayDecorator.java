package codman.seminho.Calendar;


import android.graphics.Typeface;

import android.provider.CalendarContract;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Date;

/**
 * Decorate a day by making the text big and bold
 */
public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    int count;
    final String TAG = "Seminho";
    int color;
    int colorToday;

    public OneDayDecorator(CalendarDay date, int count, int color,int colorToday) {
        this.date = date;
        this.count = count;
        this.color = color;
        this.colorToday = colorToday;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        boolean res = (date != null && day.equals(date));

        return res;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        //if(this.date== CalendarDay.today())
        if(this.date.getDay()== CalendarDay.today().getDay()
                &&this.date.getMonth()== CalendarDay.today().getMonth()
                &&this.date.getYear()== CalendarDay.today().getYear())
        {
            codman.seminho.Calendar.DotSpan ds = new codman.seminho.Calendar.DotSpan(1, String.valueOf(count), color,true,colorToday);
            view.addSpan(ds);
          //  view.addSpan(new RelativeSizeSpan(1.4f));
        }
        else{
            codman.seminho.Calendar.DotSpan ds = new codman.seminho.Calendar.DotSpan(1, String.valueOf(count), color);
            view.addSpan(ds);

             // DotSpan ds =new RelativeSizeSpan(1.4f);
                    //new codman.seminho.Calendar.DotSpan(1, String.valueOf(count), color);
        }



    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
