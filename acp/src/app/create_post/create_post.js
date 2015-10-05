'use strict';

angular.module('inspinia').controller('CreatePostCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval) {


    var init = function() {
        $scope.post = {
            title: "",
            audio: "",
            text: "",
            type: 0, //audio
            prev: 0,
            status: 0 //pending
        }
    }
    init();

    $scope.onPost = function() {
        if (firebaseHelper.getUID()) {
            $scope.post.created_date = Date.now();
            $scope.post.created_by = firebaseHelper.getUID();
            if ($scope.post.title) {
                firebaseHelper.pushItemOne("posts", "users", firebaseHelper.getUID(), cs.purify($scope.post), {
                    success: function() {
                        $rootScope.notifySuccess();
                        init();
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
