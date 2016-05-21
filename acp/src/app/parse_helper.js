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
        var data = {
            alert: message
        }
        if (extra) {
            for (var k in extra) {
                data[k] = extra[k];
            }
        }
        var parse_data = {
            where: {
                user_id: to_user
            },
            data: data,
        };
        Parse.Push.send(parse_data, {
            success: function() {
            },
            error: function(error) {
                console.log(error);
            }
        });

    }
});
