package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/7/15.
 */
public class FragmentWriting extends FragmentBase {
    public FragmentWriting() {
        super();
    }
    EditText text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_writing, container, false);
        text = (EditText)rootView.findViewById(R.id.textAnswer);

        BaseActivity.applyFont(rootView);
        return rootView;
    }

    public void reset() {
        text.setText("");
    }
    public String getText() {
        return this.text.getText().toString();
    }
}