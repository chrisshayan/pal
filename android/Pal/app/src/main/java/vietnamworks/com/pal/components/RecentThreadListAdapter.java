package vietnamworks.com.pal.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    public interface OnItemClickListener {
        void onItemClick(View view, int type, int position);
    }
    OnItemClickListener mItemClickListener;

    public final static int ECARDTYPE_SEND_ME = 0;
    public final static int ECARDTYPE_RECENT_THREAD = 1;

    public  RecentThreadListAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(final RecentThreadListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ECARDTYPE_SEND_ME) {
            return new ViewHolder_Sendme(LayoutInflater.from(parent.getContext()).inflate(R.layout.sendme, parent, false));
        } else if (viewType ==  ECARDTYPE_RECENT_THREAD) {
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
            return ECARDTYPE_SEND_ME;
        } else {
            return ECARDTYPE_RECENT_THREAD;
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

    class ViewHolder_RecentThread extends RecentThreadListAdapter.ViewHolder implements View.OnClickListener{
        protected TextView uiTitle;
        protected TextView uiCreatedDate;
        protected LinearLayout holder;

        public ViewHolder_RecentThread(View view) {
            super(view);
            this.uiTitle = (TextView) view.findViewById(R.id.title);
            this.uiCreatedDate = (TextView) view.findViewById(R.id.createdDate);
            this.holder = (LinearLayout) view.findViewById(R.id.mainHolder);
            this.holder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                int position = getAdapterPosition();
                int type = getItemViewType();
                if (type == ECARDTYPE_RECENT_THREAD) {
                    position --;
                }
                mItemClickListener.onItemClick(itemView, type, position);
            }
        }
    }

    class ViewHolder_Sendme extends RecentThreadListAdapter.ViewHolder{
        public ViewHolder_Sendme(View view) {
            super(view);

        }
    }
}
