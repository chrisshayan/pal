'use strict';

angular.module('inspinia').controller('CreatePostCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval) {

    var post = new Post();
    var init = function() {
        $scope.post = post.data;
    }
    init();

    $scope.onPost = function() {
        if (firebaseHelper.getUID()) {
            if ($scope.post.title) {
                post.set("status", PostStatus.Ready).doCreate(firebaseHelper.getUID());
                firebaseHelper.getFireBaseInstance("posts").push().set(cs.purify($scope.post), function(error) {
                    if (!error) {
                        $rootScope.notifySuccess();
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
