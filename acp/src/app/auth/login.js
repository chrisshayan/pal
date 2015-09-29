'use strict';

angular.module('inspinia').controller('LoginCtrl', function ($scope, $state, firebaseHelper) {
	$scope.email = "admin01@email.com";
	$scope.password = "1234";
	$scope.onlogin = function() {
        firebaseHelper.login($scope.email, $scope.password, {
            success: function(data) {
                $state.go("index.main");
            }
        });
		return true;
	}
});
