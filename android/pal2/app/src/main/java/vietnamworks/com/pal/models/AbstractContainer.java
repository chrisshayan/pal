package vietnamworks.com.pal.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import vietnamworks.com.pal.services.AsyncCallback;

/**
 * Created by duynk on 9/11/15.
 */
public abstract class AbstractContainer<T> {
    public final static int SELECTED_ITEM_NONE = -1;

    protected AsyncCallback onLoadAsyncCallback;
    protected ArrayList<T> data = new ArrayList<>();
    //protected int mActiveIndex = -1;


    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) {
        this.data = data;
    }
    public void loadAsync(Context context, AsyncCallback callback) {
        //mActiveIndex = SELECTED_ITEM_NONE;
    }

    public void loadAsync(Context context, Map<String, Object>params, AsyncCallback callback) {
        //mActiveIndex = SELECTED_ITEM_NONE;
    }

    public void setActiveItemIndex(int index) {
        //mActiveIndex = index;
    }

    /*
    public int getActiveItemIndex() {
        return mActiveIndex;
    }

    public T getActiveItem() {
        if (mActiveIndex >= 0 && data != null && mActiveIndex <= data.size() - 1) {
            return data.get(mActiveIndex);
        }
        return null;
    }
    */
}
