angular.module('inspinia')
.directive('customFinishedPost', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            ref: '=',
            group: '@'
        },
        controller: function($scope, firebaseHelper, $sce, $rootScope, cs) {
            $scope.isShowDetail = false;
            $scope.formatTime = cs.formatTime;
            $scope.formatDate = cs.formatDate;
            $scope.formatDateTime = cs.formatDateTime;

            $scope.data = $scope.ref;
            $scope.uid = firebaseHelper.getUID();

            $scope.user = firebaseHelper.getFireBaseInstance(["profiles_pub", $scope.data.created_by, "display_name"]).once('value', function(snapshot) {
                $scope.user_display_name = snapshot.val() || "Unknowned";
                setTimeout(function(){
                    $scope.$digest();
                }, 100);
            }, function() {});

            $scope.onOpenTask = function(id) {
                $scope.isShowDetail = !$scope.isShowDetail;
                if ($scope.isShowDetail) {
                    $rootScope.$broadcast('done_task:open', {id: $scope.ref.$id});
                }
            }
            $scope.$on("done_task:open", function(sender, data) {
                if (data.id != $scope.ref.$id) {
                    $scope.isShowDetail = false;
                }
            })
        },
        templateUrl: "app/partials/finished_post/finished_post.html"
    }
})
