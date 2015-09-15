package vietnamworks.com.pal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SignUpFragment extends Fragment {
    Activity mRefActivity;

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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    public void setActivity(Activity act) {
        this.mRefActivity = act;
    }
}
