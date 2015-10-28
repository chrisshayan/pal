package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/28/15.
 */
public class UserProfileNavView extends LinearLayout {
    TextView txtTotalPosts, txtAvgPoints, txtTotalFollowing;
    private boolean hasInit = false;
    int total_posts;
    float avg_point;
    int total_following;

    public static UserProfileNavView create(Context context, final int total_posts, final float avg_point, final int total_following ) {
        UserProfileNavView obj = new UserProfileNavView(context);
        obj.total_posts = 0;
        obj.avg_point = 0;
        obj.total_following = 0;
        return obj;
    }

    public UserProfileNavView(Context context) {
        super(context);
        initializeViews(context);
    }

    public UserProfileNavView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initializeViews(context);
    }

    public UserProfileNavView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }


    @Override
    protected void onFinishInflate() {
        if (!hasInit) {
            super.onFinishInflate();
            txtTotalPosts = (TextView) findViewById(R.id.total_posts);
            txtAvgPoints = (TextView) findViewById(R.id.avg_points);
            txtTotalFollowing = (TextView) findViewById(R.id.total_following);
            hasInit = true;
            updateStat(total_posts, avg_point, total_following);
        }
        BaseActivity.applyFont(this);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_timeline_nav_header, this);
        onFinishInflate();
    }

    public void updateStat(final int total_posts, final float avg_point, final int total_following) {
        if (hasInit) {
            final int pts = Math.round(avg_point * 10);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    String txtTotalPost = total_posts + "";
                    txtTotalPosts.setText(txtTotalPost);
                    if (pts == 0) {
                        txtAvgPoints.setText("_");
                    } else {
                        String tmp = (pts / 10) + "." + (pts % 10);
                        txtAvgPoints.setText(tmp);
                    }
                    String txt_follow = total_following + "";
                    txtTotalFollowing.setText(txt_follow);
                }
            });
        }
    }
}
