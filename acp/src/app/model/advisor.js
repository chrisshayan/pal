angular.module('inspinia').service('AdvisorService', function (firebaseHelper) {
    this.getAdvisorById = function(uid, callback) {
        firebaseHelper.getFireBaseInstance(["profiles_pub", uid]).once('value', function(snapshot) {
            var obj = snapshot.val();
            obj.$id = snapshot.key();
            if (callback) {
                callback(obj);
            }
        })
    }
});
