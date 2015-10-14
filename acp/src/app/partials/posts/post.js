angular.module('inspinia')
.directive('customPost', function() {
    return {
        restrict: 'AE',
        transclude: true,
        scope: {
            ref: '=',
            group: '@'
        },
        controller: function($scope, firebaseHelper, $sce, $rootScope, cs, $http, parseHelper) {
            $scope.isShowDetail = false;
            $scope.formatTime = cs.formatTime;
            $scope.formatDate = cs.formatDate;
            $scope.formatDateTime = cs.formatDateTime;
            $scope.audio = null;
            $scope.teacher_audio = null;
            $scope.audio_percent = 0;
            $scope.audioRecorder = null;
            $scope.vote = 0;
            $scope.pre_vote = 0;
            $scope.comment = "";
            $scope.isSubmitting = false;

            $scope.data = firebaseHelper.syncObject(["posts", $scope.ref.$id]);

            $scope.user = firebaseHelper.getFireBaseInstance(["profiles_pub", $scope.data.created_by, "display_name"]).once('value', function(snapshot) {
                $scope.user_display_name = snapshot.val() || "unknowned";
                setTimeout(function(){
                    $scope.$digest();
                }, 100);
            }, function() {});

            $scope.playPause = function(){
                if (!$scope.audio) {
                    $scope.audio = new Audio();
                    $scope.audio.src = "https://api.cloudconvert.com/convert?apikey=3suj4KZn3UoTKbksedeKLiDLIBQ9JBl0CBzVQ6FV_IKknHzkcHTEUT_DP6O2DopvIWSM4-nL1w0xIZCd1INaJQ&input=download&download=inline&inputformat=3gp&outputformat=mp3&file=" + $scope.data.audio;
                    console.log($scope.audio.src);

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

                    // var recordedBlob = $scope.audioRecorder.getBlob();
                    // $scope.audioRecorder.getDataURL(function(dataURL) { });
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

            var submit = function(audio) {
                //posted audio, now save question content
                firebaseHelper.getFireBaseInstance(["posts", $scope.data.$id]).transaction(function(recent){
                    if (!recent) {
                        $rootScope.notifyError("Something wrong. Please try again");
                        return;
                    } else {
                        var uid = firebaseHelper.getUID();
                        if (recent.status == PostStatus.AdvisorProcessing && recent.advisor_id == uid) {
                            recent = new Post(recent)
                                .set("status", PostStatus.AdvisorEvaluated)
                                .set("score", $scope.vote)
                                .set("hasRead", false)
                                .push("conversation", {
                                    uid: uid,
                                    audio: audio || "",
                                    text: $scope.comment
                                })
                                .doModify(uid)
                                .get();
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
                        parseHelper.push($scope.data.created_by, "You've got new feedback from advisor");
                    }
                    $scope.isSubmitting = false;
                    $scope.$apply();
                });
            }

            $scope.onSubmit = function() {
                if (firebaseHelper.getUID()) {
                    $scope.isSubmitting = true;
                    //post audio file to server
                    if ($scope.audioRecorder) {
                        $scope.audioRecorder.getDataURL(function(dataURL) {
                            var fileName = "advisors_" + firebaseHelper.getUID() + "_" + $scope.data.$id + "_" + Date.now();
                            var audioType = $scope.audioRecorder.getBlob().type;
                            var audio = {
                                name: fileName + '.' + audioType.split('/')[1],
                                type: audioType,
                                contents: dataURL
                            }
                            $http.post(window.RTC_SERVER, audio).then(function(data) {
                                console.log(data.data.url);
                                if (!data.data.url) {
                                    $rootScope.notifyError("Fail to upload audio. Invalid response");
                                    $scope.isSubmitting = false;
                                    return;
                                }
                                submit(data.data.url);
                            }, function() {
                                $rootScope.notifyError("Fail to upload audio");
                                $scope.isSubmitting = false;
                            });
                        });
                    } else {
                        submit();
                    }
                } else {
                    $rootScope.notifyError("Something wrong");
                }
            }

            $scope.onPutBack = function() {
                if (firebaseHelper.getUID()) {
                    firebaseHelper.getFireBaseInstance(["posts", $scope.data.$id]).update({
                        status: PostStatus.Ready,
                        index_user_status: PostHelper.buildIndex($scope.data.created_by, PostStatus.Ready),
                        index_advisior_status: PostHelper.buildIndex($scope.data.advisor_id, PostStatus.Ready)
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

            $scope.onOpenTask = function(id) {
                $scope.isShowDetail = !$scope.isShowDetail;
                if ($scope.isShowDetail) {
                    $rootScope.$broadcast('todo_task:open', {id: $scope.ref.$id});
                }
            }
            $scope.$on("todo_task:open", function(sender, data) {
                if (data.id != $scope.ref.$id) {
                    $scope.isShowDetail = false;
                }
            })

            $scope.isSpeakingTask = function() {
                return $scope.data.type == PostType.Speaking;
            }
        },
        templateUrl: "app/partials/posts/post.html"
    }
})
