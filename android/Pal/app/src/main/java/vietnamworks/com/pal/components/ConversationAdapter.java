package vietnamworks.com.pal.components;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.entities.BaseEntity;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/15/15.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    Post post;
    AudioMixerController mixer;

    public ConversationAdapter(AudioMixerController mixer) {
        super();
        this.mixer = mixer;
    }


    public void setPost(Post p) {
        post = p;
        notifyDataSetChanged();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        ConversationView view;

        ConversationViewHolder(ConversationView v) {
            super(v);
            this.view = v;
        }
    }

    @Override
    public int getItemCount() {
        if (post != null) {
            return post.getConversationList().size();
        }
        return 0;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ConversationView v = new ConversationView(viewGroup.getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ConversationViewHolder view = new ConversationViewHolder(v);
        BaseActivity.applyFont(v);
        return view;
    }

    @Override
    public void onBindViewHolder(final ConversationViewHolder holder, final int i) {
        if (post != null) {// TODO: 10/15/15 define conservation activity here
            HashMap<String, Object> conversation = post.getConversationList().get(i);
            String title = "You said:"; //TODO: remove hard code text
            if (!conversation.get("uid").toString().equalsIgnoreCase(FirebaseService.authData.getUid())) {
                title = "Advisor said:"; //TODO: remove hard code text
            }
            String message = BaseEntity.safeGetString(conversation, "text");
            String audio = BaseEntity.safeGetString(conversation, "audio");
            holder.view.setData(mixer, title, message, audio, BaseEntity.safeGetLong(conversation, "created_date"));
        }
    }
}