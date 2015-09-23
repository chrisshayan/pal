angular.module('firebaseHelper', [])
.service('firebaseHelper', function($firebaseObject, $firebaseArray, $firebaseAuth, $rootScope, $state) {
    var self = this;

    this.getFireBaseInstance = function(key) {
        var base = "https://flickering-fire-25.firebaseio.com";
        return new Firebase(key?base + "/" + key:base);
    }

    this.bindObject = function($scope, key) {
        var syncObject = $firebaseObject(self.getFireBaseInstance(key));
        syncObject.$bindTo($scope, key);
    }

    this.syncArray = function(key) {
        return $firebaseArray(self.getFireBaseInstance(key));
    }

    this.auth = $firebaseAuth(self.getFireBaseInstance());
    this.authData = null;
    this.auth.$onAuth(function(authData) {
        self.authData = authData;
        $rootScope.$broadcast('user:login',authData);
        console.log("$onAuth", authData);
    });

    this.hasAlreadyLogin = function() {
        return this.authData != null;
    }

    this.getAuthEmail = function() {
        if (this.authData) {
            if (this.authData.password && this.authData.password.email) {
                return this.authData.password.email;
            }
        }
        return "";
    }

    this.logout = function() {
        self.auth.$unauth();
        self.authData = null;
        $state.go("login");
    }

    this.login = function(email, password, callback) {
        callback = callback || {};
        self.auth.$authWithPassword({email: email, password: password})
            .then(function(authData) {
                self.authData = authData;
                if (callback.success) {callback.success(authData);}
            })
            .catch(function(error) {
                self.authData = null;
                if (callback.error) {callback.error(error);}
            });

    }
})
