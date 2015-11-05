package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 11/5/15.
 */
public class RatingChart extends LinearLayout {
    boolean hasInit = false;
    View rate5, rate4, rate3, rate2, rate1;

    public RatingChart(Context context) {
        super(context);
        initializeViews(context);
    }

    public RatingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initializeViews(context);
    }

    public RatingChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cv_rating_chart, this);

        rate5 = findViewById(R.id.rate5);
        rate4 = findViewById(R.id.rate4);
        rate3 = findViewById(R.id.rate3);
        rate2 = findViewById(R.id.rate2);
        rate1 = findViewById(R.id.rate1);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        if (!hasInit) {
            super.onFinishInflate();
            setRating(0, 0, 0, 0, 0);
            hasInit = true;
        }
    }

    public void setRating(int r5, int r4, int r3, int r2, int r1) {
        int m = Math.max(Math.max(Math.max(Math.max(Math.max(r5, r4), r3), r2), r1), 1);
        final float p5 = Math.max(r5*1.0f/m, 0.01f);
        final float p4 = Math.max(r4*1.0f/m, 0.01f);
        final float p3 = Math.max(r3*1.0f/m, 0.01f);
        final float p2 = Math.max(r2*1.0f/m, 0.01f);
        final float p1 = Math.max(r1*1.0f/m, 0.01f);
        final int max_width = this.getWidth();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                rate5.animate().scaleX(p5*max_width).setDuration(100).start();
                rate4.animate().scaleX(p4*max_width).setDuration(100).start();
                rate3.animate().scaleX(p3*max_width).setDuration(100).start();
                rate2.animate().scaleX(p2*max_width).setDuration(100).start();
                rate1.animate().scaleX(p1*max_width).setDuration(100).start();
            }
        });
    }
}
