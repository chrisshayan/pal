'use strict';

angular.module('inspinia').controller('MainCtrl', function ($scope, firebaseHelper, $rootScope, cs) {
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
        var topic = $scope.addTopicTitle;
        var url = $scope.addTopicAudioURL;
        firebaseHelper.pushItemOne("posts", "users", firebaseHelper.getUID(), {
            created_date: Date.now(),
            created_by: firebaseHelper.getUID(),
            title: topic,
            audio: url,
            status: 0
        }, {
            success: function() {
                $scope.addTopicTitle = "";
                $scope.addTopicAudioURL = "";
                $scope.$apply();
            }
        });
        return true;
    }
});
