require("./config");
var Firebase = require('firebase');
var posts = new Firebase(FIREBASE_ENDPOINT + "/posts");
var users_posts = new Firebase(FIREBASE_ENDPOINT + "/users_posts");

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

posts.on('child_added',   onChanged);

posts.on('child_changed', onChanged);

posts.on('child_removed', function(snapshot) {
    console.log(snapshot.val());
});


function onChanged(snapshot) {
    var val = snapshot.val();
    var key = snapshot.key();

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
}


