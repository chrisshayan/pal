require("./config");
var Firebase = require('firebase');
var ref = new Firebase(FIREBASE_ENDPOINT);
var posts, users_posts;

PostStatus = {
    None: 0,
    UserPending: 1,
    UserError: 2,
    Sync: 3,
    Ready: 4,
    AdvisorProcessing: 5,
    AdvisorEvaluated: 6,
    UserConversation :7,
    AdvisorConversation: 8,
    ClosedByUser: 9,
    ClosedByRedo: 10
}


ref.authWithCustomToken(FIREBASE_TOKEN, function(error, authData) {
    if (error) {
        console.log("Authentication Failed!", error);
        process.exit(0);
        return;
    }
    console.log("Authentication Success. Start Listening ...");
    posts = ref.child("posts");
    users_posts = ref.child("users_posts");

    posts.on('child_added',   onChanged);
    posts.on('child_changed', onChanged);
    posts.on('child_removed', function(snapshot) {
        try {
            var val = snapshot.val();
            var key = snapshot.key();
            users_posts.child(val.created_by).child("all").child(key).remove();
            users_posts.child(val.created_by).child("evaluated").child(key).remove();
            users_posts.child(val.created_by).child("unread").child(key).remove();
            users_posts.child(val.created_by).child("evaluated_unread").child(key).remove();
        } catch (e) {
            Console.log(e);
        }
    });
});

function onChanged(snapshot) {
    try {
        var val = snapshot.val();
        var key = snapshot.key();

        if (val.status <= PostStatus.Ready) {
            val.has_read = true; //fix user cancel task
        }
        var obj = {
            status: val.status,
            last_modified_date: val.last_modified_date,
            audio: val.audio,
            has_read: val.has_read,
            score: val.score,
            title: val.title,
            text: val.text
        }

        users_posts.child(val.created_by).child("all").child(key).setWithPriority(obj, val.last_modified_date);

        if (val.status === PostStatus.AdvisorEvaluated) {
            users_posts.child(val.created_by).child("evaluated").child(key).setWithPriority(obj, val.last_modified_date);
        }

        if (val.status === PostStatus.Sync) {
            posts.child(key).update({
                status: PostStatus.Ready
            });
        }

        if (val.has_read === false) {
            users_posts.child(val.created_by).child("unread").child(key).set(true);
            if (val.status == PostStatus.AdvisorEvaluated) {
                users_posts.child(val.created_by).child("evaluated_unread").child(key).set(true);
            }
        } else {
            users_posts.child(val.created_by).child("unread").child(key).remove();
            users_posts.child(val.created_by).child("evaluated_unread").child(key).remove();
        }
    } catch (e) {
        console.log(e);
    }
}
