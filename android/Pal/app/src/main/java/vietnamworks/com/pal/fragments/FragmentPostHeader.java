package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.PostsActivity;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/13/15.
 */
public class FragmentPostHeader extends Fragment {
    TextView title;
    ImageButton btnHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_header_posts, container, false);
        title = (TextView)rootView.findViewById(R.id.lb_title);
        BaseActivity.applyFont(rootView);
        btnHome = (ImageButton)rootView.findViewById(R.id.nav_button);
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

    public void updateHomeButton() {
        PostsActivity act = (PostsActivity) this.getActivity();
        Fragment f = act.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof FragmentPostDetail) {
            btnHome.setImageResource(R.drawable.ic_hardware_keyboard_backspace);
        } else if (f instanceof  FragmentPostList) {
            btnHome.setImageResource(R.drawable.ic_menu_borderless_white);
        }
    }
}
