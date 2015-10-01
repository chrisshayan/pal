angular.module('inspinia').controller('TopicsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state ) {

    $scope.onAddTopic = function() {
        var title = $scope.addTopicTitle;
        if (title) {
            firebaseHelper.getFireBaseInstance("topics").push().set({
                title: title,
                status: 1,
                created_date: Date.now(),
                created_by: firebaseHelper.getUID()
            }, function(error) {
                if (error) {
                    $rootScope.notifyError(error);
                } else {
                    $scope.addTopicTitle = "";
                    $rootScope.notifySuccess();
                }
            })
        }
        return true;
    }

    $scope.onCancelAddTopic = function() {
        $scope.addTopicTitle = "";
        $scope.addTopicMode = false;
    }

    $scope.onSetTopicStatus = function(id, status) {
        firebaseHelper.getFireBaseInstance(["topics", id]).update({
            status: status,
            last_modified_date: Date.now(),
            last_modified_by: firebaseHelper.getUID()
        }, function(error) {
            if (error) {
                $rootScope.notifyError(error);
            } else {
                $rootScope.notifySuccess();
            }
        })
    }

    var init = function() {
        if (firebaseHelper.getRole() != "admin") {
            $state.go("index.tasks");
            $rootScope.notifyError("Your access privileges do not allow you to perform this action");
            return;
        }

        $scope.addTopicMode = false;
        $scope.addTopicTitle = "";

        $scope.topics = firebaseHelper.syncArray("topics");
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
