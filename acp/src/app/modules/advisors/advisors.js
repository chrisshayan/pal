angular.module('inspinia').controller('AdvisorsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state ) {

    $scope.checked = {};
    $scope.nChecked = 0;
    $scope.isBan = true;
    $scope.search = "";

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
        var email = $scope.addUserEmail;
        var display_name = $scope.addUserDisplayName;
        if (email && display_name) {
            var password = md5(Date.now() + Math.random());
            firebaseHelper.getFireBaseInstance().createUser({
                email: email,
                password: password
            }, function(error, userData) {
                if (error) {
                    switch (error.code) {
                        case "EMAIL_TAKEN":
                            $rootScope.notifyError("The new user account cannot be created because the email is already in use.");
                            break;
                        case "INVALID_EMAIL":
                            $rootScope.notifyError("The specified email is not a valid email.");
                            break;
                        default:
                            $rootScope.notifyError("Error creating user: " + error);
                    }
                } else {
                    firebaseHelper.getFireBaseInstance(["profiles", userData.uid]).set({role: 'advisor'}, function(error) {
                        if (error) {
                            $rootScope.notifyError("Fail to create user profile " + error);
                            firebaseHelper.getFireBaseInstance().removeUser({email: email, password: password});
                        } else {
                            firebaseHelper.getFireBaseInstance(["profiles_pub", userData.uid]).set({
                                display_name: display_name,
                                email: email,
                                created_date: Date.now()
                            }, function(error) {
                                if (error) {
                                    $rootScope.notifyError("Fail to create user public profile " + error);
                                    firebaseHelper.getFireBaseInstance().removeUser({email: email, password: password});
                                } else {
                                    $rootScope.notifySuccess("Successfully created user account");
                                }
                            });
                        }
                    });
                }
            });
        }
        return true;
    }

    $scope.onCancelAddAdvisor = function() {
        $scope.addUserEmail = "";
        $scope.addUserDisplayName = "";
        $scope.addUserMode = false;
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
            alert(user.uid)
        } else {
            for (var k in $scope.checked) {
                alert(k);
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

        $scope.addUserMode = false;
        $scope.addUserEmail = "";
        $scope.addUserDisplayName = "";

        $scope.advisors = {};
        firebaseHelper.getFireBaseInstance("profiles").orderByChild("role").equalTo("advisor").on('value', function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                var uid = childSnapshot.key();
                var ban = childSnapshot.val().ban;
                firebaseHelper.getFireBaseInstance(["profiles_pub", uid]).on('value', function(snapshot2) {
                    var obj = snapshot2.val();
                    obj.uid = uid;
                    obj.ban = ban;
                    $scope.advisors[uid] = obj;
                    setTimeout(function() {
                        $scope.$digest();
                    })
                })
            });
        })
    }

    $scope.loading = true;
    if (firebaseHelper.getRole()) {
        init();
    } else {
        $scope.$on("user:login", function(data) {
            init();
        });
    }
})
