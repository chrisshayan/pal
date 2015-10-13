package vietnamworks.com.pal.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/13/15.
 */
public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.CardViewHolder> {
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView createdDate;
        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            createdDate = (TextView)itemView.findViewById(R.id.created_date);
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
        return view;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder view, final int i) {
        Post p = AppModel.posts.getData().get(i);
        if (p != null) {
            if (p.getStatus() == Post.STATUS_NONE) { //not sync yet
                FirebaseService.newRef(Arrays.asList("posts", p.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AppModel.posts.getData().set(i, dataSnapshot.getValue(Post.class));
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
            view.title.setText(AppModel.posts.getData().get(i).getTitle());
            view.createdDate.setText(AppModel.posts.getData().get(i).getCreated_date() + "");
        }
    }
}
