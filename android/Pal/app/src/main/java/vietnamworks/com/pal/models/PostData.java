package vietnamworks.com.pal.models;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/12/15.
 */
public class PostData extends AbstractContainer<Post> {
    protected PostData(){
        super();
        ArrayList<Post> data = new ArrayList<>();
        this.setData(data);
    }

    public static String add(Post p) {
        Firebase ref = FirebaseService.newRef("posts");
        ref.push().setValue(p);
        return ref.getKey();
    }

    public static String addText(String title, String ref_topic, String text) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setText(text);
        p.setTitle(title);
        p.setStatus(Post.STATUS_READY);
        return add(p);
    }

    public static String addAudioAsync(String title, String ref_topic) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setTitle(title);
        p.setStatus(Post.STATUS_USER_PENDING);
        return add(p);
    }
}
