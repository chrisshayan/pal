'use strict';

angular.module('inspinia', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngResource', 'ui.router', 'ui.bootstrap', 'firebase', 'firebaseHelper', 'cgNotify', 'cs', 'parseHelper'])

.config(function ($stateProvider, $urlRouterProvider, firebaseHelperConfigProvider, parseHelperConfigProvider) {
    firebaseHelperConfigProvider.setURL("https://pal-dev.firebaseio.com");
    parseHelperConfigProvider.init("WRcgKehX6zd2idIpSUj6GGmwtcMipq7Y0tXzwJ2s", "1ORxyTaVj2qzjtFuJS7dMuIgRDnbhhOSo9FIDxV6");

    $stateProvider

    .state('login', {
        url: '/login',
        templateUrl: "app/auth/login.html"
    })
    .state('change_pass', {
        url: '/change_pass',
        templateUrl: "app/auth/change_pass.html"
    })

    .state('index', {
        abstract: true,
        url: "/index",
        templateUrl: "components/common/content.html",
        resolve: {
            currentAuth: function(firebaseHelper) {
                return firebaseHelper.auth.$requireAuth();
            }
        }
    })
    .state('index.tasks', {
        url: "/tasks",
        templateUrl: "app/tasks/tasks.html",
        data: { pageTitle: 'Tasks' }
    })
    .state('index.advisors', {
        url: "/advisors",
        templateUrl: "app/advisors/advisors.html",
        data: { pageTitle: 'Advisors view' }
    })
    .state('index.topics', {
        url: "/topics",
        templateUrl: "app/topics/topics.html",
        data: { pageTitle: 'Topics view' }
    })
    .state('index.create_post', {
        url: "/create_post",
        templateUrl: "app/create_post/create_post.html",
        data: { pageTitle: 'Create Post' }
    })

    $urlRouterProvider.otherwise('/index/tasks');
})

.run(function($rootScope, $state, notify) {
    $rootScope.$on("$stateChangeError", function(event, toState, toParams, fromState, fromParams, error) {
        console.log("$stateChangeError", error);
        if (error === "AUTH_REQUIRED") {
            $state.go("login");
        }
    });
    $rootScope.inspiniaTemplate = 'components/common/notify.html';
    notify.config({
       duration: '5000',
       position: 'center'
    });
    $rootScope.notifySuccess = function(message) {
        notify({ message: message || "Your request processed successfully", classes: 'alert-success', templateUrl: $rootScope.inspiniaTemplate});
    }
    $rootScope.notifyError = function(message) {
        message = message || "Uh-oh, something went wrong!";
        notify({ message: message, classes: 'alert-danger', templateUrl: $rootScope.inspiniaTemplate});
    }
})
;
