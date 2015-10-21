'use strict';

angular.module('inspinia').controller('TasksCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval) {
    $scope.loading = true;
    $scope.hasFullfillProfile = false;

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

    var init = function() {
        var pubProfile = firebaseHelper.getPublicProfile();
        $scope.hasFullfillProfile = firebaseHelper.getRole() == 'admin' ||
            (
                pubProfile.first_name && pubProfile.last_name && pubProfile.avatar && pubProfile.exp
            );

        $scope.newPosts = firebaseHelper.syncArray(
            firebaseHelper
                .getFireBaseInstance("posts")
                .orderByChild("status")
                .equalTo(PostStatus.Ready)
                .limitToFirst(5));

        $scope.completedPosts = firebaseHelper.syncArray(
            firebaseHelper
                .getFireBaseInstance("posts")
                .orderByChild("index_advisior_status")
                .startAt(PostHelper.buildIndex(firebaseHelper.getUID(), PostStatus.AdvisorProcessing + 1))
                .endAt(PostHelper.buildIndex(firebaseHelper.getUID(), PostStatus.AdvisorProcessing + 1)));
        $scope.loading = false;
    }
    if (firebaseHelper.hasAlreadyLogin()) {
        init();
    } else {
        $scope.$on("user:login", function() {
            init();
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

        firebaseHelper.getFireBaseInstance(["posts"]).orderByChild("status").equalTo(PostStatus.Ready).limitToFirst(1).once('value', function(snapshot) {
            var numChildren = snapshot.numChildren();
            if (numChildren === 0) {
                $rootScope.notifyError("No more task available. Please try again later");
                $scope.startCountDown(15);
                return;
            }
            snapshot.forEach(function(childSnapshot) {
                var key = childSnapshot.key();
                var childData = childSnapshot.val();
                if (childData.status === PostStatus.Ready) {
                    firebaseHelper.getFireBaseInstance(["posts", key]).transaction(function(current) {
                        if (!current) {
                            $rootScope.notifyError("Something wrong. Please try again");
                            $scope.startCountDown(5);
                            return;
                        } else {
                            if (current.status === PostStatus.Ready) {
                                current = new Post(current)
                                    .set("status", PostStatus.AdvisorProcessing)
                                    .set("has_read", false)
                                    .set("advisor_id", uid)
                                    .doModify(uid)
                                    .get();
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
