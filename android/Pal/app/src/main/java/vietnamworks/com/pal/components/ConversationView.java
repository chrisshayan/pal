package vietnamworks.com.pal.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/15/15.
 */
public class ConversationView extends LinearLayout implements AudioMixerSubscriber {
    TextView titleView;
    ImageButton btnAudio;
    TextView bodyView;
    TextView createdDate;

    String audioURL = "";
    String title = "";
    String body = "";
    long timeStamp = 0;
    AudioMixerController mixer;

    public ConversationView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ConversationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ConversationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_conversation, this);
        BaseActivity.applyFont(this);

        titleView = (TextView) findViewById(R.id.title);
        btnAudio = (ImageButton) findViewById(R.id.btn_audio);
        bodyView = (TextView) findViewById(R.id.body);
        createdDate = (TextView) findViewById(R.id.created_date);
        titleView.setTypeface(BaseActivity.RobotoB);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView = (TextView) findViewById(R.id.title);
        btnAudio = (ImageButton) findViewById(R.id.btn_audio);
        bodyView = (TextView) findViewById(R.id.body);
        createdDate = (TextView) findViewById(R.id.created_date);
        titleView.setTypeface(BaseActivity.RobotoB);
    }

    public static ConversationView create(Context ctx, AudioMixerController audioCtrl, String title, String body, String audio, long timeStamp) {
        ConversationView view = new ConversationView(ctx);
        view.audioURL = audio;
        view.title = title;
        view.body = body;
        view.timeStamp = timeStamp;
        view.mixer = audioCtrl;
        view.updateUI();
        return view;
    }

    public void setData(AudioMixerController audioCtrl, String title, String body, String audio, long timeStamp) {
        this.audioURL = audio;
        this.title = title;
        this.body = body;
        this.timeStamp = timeStamp;
        this.mixer = audioCtrl;
        this.updateUI();
    }

    private void updateUI() {
        createdDate.setText(Common.getDateString(timeStamp));
        titleView.setTypeface(BaseActivity.RobotoB);
        this.titleView.setText(this.title);
        if (this.body == null || this.body.length() == 0) {
            this.bodyView.setVisibility(GONE);
        } else {
            this.bodyView.setVisibility(VISIBLE);
            this.bodyView.setText(this.body);
        }
        if (audioURL == null || audioURL.length() == 0) {
            ((ViewGroup)btnAudio.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup)btnAudio.getParent()).setVisibility(View.VISIBLE);
        }
    }


    private boolean audioPlaying = false;
    public void onToggleAudio(View v) {
        audioPlaying = !audioPlaying;
        if (audioPlaying) {
            getContext();
            mixer.playAudio(this.audioURL, this);
        } else {
            onStopAudio();
            mixer.stopAudio(this);
        }
    }

    public void onPlayAudio() {
        audioPlaying = true;
        btnAudio.setImageResource(R.drawable.ic_stop_blue);
    }

    public void onStopAudio() {
        audioPlaying = false;
        btnAudio.setImageResource(R.drawable.ic_play_blue);
    }

}
