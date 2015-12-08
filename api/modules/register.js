var crypto = require('crypto');

var createAccount = function(email, name, password, callback) {
    ref.createUser({
        email: email,
        password: password
    }, function(error, userData) {
        if (error) {
            callback(new errors.ConflictError("Account is existed"));
        } else {
            ref.child("profiles").child(userData.uid).set({role: 'user', first_password: password}, function(error) {
                if (error) {
                    ref.removeUser({email: email, password: password}, function(){});
                    callback(new errors.InternalError());
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
                            callback(new errors.InternalError());
                        } else {
                            ref.child("mail_queue").child("tasks").push().set({
                                type: "user_invitation",
                                to: email,
                                password: password,
                                link: config.download_url.android
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

var queueAccount = function(email, fullname, callback) {
    var email_hash = crypto.createHash('md5').update(email).digest("hex");
    ref.child("register").child(email_hash).set({
        email: email,
        fullname: fullname,
        last_modified_date: Date.now()
    }, function(error) {
        if (error) {
            callback(new errors.InternalError());
        } else {
            callback(null);
        }
    });
}

var validateDomain = function(email) {
    var email_parts = email.split("@");
    var email_domain = email_parts[email_parts.length - 1];
    var email_domain_parts = email_domain.split(".");

    var domains = config.register_domain_filter.split(",");
    for (var i = 0; i < domains.length; i ++) {
        var d = domains[i];
        var d_parts = d.split(".");
        var error = true;
        if (d_parts.length == email_domain_parts.length) {
            error = false;
            for (var j = 0; j < d_parts.length; j++) {
                d_parts[j] = d_parts[j].trim();
                email_domain_parts[j] = email_domain_parts[j].trim();
                if (!(d_parts[j] === "*" || d_parts[j] === email_domain_parts[j])) {
                    error = true;
                    break;
                }
            }
        }
        if (!error) {
            return true;
        }
    }
    return false;
}

server.post('/register', function (req, res, next) {
    var body = req.body;
    if (typeof(body) === "string") {
        try {
            body = JSON.parse(req.body) || {};
        } catch (e) {
            res.send(new errors.InvalidArgumentError("Invalid JSON format"));
            return next();
        }
    }
    if (typeof(body) !== "object") {
        res.send(new errors.InvalidArgumentError("Can not parse body"));
        return next();
    }

    if (!_.validateEmail(body.email)) {
        res.send(new errors.InvalidArgumentError("Invalid email format"));
        return next();
    }

    if (!body.email || !body.fullname || !body.password) {
        res.send(new errors.MissingParameterError("`email` and `fullname` and `password` are required"));
        return next();
    }

    if (validateDomain(body.email)) {
        createAccount(body.email, body.fullname, body.password, function(error) {
            if (error) {
                res.send(error);
            } else {
                res.send(200, {result: 1});
            }
        });
    } else {
        queueAccount(body.email, body.fullname, function(error) {
            if (error) {
                res.send(error);
            } else {
                res.send(200, {result: 0});
            }
        });
    }
	return next();
});

server.post('/verify-new-account', function(req, res, next) {
    var body = req.body;
    if (typeof(body) === "string") {
        try {
            body = JSON.parse(req.body) || {};
        } catch (e) {
            res.send(new errors.InvalidArgumentError("Invalid JSON format"));
            return next();
        }
    }
    if (typeof(body) !== "object") {
        res.send(new errors.InvalidArgumentError("Can not parse body"));
        return next();
    }

    if (!_.validateEmail(body.email)) {
        res.send(new errors.InvalidArgumentError("Invalid email format"));
        return next();
    }

    if (!body.email) {
        res.send(new errors.MissingParameterError("`email` is required"));
        return next();
    }

    if (validateDomain(body.email)) {
        ref.changePassword({email: body.email, oldPassword: "********^********^^********^^^********^^********^********", newPassword: "lol"}, function(error) {
            switch (error.code) {
                case "INVALID_PASSWORD":
                    res.send(200, {result: 2});
                    break;
                case "INVALID_USER":
                    res.send(200, {result: 1});
                    break;
                default:
                    res.send(new errors.InternalError());
                    break;
            }
        });
    } else {
        queueAccount(body.email, body.fullname, function(error) {
            if (error) {
                res.send(error);
            } else {
                res.send(200, {result: 0});
            }
        });
    }
	return next();
})
