'use strict';

angular.module('inspinia', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngResource', 'ui.router', 'ui.bootstrap', 'firebase', 'firebaseHelper', 'cgNotify', 'cs', 'parseHelper'])

.config(function ($stateProvider, $urlRouterProvider, firebaseHelperConfigProvider, parseHelperConfigProvider) {
    firebaseHelperConfigProvider.setURL(FIREBASE);
    parseHelperConfigProvider.init(window.PARSE_APP, window.PARSE_JS_KEY);

    $stateProvider

    .state('login', {
        url: '/login',
        templateUrl: "app/modules/auth/login.html"
    })
    .state('change_pass', {
        url: '/change_pass',
        templateUrl: "app/modules/auth/change_pass.html"
    })
    .state('activate', {
        url: '/activate/:token',
        templateUrl: "app/modules/auth/activate.html"
    })
    .state('error', {
        url: '/error',
        templateUrl: "app/modules/error/error.html"
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
        templateUrl: "app/modules/tasks/tasks.html",
    })
    .state('index.advisors', {
        url: "/advisors",
        templateUrl: "app/modules/advisors/advisors.html",
    })
    .state('index.topics', {
        url: "/topics",
        templateUrl: "app/modules/topics/topics.html",
    })
    .state('index.create_post', {
        url: "/create_post",
        templateUrl: "app/modules/create_post/create_post.html",
    })
    .state('index.profiles', {
        url: "/profiles",
        templateUrl: "app/modules/profiles/profiles.html",
    })
    .state('index.settings', {
        url: "/settings",
        templateUrl: "app/modules/settings/settings.html",
    })

    .state('database', {
        abstract: true,
        url: "/database",
        templateUrl: "components/common/content.html",
        resolve: {
            currentAuth: function(firebaseHelper) {
                return firebaseHelper.auth.$requireAuth();
            }
        }
    })
    .state('database.cities', {
        url: "/cities",
        templateUrl: "app/modules/cities/cities.html",
    })
    .state('database.nations', {
        url: "/nations",
        templateUrl: "app/modules/nations/nations.html",
    })
    .state('database.schools', {
        url: "/schools",
        templateUrl: "app/modules/schools/schools.html",
    })


    ;

    $urlRouterProvider.otherwise('/index/tasks');
})

.run(function($rootScope, $state, notify, firebaseHelper) {
    $rootScope.$on("user:logout", function() {
		$state.go("login");
	});

    $rootScope.$on("$stateChangeError", function(event, toState, toParams, fromState, fromParams, error) {
        console.log("$stateChangeError", error);
        if (error === "AUTH_REQUIRED") {
            $state.go("login");
        }
    });
    cloudinary.setCloudName('vnw-owner');
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

    firebaseHelper.bindObject("config", $rootScope, 'config');
})
;
