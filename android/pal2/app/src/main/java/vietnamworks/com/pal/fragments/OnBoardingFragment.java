package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 10/26/15.
 */
public class OnBoardingFragment extends BaseFragment {
    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL_PAGE = "total_page";
    private int mPageNumber;
    private int mTotalPage;

    public static OnBoardingFragment create(int pageNumber, int totalPage) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_TOTAL_PAGE, totalPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTotalPage  = getArguments().getInt(ARG_TOTAL_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_onboarding, container, false);

        TextView header = ((TextView) rootView.findViewById(R.id.tutor_header));
        header.setText(getResources().getStringArray(R.array.tutor_header)[mPageNumber]);

        ((TextView) rootView.findViewById(R.id.tutor_body)).setText(getResources().getStringArray(R.array.tutor_body)[mPageNumber]);

        ImageView img = (ImageView) rootView.findViewById(R.id.img_tutor);
        int []res = new int[] {R.drawable.ic_ongoing_card_1, R.drawable.ic_ongoing_card_2, R.drawable.ic_ongoing_card_3};
        img.setImageResource(res[mPageNumber % res.length]);

        BaseActivity.applyFont(rootView);

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public int getTotalPages() {
        return mTotalPage;
    }
}
