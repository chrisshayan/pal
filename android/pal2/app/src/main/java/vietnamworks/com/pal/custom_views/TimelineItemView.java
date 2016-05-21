package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.PicassoCircleTransform;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.entities.Post;

/**
 * Created by duynk on 11/2/15.
 */
public class TimelineItemView extends TimelineItemBaseView {
    boolean hasInit;
    String itemId;

    ImageView icon;
    TextView txtSubject, txtSub1, txtSub2, txtText;
    AudioPlayer player;
    ViewGroup textGroup, audioGroup;
    TextView scoreText;
    View holder, cta;
    Button cta1;

    Context ctx;

    public TimelineItemView(View itemView, Context ctx) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        txtSubject = (TextView) itemView.findViewById(R.id.subject);
        txtSub1 = (TextView) itemView.findViewById(R.id.desc1);
        txtSub2 = (TextView) itemView.findViewById(R.id.desc2);
        txtText = (TextView) itemView.findViewById(R.id.text);
        player = (AudioPlayer) itemView.findViewById(R.id.player);
        textGroup = (ViewGroup) itemView.findViewById(R.id.text_group);
        audioGroup = (ViewGroup) itemView.findViewById(R.id.audio_group);
        scoreText = (TextView) itemView.findViewById(R.id.star);

        cta = itemView.findViewById(R.id.cta);
        cta1 = (Button) cta.findViewById(R.id.cta1);
        cta.setVisibility(View.GONE);

        holder = itemView.findViewById(R.id.holder);

        scoreText.setVisibility(View.GONE);

        container = holder;

        this.ctx = ctx;
        BaseActivity.applyFont(itemView);
        highlight(false);
    }

    public interface OnClickEventListener {
        void onClick(String itemId);
    }

    public void setClickEventListener(final OnClickEventListener l) {
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(itemId);
            }
        });
    }

    public void setOnAvatarClickEventListener(final OnClickEventListener l) {
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(itemId);
            }
        });
    }

    public void setValue(int icon, String subject, String sub1, String sub2, String text, String audio_url, boolean preview) {
        this.icon.setImageResource(icon);
        setValue(subject, sub1, sub2, text, audio_url, preview);
    }

    public void setValue(String iconURL, String subject, String sub1, String sub2, String text, String audio_url, boolean preview) {
        Picasso.with(ctx).load(iconURL).transform(new PicassoCircleTransform()).into(this.icon);
        setValue(subject, sub1, sub2, text, audio_url, preview);
    }

    public void highlight(boolean val) {
        if (val) {
            txtSubject.setTypeface(BaseActivity.RobotoB);
            txtSub1.setTypeface(BaseActivity.RobotoL);
            txtSub2.setTypeface(BaseActivity.RobotoL);
            txtText.setTypeface(BaseActivity.RobotoL);
        } else {
            txtSubject.setTypeface(BaseActivity.RobotoR);
            txtSub1.setTypeface(BaseActivity.RobotoL);
            txtSub2.setTypeface(BaseActivity.RobotoL);
            txtText.setTypeface(BaseActivity.RobotoL);
        }
    }

    public void setValue(String subject, String sub1, String sub2, String text, String audio_url, boolean preview_mode) {
        if (preview_mode) {
            int l = text.length();
            text = Utils.getFirstWords(text, 24);
            if (text.length() < l) {
                text = text + "...";
            }
        }
        txtSubject.setText(subject);
        txtSub1.setText(sub1 == null ? "" : sub1);
        txtSub2.setText(sub2 == null ? "" : sub2);
        if (text != null && text.length() > 0) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
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
        setValue(icon, p, false);
    }

    public void startIconAnim() {
        Drawable d = icon.getDrawable();
        if (d instanceof AnimationDrawable) {
            ((AnimationDrawable) icon.getDrawable()).start();
        }
    }

    public void setValue(int icon, Post p, boolean preview_mode) {
        this.icon.setImageResource(icon);
        String title = p.getTitle();
        if (title == null || title.length() == 0) {
            if (p.getAudio() == null || p.getAudio().length() == 0) {
                title = BaseActivity.sInstance.getString(R.string.you_wrote);
            } else {
                title = BaseActivity.sInstance.getString(R.string.you_said);
            }
        }

        int score = p.getScore();
        if (score <= 0) {
            scoreText.setVisibility(View.GONE);
            txtSub2.setVisibility(View.VISIBLE);
        } else {
            scoreText.setVisibility(View.VISIBLE);
            txtSub2.setVisibility(View.GONE);

            scoreText.setText(BaseActivity.sInstance.getResources().getStringArray(R.array.post_rate)[Math.max(Math.min(score, 5), 1) - 1]);
        }

        setValue(title, Utils.getDuration(p.getLast_modified_date()), p.statusString(), p.getText(), p.getAudio(), preview_mode);
    }

    public void setValue(String icon, Post p) {
        setValue(icon, p, false);
    }

    public void setValue(String icon, Post p, boolean preview_mode) {
        Picasso.with(ctx).load(icon).transform(new PicassoCircleTransform()).into(this.icon);
        String title = p.getTitle();
        if (title == null || title.length() == 0) {
            if (p.getAudio() == null || p.getAudio().length() == 0) {
                title = BaseActivity.sInstance.getString(R.string.you_wrote);
            } else {
                title = BaseActivity.sInstance.getString(R.string.you_said);
            }
        }

        int score = p.getScore();
        if (score <= 0) {
            scoreText.setVisibility(View.GONE);
            txtSub2.setVisibility(View.VISIBLE);
        } else {
            scoreText.setVisibility(View.VISIBLE);
            txtSub2.setVisibility(View.GONE);
            scoreText.setText(BaseActivity.sInstance.getResources().getStringArray(R.array.post_rate)[Math.max(Math.min(score, 5), 1) - 1]);
        }
        setValue(title, Utils.getDuration(p.getLast_modified_date()), p.statusString(), p.getText(), p.getAudio(), preview_mode);
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public void setIcon(int icon) {
        this.icon.setImageResource(icon);
    }

    public void setIcon(String icon) {
        Picasso.with(ctx).load(icon).transform(new PicassoCircleTransform()).into(this.icon);
    }

    public void setSubject(String subject) {
        txtSubject.setText(subject);
    }

    public void setCTA(String text, final OnClickEventListener l) {
        cta.setVisibility(View.VISIBLE);
        cta1.setText(text);
        cta1.setVisibility(View.VISIBLE);
        cta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(itemId);
            }
        });
    }
}

