package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/13/15.
 */
public class FragmentHeader extends Fragment {
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_header_posts, container, false);
        title = (TextView)rootView.findViewById(R.id.lb_title);
        BaseActivity.applyFont(rootView);
        return rootView;
    }

    public void setTitle(final String txt) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                title.setText(txt);
            }
        });
    }
}
