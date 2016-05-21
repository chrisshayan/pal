package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 11/12/15.
 */
public class TimelineItemQuest extends TimelineItemBaseView {
    Context ctx;
    TextView txtQuest;

    public TimelineItemQuest(View itemView, Context ctx) {
        super(itemView);
        this.ctx = ctx;
        txtQuest = (TextView) itemView.findViewById(R.id.quest);
        BaseActivity.applyFont(itemView);
    }

    public void setQuestTitle(String title) {
        txtQuest.setText(title);
    }
}
