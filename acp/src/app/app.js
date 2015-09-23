'use strict';

angular.module('inspinia', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngResource', 'ui.router', 'ui.bootstrap', 'firebase', 'firebaseHelper'])

.config(function ($stateProvider, $urlRouterProvider) {
    $stateProvider

    .state('login', {
        url: '/login',
        templateUrl: "app/auth/login.html"
    })

    .state('index', {
        abstract: true,
        url: "/index",
        templateUrl: "components/common/content.html",
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

.run(function() {
})

;
