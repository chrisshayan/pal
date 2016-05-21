angular.module('inspinia')
.directive('audioPlayer', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            audio: '@',
            convertFrom: '@',
            convertTo: '@'
        },
        controller: function($scope) {
            var ext = $scope.audio.substring($scope.audio.lastIndexOf(".") + 1, $scope.audio.length);
            if (ext.length != $scope.audio.length) {
                $scope.convertFrom = ext;
            } else {
                ext = $scope.convertFrom;
            }

            if (!$scope.convertFrom || $scope.convertFrom != ext) {
                $scope.convertFrom = ext;
            }

            // console.log("create audio player", $scope.convertFrom, "->", $scope.convertTo, $scope.audio);

            if ($scope.convertFrom && $scope.convertTo && ($scope.convertFrom != $scope.convertTo)) {
                $scope.audio = "https://api.cloudconvert.com/convert?apikey=3suj4KZn3UoTKbksedeKLiDLIBQ9JBl0CBzVQ6FV_IKknHzkcHTEUT_DP6O2DopvIWSM4-nL1w0xIZCd1INaJQ&input=download&download=inline&inputformat=" + $scope.convertFrom + "&outputformat=" + $scope.convertTo + "&file=" + $scope.audio;
            }

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
