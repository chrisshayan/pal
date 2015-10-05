angular.module('parseHelper', [])

.provider('parseHelperConfig', [function() {
    var data = {
        app_id: "",
        app_key: ""
    }
    this.init = function(app_id, app_key) {
        data.app_id = app_id;
        data.app_key = app_key;
    }
    this.$get = [function() {
        return data;
    }]
}])

.service('parseHelper', function(parseHelperConfig, $http) {
    var self = this;
    this.push = function(to_user, message) {
        $http({
            method: 'POST',
            url: 'https://api.parse.com/1/push',
            headers: {
                "X-Parse-Application-Id": parseHelperConfig.app_id,
                "X-Parse-REST-API-Key": parseHelperConfig.app_key,
                "Content-Type": "application/json"
            },
            data: {
                where: {
                    user_id: to_user
                },
                data: {
                    "alert": message
                }
            }
        }).then(function successCallback(response) {
        }, function errorCallback(response) {
        });
    }
});
