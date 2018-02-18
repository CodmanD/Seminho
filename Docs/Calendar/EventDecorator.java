package kodman.seminho.Calendar;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

//import com.prolificinteractive.materialcalendarview.CalendarDay;
import kodman.seminho.Calendar.CalendarDay;
import kodman.seminho.Calendar.DayViewDecorator;
import kodman.seminho.Calendar.DayViewFacade;
//import com.prolificinteractive.materialcalendarview.DayViewDecorator;
//import com.prolificinteractive.materialcalendarview.DayViewFacade;


import java.util.Collection;
import java.util.HashSet;

import kodman.seminho.Calendar.DotSpan;

import static kodman.seminho.R.drawable.ic_add_white_24dp;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements kodman.seminho.Calendar.DayViewDecorator {


    String TAG="Seminho";
    private int color;
    private HashSet<CalendarDay> dates;
    private Resources res;

    public EventDecorator(int color, Collection<CalendarDay> dates,Resources res) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.res=res;
    }

    @Override
    public boolean shouldDecorate(kodman.seminho.Calendar.CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(kodman.seminho.Calendar.DayViewFacade view) {
        DotSpan ds = new DotSpan(5, color);
       // StyleSpan dd = new StyleSpan(Typeface.BOLD_ITALIC);
     //   Log.d(TAG,"DECORATE===============");

        view.addSpan(new DotSpan(5, color));
      //  view.addSpan(new SpannableString("sadsad"));
      //  view.addSpan(new ImageSpan(null,R.drawable.ic_add_white_24dp));

     //   Drawable d=new TextDrawable("ddd");

       // view.setSelectionDrawable(d);
       // view.setBackgroundDrawable(d);

        // view.setBackgroundDrawable(ne());
    }





     class TextDrawable extends ColorDrawable {
        private static final int DEFAULT_COLOR = Color.WHITE;
        private static final int DEFAULT_TEXTSIZE = 15;
        private Paint mPaint;
        private CharSequence mText;
        private int mIntrinsicWidth;
        private int mIntrinsicHeight;
        public TextDrawable(CharSequence text)
        { mText = text;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setTextAlign(Paint.Align.CENTER);
        float textSize =20;// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXTSIZE, res.getDisplayMetrics());
        mPaint.setTextSize(textSize);
        mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);
        }
        @Override
        public void draw(Canvas canvas) {
            Log.d(TAG,"DRAW===============");
            Rect bounds = getBounds();
            canvas.drawText(mText, 0, mText.length(), bounds.centerX(), bounds.centerY(), mPaint);
        }
        @Override
        public int getOpacity()
        { return mPaint.getAlpha(); }
        @Override public int getIntrinsicWidth() { return mIntrinsicWidth; }
        @Override public int getIntrinsicHeight() { return mIntrinsicHeight; }
        @Override public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
    @Override public void setColorFilter(ColorFilter filter) { mPaint.setColorFilter(filter); } }
/*
    class TextDrawable extends ColorDrawable {

        private final String text;
        private final Paint paint;

        public TextDrawable(int color,String text) {

            super(color);
            this.text = text;

            this.paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(22f);
            paint.setAntiAlias(true);
            paint.setFakeBoldText(true);
            paint.setShadowLayer(6f, 0, 0, Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.LEFT);
        }

        @Override
        public void draw(Canvas canvas) {
            Paint p= new Paint();
            p.setColor(Color.MAGENTA);
            p.setAlpha(0);
            canvas.drawText(text, 250, 250, p);
            Log.d(TAG,"DRAW===============");
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

*/


}
