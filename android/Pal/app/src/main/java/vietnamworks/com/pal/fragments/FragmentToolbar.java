package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import it.sephiroth.android.library.tooltip.TooltipManager;
import vietnamworks.com.pal.ActivityBase;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/8/15.
 */

public class FragmentToolbar extends FragmentBase {
    public FragmentToolbar() {
        super();
    }
    ImageButton btnAudioMode;
    boolean useAudio = true;
    boolean hasDisable = false;

    boolean hasShowEnableAudioTooltip = false;
    boolean hasShowDisableAudioTooltip = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_header, container, false);
        btnAudioMode = (ImageButton)rootView.findViewById(R.id.btn_audio_mode);
        return rootView;
    }

    public void setAudioMode(boolean val) {
        useAudio = val;
        if (hasDisable) {
            btnAudioMode.setImageResource(val ? R.drawable.ic_microphone_borderless_grey : R.drawable.ic_microphone_disable_borderless_grey);
        } else {
            btnAudioMode.setImageResource(val ? R.drawable.ic_microphone_borderless_lightblue : R.drawable.ic_microphone_disable_borderless_lightblue);
        }

        if (!hasDisable) {
            if (useAudio) {
                ActivityBase.sInstance.setTimeout(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!hasDisable && !hasShowDisableAudioTooltip) {
                                    hasShowDisableAudioTooltip = true;
                                    TooltipManager.getInstance()
                                            .create(ActivityBase.sInstance, R.id.btn_audio_mode)
                                            .anchor(btnAudioMode, TooltipManager.Gravity.TOP)
                                            .closePolicy(TooltipManager.ClosePolicy.TouchOutside, 3000)
                                            .activateDelay(800)
                                            .text("Touch here to hide all speaking tasks")
                                            .show();
                                }
                            }
                        }, 3000);
            } else {
                ActivityBase.sInstance.setTimeout(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!hasDisable && !hasShowEnableAudioTooltip) {
                                    hasShowEnableAudioTooltip = true;
                                    TooltipManager.getInstance()
                                            .create(ActivityBase.sInstance, R.id.btn_audio_mode)
                                            .anchor(btnAudioMode, TooltipManager.Gravity.TOP)
                                            .closePolicy(TooltipManager.ClosePolicy.TouchOutside, 3000)
                                            .activateDelay(800)
                                            .text("Touch here to enable speaking tasks")
                                            .show();
                                }
                            }
                        }, 3000);
            }
        }
    }

    public void enableAudioButton(boolean val) {
        btnAudioMode.setEnabled(val);
        hasDisable = !val;
        setAudioMode(useAudio);
    }
}