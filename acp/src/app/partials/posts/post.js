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
            $scope.audio = null;
            $scope.teacher_audio = null;
            $scope.audio_percent = 0;
            $scope.audioRecorder = null;
            $scope.vote = 0;
            $scope.pre_vote = 0;

            $scope.user = firebaseHelper.getFireBaseInstance(["profiles_pub", $scope.data.created_by, "display_name"]).once('value', function(snapshot) {
                $scope.user_display_name = snapshot.val();
                $scope.$digest();
            }, function() {});

            $scope.playPause = function(){
                if (!$scope.audio) {
                    $scope.audio = new Audio();
                    $scope.audio.src = $scope.data.audio;

                    $scope.audio.addEventListener('timeupdate', function(event){
                    });
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

            $scope.onSubmit = function() {
                if (firebaseHelper.getUID()) {
                    firebaseHelper.getFireBaseInstance(["posts", $scope.data.$id]).transaction(function(recent){
                        if (!recent) {
                            $rootScope.notifyError("Something wrong. Please try again");
                            return;
                        } else {
                            if (recent.status == 1 && recent.picked_by == firebaseHelper.getUID()) {
                                recent.status = 2;
                                recent.answer_date == Date.now();
                                recent.score == $scope.vote;
                                recent.answer_audio == "https://d1ngnrwwqlw7nm.cloudfront.net/0096-03-OT-70/06_joshua/06_joshua_006.mp3?Expires=1451563200&Signature=HT0YT4rtQ7yNmF7wordPvcAfkl-5iucR6vIt2BpX3EbCQIjZqQOzPE4YaPahKEqnpmbyskSpX68i1364JLKjXNoJCjHz~KwWsY8nl5QKZb4OOZbonj3tUbWYRCFTi4EUWZ3KdxdgIcDLxk0ch1qnKtbffpGUgAyjGJxZPpIRejUTpXmXxdsA25ncW7wGmw--C-vi5JWWuxcIqjg5AZ-PRTVbmHB~CQ3KpN7sAE44~MWfOsWnrGUyW-rLr9ogu1pQYrWVgw98f2dOinkUoFT-12QRu0sjTK4SiSTqhkqMH2D4RcnqhqbDvmZyWI19fHeggNFldsRxkVRnS8dVxFA05Q__&Key-Pair-Id=APKAJQXQBQHDBU72DTAQ";
                                return recent;
                            } else {
                                $rootScope.notifyError("This task was tranferred to another advisor before");
                                return;
                            }
                        }

                    }, function(error, committed, snapshot){
                        if (error) {
                            $rootScope.notifyError("Something wrong. Please try again");
                        } else if (!committed) {
                            $rootScope.notifyError("Fail to save data");
                        } else {
                            $rootScope.notifySuccess("You have solved a task");
                        }
                        $scope.$apply();
                    });
                } else {
                    $rootScope.notifyError("Something wrong");
                }
            }

            $scope.onPutBack = function() {
                if (firebaseHelper.getUID()) {
                    firebaseHelper.getFireBaseInstance(["posts", $scope.data.$id]).update({
                        status:0
                    }, function(error) {
                        if (error) {
                            $rootScope.notifyError(error);
                        } else {
                            $rootScope.notifySuccess("You have rejected a task");
                        }
                    });
                } else {
                    $rootScope.notifyError("Something wrong");
                }
            }
        },
        templateUrl: "app/partials/posts/post.html"
    }
})
