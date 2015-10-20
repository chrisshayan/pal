angular.module('inspinia').controller('LoginCtrl', function ($scope, $state, firebaseHelper, $timeout) {
	$scope.email = "";
	$scope.password = "";
	$scope.isForgotPasswordMode = false;
	$scope.$on("user:login", function(data) {
		$state.go("index.tasks");
	});
	
	$scope.onlogin = function() {
		if ($scope.isForgotPasswordMode) {
			firebaseHelper.resetPassword($scope.email, {
				success: function(data) {
					$scope.isForgotPasswordMode = false;
					$timeout(function() {
						$scope.$apply();
					}, 100);

				}
			});
		} else {
			firebaseHelper.login($scope.email, $scope.password);
		}
		return true;
	}
});
