angular.module('inspinia').service('CityService', function ($rootScope, firebaseHelper) {
    this.update = function(entity, onComplete) {
        entity.doCreateOrModify(firebaseHelper.getUID());
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

    this.getOnce = function(schoolsRef, callback) {
        firebaseHelper.getFireBaseInstance("cities").once('value', function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                schoolsRef[childSnapshot.key()] = childSnapshot.val();
                schoolsRef[childSnapshot.key()].$id = childSnapshot.key();
            })
            if (callback) {callback();}
        })
    }
});
