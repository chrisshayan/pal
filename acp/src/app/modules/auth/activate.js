angular.module('inspinia').controller('ActivateCtrl', function ($scope, $rootScope, $state, $stateParams, $timeout, firebaseHelper) {
	$scope.email = "";
    $scope.password = "";
    $scope.new_password = "";
	$scope.new_password_confirm = "";
    $scope.ready = false;
    var token = $stateParams.token;

    if (!token) {
        $rootScope.notifyError("No token found");
        $timeout(function() {
            $state.go("index.tasks");
        }, 3000);
        return;
    }

    firebaseHelper.getFireBaseInstance(["confirm_token", token]).once('value', function(snapshot) {
        var val = snapshot.val();
        if (val == null) {
            $rootScope.notifyError("Invalid token");
            $timeout(function() {
                $state.go("index.tasks");
            }, 3000);
            return;
        } else {
            $scope.email = val.email;
            $scope.password = val.password;
            $scope.new_password = "";
        	$scope.new_password_confirm = "";
            $scope.ready = true;
            $scope.$apply();
        }
    });

	$scope.onChangePassword = function() {
        firebaseHelper.getFireBaseInstance().changePassword({email: $scope.email, oldPassword: $scope.password, newPassword: $scope.new_password}, function(error) {
            if (!error) {
                firebaseHelper.getFireBaseInstance(["confirm_token", token]).remove();
                firebaseHelper.getFireBaseInstance(["profiles", token]).update({confirmed: true});
                $state.go("index.tasks");
            } else {
                $rootScope.notifyError(error);
            }
        });
		return true;
	}
});
