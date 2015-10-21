package vietnamworks.com.pal.components;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.fragments.FragmentPostDetail;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/13/15.
 */
public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.CardViewHolder> {
    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView lastModifiedDate;
        TextView status;
        TextView score;
        View holder;
        String itemId;

        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            lastModifiedDate = (TextView)itemView.findViewById(R.id.last_modified_date);
            status = (TextView)itemView.findViewById(R.id.status);
            score = (TextView)itemView.findViewById(R.id.score);
            holder = (View)itemView.findViewById(R.id.holder);
            holder.setOnClickListener(this);
        }

        public void setItemId(String id) {
            this.itemId = id;
        }

        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putString("id", itemId);
            Posts.markAsRead(itemId);
            BaseActivity.sInstance.openFragment(FragmentPostDetail.create(b), R.id.fragment_container, true);
        }
    }

    @Override
    public int getItemCount() {
        return AppModel.posts.getData().size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_card, viewGroup, false);
        CardViewHolder view = new CardViewHolder(v);
        BaseActivity.applyFont(v);
        return view;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder view, final int i) {
        Post p = AppModel.posts.getData().get(i);
        if (p != null) {
            view.title.setText(p.getTitle());
            Date last_modified = new Date(p.getLast_modified_date());
            view.lastModifiedDate.setText(Common.getDateString(last_modified));
            view.status.setText(p.getStatusString());
            view.score.setText(p.getScore() > 0?p.getScore() + "":"?");
            view.setItemId(p.getId());

            if (!p.isHas_read()) {
                view.title.setTypeface(BaseActivity.RobotoB);
                view.status.setTypeface(BaseActivity.RobotoBI);
                view.lastModifiedDate.setTypeface(BaseActivity.RobotoBI);
                view.score.setTypeface(BaseActivity.RobotoB);

            } else {
                view.title.setTypeface(BaseActivity.RobotoL);
                view.status.setTypeface(BaseActivity.RobotoLI);
                view.lastModifiedDate.setTypeface(BaseActivity.RobotoLI);
                view.score.setTypeface(BaseActivity.RobotoL);
            }
        }
    }
}
