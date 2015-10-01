package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import vietnamworks.com.pal.ActivityMain;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.BaseService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 9/17/15.
 */
public class FragmentSubmitTopic extends FragmentBase {
    ProgressBar mProgressBar;
    ViewGroup mSubmitForm;
    ViewGroup mSubmitFormBtnOk;
    ViewGroup mSubmitFormBtnCancel;
    ViewGroup mSubmitFormBtnRetry;
    EditText mTxtUserTopic;
    TextView mLbThankYou;

    public static final String ARG_ARTICLE_INDEX = "article";

    private int mArticleIndex;

    public static FragmentSubmitTopic create(int article_index) {
        FragmentSubmitTopic fragment = new FragmentSubmitTopic();

        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_INDEX, article_index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleIndex = getArguments().getInt(ARG_ARTICLE_INDEX);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_submit_topic, container, false);

        mProgressBar = ((ProgressBar) rootView.findViewById(R.id.submit_record_progressbar));
        mSubmitForm = ((ViewGroup) rootView.findViewById(R.id.topic_submit_form));
        mSubmitFormBtnOk = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_submit_btn_ok));
        mSubmitFormBtnCancel = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_submit_btn_cancel));
        mSubmitFormBtnRetry = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_submit_btn_retry));
        mTxtUserTopic = ((EditText) rootView.findViewById(R.id.txt_user_custom_topic));
        mLbThankYou = (TextView) rootView.findViewById(R.id.lb_thank_you);

        if (mArticleIndex >= 0) {
            onSubmit();
        } else {
            mLbThankYou.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mSubmitForm.setVisibility(View.VISIBLE);
            mSubmitFormBtnOk.setVisibility(View.VISIBLE);
            mSubmitFormBtnCancel.setVisibility(View.GONE);
            mSubmitFormBtnRetry.setVisibility(View.GONE);
        }

        this.getActivityRef(ActivityMain.class).showActionBar();
        return rootView;
    }

    private void sayThankYou() {
        mLbThankYou.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mSubmitForm.setVisibility(View.INVISIBLE);
        mSubmitFormBtnOk.setVisibility(View.GONE);
        mSubmitFormBtnCancel.setVisibility(View.GONE);
        mSubmitFormBtnRetry.setVisibility(View.GONE);
        mLbThankYou.setText(getString(R.string.message_thank_you));
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivityRef(ActivityMain.class).onCancelRecorder(null);
            }
        }, 2000L);
    }

    private void onSubmitError() {
        mLbThankYou.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mSubmitForm.setVisibility(View.INVISIBLE);
        mSubmitFormBtnOk.setVisibility(View.GONE);
        mSubmitFormBtnCancel.setVisibility(View.VISIBLE);
        mSubmitFormBtnRetry.setVisibility(View.VISIBLE);
        mLbThankYou.setText(getString(R.string.message_fail_to_connect_server));
    }

    public void onCancel() {
        getActivityRef(ActivityMain.class).onCancelRecorder(null);
    }

    public void onRetry() {
        onSubmit();
    }

    public void onSubmit() {
        if (this.mArticleIndex < 0 && mTxtUserTopic.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivityRef(ActivityMain.class), getString(R.string.user_topic_validation_empty_string),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mLbThankYou.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSubmitForm.setVisibility(View.INVISIBLE);
        mSubmitFormBtnOk.setVisibility(View.GONE);
        mSubmitFormBtnCancel.setVisibility(View.GONE);
        mSubmitFormBtnRetry.setVisibility(View.GONE);

        BaseService.PostFile(this.getActivityRef(ActivityMain.class), Common.getSampleRecordPath());

        Post p = null;
        int current_topic = ((ActivityMain)this.getActivity()).mCurrentTopicIndex;
        if (current_topic >= 0) {
            p = new Post(AppModel.topics.getData().get(current_topic));
        } else {
            p = new Post();
            p.setTitle(mTxtUserTopic.getText().toString());
        }
        p.setCreated_by(FirebaseService.authData.getUid());
        p.setCreated_date(System.currentTimeMillis());
        p.setAnswer_audio("");
        p.setStatus(0);

        Firebase newPostRef = FirebaseService.newRef("posts").push();
        newPostRef.setValue(p);
        sayThankYou();

        ///TODO: should handle submit error status via onSubmitError();
    }
}
