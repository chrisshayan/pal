package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.sInstance.hideActionBar();
        GaService.trackScreen(R.string.ga_screen_profile);
    }

    @Override
    public void onPause() {
        BaseActivity.sInstance.showActionBar();
        super.onPause();
    }
}
