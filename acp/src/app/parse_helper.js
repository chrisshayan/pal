angular.module('parseHelper', [])

.provider('parseHelperConfig', [function() {
    var data = {
        app_id: "",
        app_key: ""
    }
    this.init = function(app_id, app_key) {
        data.app_id = app_id;
        data.app_key = app_key;
        Parse.initialize(app_id, app_key);
    }
    this.$get = [function() {
        return data;
    }]
}])

.service('parseHelper', function(parseHelperConfig, $http) {
    var self = this;
    this.push = function(to_user, message, extra) {
        // $http({
        //     method: 'POST',
        //     url: 'https://api.parse.com/1/push',
        //     headers: {
        //         "X-Parse-Application-Id": parseHelperConfig.app_id,
        //         "X-Parse-REST-API-Key": parseHelperConfig.app_key,
        //         "Content-Type": "application/json"
        //     },
        //     data: {
        //         where: {
        //             user_id: to_user
        //         },
        //         data: {
        //             "alert": message
        //         }
        //     }
        // }).then(function successCallback(response) {
        // }, function errorCallback(response) {
        // });
        var data = {
            alert: message
        }
        if (extra) {
            for (var k in extra) {
                data[k] = extra[k];
            }
        }
        Parse.Push.send({
            where: {
                user_id: to_user
            },
            data: data,
        }, {
            success: function() {
            },
            error: function(error) {
                // Handle error
            }
        });

    }
});
