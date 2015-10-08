package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/8/15.
 */

public class FragmentToolbar extends FragmentBase {
    public FragmentToolbar() {
        super();
    }
    ImageButton btnAudioMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_header, container, false);
        btnAudioMode = (ImageButton)rootView.findViewById(R.id.btn_audio_mode);
        return rootView;
    }

    public void setAudioMode(boolean val) {
        btnAudioMode.setImageResource(val?R.drawable.ic_microphone_borderless_lightblue:R.drawable.ic_microphone_disable_borderless_lightblue);
    }
}