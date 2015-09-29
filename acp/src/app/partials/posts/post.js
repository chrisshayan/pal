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
            $scope.teacher_audio = null;
            $scope.audio_percent = 0;
            $scope.audioRecorder = null;
            $scope.vote = 0;
            $scope.pre_vote = 0;

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

            $scope.isRecording = false;
            $scope.startRecord = function() {
                if ($scope.teacher_audio && $scope.teacher_audio.playing) {
                    $scope.teacher_audio.pause();
                }
                navigator.getUserMedia({audio:true, video:false}, function(stream) {
                    $scope.audioRecorder = RecordRTC(stream, {
                        sampleRate: 44100,
                        bufferSize: 16384,
                        numberOfAudioChannels: 1
                    });
                    $scope.audioRecorder.startRecording();
                    $scope.isRecording = true;
                    $scope.$apply();
                }, function(error) {
                    console.log(error)
                });
            }

            $scope.stopRecord = function() {
                $scope.audioRecorder.stopRecording(function (audioVideoWebMURL) {
                    $scope.isRecording = false;
                    $scope.teacher_audio = new Audio();
                    $scope.teacher_audio.src = audioVideoWebMURL;
                    $scope.teacher_audio.playing = false;
                    $scope.teacher_audio.addEventListener('ended', function(){
                        $scope.teacher_audio.playing = false;
                        $scope.$digest();
                    });

                    var recordedBlob = $scope.audioRecorder.getBlob();
                    $scope.audioRecorder.getDataURL(function(dataURL) { });
                    $scope.$apply();
                });
            }

            $scope.playStopRecord = function() {
                if ($scope.teacher_audio.playing) {
                    $scope.teacher_audio.pause()
                } else {
                    $scope.teacher_audio.play()
                }
                $scope.teacher_audio.playing = !$scope.teacher_audio.playing;
            }
        },
        templateUrl: "app/partials/posts/post.html"
    }
})
