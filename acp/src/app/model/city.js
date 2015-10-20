angular.module('inspinia').service('CityService', function ($rootScope, firebaseHelper) {
    this.update = function(entity, onComplete) {
        var id = entity.get("$id");
        if (id) {
            firebaseHelper.getFireBaseInstance(["cities", entity.get("$id")]).update(entity.getProperty(), function(error) {
                if (onComplete) {onComplete(error);}
            });
        } else {
            firebaseHelper.getFireBaseInstance(["cities"]).push().set(entity.getProperty(), function(error) {
                if (onComplete) {onComplete(error);}
            });
        }
    }
});
