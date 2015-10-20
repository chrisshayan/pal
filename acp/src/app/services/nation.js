angular.module('inspinia').service('NationService', function ($rootScope, firebaseHelper) {
    this.update = function(entity, onComplete) {
        entity.doCreateOrModify(firebaseHelper.getUID());
        var id = entity.get("$id");
        if (id) {
            firebaseHelper.getFireBaseInstance(["nations", entity.get("$id")]).update(entity.getProperty(), function(error) {
                if (onComplete) {onComplete(error);}
            });
        } else {
            firebaseHelper.getFireBaseInstance(["nations"]).push().set(entity.getProperty(), function(error) {
                if (onComplete) {onComplete(error);}
            });
        }
    }
});
