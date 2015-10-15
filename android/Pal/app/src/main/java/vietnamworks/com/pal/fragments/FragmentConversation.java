package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/15/15.
 */
public class FragmentConversation extends FrameLayout {
    TextView titleView;
    ImageButton btnAudio;
    TextView bodyView;

    String audioURL;
    String title;
    String body;

    public FragmentConversation(Context context) {
        super(context);
        initializeViews(context);
    }

    public FragmentConversation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public FragmentConversation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_conversation, this);
        BaseActivity.applyFont(this);
    }

    public static FragmentConversation create(Context ctx, String title, String body, String audio) {
        FragmentConversation view = new FragmentConversation(ctx);
        view.audioURL = audio;
        view.title = title;
        view.body = body;
        view.updateUI();
        return view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateUI();
    }

    private void updateUI() {
        titleView = (TextView) findViewById(R.id.title);
        btnAudio = (ImageButton) findViewById(R.id.btn_audio);
        bodyView = (TextView) findViewById(R.id.body);

        titleView.setTypeface(BaseActivity.RobotoB);

        this.titleView.setText(this.title);
        if (this.body == null || this.body.length() == 0) {
            this.bodyView.setVisibility(GONE);
        } else {
            this.bodyView.setText(this.body);
        }
        if (audioURL == null || audioURL.length() == 0) {
            ((ViewGroup)btnAudio.getParent()).setVisibility(View.GONE);
        }
    }

}
