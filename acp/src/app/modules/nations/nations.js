angular.module('inspinia').controller('NationsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state, $uibModal) {
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

        var ref = firebaseHelper.getFireBaseInstance("nations");
        ref.on('child_added', function(snapshot) {
            onRecordUpdate(snapshot, "add");
        });
        ref.on('child_changed', function(snapshot) {
            onRecordUpdate(snapshot, "changed");
        });
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
            templateUrl: 'nation_modal.html',
            controller: 'NationModalCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return $scope.data[id];
                }
            }
        });
    }
});

angular.module('inspinia').controller('NationModalCtrl', function($rootScope, $scope, $modalInstance, item, cs, firebaseHelper, NationService) {
    $scope.data = cs.purify(item || {});
    $scope.isNew = true;
    $scope.isProcessing = false;
    if ($scope.data.$id) {
        $scope.isNew = false;
    }
    $scope.onDone = function () {
        if (!$scope.isProcessing && $scope.data.en && $scope.data.vi) {
            $scope.isProcessing = true;
            var obj = new Nation(cs.purify($scope.data));
            NationService.update(obj, function(error) {
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
