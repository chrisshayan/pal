package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.fragments.PostDetailFragment;
import vietnamworks.com.pal.models.Posts;

/**
 * Created by duynk on 11/2/15.
 */
public class TimelineItem extends RecyclerView.ViewHolder implements View.OnClickListener {
    boolean hasInit;
    String itemId;

    ImageView icon;
    TextView txtSubject, txtSub1, txtSub2, txtText;
    AudioPlayer player;
    ViewGroup textGroup, audioGroup, scoreGroup;
    ArrayList<ImageView> stars = new ArrayList<>();
    View holder;

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
        scoreGroup = (ViewGroup) itemView.findViewById(R.id.star);

        stars.add((ImageView) itemView.findViewById(R.id.star1));
        stars.add((ImageView) itemView.findViewById(R.id.star2));
        stars.add((ImageView) itemView.findViewById(R.id.star3));
        stars.add((ImageView) itemView.findViewById(R.id.star4));
        stars.add((ImageView) itemView.findViewById(R.id.star5));

        holder = (View)itemView.findViewById(R.id.holder);
        holder.setOnClickListener(this);

        this.ctx = ctx;
        BaseActivity.applyFont(itemView);
    }

    public void setValue(int icon, String subject, String sub1, String sub2, String text, String audio_url, boolean preview) {
        Picasso.with(ctx).load(icon).into(this.icon);
        setValue(subject, sub1, sub2, text, audio_url, preview);
    }

    public void setValue(String iconURL, String subject, String sub1, String sub2, String text, String audio_url, boolean preview) {
        Picasso.with(ctx).load(iconURL).into(this.icon);
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

    public void setValue(int icon, Post p, boolean preview_mode) {
        Picasso.with(ctx).load(icon).into(this.icon);
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
            scoreGroup.setVisibility(View.GONE);
            txtSub2.setVisibility(View.VISIBLE);
        } else {
            scoreGroup.setVisibility(View.VISIBLE);
            txtSub2.setVisibility(View.GONE);
            for (int i = 0; i < score; i++) {
                stars.get(i).setAlpha(1.0f);
            }
            for (int i = score; i < stars.size(); i++) {
                stars.get(i).setAlpha(0.25f);
            }
        }

        setValue(title, Utils.getDuration(p.getLast_modified_date()), p.statusString(), p.getText(), p.getAudio(), preview_mode);
    }

    public void setValue(String icon, Post p) {
        setValue(icon, p, false);
    }

    public void setValue(String icon, Post p, boolean preview_mode) {
        Picasso.with(ctx).load(icon).into(this.icon);
        setValue(p.getTitle(), Utils.getDateString(p.getLast_modified_date()), p.statusString(), p.getText(), p.getAudio(), preview_mode);
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    @Override
    public void onClick(View v) {
        Posts.markAsRead(itemId);
        BaseActivity.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                Bundle b = new Bundle();
                b.putString("id", itemId);
                BaseActivity.sInstance.openFragment(PostDetailFragment.create(b), R.id.fragment_holder, true);
            }
        }, 200);

    }
}
