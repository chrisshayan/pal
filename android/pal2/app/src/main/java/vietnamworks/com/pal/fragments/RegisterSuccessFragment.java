package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/26/15.
 */
public class RegisterSuccessFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_register_success, container, false);

        BaseActivity.applyFont(rootView);

        return rootView;
    }


    public void setMessage(String message) {
        ((TextView)getView().findViewById(R.id.message)).setText(message);
    }

    public void setMessage(int message) {
        ((TextView)getView().findViewById(R.id.message)).setText(getString(message));
    }

    public void setButtonShareVisible(boolean visible) {
        getView().findViewById(R.id.btn_share).setVisibility(visible?View.VISIBLE:View.INVISIBLE);
    }
}
