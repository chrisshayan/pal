package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/26/15.
 */
public class RegisterSuccessFragment extends BaseFragment {
    Button btnShare;
    TextView txtMessage;

    String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_register_success, container, false);

        BaseActivity.applyFont(rootView);
        btnShare = (Button)rootView.findViewById(R.id.btn_share);
        txtMessage = (TextView)rootView.findViewById(R.id.message);

        setMessage(this.message);

        //TODO: no need share button for now.
        setButtonShareVisible(false);

        return rootView;
    }


    public void setMessage(String message) {
        this.message = message;
        if (message != null) {
            if (txtMessage != null) {
                txtMessage.setText(message);
            }
        }
    }

    public void setMessage(int message) {
        txtMessage.setText(getString(message));
    }

    public void setButtonShareVisible(boolean visible) {
        if (btnShare != null) {
            //btnShare.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            btnShare.setVisibility(View.INVISIBLE);
        }
    }
}
