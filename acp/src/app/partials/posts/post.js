angular.module('inspinia')
.directive('customPost', function() {
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
            $scope.showComment = false;
            $scope.audio = null;
            $scope.audio_percent = 0;

            $scope.playPause = function(){
                if (!$scope.audio) {
                    $scope.audio = new Audio();
                    $scope.audio.src = $scope.data.audio;
                    $scope.audio_percent = 0;

                    $scope.audio.addEventListener('timeupdate', function(event){
                        var percent = Math.min(100, Math.round((event.path[0].currentTime / event.path[0].duration)*100));
                        $scope.audio_percent = percent;
                        $scope.$digest();
                    });
                    $scope.audio.addEventListener('ended', function(){
                        $scope.audio.playing = false;
                        // $scope.audio_percent = 0;
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
        },
        templateUrl: "app/partials/posts/post.html"
    }
})
