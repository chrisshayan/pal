angular.module('inspinia')
.directive('customFinishedPost', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            data: '=',
            group: '@'
        },
        controller: function($scope, firebaseHelper, $sce, $rootScope, cs) {
            $scope.formatTime = cs.formatTime;
            $scope.formatDate = cs.formatDate;
            $scope.formatDateTime = cs.formatDateTime;

            $scope.audio = null;
            $scope.advisorAudio = null;

            $scope.user = firebaseHelper.getFireBaseInstance(["profiles_pub", $scope.data.created_by, "display_name"]).on('value', function(snapshot) {
                $scope.user_display_name = snapshot.val();
                $scope.$digest();
            }, function() {});

            $scope.toggleUserVoice = function(){
                if (!$scope.audio) {
                    $scope.audio = new Audio();
                    $scope.audio.src = $scope.data.audio;
                    $scope.audio.addEventListener('ended', function(){
                        $scope.audio.playing = false;
                        $scope.$digest();
                    });
                }
                if ($scope.audio.playing) {
                    $scope.audio.pause()
                } else {
                    $scope.audio.play()
                }
                $scope.audio.playing = !$scope.audio.playing;
            };

            $scope.toggleAdvisorVoice = function(){
                if (!$scope.advisorAudio) {
                    $scope.advisorAudio = new Audio();
                    $scope.advisorAudio.src = $scope.data.answer_audio;
                    $scope.advisorAudio.addEventListener('ended', function(){
                        $scope.advisorAudio.playing = false;
                        $scope.$digest();
                    });
                }
                if ($scope.advisorAudio.playing) {
                    $scope.advisorAudio.pause()
                } else {
                    $scope.advisorAudio.play()
                }
                $scope.advisorAudio.playing = !$scope.advisorAudio.playing;
            };
        },
        templateUrl: "app/partials/finished_post/finished_post.html"
    }
})
