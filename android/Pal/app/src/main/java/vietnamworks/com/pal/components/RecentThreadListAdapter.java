package vietnamworks.com.pal.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentThreadListAdapter extends RecyclerView.Adapter<RecentThreadListAdapter.ViewHolder> {
    Context context;
    public  RecentThreadListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_thread_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.uiTitle.setText("Item " + position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView uiTitle;
        public ViewHolder(View view) {
            super(view);
            this.uiTitle = (TextView) view.findViewById(R.id.recent_thread_card_title);
        }
    }
}
