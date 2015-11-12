package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.view.View;

import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 11/12/15.
 */
public class TimelineItemQuest extends TimelineItemBaseView {
    Context ctx;

    public TimelineItemQuest(View itemView, Context ctx) {
        super(itemView);
        this.ctx = ctx;
        BaseActivity.applyFont(itemView);
    }
}
