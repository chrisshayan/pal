package vietnamworks.com.pal.models;

import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Arrays;

import vietnamworks.com.pal.entities.Post;
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
        ref.setValue(p);
        //ref.setPriority(-System.currentTimeMillis());

        if (p.getRef_topic() != null && !p.getRef_topic().isEmpty()) {
            Topics.addSubmit(p.getRef_topic());
        }
        UserProfiles.increaseNumOfPost();

        return ref.getKey();
    }

    public static String addText(String title, String ref_topic, String text) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setText(text);
        p.setTitle(title);
        p.setStatus(Post.STATUS_SYNC);
        return add(p);
    }

    public static String addAudioAsync(String title, String ref_topic, String message) {
        Post p = new Post();
        p.setRef_topic(ref_topic);
        p.setTitle(title);
        p.setStatus(Post.STATUS_USER_PENDING);
        p.setText(message);
        return add(p);
    }

    public static void updateAudioLink(String postId, String audioLink) {
        Firebase ref = FirebaseService.newRef(Arrays.asList("posts", postId));
        ref.child("audio").setValue(audioLink);
        ref.child("status").setValue(Post.STATUS_SYNC);
    }

    public static void raiseError(String postId) {
        FirebaseService.newRef(Arrays.asList("posts", postId)).child("status").setValue(Post.STATUS_USER_ERROR);
    }

    public static void markAsRead(String postId) {
        FirebaseService.newRef(Arrays.asList("posts", postId, "has_read")).setValue(true);
    }

    public static Query getAllPostsQuery() {
        return FirebaseService.newRef(Arrays.asList("users_posts", FirebaseService.authData.getUid(), "all")).orderByPriority();
    }

    public static Query getEvaluatedPostsQuery() {
        return FirebaseService.newRef(Arrays.asList("users_posts", FirebaseService.authData.getUid(), "evaluated")).orderByPriority();
    }

    public static Query getUnreadPostsCounterQuery() {
        return FirebaseService.newRef(Arrays.asList("users_posts", FirebaseService.authData.getUid(), "unread")).limitToFirst(100);
    }
    public static Query getUnreadEvaluatedPostsCounterQuery() {
        return FirebaseService.newRef(Arrays.asList("users_posts", FirebaseService.authData.getUid(), "evaluated_unread")).limitToFirst(100);
    }

    public static Query getPostDetailQuery(String id) {
        return FirebaseService.newRef(Arrays.asList("posts", id));
    }

}
