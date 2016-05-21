package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.PicassoCircleTransform;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.AdvisorCommentView;
import vietnamworks.com.pal.custom_views.RatingChart;
import vietnamworks.com.pal.entities.AdvisorProfile;
import vietnamworks.com.pal.entities.BaseEntity;
import vietnamworks.com.pal.models.AdvisorProfiles;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;

/**
 * Created by duynk on 11/5/15.
 */
public class AdvisorPreviewFragment extends BaseFragment {
    @Bind(R.id.comment_view)    View        commentView;
    @Bind(R.id.profile)         View        profileView;
    @Bind(R.id.comments)        ViewGroup   commentsView;
    @Bind(R.id.avatar)          ImageView   avatar;
    @Bind(R.id.display_name)    TextView    txtDisplayName;
    @Bind(R.id.score)           TextView    txtScore;
    @Bind(R.id.nrate)           TextView    txtRate;
    @Bind(R.id.rating_bar)      RatingBar   ratingBar;
    @Bind(R.id.rate_chart)      RatingChart chart;
    @Bind(R.id.btn_submit)      Button      btnOK;
    @Bind(R.id.btn_cancel)      Button      btnCancel;
    @Bind(R.id.comment)         EditText    comment;
    @Bind(R.id.no_vote)         TextView    txtNoVote;

    String advisorId = "";

    Query recentComments;
    Query advisorProfile;
    Query userRating;

    public static AdvisorPreviewFragment create(String advisor_id) {
        AdvisorPreviewFragment fragment = new AdvisorPreviewFragment();
        Bundle args = new Bundle();
        args.putString("id", advisor_id);
        fragment.setArguments(args);
        return fragment;
    }

    private ValueEventListener recentCommentListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            BaseActivity.timeout(new Runnable() {
                @Override
                public void run() {
                    commentsView.removeAllViews();
                    if (dataSnapshot.getChildrenCount() > 0) {
                        txtNoVote.setVisibility(View.GONE);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> data = snapshot.getValue(HashMap.class);

                            String display_name = BaseEntity.safeGetString(data, "display_name");
                            if (snapshot.getKey().equalsIgnoreCase(FirebaseService.getUid())) {
                                display_name = getString(R.string.you);
                            }

                            commentsView.addView(AdvisorCommentView.create(getContext(),
                                    BaseEntity.safeGetString(data, "avatar"),
                                    display_name,
                                    BaseEntity.safeGetLong(data, "created_date", 0),
                                    BaseEntity.safeGetString(data, "message"),
                                    BaseEntity.safeGetInt(data, "rate")
                            ));
                        }
                    }
                }
            });
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ValueEventListener advisorProfileListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            BaseActivity.timeout(new Runnable() {
                @Override
                public void run() {
                    AdvisorProfile p = (AdvisorProfile) new AdvisorProfile().importData(dataSnapshot);
                    if (p.getAvatar() != null && !p.getAvatar().isEmpty()) {
                        Picasso.with(getContext()).load(p.getAvatar()).transform(new PicassoCircleTransform()).into(avatar);
                    }
                    txtDisplayName.setText(p.getDisplay_name());
                    if (p.totalRating() > 1) {
                        txtRate.setText(String.format(BaseActivity.sInstance.getString(R.string.advisor_total_vote), Utils.counterFormat(p.totalRating())));
                    } else {
                        txtRate.setText(String.format(BaseActivity.sInstance.getString(R.string.advisor_total_vote_single), Utils.counterFormat(p.totalRating())));
                    }
                    txtScore.setText(p.avgRate() + "");
                    chart.setRating(p.getRate5(), p.getRate4(), p.getRate3(), p.getRate2(), p.getRate1());
                }
            });
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ValueEventListener userRatingListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            BaseActivity.timeout(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> v = dataSnapshot.getValue(HashMap.class);

                    if (v != null && v.containsKey("rate")) {
                        ratingBar.setRating(BaseEntity.safeGetInt(v, "rate", 0));
                    }
                }
            });
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        advisorId = getArguments().getString("id");
    }

    @Override
    public void onPause() {
        super.onPause();
        recentComments.removeEventListener(recentCommentListener);
        advisorProfile.removeEventListener(advisorProfileListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recentComments == null) {
            recentComments = AdvisorProfiles.getRecentComments(getArguments().getString("id"));
        }
        recentComments.addValueEventListener(recentCommentListener);

        if (advisorProfile == null) {
            advisorProfile = AdvisorProfiles.getAdvisorProfile(getArguments().getString("id"));
        }
        advisorProfile.addValueEventListener(advisorProfileListener);

        GaService.trackScreen(R.string.ga_screen_advisor_preview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_advisor_preview, container, false);

        BaseActivity.applyFont(rootView);

        ButterKnife.bind(this, rootView);

        txtNoVote.setVisibility(View.VISIBLE);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    commentView.setVisibility(View.VISIBLE);
                    comment.requestFocus();
                    BaseActivity.sInstance.showKeyboard();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentView.setVisibility(View.GONE);
                BaseActivity.sInstance.hideKeyboard();
                ratingBar.setRating(0);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvisorProfiles.vote(advisorId, (int) ratingBar.getRating(), comment.getText().toString().trim());
                commentView.setVisibility(View.GONE);
                BaseActivity.sInstance.hideKeyboard();
                BaseActivity.toast(R.string.thank_for_voting);
            }
        });

        if (userRating == null) {
            userRating = AdvisorProfiles.getAdvisorRatingByUser(getArguments().getString("id"), FirebaseService.getUid());
        }
        userRating.addListenerForSingleValueEvent(userRatingListener);

        return rootView;
    }

    public void onLayoutChanged(final boolean isKeyboardShown) {
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                profileView.setVisibility(isKeyboardShown ? View.GONE : View.VISIBLE);
            }
        });
    }
}
