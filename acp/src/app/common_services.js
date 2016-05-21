angular.module('cs', []).service('cs', function () {
    this.formatDateTime = function(input) {
        if (!input) return "";
        var date = new Date(input)
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        var hour = date.getHours();
        var minute = date.getMinutes();
        var sec = date.getSeconds();
        if (month < 10) {month = "0" + month};
        if (day < 10) {day = "0" + day};
        if (hour < 10) {hour = "0" + hour};
        if (minute < 10) {minute = "0" + minute};
        if (sec < 10) {sec = "0" + sec};
        return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + sec;
    };

    this.formatDate = function(input) {
        var date = new Date(input)
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        if (month < 10) {month = "0" + month};
        if (day < 10) {day = "0" + day};
        return year + "/" + month + "/" + day;
    };

    this.formatTime = function(input) {
        var date = new Date(input)
        var hour = date.getHours();
        var minute = date.getMinutes();
        var sec = date.getSeconds();
        if (hour < 10) {hour = "0" + hour};
        if (minute < 10) {minute = "0" + minute};
        if (sec < 10) {sec = "0" + sec};
        return hour + ":" + minute + ":" + sec;
    };

    this.purify = function(input) {
        var obj = JSON.parse(angular.toJson(input));
        return obj;
    }
});
