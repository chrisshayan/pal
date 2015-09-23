window.FIREBASE_URL = "https://flickering-fire-25.firebaseio.com";

angular.module('firebaseHelper', [])
.service('firebaseHelper', function($firebaseObject, $firebaseArray, $firebaseObject, $firebaseAuth, $rootScope, $state, notify) {
    var self = this;

    this.getFireBaseInstance = function(key) {
        return new Firebase(key?FIREBASE_URL + "/" + key:FIREBASE_URL);
    }

    this.bindObject = function(path, $scope, key) {
        console.log("bindObject", path);
        var syncObject = $firebaseObject(self.getFireBaseInstance(path));
        syncObject.$bindTo($scope, key);
    }

    this.syncObject = function(path) {
        console.log("syncObject", path);
        return $firebaseObject(self.getFireBaseInstance(path));
    }

    this.syncProtectedObject = function(path) {
        console.log("syncProtectedObject", path);
        return $firebaseObject(self.getFireBaseInstance(path + "/" + self.getUID()));
    }

    this.syncArray = function(path) {
        console.log("syncArray", path);
        return $firebaseArray(self.getFireBaseInstance(path));
    }

    this.syncProtectedArray = function(path) {
        console.log("syncArray", path + "/" + self.getUID());
        return $firebaseArray(self.getFireBaseInstance(path + "/" + self.getUID()));
    }

    this.auth = $firebaseAuth(self.getFireBaseInstance());
    this.authData = null;
    this.profileData = null;
    this.auth.$onAuth(function(authData) {
        console.log("$onAuth", authData);
        self.authData = authData;
        if (authData) {
            self.syncObject("profiles/" + self.getUID()).$loaded(
                function (data) {
                    self.profileData = data;
                    $rootScope.$broadcast('user:login',authData);
                    // if (data.role !== "admin") {
                    //     $state.go("login");
                    //     $rootScope.notifyError("Invalid permission");
                    // }
                },
                function (error) {
                    $rootScope.notifyError("Fail to get data");
                    $state.go("login");
                }
            )
        }
    });

    this.isAdmin = function() {
        return (this.profileData && this.profileData.role === "admin");
    }

    this.getUID = function() {
        if (this.authData && this.authData.uid) {
            return this.authData.uid;
        }
        return "";
    }

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
                $rootScope.notifyError("Invalid account");
                self.authData = null;
                if (callback.error) {callback.error(error);}
            });

    }
});
