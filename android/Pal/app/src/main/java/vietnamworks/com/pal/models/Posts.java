package vietnamworks.com.pal.models;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.Topic;
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
        p.setUser_last_request(System.currentTimeMillis());
        p.setStatus(p.getStatus()); //update status index
        p.setType(p.getType()); //update type index
        ref.setValue(p);
        ref.setPriority(-System.currentTimeMillis());

        return ref.getKey();
    }

    public static String addText(String title, String ref_topic, String text) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setText(text);
        p.setTitle(title);
        p.setStatus(Post.STATUS_READY);
        p.setType(Topic.TYPE_WRITING);
        return add(p);
    }

    public static String addAudioAsync(String title, String ref_topic) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setTitle(title);
        p.setStatus(Post.STATUS_USER_PENDING);
        p.setType(Topic.TYPE_SPEAKING);
        return add(p);
    }

    public static void updateAudioLink(String postId, String audioLink) {
        Firebase ref = FirebaseService.newRef(Arrays.asList("posts", postId));
        ref.child("audio").setValue(audioLink);
        ref.child("status").setValue(Post.STATUS_READY);
    }

    public static void raiseError(String postId) {
        FirebaseService.newRef(Arrays.asList("posts", postId)).child("status").setValue(Post.STATUS_USER_ERROR);
    }

    public static void markAsRead(String postId) {
        FirebaseService.newRef(Arrays.asList("posts", postId, "has_read")).setValue(true);
    }
}
