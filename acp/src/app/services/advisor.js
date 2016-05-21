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
            var d = advisor.getProperty();
            d.teaching_exp = parseInt(d.teaching_exp);
            firebaseHelper.getFireBaseInstance(["profiles_pub", advisor.get("$id")]).update(d, function(error) {
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

    this.addEvaluatingPoint = function(advisor_id, post_id, onComplete) {
        var pts = $rootScope.config.advisor_point_earned_from_evaluating_post;
        this.addPoint(advisor_id, "evaluate", pts, {
            post: post_id
        }, function() {
            onComplete(pts);
        });
    }

    this.addPoint = function(advisor_id, action, points, meta, onComplete) {
        var ref = firebaseHelper.getFireBaseInstance(["profiles_pub", firebaseHelper.getUID()]);
        ref.once('value', function(){
            ref.transaction(function(recent){
                if (!recent) {
                    return;
                } else {
                    var advisor = new Advisor(recent);
                    recent = advisor
                        .set("points", (advisor.get('points') || 0) + (points || 0))
                        .getProperty();
                    return recent;
                }
            }, function(error, committed, snapshot) {
                if (snapshot) {
                    firebaseHelper.getFireBaseInstance(["log_advisor_points", firebaseHelper.getUID()]).push().set({
                        created_date: Date.now(),
                        action: action,
                        points: points,
                        meta: meta
                    }, function(error) {})
                }
                onComplete();
            });
        });
    }
});
