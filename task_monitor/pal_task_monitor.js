require("./config");
var Queue = require('firebase-queue'),
    Firebase = require('firebase');

var user_quest_queue = new Firebase(FIREBASE_ENDPOINT + "/quest_queue");
var all_topics_query = new Firebase(FIREBASE_ENDPOINT + "/topics");
var all_topics = {};

function shuffle(o){
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
}

var queue = new Queue(user_quest_queue, function(data, progress, resolve, reject) {
    console.log(data);
    if (data.user_id) {
        if (data.action == "request") {
            var keys = Object.keys(all_topics);
            var total_record = keys.length;
            var indice = [];
            for (var i = 0; i < total_record; i++) {
                indice[i] = i;
            }
            indice = shuffle(indice);
            console.log(indice);
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

process.on('SIGINT', function() {
    console.log('Starting queue shutdown');
    queue.shutdown().then(function() {
        console.log('Finished queue shutdown');
        process.exit(0);
    });
});
