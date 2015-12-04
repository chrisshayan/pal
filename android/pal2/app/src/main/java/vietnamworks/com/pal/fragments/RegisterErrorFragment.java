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
    TextView txtMessage;
    String message;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_register_error, container, false);
        BaseActivity.applyFont(rootView);
        txtMessage = (TextView)rootView.findViewById(R.id.error_message);
        setError(this.message);
        return rootView;
    }

    public void setError(String message) {
        this.message = message;
        if (message != null) {
            if (txtMessage != null) {
                txtMessage.setText(message);
            }
        }
    }

    public void setError(int message) {
        txtMessage.setText(getString(message));
    }
}