package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.entities.Post;

/**
 * Created by duynk on 11/2/15.
 */
public class TimelineItem extends RecyclerView.ViewHolder {
    boolean hasInit;
    String itemId;

    ImageView icon;
    TextView txtSubject, txtSub1, txtSub2, txtText;
    AudioPlayer player;
    ViewGroup textGroup, audioGroup;

    Context ctx;

    public TimelineItem(View itemView, Context ctx) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        txtSubject = (TextView) itemView.findViewById(R.id.subject);
        txtSub1 = (TextView) itemView.findViewById(R.id.desc1);
        txtSub2 = (TextView) itemView.findViewById(R.id.desc2);
        txtText = (TextView) itemView.findViewById(R.id.text);
        player = (AudioPlayer) itemView.findViewById(R.id.player);
        textGroup = (ViewGroup) itemView.findViewById(R.id.text_group);
        audioGroup = (ViewGroup) itemView.findViewById(R.id.audio_group);
        this.ctx = ctx;
        BaseActivity.applyFont(itemView);
    }

    public void setValue(int icon, String subject, String sub1, String sub2, String text, String audio_url) {
        Picasso.with(ctx).load(icon).into(this.icon);
        setValue(subject, sub1, sub2, text, audio_url);
    }

    public void setValue(String iconURL, String subject, String sub1, String sub2, String text, String audio_url) {
        Picasso.with(ctx).load(iconURL).into(this.icon);
        setValue(subject, sub1, sub2, text, audio_url);
    }

    public void highlight(boolean val) {
        if (val) {
            txtSubject.setTypeface(BaseActivity.RobotoB);
            txtSub1.setTypeface(BaseActivity.RobotoL);
            txtSub2.setTypeface(BaseActivity.RobotoL);
        } else {
            txtSubject.setTypeface(BaseActivity.RobotoR);
            txtSub1.setTypeface(BaseActivity.RobotoL);
            txtSub2.setTypeface(BaseActivity.RobotoL);
        }
    }

    public void setValue(String subject, String sub1, String sub2, String text, String audio_url) {
        txtSubject.setText(subject);
        txtSub1.setText(sub1 == null ? "" : sub1);
        txtSub2.setText(sub2 == null?"":sub2);
        if (text != null && text.length() > 0) {
            txtText.setText(text);
            textGroup.setVisibility(View.VISIBLE);
        } else {
            textGroup.setVisibility(View.GONE);
        }

        if (audio_url != null && audio_url.length() > 0) {
            player.setAudioSource(audio_url);
            audioGroup.setVisibility(View.VISIBLE);
        } else {
            audioGroup.setVisibility(View.GONE);
        }
    }

    public void setValue(int icon, Post p) {
        Picasso.with(ctx).load(icon).into(this.icon);
        String title = p.getTitle();
        if (title == null || title.length() == 0) {
            if (p.getAudio() == null && p.getAudio().length() == 0) {
                title = BaseActivity.sInstance.getString(R.string.you_wrote);
            } else {
                title = BaseActivity.sInstance.getString(R.string.you_said);
            }
        }
        setValue(title, Utils.getDuration(p.getLast_modified_date()), p.getStatusString(), p.getText(), p.getAudio());
    }

    public void setValue(String icon, Post p) {
        Picasso.with(ctx).load(icon).into(this.icon);
        setValue(p.getTitle(), Utils.getDateString(p.getLast_modified_date()), p.getStatusString(), p.getText(), p.getAudio());
    }

    public void setItemId(String id) {
        this.itemId = id;
    }
}
