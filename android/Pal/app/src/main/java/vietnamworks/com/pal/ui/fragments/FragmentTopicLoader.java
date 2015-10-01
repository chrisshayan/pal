package vietnamworks.com.pal.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.R;

public class FragmentTopicLoader extends Fragment {

    ViewGroup mProcessingGroup;
    ViewGroup mFailGroup;
    private boolean isLoadingFail = false;

    public FragmentTopicLoader() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_topic_loading, container, false);

        mProcessingGroup = ( (ViewGroup) rootView.findViewById(R.id.loading_topic_card_processing));
        mFailGroup = ( (ViewGroup) rootView.findViewById(R.id.loading_topic_card_fail));
        if (isLoadingFail) {
            onLoadingFail();
        } else {
            onStartLoading();
        }
        return rootView;
    }

    public void onStartLoading() {
        isLoadingFail = false;
        mProcessingGroup.setVisibility(View.VISIBLE);
        mFailGroup.setVisibility(View.GONE);
    }

    public void onLoadingFail() {
        isLoadingFail = true;
        if (mProcessingGroup != null) {
            mProcessingGroup.setVisibility(View.GONE);
        }
        if (mFailGroup != null) {
            mFailGroup.setVisibility(View.VISIBLE);
        }
    }
}