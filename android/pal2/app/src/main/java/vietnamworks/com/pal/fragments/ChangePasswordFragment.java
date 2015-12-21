package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/30/15.
 */
public class ChangePasswordFragment extends BaseFragment {
    @Bind(R.id.error_view) View errorView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_change_password, container, false);

        ButterKnife.bind(this, rootView);
        errorView.setVisibility(View.INVISIBLE);

        final Button btn_change_password = (Button)rootView.findViewById(R.id.btn_change_password);
        final Button btn_cancel = (Button)rootView.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TimelineActivity) getActivity()).onBackPressed();
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View container = getView();
                final EditText view_current_password = ((EditText) container.findViewById(R.id.old_password));
                final EditText view_new_password = ((EditText) container.findViewById(R.id.new_password));
                final EditText view_confirm_password = ((EditText) container.findViewById(R.id.confirm_new_password));


                String current_password = view_current_password.getText().toString();
                String new_password = view_new_password.getText().toString();
                String confirm_password = view_confirm_password.getText().toString();

                if (current_password.isEmpty()) {
                    setError(R.string.require_password);
                    view_current_password.requestFocus();
                } else if (new_password.isEmpty()) {
                    setError(R.string.require_new_password);
                    view_new_password.requestFocus();
                } else if (confirm_password.isEmpty()) {
                    setError(R.string.require_confirm_password);
                    view_confirm_password.requestFocus();
                } else if (!new_password.equals(confirm_password)) {
                    setError(R.string.password_not_matched);
                    view_new_password.setText("");
                    view_confirm_password.setText("");
                    view_new_password.requestFocus();
                } else {
                    setError(null);

                    view_current_password.setEnabled(false);
                    view_new_password.setEnabled(false);
                    view_confirm_password.setEnabled(false);
                    btn_change_password.setEnabled(false);
                    btn_cancel.setEnabled(false);
                    ((TimelineActivity) getActivity()).lockBackKey(true);

                    FirebaseService.newRef().changePassword(FirebaseService.getUserProfileStringValue("email"), current_password, new_password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            HashMap<String, Object> mail = new HashMap<String, Object>();
                            mail.put("to", FirebaseService.getUserProfileStringValue("email"));
                            mail.put("type", "password_changed");
                            FirebaseService.newRef("mail_queue").child("tasks").push().setValue(mail);
                            BaseActivity.toast(R.string.password_has_changed);
                            ((TimelineActivity) getActivity()).lockBackKey(false);
                            ((TimelineActivity) getActivity()).onBackPressed();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            ((TimelineActivity) getActivity()).lockBackKey(false);
                            view_current_password.setEnabled(true);
                            view_new_password.setEnabled(true);
                            view_confirm_password.setEnabled(true);
                            btn_change_password.setEnabled(true);
                            btn_cancel.setEnabled(true);

                            setError(firebaseError.getMessage());
                        }
                    });
                }
            }
        });

        BaseActivity.applyFont(rootView);
        return rootView;
    }

    public void setError(int txt) {
        String str = getContext().getString(txt);
        setError(str);
    }

    public void setError(String error) {
        if (error == null) {
            errorView.setVisibility(View.INVISIBLE);
        } else {
            errorView.setVisibility(View.VISIBLE);
            TextView txt = (TextView)errorView.findViewById(R.id.error);
            txt.setText(error);
        }
    }

    public void onLayoutChanged(final boolean isKeyboardShown) {
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                if (isKeyboardShown) {
                    ((TimelineActivity)getActivity()).hideActionBar();
                } else {
                    ((TimelineActivity)getActivity()).showActionBar();
                }
            }
        });
    }
}
