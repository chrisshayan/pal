angular.module('inspinia').service('MailService', function ($rootScope, firebaseHelper, cs) {
    this.send = function(type, data) {
        data.type = type;
        firebaseHelper.getFireBaseInstance(["mail_queue", "tasks"]).push().set(cs.purify(data));
    };

    this.sendAdvisorGreeting = function(to, link) {
        this.send("advisor_greeting", {to: to, link: link});
    }
});
