package codman.seminho.Calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;

/**
 * Span to draw a dot centered under a section of text
 */
public class DotSpan implements LineBackgroundSpan {

    /**
     * Default radius used
     */
    public static final float DEFAULT_RADIUS = 3;

    final String TAG = "Seminho";

    private final float radius;
    private final int color;
    private final int colorForBack;
    private static int colorToday;
    private String str = "";
    int count = 0;
    boolean today = false;

    private HashSet<CalendarDay> dates;

    /**
     * Create a span to draw a dot using default radius and color
     *
     * @see #DotSpan(float, int)
     * @see #DEFAULT_RADIUS
     */
    public DotSpan() {
        this.radius = DEFAULT_RADIUS;
        this.color = 0;
       this.colorForBack = 0;
    }

    /**
     * Create a span to draw a dot using a specified color
     *
     * @param color color of the dot
     * @see #DotSpan(float, int)
     * @see #DEFAULT_RADIUS
     */
    public DotSpan(int color) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
       this.colorForBack = 0;
    }


    public void setDate(HashSet<CalendarDay> dates) {
        this.dates = dates;
    }

    /**
     * Create a span to draw a dot using a specified radius
     *
     * @param radius radius for the dot
     * @see #DotSpan(float, int)
     */
    public DotSpan(float radius) {
        this.radius = radius;
        this.color = 0;
        this.colorForBack = 0;
    }

    /**
     * Create a span to draw a dot using a specified radius and color
     *
     * @param radius radius for the dot
     * @param color  color of the dot
     */
    public DotSpan(float radius, int color) {
        this.radius = radius;
        this.color = color;
        this.colorForBack = 0;
    }

    public DotSpan(float radius, int color, String str) {
        this.radius = radius;
        this.color = color;
        this.str = str;
        this.colorForBack = 0;
    }

    public DotSpan(int color, String str,int colorForBack) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
        this.str = str;
        this.colorForBack = colorForBack;
    }

    public DotSpan(int color, String str,int colorForBack,boolean today,int colorToday) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
        this.str = str;
        this.colorForBack = colorForBack;
        this.today=today;
        DotSpan.colorToday=colorToday;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum) {
     
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }



        canvas.drawCircle((left + right) / 2, bottom + radius, radius, paint);
        paint.setColor(Color.RED);

        if (!str.equals("0")) {
            paint.setColor(colorForBack);
            //paint.setColor(Color.YELLOW);
            canvas.drawRect(0, 0, right, right, paint);
            if(today)
            {
                paint.setColor(colorToday);
                canvas.drawCircle((left + right) / 2, bottom/3 , right/3, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle((left + right) / 2, bottom/3 , right/4, paint);
            }

            paint.setColor(Color.BLUE);
          //  canvas.drawText(str, right / 2 + right / 6, bottom + baseline, paint);
            canvas.drawText(str, right  - right / 6, bottom + baseline, paint);

        }
        else
        if(today)
        {
            paint.setColor(colorToday);
            canvas.drawCircle((left + right) / 2, bottom /3, right/3, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle((left + right) / 2, bottom/3, right/4, paint);
        }

        paint.setColor(oldColor);

    }
}
