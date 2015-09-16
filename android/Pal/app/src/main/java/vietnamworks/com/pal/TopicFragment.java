package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/16/15.
 */
public class TopicFragment extends Fragment{
    private int mDataIndex;
    public TopicFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_topic, container, false);

        ((TextView) rootView.findViewById(R.id.lb_topic_title)).setText(AppModel.topics.getData().get(mDataIndex).mTitle);
        return rootView;
    }

    public static TopicFragment create(int index) {
        TopicFragment obj = new TopicFragment();
        obj.mDataIndex = index;
        return obj;
    }
}
