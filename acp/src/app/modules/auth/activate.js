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

	var checksum_key = token.substring(0,32);
	var checksum = token.substring(32,64);
	var user_id = token.substring(64,token.length);

    firebaseHelper.getFireBaseInstance(["confirm_token", user_id]).once('value', function(snapshot) {
        var val = snapshot.val();
        if (val == null) {
            $rootScope.notifyError("Invalid token");
            $timeout(function() {
                $state.go("error");
            }, 1000);
            return;
        } else {
			var data_checksum = md5(val.email + val.password + checksum_key);
			if (data_checksum == val.checksum && data_checksum == checksum) {
				$scope.email = val.email;
	            $scope.password = val.password;
	            $scope.new_password = "";
	        	$scope.new_password_confirm = "";
	            $scope.ready = true;
	            $scope.$apply();
			} else {
				$rootScope.notifyError("Token checksum failure");
				$timeout(function() {
	                $state.go("error");
	            }, 1000);
			}
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
