angular.module('inspinia')
.directive('checkbox', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            ngId: '@',
            ngModel: '=',
            ngChanged: '&'
        },
        controller: function($scope, $timeout) {
            $scope.onClick = function() {
                if (typeof($scope.ngModel) == 'undefined') {
                    $scope.ngModel = true;
                } else {
                    $scope.ngModel = !$scope.ngModel;
                }
                $timeout(function() {
                    $scope.ngChanged($scope.ngId, $scope.ngModel);
                }, 100);
            };
        },
        templateUrl: "app/partials/checkbox/checkbox.html"
    }
});
