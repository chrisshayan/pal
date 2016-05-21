package vietnamworks.com.pal.custom_views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;

/**
 * Created by duynk on 11/4/15.
 */
public class TopicItemView extends RecyclerView.ViewHolder {
    public final static int LEVEL_BEGINNING = 0;
    public final static int LEVEL_PRE_INTERMEDIATE = 1;
    public final static int LEVEL_INTERMEDIATE = 2;
    public final static int LEVEL_ADVANCE = 3;

    public final static int LEVEL_COLORS[] = {0xff34495e, 0xff2e8ece, 0xff27ae60, 0xffe74c3c};

    TextView content, txtLevel, total_views, total_done;
    View topBar, holder;
    String itemId, hint;
    int level;
    public View container;

    public interface OnClickEventListener {
        void onClicked(String itemId, String subject, int level, String hint);
    }

    public TopicItemView(View itemView) {
        super(itemView);
        topBar = itemView.findViewById(R.id.topbar);
        content = (TextView) itemView.findViewById(R.id.content);
        txtLevel = (TextView) itemView.findViewById(R.id.level);
        holder = itemView.findViewById(R.id.holder);
        total_views = (TextView) itemView.findViewById(R.id.total_views);
        total_done = (TextView) itemView.findViewById(R.id.total_done);

        BaseActivity.applyFont(itemView, BaseActivity.RobotoL);

        container = itemView.findViewById(R.id.card_item_holder);
    }

    public void setData(String itemId, final int _level, final String _content, String hint, final long views, final long done) {
        this.itemId = itemId;
        this.hint = hint;
        this.level =  Math.max(Math.min(_level, LEVEL_COLORS.length - 1), 0);
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                int lv = level;
                topBar.setBackgroundColor(LEVEL_COLORS[lv]);
                txtLevel.setText(BaseActivity.sInstance.getResources().getStringArray(R.array.topics_level)[lv]);

                if (views > 1) {
                    total_views.setText(String.format(BaseActivity.sInstance.getString(R.string.n_view), Utils.counterFormat(views)));
                } else {
                    total_views.setText(String.format(BaseActivity.sInstance.getString(R.string.single_view), Utils.counterFormat(views)));
                }

                if (done > 1) {
                    total_done.setText(String.format(BaseActivity.sInstance.getString(R.string.n_submit), Utils.counterFormat(done)));
                } else {
                    total_done.setText(String.format(BaseActivity.sInstance.getString(R.string.single_submit), Utils.counterFormat(done)));
                }
                content.setText(_content);
            }
        });
    }

    public void setClickEventListener(final OnClickEventListener l) {
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClicked(itemId, content.getText().toString(), level, hint);
            }
        });
    }
}
