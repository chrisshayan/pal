package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.entities.UserProfile;
import vietnamworks.com.pal.models.CurrentUserProfile;
import vietnamworks.com.pal.services.GaService;

/**
 * Created by duynk on 12/2/15.
 */
public class UpdateProfileFragment extends BaseFragment {
    EditText firstName, lastName, jobTitle, displayName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_update_profile, container, false);
        BaseActivity.applyFont(rootView);

        final UserProfile p = UserProfile.getCurrentUserProfile();

        firstName = (EditText)rootView.findViewById(R.id.edit_firstname);
        lastName = (EditText)rootView.findViewById(R.id.edit_lastname);
        jobTitle = (EditText)rootView.findViewById(R.id.edit_job_title);
        displayName = (EditText)rootView.findViewById(R.id.edit_displayname);

        firstName.setText(p.getFirstName());
        lastName.setText(p.getLastName());
        displayName.setText(p.getDisplayName());
        jobTitle.setText(p.getJobTitle());

        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (displayName.getText().toString().isEmpty()) {
                        String display_name = firstName.getText().toString() + " " + lastName.getText().toString();
                        displayName.setText(display_name);
                    }
                }
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (displayName.getText().toString().isEmpty()) {
                        String display_name = firstName.getText().toString() + " " + lastName.getText().toString();
                        displayName.setText(display_name);
                    }
                }
            }
        });

        rootView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TimelineActivity)getActivity()).onBackPressed();
            }
        });

        ((TimelineActivity)getActivity()).setTitle(R.string.title_update_profile);
        ((TimelineActivity)getActivity()).showActionBar();

        return rootView;
    }

    @Override
    public void onPause() {
        CurrentUserProfile.updateBasicProfile(firstName.getText().toString(), lastName.getText().toString(), displayName.getText().toString(), jobTitle.getText().toString());
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        GaService.trackScreen(R.string.ga_screen_update_basic_profile);
    }
}
