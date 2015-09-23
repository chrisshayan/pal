'use strict';

angular.module('inspinia')
  .controller('MainCtrl', function ($scope, firebaseHelper, $rootScope) {

        this.userName = 'Example user';
        this.helloText = 'Welcome in INSPINIA Gulp SeedProject';
        this.descriptionText = 'It is an application skeleton for a typical AngularJS web app. You can use it to quickly bootstrap your angular webapp projects.';

        $scope.topics = null;
        $scope.posts = null;
        $scope.isAdmin = false;

        $scope.$on("user:login", function() {
            firebaseHelper.bindObject("profiles/" + firebaseHelper.getUID(), $scope, "data");
            $scope.topics = firebaseHelper.syncArray("topics");
            if (firebaseHelper.isAdmin()) {
                $scope.isAdmin = true;
                $scope.posts = firebaseHelper.syncArray("posts");
                $scope.posts.$watch(function(event) {
                    console.log(event);
                })
            } else {
                $scope.isAdmin = false;
                $scope.posts = firebaseHelper.syncProtectedArray("posts");
            }

        })

        $scope.onAddTopic = function() {
            $scope.topics.$add({
                title: "this is a test"
            }).catch(function(error) {
                $rootScope.notifyError(error.code);
            });
        }

        $scope.onPost = function(fromTopic) {
            $scope.posts.$add({
                title: fromTopic.title,
                topicRef: fromTopic.$id,
                uid: firebaseHelper.getUID()
            }).catch(function(error) {
                $rootScope.notifyError(error.code);
            });
        }
    });
