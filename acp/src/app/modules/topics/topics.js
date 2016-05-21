angular.module('inspinia').controller('TopicsCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval,$state ) {

    $scope.parseInt = parseInt;

    $scope.onAddTopic = function() {
        if ($scope.newTopic.title) {
            $scope.newTopic.created_date = Date.now();
            $scope.newTopic.created_by = firebaseHelper.getUID();
            firebaseHelper.getFireBaseInstance("topics").push().set(cs.purify($scope.newTopic), function(error) {
                if (error) {
                    $rootScope.notifyError(error);
                } else {
                    $scope.initNewData();
                    $rootScope.notifySuccess();
                }
            })
        }
        return true;
    }

    $scope.onCancelAddTopic = function() {
        $scope.initNewData();
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

    $scope.initNewData = function(){
        $scope.addTopicMode = false;
        $scope.newTopic = {
            title: "",
            type: 0,
            status: 1,
            level: 0
        }
    }
    var init = function() {
        if (firebaseHelper.getRole() != "admin") {
            $state.go("index.tasks");
            $rootScope.notifyError("Your access privileges do not allow you to perform this action");
            return;
        }
        $scope.initNewData();
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
