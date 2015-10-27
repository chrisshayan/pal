package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/26/15.
 */
public class LoginFragment extends BaseFragment {
    EditText txtPassword;
    EditText txtEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_login, container, false);

        BaseActivity.applyFont(rootView);

        txtEmail = (EditText)rootView.findViewById(R.id.email);
        txtPassword = (EditText)rootView.findViewById(R.id.password);

        return rootView;
    }

    public void onLayoutChanged() {
        View view = this.getView();
        Activity act = getActivity();
        if (view != null && act != null) {
            Rect r = new Rect();
            act.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            view.animate().y((r.height() - view.getHeight()) >> 1).setDuration(100).start();
        }
    }

    public String getEmail() {
        return txtEmail.getText().toString();
    }

    public String getPassword() {
        return txtPassword.getText().toString();
    }

    public void focusEmail() {
        txtEmail.requestFocus();
    }

    public void focusPassword() {
        txtPassword.requestFocus();
    }

    public void resetForm() {
        txtPassword.setText("");
        txtEmail.setText("");
    }

}
