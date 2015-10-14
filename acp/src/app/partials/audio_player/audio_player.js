angular.module('inspinia')
.directive('audioPlayer', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            audio: '@'
        },
        controller: function($scope) {
            $scope.audioPlayer = null;
            $scope.toggleAudio = function(){
                if (!$scope.audioPlayer) {
                    $scope.audioPlayer = new Audio();
                    $scope.audioPlayer.src = $scope.audio;
                    $scope.audioPlayer.addEventListener('ended', function(){
                        $scope.audioPlayer.playing = false;
                        $scope.$digest();
                    });
                }
                if ($scope.audioPlayer.playing) {
                    $scope.audioPlayer.pause()
                } else {
                    $scope.audioPlayer.play()
                }
                $scope.audioPlayer.playing = !$scope.audioPlayer.playing;
            };
        },
        templateUrl: "app/partials/audio_player/audio_player.html"
    }
});
