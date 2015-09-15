package vietnamworks.com.pal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class SignUpFragment extends Fragment {
    Activity mRefActivity;
    EditText mTxtEmail;

    public SignUpFragment() {
    }

    public static SignUpFragment create(Activity act) {
        SignUpFragment fragment = new SignUpFragment();
        fragment.mRefActivity = act;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
              ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_signup, container, false);

        mTxtEmail = ((EditText) rootView.findViewById(R.id.input_signup_email));
        return rootView;
    }

    public String getEmail() {
        return mTxtEmail.getText().toString().trim();
    }

    public void setActivity(Activity act) {
        this.mRefActivity = act;
    }
}
