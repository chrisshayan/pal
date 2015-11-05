package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.custom_views.RatingChart;
import vietnamworks.com.pal.entities.AdvisorProfile;
import vietnamworks.com.pal.models.AdvisorProfiles;
import vietnamworks.com.pal.models.UserProfiles;
import vietnamworks.com.pal.services.AsyncCallback;

/**
 * Created by duynk on 11/5/15.
 */
public class AdvisorPreviewFragment extends BaseFragment {
    View commentView, profileView;
    ImageView avatar;
    TextView txtDisplayName, txtScore, txtRate;
    RatingBar ratingBar;
    RatingChart chart;
    Button btnOK, btnCancel;
    EditText comment;

    String advisorId = "";

    public static AdvisorPreviewFragment create(String advisor_id) {
        AdvisorPreviewFragment fragment = new AdvisorPreviewFragment();
        Bundle args = new Bundle();
        args.putString("id", advisor_id);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadData() {
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                UserProfiles.getUserProfile(getArguments().getString("id"), getContext(), new AsyncCallback() {
                    @Override
                    public void onSuccess(Context ctx, Object obj) {
                        if (ctx == getContext()) {
                            AdvisorProfile p = (AdvisorProfile) new AdvisorProfile().importData(obj);
                            if (p.getAvatar() != null && !p.getAvatar().isEmpty()) {
                                Picasso.with(ctx).load(p.getAvatar()).into(avatar);
                            }
                            txtDisplayName.setText(p.getDisplay_name());
                            txtRate.setText(String.format(BaseActivity.sInstance.getString(R.string.advisor_total_vote), p.totalRating()));
                            txtScore.setText(p.avgRate() + "");
                            chart.setRating(p.getRate5(), p.getRate4(), p.getRate3(), p.getRate2(), p.getRate1());
                        }
                    }

                    @Override
                    public void onError(Context ctx, int error_code, String message) {

                    }
                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        advisorId = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_advisor_preview, container, false);

        BaseActivity.applyFont(rootView);

        commentView = rootView.findViewById(R.id.comment_view);
        profileView = rootView.findViewById(R.id.profile);
        avatar = (ImageView) rootView.findViewById(R.id.avatar);
        txtDisplayName = (TextView) rootView.findViewById(R.id.display_name);
        txtScore = (TextView) rootView.findViewById(R.id.score);
        txtRate = (TextView) rootView.findViewById(R.id.nrate);
        chart = (RatingChart) rootView.findViewById(R.id.rate_chart);
        ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        btnOK = (Button) rootView.findViewById(R.id.btn_submit);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        comment = (EditText) rootView.findViewById(R.id.comment);

        loadData();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    commentView.setVisibility(View.VISIBLE);
                    profileView.setVisibility(View.GONE);
                    comment.requestFocus();
                    BaseActivity.sInstance.showKeyboard();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentView.setVisibility(View.GONE);
                profileView.setVisibility(View.VISIBLE);
                BaseActivity.sInstance.hideKeyboard();
                ratingBar.setRating(0);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvisorProfiles.vote(advisorId, (int) ratingBar.getRating(), comment.getText().toString().trim());
                commentView.setVisibility(View.GONE);
                profileView.setVisibility(View.VISIBLE);
                BaseActivity.sInstance.hideKeyboard();
                BaseActivity.toast(R.string.thank_for_voting);
            }
        });

        return rootView;
    }


}
