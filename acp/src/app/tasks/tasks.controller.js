'use strict';

angular.module('inspinia').controller('TasksCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval) {
    $scope.formatTime = cs.formatTime;
    $scope.formatDate = cs.formatDate;
    $scope.formatDateTime = cs.formatDateTime;
    $scope.newPosts = null;

    $scope.pickTaskCountDown = 0;

    $scope.stat = firebaseHelper.getFireBaseInstance(["posts"]).orderByChild("status").equalTo(PostStatus.Ready).on('value', function(snapshot) {
        $scope.numOfWaitingPost = snapshot.numChildren();
        setTimeout(function() {
            $scope.$digest();
        }, 100);
    });

    if (firebaseHelper.getRole()) {
        $scope.newPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["ref_advisor_posts", firebaseHelper.getUID()]).orderByValue().equalTo(1));
        $scope.completedPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["ref_advisor_posts", firebaseHelper.getUID()]).orderByValue().equalTo(2));
    } else {
        $scope.$on("user:login", function() {
            $scope.newPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["ref_advisor_posts", firebaseHelper.getUID()]).orderByValue().equalTo(1));
            $scope.completedPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["ref_advisor_posts", firebaseHelper.getUID()]).orderByValue().equalTo(2));
        })
    }

    var stop;

    $scope.startCountDown = function(n) {
        $scope.pickTaskCountDown = n;
        if ( !angular.isDefined(stop) ) {
            stop = $interval(function() {
               if ($scope.pickTaskCountDown > 0) {
                   $scope.pickTaskCountDown = $scope.pickTaskCountDown - 1;
               }
               if ($scope.pickTaskCountDown <= 0) {
                   $scope.pickTaskCountDown = 0;
                   stop = undefined;
               }
           }, 1000);
        }
    }

    $scope.onPickTask = function() {
        var uid = firebaseHelper.getUID();
        if (!uid) {
            console.log("no auth yet");
            $rootScope.notifyError("Something wrong. Please try again");
            $scope.startCountDown(5);
            return;
        }

        firebaseHelper.getFireBaseInstance(["posts"]).orderByChild("status").equalTo(0).limitToLast(1).once('value', function(snapshot) {
            var numChildren = snapshot.numChildren();
            if (numChildren === 0) {
                $rootScope.notifyError("No more task available. Please try again later");
                $scope.startCountDown(15);
                return;
            }
            snapshot.forEach(function(childSnapshot) {
                var key = childSnapshot.key();
                var childData = childSnapshot.val();
                if (childData.status === 0) {
                    firebaseHelper.getFireBaseInstance(["posts", key]).transaction(function(current) {
                        if (!current) {
                            $rootScope.notifyError("Something wrong. Please try again");
                            $scope.startCountDown(5);
                            return;
                        } else {
                            if (current.status === 0) {
                                current.status = 1;
                                current.picked_date = Date.now();
                                current.picked_by = uid;
                            }
                        }
                        return current;
                    }, function(error, committed, snapshot) {
                        if (error) {
                            $rootScope.notifyError('Transaction failed abnormally! ' + error);
                            $scope.startCountDown(5);
                        } else if (!committed) {
                            $rootScope.notifyError("No valid data found. Please try again");
                            $scope.startCountDown(5);
                        } else {
                            $rootScope.notifySuccess("You have got a task");
                            $scope.startCountDown(15);
                            firebaseHelper.getFireBaseInstance(["ref_advisor_posts", uid, key]).set(snapshot.val().status);
                        }
                    });
                }
                return;
            });
        }, function(error) {
            console.log(error);
            $rootScope.notifyError("Something wrong. Please try again");
            $scope.startCountDown(5);
        });
    }
});
