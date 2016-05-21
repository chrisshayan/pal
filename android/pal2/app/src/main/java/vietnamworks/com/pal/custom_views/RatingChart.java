package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.ResizeWidthAnimation;

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
        final int max_width = ((ViewGroup)this.getParent()).getWidth();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ResizeWidthAnimation anim5 = new ResizeWidthAnimation(rate5, (int) Math.max(p5 * max_width, BaseActivity.density));
                anim5.setDuration(500);
                rate5.startAnimation(anim5);

                ResizeWidthAnimation anim4 = new ResizeWidthAnimation(rate4, (int) Math.max(p4 * max_width, BaseActivity.density));
                anim4.setDuration(500);
                rate4.startAnimation(anim4);

                ResizeWidthAnimation anim3 = new ResizeWidthAnimation(rate3, (int) Math.max(p3 * max_width, BaseActivity.density));
                anim3.setDuration(500);
                rate3.startAnimation(anim3);

                ResizeWidthAnimation anim2 = new ResizeWidthAnimation(rate2, (int) Math.max(p2 * max_width, BaseActivity.density));
                anim2.setDuration(500);
                rate2.startAnimation(anim2);

                ResizeWidthAnimation anim1 = new ResizeWidthAnimation(rate1, (int) Math.max(p1 * max_width, BaseActivity.density));
                anim1.setDuration(500);
                rate1.startAnimation(anim1);

            }
        });
    }
}
