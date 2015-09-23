angular.module('firebaseHelper', [])
.service('firebaseHelper', function($firebaseObject, $firebaseArray) {
    var self = this;
    this.getFireBaseInstance = function(key) {
        return new Firebase("https://flickering-fire-25.firebaseio.com/" + key);
    }

    this.bindObject = function($scope, key) {
        var syncObject = $firebaseObject(self.getFireBaseInstance(key));
        syncObject.$bindTo($scope, key);
    }

    this.syncArray = function(key) {
        return $firebaseArray(self.getFireBaseInstance(key));
    }
})
