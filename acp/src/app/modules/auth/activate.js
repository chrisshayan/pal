angular.module('inspinia').controller('ActivateCtrl', function ($scope, $rootScope, $state, $stateParams, $timeout, firebaseHelper, $uibModal, CityService, SchoolService, AdvisorService) {
	$scope.email = "";
    $scope.password = "";
    $scope.new_password = "";
	$scope.new_password_confirm = "";
    $scope.ready = false;
	$scope.profile = {}
    var token = $stateParams.token;
	$scope.user_profile = null;

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
		$scope.ready = false;
		firebaseHelper.getFireBaseInstance().changePassword({email: $scope.email, oldPassword: $scope.password, newPassword: $scope.new_password}, function(error) {
            if (!error) {
                firebaseHelper.getFireBaseInstance(["confirm_token", user_id]).remove();
                firebaseHelper.getFireBaseInstance(["profiles", user_id]).update({confirmed: true});

				AdvisorService.getAdvisorById(user_id, function(obj) {
					$scope.ready = true;
					$scope.user_profile = obj;
					$scope.openModal();
					$scope.$apply();
				});
            } else {
                $rootScope.notifyError(error);
            }
        });
		return true;
	}

	$scope.schools = {}
    SchoolService.getOnce($scope.schools, function() {
        $scope.$apply();
    });

	$scope.cities = {}
    CityService.getOnce($scope.cities, function() {
        $scope.$apply();
    });

	$scope.openModal = function() {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'activate_modal.html',
            controller: 'ActivateModalCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return {
                        data: $scope.user_profile,
                        schools: $scope.schools,
						cities: $scope.cities
                    }
                }
            }
        });
    }
});


angular.module('inspinia').controller('ActivateModalCtrl', function($rootScope, $scope, $timeout, $modalInstance, item, cs, firebaseHelper, AdvisorService, $state) {
    item = item || {};
    $scope.isProcessing = false;
	$scope.schools = item.schools;
    $scope.cities = item.cities;

    $timeout(function() {
        $scope.data = cs.purify(item.data || {});
    }, 500);

    $scope.onDone = function () {
        if (!$scope.isProcessing && $scope.data.first_name && $scope.data.last_name) {
            $scope.isProcessing = true;
            var obj = new Advisor(cs.purify($scope.data));
			obj.set('display_name', obj.get('first_name') + " " + obj.get('last_name'));
			obj.doModify(obj.get('$id'));
			AdvisorService.updateAdvisor(obj, function(error) {
				$scope.isProcessing = false;
				if (error) {
					$rootScope.notifyError(error);
				} else {
					$rootScope.notifySuccess();
					$modalInstance.close();
					$state.go("index.tasks");
				}
			});
        }
    };

    $scope.cancel = function () {
        $scope.isProcessing = false;
        $modalInstance.dismiss('cancel');
		$state.go("index.tasks");
    };
});
