package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.PicassoCircleTransform;
import vietnamworks.com.pal.common.Utils;

/**
 * Created by duynk on 11/5/15.
 */
public class AdvisorCommentView extends LinearLayout {
    ImageView imgAvatar, rate1, rate2, rate3, rate4, rate5;
    TextView txtDisplayName, txtCreatedDate, txtComment;
    boolean hasInit = false;

    String avatar = "", displayName = "", comment = "";
    long createdDate = 0;
    int rate;

    public static AdvisorCommentView create(Context context, String avatar, String displayName, long createdDate, String comment, int rate) {
        AdvisorCommentView v = new AdvisorCommentView(context);
        v.setData(avatar, displayName, createdDate, comment, rate);
        return v;
    }

    public AdvisorCommentView(Context context) {
        super(context);
        initializeViews(context);
    }

    public AdvisorCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initializeViews(context);
    }

    public AdvisorCommentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cv_comment_item, this);

        imgAvatar = (ImageView) findViewById(R.id.avatar);
        rate1 = (ImageView) findViewById(R.id.rate1);
        rate2 = (ImageView) findViewById(R.id.rate2);
        rate3 = (ImageView) findViewById(R.id.rate3);
        rate4 = (ImageView) findViewById(R.id.rate4);
        rate5 = (ImageView) findViewById(R.id.rate5);

        txtComment = (TextView) findViewById(R.id.comment);
        txtCreatedDate = (TextView) findViewById(R.id.created_date);
        txtDisplayName = (TextView) findViewById(R.id.display_name);

        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        if (!hasInit) {
            super.onFinishInflate();
            hasInit = true;
        }
    }

    public void setData(final String avatar, final  String displayName, final  long createdDate,  final String comment, final int rate) {
        this.rate = rate;
        this.avatar = avatar;
        this.displayName = displayName;
        this.createdDate = createdDate;
        this.comment = comment;

        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                if (avatar != null && !avatar.isEmpty()) {
                    Picasso.with(getContext()).load(avatar).transform(new PicassoCircleTransform()).into(imgAvatar);
                }
                txtDisplayName.setText(displayName);
                if (comment != null && !comment.isEmpty()) {
                    txtComment.setText(comment);
                } else {
                    txtComment.setVisibility(GONE);
                }

                txtCreatedDate.setText(Utils.getDuration(createdDate));

                rate1.setVisibility(rate >= 1 ? VISIBLE : GONE);
                rate2.setVisibility(rate >= 2 ? VISIBLE : GONE);
                rate3.setVisibility(rate >= 3 ? VISIBLE : GONE);
                rate4.setVisibility(rate >= 4 ? VISIBLE : GONE);
                rate5.setVisibility(rate >= 5 ? VISIBLE : GONE);
            }
        });
    }
}
