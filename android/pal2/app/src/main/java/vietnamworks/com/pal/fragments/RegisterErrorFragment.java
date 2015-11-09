package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/27/15.
 */
public class RegisterErrorFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_register_error, container, false);
        BaseActivity.applyFont(rootView);

        return rootView;
    }

    public void setError(String message) {
        ((TextView)getView().findViewById(R.id.error_message)).setText(message);
    }

    public void setError(int message) {
        ((TextView)getView().findViewById(R.id.error_message)).setText(getString(message));
    }
}