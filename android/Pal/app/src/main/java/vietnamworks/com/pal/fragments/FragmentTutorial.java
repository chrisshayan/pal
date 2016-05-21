package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTutorial extends Fragment {

    public FragmentTutorial() {
    }

    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL_PAGE = "total_page";
    private int mPageNumber;
    private int mTotalPage;

    public static FragmentTutorial create(int pageNumber, int totalPage) {
        FragmentTutorial fragment = new FragmentTutorial();
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
                .inflate(R.layout.fragment_tutorial, container, false);

        String str = getResources().getStringArray(R.array.tutor)[mPageNumber];
        ((TextView) rootView.findViewById(R.id.tutorTextView)).setText(str);

        ImageView img = (ImageView) rootView.findViewById(R.id.img_tutor);
        int []res = new int[] {R.drawable.ic_tutor_1, R.drawable.ic_tutor_2, R.drawable.ic_tutor_3};
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
