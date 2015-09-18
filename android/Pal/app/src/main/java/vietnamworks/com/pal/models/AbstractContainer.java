package vietnamworks.com.pal.models;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by duynk on 9/11/15.
 */
public abstract class AbstractContainer<T> {
    public final static int SELECTED_ITEM_NONE = -1;

    protected OnLoadAsyncCallback onLoadAsyncCallback;
    protected ArrayList<T> data = new ArrayList<>();
    protected int mActiveIndex = -1;


    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) {
        this.data = data;
    }
    public void loadAsync(Context context, OnLoadAsyncCallback callback) {
        mActiveIndex = SELECTED_ITEM_NONE;
    }

    public interface OnLoadAsyncCallback {
        void onSuccess(Context context);
        void onError(Context context);
    }

    public void setActiveItemIndex(int index) {
        mActiveIndex = index;
    }

    public int getActiveItemIndex() {
        return mActiveIndex;
    }

    public T getActiveItem() {
        if (mActiveIndex >= 0 && data != null && mActiveIndex <= data.size() - 1) {
            return data.get(mActiveIndex);
        }
        return null;
    }
}
