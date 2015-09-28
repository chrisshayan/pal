'use strict';

angular.module('inspinia')
    .controller('MainCtrl', function ($scope, firebaseHelper, $rootScope, cs) {
        $scope.formatTime = cs.formatTime;
        $scope.formatDate = cs.formatDate;
        $scope.formatDateTime = cs.formatDateTime;
        $scope.newPosts = null;

        $scope.$on("user:login", function() {
            $scope.newPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["posts"]).orderByChild("status").equalTo(0));
            $scope.completedPosts = firebaseHelper.syncArray(firebaseHelper.getFireBaseInstance(["posts"]).orderByChild("status").equalTo(1));
        })

        $scope.addTopicTitle = "";
        $scope.addTopicAudioURL = "";
        $scope.onPost = function() {
            firebaseHelper.pushItemOne("posts", "users", firebaseHelper.getUID(), {
                createdDate: Date.now(),
                createdBy: firebaseHelper.getUID(),
                title: $scope.addTopicTitle,
                audio: $scope.addTopicAudioURL,
                status: 0
            }, {
                success: function() {
                    $scope.addTopicTitle = "";
                    $scope.addTopicAudioURL = "";
                }
            });
            return true;
        }
    });
