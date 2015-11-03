package vietnamworks.com.pal.custom_views;

import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 11/3/15.
 */
public class ListItemNullView extends TimelineItemView {
    TextView txtSubject;
    public ListItemNullView(View itemView) {
        super(itemView);
        txtSubject = (TextView) itemView.findViewById(R.id.subject);
        BaseActivity.applyFont(itemView, BaseActivity.RobotoLI);
    }

    public void setText(String text) {
        txtSubject.setText(text);
    }
}
