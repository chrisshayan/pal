package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.custom_views.AudioPlayer;

/**
 * Created by duynk on 10/29/15.
 */
public class ComposerFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_composer, container, false);

        BaseActivity.applyFont(rootView);

        ((AudioPlayer)rootView.findViewById(R.id.player)).setAudioSource("https://d1ngnrwwqlw7nm.cloudfront.net/0200-02-NT-70/01_matthew/01_matthew_01.mp3?Expires=1451563200&Signature=bVHg6xXNCkSGD6GaxSHr8DVSPhuTnZffp2bhYWynqhpevxGllnkM8KQqgoEeiqVUiyyJoEE7w~7M5VBHriWFQ3gWm6F9fI0mEyVUJAXeB4mhnD4rb1IMGoHocMiTvgDzIZ7cwniUccuqpaM3MgWx5uFl8K0C-WCPM0UUetnyKR~KpV7t-~CY8IAD2vi9LwycIUEAu-jIKBa235pBnSihFkEJhl4obVAp-~JTRXcIJqnq66ZguvWfAiP4M6v4gFeoRnUq6LGCcJkAAEVlhw98ZuVgvLbmB6wcHusTn1Iwi3mpqz~IIIJGBEX0H3fpJ0L~CIVXZ6crx2rvyY~d2E4MWg__&Key-Pair-Id=APKAJQXQBQHDBU72DTAQ", true);
        return rootView;
    }
}
