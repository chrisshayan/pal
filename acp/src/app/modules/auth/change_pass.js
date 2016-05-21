angular.module('inspinia').controller('ChangePassCtrl', function ($scope, $state, firebaseHelper) {
	$scope.email = "";
    $scope.password = "";
    $scope.new_password = "";
	$scope.new_password_confirm = "";
    $scope.ready = false;

	$scope.email = firebaseHelper.getAuthEmail();
	$scope.ready = (!(!$scope.email));
    $scope.$on("user:login", function() {
        $scope.ready = true;
        $scope.email = firebaseHelper.getAuthEmail();
    })

	$scope.onChangePassword = function() {
        firebaseHelper.updatePassword($scope.password, $scope.new_password, {
            success: function(data) {
                $state.go("index.tasks");
            }
        });
		return true;
	}

	$scope.goBack = function() {
		$state.go("index.tasks");
	}
});
