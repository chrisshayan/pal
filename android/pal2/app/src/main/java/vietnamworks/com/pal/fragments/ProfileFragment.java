package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.PicassoCircleTransform;
import vietnamworks.com.pal.entities.UserProfile;
import vietnamworks.com.pal.services.GaService;

/**
 * Created by duynk on 12/1/15.
 */
public class ProfileFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_profile, container, false);
        BaseActivity.applyFont(rootView);

        ((ImageButton)rootView.findViewById(R.id.btn_profile_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TimelineActivity)getActivity()).onBackPressed();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.sInstance.hideActionBar();
        GaService.trackScreen(R.string.ga_screen_profile);
        loadData();
    }

    @Override
    public void onPause() {
        BaseActivity.sInstance.showActionBar();
        super.onPause();
    }

    void loadData() {
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                UserProfile u = UserProfile.getCurrentUserProfile();
                View v = getView();
                if (v != null) {
                    if (!u.getAvatar().isEmpty()) {
                        Picasso.with(getContext()).load(u.getAvatar()).transform(new PicassoCircleTransform()).into(((ImageView) v.findViewById(R.id.avatar)));
                    }
                    ((TextView) v.findViewById(R.id.firstname)).setText(u.getFirstName());
                    ((TextView) v.findViewById(R.id.lastname)).setText(u.getLastName());
                    ((TextView) v.findViewById(R.id.display_name)).setText(u.getDisplayName());
                    ((TextView) v.findViewById(R.id.email)).setText(u.getEmail());

                    ((TextView) v.findViewById(R.id.posts)).setText(String.format("%d", u.getTotalPosts()));
                    ((TextView) v.findViewById(R.id.avg_pts)).setText(String.format("%.2f", u.getScore()));
                    ((TextView) v.findViewById(R.id.exp_pts)).setText(String.format("%d", u.getExp()));
                    ((TextView) v.findViewById(R.id.level)).setText(u.getLevelName());
                    ((TextView) v.findViewById(R.id.level_percent)).setText(String.format("%d %%", u.getLevelCompletion()));
                }
            }
        }, 100);
    }
}
