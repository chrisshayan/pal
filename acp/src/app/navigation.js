'use strict';

angular.module('inspinia')

.controller('NavCtrl', function ($scope, firebaseHelper, $timeout, $state, $rootScope) {
    $scope.info = {
        email: firebaseHelper.getAuthEmail(),
        role: firebaseHelper.getRole()
    }

    var scope = $scope;
    $rootScope.$on("user:login", function() {
        $scope.info.email = firebaseHelper.getAuthEmail();
        $scope.info.role = firebaseHelper.getRole();
    })

    $scope.onLogout = function() {
        firebaseHelper.logout();
    }

    $scope.onChangePassword = function() {
        $state.go("change_pass");
    }
})
.controller('TopNavCtrl', function ($scope, firebaseHelper, $rootScope) {
    $scope.onLogout = function() {
        firebaseHelper.logout();
    }

    $scope.onSelectGroup = function(r) {
        $rootScope.currentGroup = r;
        localStorage.setItem("lastOpenedGroup", r);
    }
})
;
