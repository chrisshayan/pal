var program = require('commander');
var parse = require('csv-parse');
var fs = require('fs');
var transform = require('stream-transform');
var Firebase = require('firebase');
var crypto = require('crypto');
require("./config");

var ref = new Firebase(FIREBASE_ENDPOINT);
program
    .version('0.0.1')
    .option('--csv [file]', 'csv file')
    .option('--task [import]', 'task name')
    .parse(process.argv);

var android_url = "";

var createAccount = function(email, name, callback) {
    var password = crypto.createHash('md5').update((Date.now() + "" + Math.random())).digest("hex").substring(0, 8);
    ref.createUser({
        email: email,
        password: password
    }, function(error, userData) {
        if (error) {
            callback(error);
        } else {
            ref.child("profiles").child(userData.uid).set({role: 'user', first_password: password}, function(error) {
                if (error) {
                    ref.removeUser({email: email, password: password}, function(){});
                    callback(error);
                } else {
                    ref.child("profiles_pub").child(userData.uid).update({
                        created_by: "",
                        created_date: Date.now(),
                        display_name: name,
                        email: email,
                        avatar: "",
                    }, function(error) {
                        if (error) {
                            ref.removeUser({email: email, password: password}, function(){});
                            callback(error);
                        } else {
                            ref.child("mail_queue").child("tasks").push().set({
                                type: "user_invitation",
                                to: email,
                                password: password,
                                link: android_url
                            }, function() {
                                callback(null, email);
                            });
                        }
                    });
                }
            });
        }
    });
}

var main = function() {
    var output = [];
    var parser = parse({delimiter: ','})
    var input = fs.createReadStream(program.csv);
    var processing = 0;
    var report = [];
    var transformer = transform(function(record, callback){
        processing ++;
        setTimeout(function(){
            var f = createAccount;
            f(record[0], record[1], function(error, data) {
                report.push({email: record[0], status: error===null?"OK":error});
                processing--;
                if (processing <= 0) {
                    fs.writeFile("log.txt", JSON.stringify(report), function(err) {
                        if(err) {
                            console.log(err);
                        } else {
                            console.log("log file saved");
                        }
                        process.exit(0);
                    });
                }
            });
        }, 500);
    }, {parallel: 10});

    input.pipe(parser).pipe(transformer);
}

if (program.csv && typeof(program.csv) == "string") {
    ref.authWithCustomToken(FIREBASE_TOKEN, function(error, authData) {
        if (error) {
            console.log("Authentication Failed!", error);
            process.exit(0);
        } else {
            ref.child("config").child("download_url").once("value", function(snap) {
                android_url = snap.val().android;
                main();
            });
        }
    });
}
