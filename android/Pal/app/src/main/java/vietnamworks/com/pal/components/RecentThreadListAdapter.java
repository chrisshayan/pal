package vietnamworks.com.pal.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.models.RecentThread;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentThreadListAdapter extends RecyclerView.Adapter<RecentThreadListAdapter.ViewHolder> {
    Context context;
    public enum ECardType {
        SEND_ME, RECENT_THREAD
    }

    public  RecentThreadListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ECardType.SEND_ME.ordinal()) {
            return new ViewHolder_Sendme(LayoutInflater.from(parent.getContext()).inflate(R.layout.sendme, parent, false));
        } else if (viewType == ECardType.RECENT_THREAD.ordinal()) {
            return new ViewHolder_RecentThread(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_thread_card, parent, false));
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int totalItem = AppModel.recentThreadData.getData().size();
        return totalItem + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ECardType.SEND_ME.ordinal();
        } else {
            return ECardType.RECENT_THREAD.ordinal();
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) { //header
            ViewHolder_Sendme item = (ViewHolder_Sendme)holder;
        } else {
            RecentThread item = (RecentThread) AppModel.recentThreadData.getData().get(position-1);
            ViewHolder_RecentThread view = (ViewHolder_RecentThread)holder;
            view.uiTitle.setText(item.title);
            view.uiCreatedDate.setText(Common.getDateString(item.createdDate));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view){
            super(view);
        }
    }

    class ViewHolder_RecentThread extends RecentThreadListAdapter.ViewHolder{
        protected TextView uiTitle;
        protected TextView uiCreatedDate;
        public ViewHolder_RecentThread(View view) {
            super(view);
            this.uiTitle = (TextView) view.findViewById(R.id.title);
            this.uiCreatedDate = (TextView) view.findViewById(R.id.createdDate);
        }
    }

    class ViewHolder_Sendme extends RecentThreadListAdapter.ViewHolder{
        public ViewHolder_Sendme(View view) {
            super(view);

        }
    }
}
