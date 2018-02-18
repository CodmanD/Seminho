package kodman.seminho.Calendar;


import android.graphics.Typeface;

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

    public OneDayDecorator(CalendarDay date, int count, int color) {
        this.date = date;
        this.count = count;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        boolean res = (date != null && day.equals(date));

        return res;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        kodman.seminho.Calendar.DotSpan ds = new kodman.seminho.Calendar.DotSpan(1, String.valueOf(count),color);
        view.addSpan(ds);
    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
