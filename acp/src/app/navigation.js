'use strict';

angular.module('inspinia')

.controller('NavCtrl', function ($scope, firebaseHelper, $timeout) {
    $scope.email = firebaseHelper.getAuthEmail();
    $scope.$on('user:login', function(event,data) {
        $scope.email = firebaseHelper.getAuthEmail();
    });

    $timeout(function() {
        if (!firebaseHelper.getAuthEmail()) {
            firebaseHelper.logout();
        }
    }, 500);

    $scope.onLogout = function() {
        firebaseHelper.logout();
    }
})
.controller('TopNavCtrl', function ($scope, firebaseHelper) {
    $scope.onLogout = function() {
        firebaseHelper.logout();
    }
})
;
