require("./config");
var Sendgrid  = require('sendgrid')(SENDGRID_API_KEY),
    Queue = require('firebase-queue'),
    Firebase = require('firebase');

var ref = new Firebase(FIREBASE_ENDPOINT);
var mail_queue, queue

ref.authWithCustomToken(FIREBASE_TOKEN, function(error, authData) {
    if (error) {
        console.log("Authentication Failed!", error);
        process.exit(0);
        return;
    }
    console.log("Authentication Success. Start Listening ...");

    mail_queue = ref.child("mail_queue")
    queue = new Queue(mail_queue, function(data, progress, resolve, reject) {
        if (data.type == "advisor_greeting") {
            if (data.to && data.link) {
                var email = new Sendgrid.Email();
                email.addTo(data.to);
                email.subject = "Welcome to Pal";
                email.from = SENDER_EMAIL;
                email.html = data.to;
                email.addFilter('templates', 'enable', 1);
                email.addFilter('templates', 'template_id', TEMPLATE_AVISOR_GREETING);
                email.addSubstitution('_LINK_', data.link);
                Sendgrid.send(email, function(err, json) {
                    if (err) {
                        setTimeout(function() {
                            reject(err);
                        }, 10000);
                    } else {
                        setTimeout(function() {
                            resolve();
                        }, 10000);
                    }
                });
            } else {
                reject("invalid params");
            }
        } else if (data.type == "user_invitation") {
            if (data.to && data.link && data.password) {
                var email = new Sendgrid.Email();
                email.addTo(data.to);
                email.subject = "Welcome to Pal";
                email.from = SENDER_EMAIL;
                email.html = data.to;
                email.addFilter('templates', 'enable', 1);
                email.addFilter('templates', 'template_id', TEMPLATE_USER_INVITE);
                email.addSubstitution('_LINK_', data.link);
                email.addSubstitution('_PASSWORD_', data.password);
                Sendgrid.send(email, function(err, json) {
                    if (err) {
                        setTimeout(function() {
                            reject(err);
                        }, 10000);
                    } else {
                        setTimeout(function() {
                            resolve();
                        }, 10000);
                    }
                });
            } else {
                reject("invalid params");
            }
        } else if (data.type == "password_changed") {
            if (data.to) {
                var email = new Sendgrid.Email();
                email.addTo(data.to);
                email.subject = "You’ve just changed your password on your PAL account";
                email.from = SENDER_EMAIL;
                email.html = "";
                email.addFilter('templates', 'enable', 1);
                email.addFilter('templates', 'template_id', TEMPLATE_CHANGE_PASSWORD);
                Sendgrid.send(email, function(err, json) {
                    if (err) {
                        setTimeout(function() {
                            reject(err);
                        }, 10000);
                    } else {
                        setTimeout(function() {
                            resolve();
                        }, 10000);
                    }
                });
            } else {
                reject("invalid params");
            }
        } else {
            reject("type if not supported");
        }
    });
});



process.on('SIGINT', function() {
    console.log('Starting queue shutdown');
    queue.shutdown().then(function() {
        console.log('Finished queue shutdown');
        process.exit(0);
    });
});
