require("./config");
var Sendgrid  = require('sendgrid')(SENDGRID_API_KEY),
    Queue = require('firebase-queue'),
    Firebase = require('firebase');

var ref = new Firebase(FIREBASE_ENDPOINT);

var queue = new Queue(ref, function(data, progress, resolve, reject) {
    if (data.type == "advisor_greeting") {
        if (data.to && data.link) {
            var email = new Sendgrid.Email();
            email.addTo(data.to);
            email.subject = "Welcome to Pal";
            email.from = SENDER_EMAIL;
            email.html = data.to;
            email.addFilter('templates', 'enable', 1);
            email.addFilter('templates', 'template_id', '8ef30a12-8938-450a-8741-5c121e2a1507');
            email.addSubstitution('_LINK_', data.link);
            Sendgrid.send(email, function(err, json) {
                if (err) {
                    reject(err);
                } else {
                    resolve();
                }
            });
        } else {
            reject("invalid params");
        }
    } else {
        reject("type if not supported");
    }
});

process.on('SIGINT', function() {
    console.log('Starting queue shutdown');
    queue.shutdown().then(function() {
        console.log('Finished queue shutdown');
        process.exit(0);
    });
});
