'use strict';

angular.module('inspinia').controller('CreatePostCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval) {
    $scope.addTopicTitle = "";
    $scope.addTopicAudioURL = "";
    $scope.onPost = function() {
        if (firebaseHelper.getUID()) {
            var topic = $scope.addTopicTitle;
            var url = $scope.addTopicAudioURL;
            if (topic && url) {
                firebaseHelper.pushItemOne("posts", "users", firebaseHelper.getUID(), {
                    created_date: Date.now(),
                    created_by: firebaseHelper.getUID(),
                    title: topic,
                    audio: url,
                    status: 0
                }, {
                    success: function() {
                        $rootScope.notifySuccess();
                        $scope.addTopicTitle = "";
                        $scope.addTopicAudioURL = "";
                        $scope.$apply();
                    }
                });
            } else {
                $rootScope.notifyError("Please fullfill the form");
            }
        } else {
            $rootScope.notifyError("Authenticaton failed. Please retry");
        }
        return true;
    }
});
