'use strict';

angular.module('inspinia', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngResource', 'ui.router', 'ui.bootstrap', 'firebase', 'firebaseHelper', 'cgNotify', 'cs'])

.config(function ($stateProvider, $urlRouterProvider, firebaseHelperConfigProvider) {
    firebaseHelperConfigProvider.setURL("https://pal-dev.firebaseio.com");
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
    .state('index.main', {
        url: "/main",
        templateUrl: "app/main/main.html",
        data: { pageTitle: 'Example view' }
    })
    .state('index.minor', {
        url: "/minor",
        templateUrl: "app/minor/minor.html",
        data: { pageTitle: 'Example view' }
    })

    $urlRouterProvider.otherwise('/login');
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
        notify({ message: message, classes: 'alert-success', templateUrl: $rootScope.inspiniaTemplate});
    }
    $rootScope.notifyError = function(message) {
        notify({ message: message, classes: 'alert-danger', templateUrl: $rootScope.inspiniaTemplate});
    }
})
;
