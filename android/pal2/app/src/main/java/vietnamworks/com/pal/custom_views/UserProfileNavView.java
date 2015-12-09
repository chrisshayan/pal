package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.PicassoCircleTransform;

/**
 * Created by duynk on 10/28/15.
 */
public class UserProfileNavView extends LinearLayout {
    TextView txtTotalPosts, txtAvgPoints, txtUserName, txtUserLevel;
    DonutProgress lvProgress;
    ImageView imgAvatar;
    private boolean hasInit = false;
    int total_posts;
    float avg_point;
    int exp_percent;

    public static UserProfileNavView create(Context context, final int total_posts, final float avg_point, final int exp_percent ) {
        UserProfileNavView obj = new UserProfileNavView(context);
        obj.total_posts = total_posts;
        obj.avg_point = avg_point;
        obj.exp_percent = exp_percent;
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
            lvProgress = (DonutProgress) findViewById(R.id.lv_progress);
            txtUserLevel = (TextView) findViewById(R.id.user_level);
            txtUserName = (TextView) findViewById(R.id.username);
            imgAvatar = (ImageView) findViewById(R.id.avatar);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TimelineActivity)BaseActivity.sInstance).onOpenUserProfile(v);
                }
            });

            hasInit = true;
            updateStat(total_posts, avg_point, exp_percent);
            updateProfile("", "", "");
        }
        BaseActivity.applyFont(this);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_timeline_nav_header, this);
        onFinishInflate();
    }

    public void updateStat(final int total_posts, final float avg_point, final int exp_percent) {
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
                    lvProgress.setProgress(exp_percent);
                }
            });
        }
    }

    public void updateProfile(final String name, final  String level, final String avatar) {
        if (hasInit) {
            final int pts = Math.round(avg_point * 10);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    txtUserLevel.setText(level);
                    txtUserName.setText(name);
                    if (avatar != null && avatar.trim().length() > 0) {
                        Picasso.with(getContext()).load(avatar).transform(new PicassoCircleTransform()).placeholder(R.drawable.ic_action_account_box).into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_action_account_box);
                    }
                }
            });
        }
    }
}
