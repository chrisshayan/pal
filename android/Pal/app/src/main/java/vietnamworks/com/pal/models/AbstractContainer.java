package vietnamworks.com.pal.models;

import java.util.ArrayList;

/**
 * Created by duynk on 9/11/15.
 */
public abstract class AbstractContainer<T> {
    protected ArrayList<T> data = new ArrayList<>();
    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) {
        this.data = data;
    }
}
