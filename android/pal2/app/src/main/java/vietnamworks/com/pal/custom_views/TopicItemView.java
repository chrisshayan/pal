package vietnamworks.com.pal.custom_views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 11/4/15.
 */
public class TopicItemView extends RecyclerView.ViewHolder {
    public final static int LEVEL_BEGINNING = 0;
    public final static int LEVEL_PRE_INTERMEDIATE = 1;
    public final static int LEVEL_INTERMEDIATE = 2;
    public final static int LEVEL_ADVANCE = 3;

    public final static int LEVEL_COLORS[] = {0xff34495e, 0xff2e8ece, 0xff27ae60, 0xffe74c3c};

    TextView subject, content, level;
    View topBar;

    public TopicItemView(View itemView) {
        super(itemView);
        topBar = (View) itemView.findViewById(R.id.topbar);
        subject = (TextView) itemView.findViewById(R.id.cateogry);
        content = (TextView) itemView.findViewById(R.id.content);
        level = (TextView) itemView.findViewById(R.id.level);
    }

    public void setData(final int l, final String s, final String c) {
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                int lv = Math.max(Math.min(l, LEVEL_COLORS.length - 1), 0);
                topBar.setBackgroundColor(LEVEL_COLORS[lv]);
                level.setText(BaseActivity.sInstance.getResources().getStringArray(R.array.topics_level)[lv]);
                if (s == null || s.isEmpty()) {
                    subject.setText(BaseActivity.sInstance.getString(R.string.common_topic));
                } else {
                    subject.setText(s);
                }
                content.setText(c);
            }
        });
    }
}
