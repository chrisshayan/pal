package vietnamworks.com.pal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import vietnamworks.com.pal.components.CustomCardStackView;
import vietnamworks.com.pal.components.CustomCardStackViewDelegate;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.fragments.FragmentRecording;
import vietnamworks.com.pal.fragments.FragmentToolbar;
import vietnamworks.com.pal.fragments.FragmentWriting;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.AsyncCallback;

/**
 * Created by duynk on 10/6/15.
 */
public class ActivityTaskList extends ActivityBase {
    private CustomCardStackView stackView;
    public View fragment_writing;
    public View fragment_speaking;
    public ViewGroup groupSaySomething;

    private boolean isUseAudioTask = true;

    public ActivityTaskList() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        stackView = (CustomCardStackView)this.findViewById(R.id.cards_stack_view);
        stackView.setDelegate(new MyCustomCardStackViewDelegate());

        groupSaySomething = (ViewGroup)this.findViewById(R.id.group_say_something);
        groupSaySomething.setVisibility(View.INVISIBLE);

        fragment_writing = (View)this.findViewById(R.id.fragment_writing);
        fragment_writing.setVisibility(View.GONE);

        fragment_speaking = (View)this.findViewById(R.id.fragment_speaking);
        fragment_speaking.setVisibility(View.GONE);

        this.setTimeout(new Runnable() {
            @Override
            public void run() {
                ((ActivityTaskList) (ActivityTaskList.sInstance)).showSaySomethingGroup();
                startLoadingTask();
            }
        }, 500);

    }

    public void onSubmitText(View v) {
        this.hideKeyboard();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                fragment_writing.setVisibility(View.GONE);
                ((FragmentWriting) getSupportFragmentManager().findFragmentById(R.id.fragment_writing)).reset();
                stackView.closeCard();
                showSaySomethingGroup();
            }
        }, 100);
    }

    public void onCancelSubmitText(View v) {
        this.hideKeyboard();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                fragment_writing.setVisibility(View.GONE);
                ((FragmentWriting) getSupportFragmentManager().findFragmentById(R.id.fragment_writing)).reset();
                stackView.closeCard();
                showSaySomethingGroup();
            }
        }, 10);
    }

    public void onSubmitAudio(View v) {
        this.fragment_speaking.setVisibility(View.GONE);
        ((FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking)).reset();
        showSaySomethingGroup();
        stackView.closeCard();
    }

    public void onCancelSubmitAudio(View v) {
        this.fragment_speaking.setVisibility(View.GONE);
        ((FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking)).reset();
        showSaySomethingGroup();
        stackView.closeCard();
    }

    public void onToggleRecorder(View v) {
        FragmentRecording currentFragment = (FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking);
        currentFragment.toggleRecording();

    }

    public void onToggleReplay(View v) {
        FragmentRecording currentFragment = (FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking);
        currentFragment.toggleReplay();
    }

    public void showSaySomethingGroup() {
        groupSaySomething.setVisibility(View.VISIBLE);
        ObjectAnimator animY1 = ObjectAnimator.ofFloat(groupSaySomething, "translationY", groupSaySomething.getHeight());
        animY1.setDuration(0);
        ObjectAnimator animY2 = ObjectAnimator.ofFloat(groupSaySomething, "translationY", 0);
        animY2.setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.play(animY1).before(animY2);
        set.start();
    }

    public void hideSaySomethingGroup() {
        groupSaySomething.animate().translationY(groupSaySomething.getHeight()).setDuration(100).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                groupSaySomething.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void onToggleAudioMode(View v) {
        this.isUseAudioTask = !this.isUseAudioTask;
        ((FragmentToolbar)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).setAudioMode(this.isUseAudioTask);
        startLoadingTask();
    }

    private AsyncCallback loadDataCallback = new AsyncCallback() {
        @Override
        public void onSuccess(Context context, Object obj) {
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    System.out.println("onSuccess");
                    int count = AppModel.topics.getData().size();
                    if (count > 0) {
                        Topic p = AppModel.topics.getData().get(0);
                        stackView.getFront().setData(p.getType() == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey, p.getTypeName(), p.getTitle());
                    }
                    if (count > 1) {
                        Topic p = AppModel.topics.getData().get(1);
                        stackView.getMid().setData(p.getType() == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey, p.getTypeName(), p.getTitle());
                    }
                    if (count > 2) {
                        Topic p = AppModel.topics.getData().get(2);
                        stackView.getBack().setData(p.getType() == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey, p.getTypeName(), p.getTitle());
                    } else {
                        Topic p = AppModel.topics.getData().get(0);
                        stackView.getBack().setData(p.getType() == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey, p.getTypeName(), p.getTitle());
                    }
                    
                    stackView.refresh();
                    stackView.unlock();
                    ((FragmentToolbar) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).enableAudioButton(true);
                }
            }, 1000);
        }

        @Override
        public void onError(Context context, int code, String message) {
            System.out.println("Fail to load data " + message);
            stackView.getFront().setData(R.drawable.ic_launcher, "", "Fail to load data. Touch to retry");
            stackView.unlock();
            stackView.snooze();
            ((FragmentToolbar) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).enableAudioButton(true);
        }
    };

    public void startLoadingTask() {
        AppModel.topics.getData().clear();
        stackView.refresh();
        stackView.getFront().setData(R.drawable.ic_launcher, "", "Loading");
        setTimeout(new Runnable() {
            @Override
            public void run() {
                stackView.lock();
                Map<String, Object> cnd = new HashMap<>();
                cnd.put("audio", isUseAudioTask);
                AppModel.topics.loadAsync(ActivityBase.sInstance, cnd, loadDataCallback);
                ((FragmentToolbar) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).enableAudioButton(false);
            }
        });
    }
}

class MyCustomCardStackViewDelegate implements CustomCardStackViewDelegate {
    @Override
    public void onLaunched(final CustomCardStackView ccsv) {
    }

    @Override
    public void onChangedActiveItem(int front, int back, CustomCardStackView ccsv) {
    }

    @Override
    public void onBeforeChangedActiveItem(int front, int back, CustomCardStackView ccsv) {
        Topic p = AppModel.topics.getData().get(back);
        ccsv.getBack().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone_grey:R.drawable.ic_keyboard_grey, p.getTypeName(), p.getTitle());
    }

    @Override
    public int getTotalRecords() {
        return AppModel.topics.getData().size();
    }


    @Override
    public void onBeforeSelectItem(int index, final CustomCardStackView ccsv) {
        ((ActivityTaskList)(ActivityTaskList.sInstance)).hideSaySomethingGroup();
    }

    @Override
    public void onSelectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onSelectItem " + index);
        if (index >= 0) {

            final int _index = index;
            ActivityBase.sInstance.setTimeout(new Runnable() {
                @Override
                public void run() {
                    ActivityTaskList act = (ActivityTaskList) ActivityTaskList.sInstance;
                    int type = AppModel.topics.getData().get(_index).getType();
                    if (type == Topic.TYPE_SPEAKING) {
                        act.fragment_speaking.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layout = (ViewGroup.LayoutParams) act.fragment_speaking.getLayoutParams();
                        layout.height = (int) (ActivityBase.getScreenHeight() - ActivityBase.getStatusBarHeight() - ccsv.getFront().getHeight() * ccsv.getFront().getScaleX());
                        act.fragment_speaking.setLayoutParams(layout);
                    } else {
                        act.fragment_writing.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layout = (ViewGroup.LayoutParams) act.fragment_writing.getLayoutParams();
                        layout.height = (int) (ActivityBase.getScreenHeight() - ActivityBase.getStatusBarHeight() - ccsv.getFront().getHeight() * ccsv.getFront().getScaleX());
                        act.fragment_writing.setLayoutParams(layout);
                    }
                }
            });
        } else {
            this.onLaunched(ccsv);
        }
    }
}

