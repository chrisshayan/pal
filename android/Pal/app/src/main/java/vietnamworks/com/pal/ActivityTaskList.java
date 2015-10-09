package vietnamworks.com.pal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import vietnamworks.com.pal.components.CustomCardStackView;
import vietnamworks.com.pal.components.CustomCardStackViewDelegate;
import vietnamworks.com.pal.components.CustomCardView;
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
        if (stackView.getFront().getText().length() == 0) {
            Toast.makeText(this.getBaseContext(), getString(R.string.user_topic_validation_empty_string),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        setTimeout(new Runnable() {
            @Override
            public void run() {
                fragment_writing.setVisibility(View.GONE);
                ((FragmentWriting) getSupportFragmentManager().findFragmentById(R.id.fragment_writing)).reset();
                stackView.closeCard();
                showSaySomethingGroup();
                enableAudioButton(true);
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
                enableAudioButton(true);
            }
        }, 10);
    }

    public void onSubmitAudio(View v) {
        if (stackView.getFront().getText().length() == 0) {
            Toast.makeText(this.getBaseContext(), getString(R.string.user_topic_validation_empty_string),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        this.fragment_speaking.setVisibility(View.GONE);
        ((FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking)).reset();
        showSaySomethingGroup();
        stackView.closeCard();
        enableAudioButton(true);
    }

    public void onCancelSubmitAudio(View v) {
        this.fragment_speaking.setVisibility(View.GONE);
        ((FragmentRecording)getSupportFragmentManager().findFragmentById(R.id.fragment_speaking)).reset();
        showSaySomethingGroup();
        stackView.closeCard();
        enableAudioButton(true);
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
        enableAudioButton(false);
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

    public void setCardData(CustomCardView card, int type, String title, String message) {
        card.setData(type, type == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey, title, message);
    }

    public void setCardData(int type, String title, String message) {
        setCardData(stackView.getFront(), type, title, message);
    }
    public void setCardData(CustomCardView card, Topic p) {
        setCardData(card, p.getType(), p.getTypeName(), p.getTitle());
    }
    public void setCardData(Topic p) {
        setCardData(stackView.getFront(), p.getType(), p.getTypeName(), p.getTitle());
    }

    public void onToggleAudioMode(View v) {
        this.isUseAudioTask = !this.isUseAudioTask;
        ((FragmentToolbar)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).setAudioMode(this.isUseAudioTask);
        startLoadingTask();
    }

    public void onAskSomething(View v) {
        setCardData(Topic.TYPE_WRITING, getResources().getString(R.string.btn_ask_something), "");
        stackView.doManualSelect();
    }

    public void onSaySomething(View v) {
        setCardData(Topic.TYPE_SPEAKING, getResources().getString(R.string.btn_say_something), "");
        stackView.doManualSelect();
    }

    private AsyncCallback loadDataCallback = new AsyncCallback() {
        @Override
        public void onSuccess(Context context, Object obj) {
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    System.out.println("onSuccess");
                    int count = AppModel.topics.getData().size();
                    if (count > 0 && stackView.getState() == CustomCardStackView.STATE_IDLE) {
                        setCardData(AppModel.topics.getData().get(0));
                        enableAudioButton(true);
                    }
                    setCardData(stackView.getMid(), AppModel.topics.getData().get(count>1?1:0));
                    setCardData(stackView.getBack(), AppModel.topics.getData().get(count>2?2:0));
                    stackView.refresh();
                    stackView.unlock();
                }
            }, 1000);
        }

        @Override
        public void onError(Context context, int code, String message) {
            System.out.println("Fail to load data " + message);
            stackView.getFront().setData(-1, R.drawable.ic_launcher, "", "Fail to load data. Touch to retry");
            stackView.unlock();
            stackView.snooze();
            enableAudioButton(true);
        }
    };

    public void startLoadingTask() {
        AppModel.topics.getData().clear();
        stackView.refresh();
        stackView.getFront().startLoading();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                stackView.lock();
                Map<String, Object> cnd = new HashMap<>();
                cnd.put("audio", isUseAudioTask);
                AppModel.topics.loadAsync(ActivityBase.sInstance, cnd, loadDataCallback);
                enableAudioButton(false);
            }
        });
    }

    private void enableAudioButton(boolean val) {
        ((FragmentToolbar) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).enableAudioButton(val);
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
        ((ActivityTaskList)(ActivityTaskList.sInstance)).setCardData(ccsv.getBack(), p);
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
        ActivityBase.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                ActivityTaskList act = (ActivityTaskList) ActivityTaskList.sInstance;
                int type = ccsv.getFront().getCustomType();
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
    }

    @Override
    public void onDeselectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onDeselectItem " + index);
        if (index >= 0) {
            Topic p = AppModel.topics.getData().get(index);
            ((ActivityTaskList)(ActivityTaskList.sInstance)).setCardData(ccsv.getFront(), p);
        }
    }
}

