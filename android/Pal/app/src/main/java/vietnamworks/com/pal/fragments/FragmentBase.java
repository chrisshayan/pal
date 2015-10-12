package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import vietnamworks.com.pal.BaseActivity;

/**
 * Created by duynk on 10/1/15.
 */
public class FragmentBase extends Fragment {
    public FragmentBase() {
        super();
    }

    public <T extends BaseActivity> T getActivityRef(Class<T> cls) {
        Activity act = this.getActivity();
        if (act != null && (act instanceof BaseActivity)) {
            return (T)act;
        }
        return null;
    }
}
