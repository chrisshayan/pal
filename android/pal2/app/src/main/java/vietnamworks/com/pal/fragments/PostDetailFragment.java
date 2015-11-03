package vietnamworks.com.pal.fragments;

import android.os.Bundle;

/**
 * Created by duynk on 11/3/15.
 */
public class PostDetailFragment extends BaseFragment {
    String title;
    String itemId;

    public static PostDetailFragment create(Bundle b) {
        PostDetailFragment obj = new PostDetailFragment();
        obj.title = b.getString("title");
        obj.itemId = b.getString("id");
        return obj;
    }

    public String getTitle() {
        return title;
    }
}
