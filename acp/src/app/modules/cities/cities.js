angular.module('inspinia').controller('CitiesCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state, $uibModal) {
    $scope.onAdd = function() {
        $scope.openModal();
    }

    $scope.onEdit = function(id) {
        $scope.openModal(id);
    }

    var init = function() {
        if (firebaseHelper.getRole() != "admin") {
            $state.go("index.tasks");
            $rootScope.notifyError("Your access privileges do not allow you to perform this action");
            return;
        }

        $scope.data = {};
        var onRecordUpdate = function(snapshot, mode) {
            var key = snapshot.key();
            var value = snapshot.val()
            $scope.data[key] = value;
            $scope.data[key].$id = key;
            setTimeout(function(key, mode) {
                $scope.$digest();
                $(key).addClass(mode == "add"?'text-info':'text-warning');
                setTimeout(function(key, mode) {
                    $(key).removeClass(mode == "add"?'text-info':'text-warning');
                }, 2000, key, mode);
            }, 100, "#item_" + key, mode);
        }

        var ref = firebaseHelper.getFireBaseInstance("cities");
        ref.on('child_added', function(snapshot) {
            onRecordUpdate(snapshot, "add");
        });
        ref.on('child_changed', function(snapshot) {
            onRecordUpdate(snapshot, "changed");
        });

        $scope.nations = {}
        firebaseHelper.getFireBaseInstance("nations").once('value', function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                $scope.nations[childSnapshot.key()] = childSnapshot.val();
                $scope.nations[childSnapshot.key()].$id = childSnapshot.key();
            })
            $scope.$apply();
        })
    }

    if (firebaseHelper.getRole()) {
        init();
    } else {
        $scope.$on("user:login", function(data) {
            init();
        });
    }

    $scope.openModal = function(id) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'city_modal.html',
            controller: 'CityModalCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return {
                        data: $scope.data[id],
                        nations: $scope.nations
                    }
                }
            }
        });
    }
});

angular.module('inspinia').controller('CityModalCtrl', function($rootScope, $scope, $modalInstance, item, cs, firebaseHelper, CityService) {
    item = item || {};
    $scope.nations = item.nations;
    $scope.isNew = true;
    $scope.isProcessing = false;

    setTimeout(function() {
        $scope.data = cs.purify(item.data || {});
        if ($scope.data.$id) {
            $scope.isNew = false;
        }
        $scope.$apply();
    }, 500);

    $scope.onDone = function () {
        if (!$scope.isProcessing && $scope.data.en && $scope.data.vi && $scope.data.nation) {
            $scope.isProcessing = true;
            var obj = new City(cs.purify($scope.data));
            CityService.update(obj, function(error) {
                if (error) {
                    $rootScope.notifyError(error);
                } else {
                    $rootScope.notifySuccess();
                    $modalInstance.close();
                }
                $scope.isProcessing = false;
            })
        }
    };

    $scope.cancel = function () {
        $scope.isProcessing = false;
        $modalInstance.dismiss('cancel');
    };
});
