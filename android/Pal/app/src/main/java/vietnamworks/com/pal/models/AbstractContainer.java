package vietnamworks.com.pal.models;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by duynk on 9/11/15.
 */
public abstract class AbstractContainer<T> {
    protected OnLoadAsyncCallback onLoadAsyncCallback;
    protected ArrayList<T> data = new ArrayList<>();
    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) {
        this.data = data;
    }
    public void loadAsync(Context context, OnLoadAsyncCallback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }

    public interface OnLoadAsyncCallback {
        void onSuccess();
        void onError();
    }
}
