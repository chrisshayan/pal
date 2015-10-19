angular.module('inspinia').controller('AdvisorsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state, $uibModal, AdvisorService) {

    $scope.checked = {};
    $scope.nChecked = 0;
    $scope.isBan = true;

    $scope.onSelectAdvisor = function() {
        var tmp = 0;
        $scope.isBan = false;
        for (var k in $scope.checked) {
            if ($scope.checked[k]) {
                tmp++;
                if (!$scope.advisors[k].ban == true) {
                    $scope.isBan = true;
                }
            }
        }
        $scope.nChecked = tmp;
    }

    $scope.onBanUsers = function() {
        for (var k in $scope.checked) {
            if ($scope.checked[k]) {
                firebaseHelper.getFireBaseInstance(["profiles", k]).update({
                    ban: true,
                    ban_date: Date.now()
                }, function(error) {
                    $scope.onSelectAdvisor();
                    $scope.$apply();
                })
            }
        }
    }

    $scope.onUnbanUsers = function() {
        for (var k in $scope.checked) {
            if ($scope.checked[k]) {
                firebaseHelper.getFireBaseInstance(["profiles", k]).update({
                    ban: false,
                    ban_date: 0
                }, function(error) {
                    $scope.onSelectAdvisor();
                    $scope.$apply();
                })
            }
        }
    }

    $scope.onAddAdvisor = function() {
        $scope.openAdvisorModal();
    }

    $scope.onResetPassword = function() {
        if ($scope.nChecked == 1) {
            for (var k in $scope.checked) {
                if ($scope.checked[k]) {
                    firebaseHelper.resetPassword($scope.advisors[k].email, {
                        success: function(data) {
                            $rootScope.notifySuccess();
                        },
                        error: function() {
                            $rootScope.notifyError();
                        }
                    });
                }
                break;
            }
        }
    }

    $scope.onEditUser = function(user) {
        if (typeof(user)!="undefined" && user) {
            $scope.openAdvisorModal(user.$id);
        } else {
            for (var k in $scope.checked) {
                $scope.openAdvisorModal(k);
                break;
            }
        }
    }

    var init = function() {
        if (firebaseHelper.getRole() != "admin") {
            $state.go("index.tasks");
            $rootScope.notifyError("Your access privileges do not allow you to perform this action");
            return;
        }

        $scope.advisors = {};

        var onRecordUpdate = function(snapshot, mode) {
            var uid = snapshot.key();
            var ban = snapshot.val().ban;
            AdvisorService.getAdvisorById(uid, function(data) {
                $scope.advisors[uid] = data;
                data.ban = ban;
                setTimeout(function(id, mode) {
                    $scope.$digest();
                    $(id).addClass(mode == "add"?'text-info':'text-warning');
                    setTimeout(function(id, mode) {
                        $(id).removeClass(mode == "add"?'text-info':'text-warning');
                    }, 2000, id, mode);
                }, 100, "#advisor_" + data.$id, mode);
            });
        }

        var ref = firebaseHelper.getFireBaseInstance("profiles").orderByChild("role").equalTo("advisor");
        ref.on('child_added', function(snapshot) {
            onRecordUpdate(snapshot, "add");
        });
        ref.on('child_changed', function(snapshot) {
            onRecordUpdate(snapshot, "changed");
        });
    }

    $scope.loading = true;
    if (firebaseHelper.getRole()) {
        init();
    } else {
        $scope.$on("user:login", function(data) {
            init();
        });
    }

    $scope.openAdvisorModal = function(id) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'advisor_modal.html',
            controller: 'AdvisorModalCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return $scope.advisors[id];
                }
            }
        });
    }
});

angular.module('inspinia').controller('AdvisorModalCtrl', function($rootScope, $scope, $modalInstance, item, cs, firebaseHelper, AdvisorService) {
    $scope.advisor = cs.purify(item || {});
    $scope.isNewUser = true;
    $scope.isProcessing = false;
    if ($scope.advisor.$id) {
        $scope.isNewUser = false;
    }
    $scope.onDone = function () {
        if (!$scope.isProcessing && $scope.advisor.email && $scope.advisor.display_name) {
            $scope.isProcessing = true;
            var obj = new Advisor(cs.purify($scope.advisor));
            var email = obj.get('email');
            if ($scope.isNewUser) {
                var password = md5(Date.now() + Math.random());
                firebaseHelper.getFireBaseInstance().createUser({
                    email: email,
                    password: password
                }, function(error, userData) {
                    if (error) {
                        $rootScope.notifyError(error);
                        $scope.isProcessing = false;
                    } else {
                        firebaseHelper.getFireBaseInstance(["profiles", userData.uid]).set({role: 'advisor'}, function(error) {
                            if (error) {
                                $scope.isProcessing = false;
                                $rootScope.notifyError("Fail to create user profile " + error);
                                firebaseHelper.getFireBaseInstance().removeUser({email: email, password: password}, function(){});
                            } else {
                                obj.set('$id', userData.uid);
                                obj.doCreate(firebaseHelper.getUID());
                                AdvisorService.updateAdvisor(obj, function(error) {
                                    if (error) {
                                        $rootScope.notifyError("Fail to create user public profile " + error);
                                        firebaseHelper.getFireBaseInstance().removeUser({email: email, password: password}, function(){});
                                    } else {
                                        $rootScope.notifySuccess("Successfully created user account");
                                        $modalInstance.close();
                                    }
                                    $scope.isProcessing = false;
                                });
                            }
                        });
                    }
                });
            } else {
                obj.doModify(firebaseHelper.getUID());
                AdvisorService.updateAdvisor(obj, function() {
                    $scope.isProcessing = false;
                    $modalInstance.close();
                });
            }
        }
    };

    $scope.cancel = function () {
        $scope.isProcessing = false;
        $modalInstance.dismiss('cancel');
    };
});
