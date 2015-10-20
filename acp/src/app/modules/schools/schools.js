angular.module('inspinia').controller('SchoolsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state, $uibModal) {
    $scope.sortedBy = "name";
    $scope.sortBy = function(key) {
        if ($scope.sortedBy === key) {
            $scope.sortedBy = "-" + key;
        } else {
            $scope.sortedBy = key;
        }
    }

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

        var ref = firebaseHelper.getFireBaseInstance("schools");
        ref.on('child_added', function(snapshot) {
            onRecordUpdate(snapshot, "add");
        });
        ref.on('child_changed', function(snapshot) {
            onRecordUpdate(snapshot, "changed");
        });

        $scope.cities = {}
        firebaseHelper.getFireBaseInstance("cities").once('value', function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                $scope.cities[childSnapshot.key()] = childSnapshot.val();
                $scope.cities[childSnapshot.key()].$id = childSnapshot.key();
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
            templateUrl: 'school_modal.html',
            controller: 'SchoolModalCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return {
                        data: $scope.data[id],
                        cities: $scope.cities
                    }
                }
            }
        });
    }
});

angular.module('inspinia').controller('SchoolModalCtrl', function($rootScope, $timeout, $scope, $modalInstance, item, cs, firebaseHelper, SchoolService) {
    item = item || {};
    $scope.cities = item.cities;
    $scope.isNew = true;
    $scope.isProcessing = false;

    $timeout(function() {
        $scope.data = cs.purify(item.data || {});
        if ($scope.data.$id) {
            $scope.isNew = false;
        }
    }, 500);

    $scope.onDone = function () {
        if (!$scope.isProcessing && $scope.data.name && $scope.data.address && $scope.data.city) {
            $scope.isProcessing = true;
            var obj = new School(cs.purify($scope.data));
            SchoolService.update(obj, function(error) {
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
