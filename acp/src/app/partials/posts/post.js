angular.module('inspinia')
.directive('customPost', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            ref: '='
        },
        controller: function($scope, firebaseHelper, $sce, $rootScope, cs, $http, parseHelper) {
            $scope.isReady = false;
            $scope.formatTime = cs.formatTime;
            $scope.formatDate = cs.formatDate;
            $scope.formatDateTime = cs.formatDateTime;
            $scope.data = $scope.ref;

            if ($scope.data && $scope.data.created_by) {
                $scope.user = firebaseHelper.getFireBaseInstance(["profiles_pub", $scope.data.created_by, "display_name"]).once('value', function(snapshot) {
                    $scope.user_display_name = snapshot.val() || "Unknown";
                    setTimeout(function(){
                        $scope.isReady = true;
                        $scope.$digest();
                    }, 100);
                }, function() {});
            }
        },
        templateUrl: "app/partials/posts/post.html"
    }
})
