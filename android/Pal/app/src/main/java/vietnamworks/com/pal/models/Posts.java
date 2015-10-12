package vietnamworks.com.pal.models;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.RefUserPosts;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/12/15.
 */
public class Posts extends AbstractContainer<Post> {
    protected Posts(){
        super();
        ArrayList<Post> data = new ArrayList<>();
        this.setData(data);
    }

    public static String add(Post p) {
        p.modifyOrCreate();
        Firebase ref = FirebaseService.newRef("posts").push();
        ref.setValue(p);

        Firebase user_posts_ref = FirebaseService.newRef("ref_user_posts");
        RefUserPosts r = new RefUserPosts();
        r.modify();
        r.setStatus(p.getStatus());
        user_posts_ref.setValue(r);

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
