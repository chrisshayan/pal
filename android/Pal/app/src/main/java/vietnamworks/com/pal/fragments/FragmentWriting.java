package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/7/15.
 */
public class FragmentWriting extends FragmentBase {
    public FragmentWriting() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_writing, container, false);
        return rootView;
    }
}