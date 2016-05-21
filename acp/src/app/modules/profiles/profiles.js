angular.module('inspinia').controller('ProfileCtrl', function ($scope, firebaseHelper, $rootScope, cs, $interval, $state, AdvisorService, SchoolService, CityService) {
    $scope.loading = true;
    $scope.NO_AVATAR = NO_AVATAR;

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

    $scope.onChangeAvatar = function() {
        cloudinary.openUploadWidget({
            upload_preset: 'pal_avatar',
            multiple: 'false',
            cropping: 'server',
            cropping_aspect_ratio: "1.0",
            cropping_default_selection_ratio: "1.0",
            max_file_size: "2097152"
        }, function(error, result) {
            if (error) {
                $rootScope.notifyError(error);
            } else {
                var coordinates = result[0].coordinates;
                var url = result[0].secure_url;
                var path = result[0].path;
                if (coordinates && coordinates.custom && coordinates.custom[0]) {
                    coordinates = coordinates.custom[0];
                    var alt_path = "c_crop,g_custom,x_" + coordinates[0] + ",y_" + coordinates[1] + ",w_" + coordinates[2] + ",h_" + coordinates[3] + "/" + path;
                    url = url.replace(path, alt_path)
                }
                $scope.profile.avatar = url;
                $scope.$apply();
            }
            // console.log(error, result)
        });
    }

    var init = function() {
        AdvisorService.getAdvisorById(firebaseHelper.getUID(), function(data) {
            $scope.profile = data;
            $scope.loading = false;
            $scope.role = firebaseHelper.getRole();
            setTimeout(function() {
                $scope.$apply();
            });
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
