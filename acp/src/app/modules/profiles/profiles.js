angular.module('inspinia').controller('ProfileCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval, $state, AdvisorService, SchoolService, CityService) {
    $scope.loading = true;

    $scope.schools = {}
    SchoolService.getOnce($scope.schools, function() {
        $scope.$apply();
    });

	$scope.cities = {}
    CityService.getOnce($scope.cities, function() {
        $scope.$apply();
    });


    $scope.onPost = function() {
        $scope.loading = true;
        var obj = new Advisor(cs.purify($scope.profile));
        obj.doModify(firebaseHelper.getUID());
        AdvisorService.updateAdvisor(obj, function(error) {
            $scope.loading = false;
            if (error) {
                $rootScope.notifyError(error);
            } else {
                $rootScope.notifySuccess();
            }
            $scope.$apply();
        });

        return true;
    }


    var init = function() {
        AdvisorService.getAdvisorById(firebaseHelper.getUID(), function(data) {
            $scope.profile = data;
            $scope.loading = false;
            $scope.role = firebaseHelper.getRole();
            $scope.$apply();
        })
    }

    if (firebaseHelper.getRole()) {
        init();
    } else {
        $scope.$on("user:login", function(data) {
            init();
        });
    }
});
