angular.module('inspinia').service('AdvisorService', function ($rootScope, firebaseHelper) {
    this.getAdvisorById = function(uid, callback) {
        firebaseHelper.getFireBaseInstance(["profiles_pub", uid]).once('value', function(snapshot) {
            var obj = snapshot.val();
            if (obj && callback) {
                obj.$id = snapshot.key();
                callback(obj);
            }
        })
    };

    this.updateAdvisor = function(advisor, onComplete) {
        var advisor_id = advisor.get("$id");
        if (advisor_id) {
            firebaseHelper.getFireBaseInstance(["profiles_pub", advisor.get("$id")]).update(advisor.getProperty(), function(error) {
                if (error) {
                    if (onComplete) {onComplete(error);}
                } else {
                    firebaseHelper.getFireBaseInstance(["profiles", advisor.get("$id")]).update({
                        pub_profile_last_update: Date.now()
                    }, function(error) {
                        if (onComplete) {onComplete(error);}
                    });
                }
            });
        } else {
            if (onComplete) {onComplete("Oops, something wrong. Invalid advisor id found");}
        }
    }
});
