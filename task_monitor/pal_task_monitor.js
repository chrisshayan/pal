require("./config");
var Queue = require('firebase-queue'),
    Firebase = require('firebase');

var ref = new Firebase(FIREBASE_ENDPOINT);

var all_topics = {};
var queue;

function shuffle(o){
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
}

function createQueue() {
    console.log("Start listening on quest queue ...");
    queue = new Queue(ref.child("quest_queue"), function(data, progress, resolve, reject) {
        if (data.user_id) {
            if (data.action == "request") {
                var keys = Object.keys(all_topics);
                var total_record = keys.length;
                var indice = [];
                for (var i = 0; i < total_record; i++) {
                    indice[i] = i;
                }
                indice = shuffle(indice);
                var tmp = {};
                for (var i = 0; i < 10 && i < indice.length; i++) {
                    var key = keys[i];
                    tmp[keys[indice[i]]] = all_topics[keys[indice[i]]];
                    tmp[keys[indice[i]]]["index"] = i;
                }
                var user_quest = new Firebase(FIREBASE_ENDPOINT + "/user_quests/" + data.user_id);
                user_quest.set(tmp, function(error) {
                    if (error) {
                        reject(error);
                    } else {
                        resolve();
                    }
                });
            } else {
                reject("missing action");
            }
        } else {
            reject("missing user_id");
        }
    });
}

function listenOnTopics() {
    console.log("Start listening on topics ...");
    var all_topics_query = ref.child("topics");

    all_topics_query.on('child_added', function(snapshot) {
        all_topics[snapshot.key()] = snapshot.val();
    });

    all_topics_query.on('child_changed', function(snapshot) {
        var val = snapshot.val();
        if (val.status == 0) {
            delete(all_topics[snapshot.key()]);
        } else {
            all_topics[snapshot.key()] = snapshot.val();
        }
    });

    all_topics_query.on('child_removed', function(snapshot) {
        delete(all_topics[snapshot.key()]);
    });
}

ref.authWithCustomToken(FIREBASE_TOKEN, function(error, authData) {
    if (error) {
        console.log("Authentication Failed!", error);
        process.exit(0);
        return;
    }
    console.log("Authentication Success");
    setTimeout(createQueue, 1000);
    setTimeout(listenOnTopics, 2000);
});


process.on('SIGINT', function() {
    console.log('Starting queue shutdown');
    if (queue) {
        queue.shutdown().then(function() {
            console.log('Finished queue shutdown');
            process.exit(0);
        });
    }
});
