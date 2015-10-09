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

    public CustomCardStackView stackView;
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

    public static int getCardIcon(int type) {
        return type == Topic.TYPE_SPEAKING ? R.drawable.ic_microphone_grey : R.drawable.ic_keyboard_grey;
    }

    public void onToggleAudioMode(View v) {
        this.isUseAudioTask = !this.isUseAudioTask;
        ((FragmentToolbar)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).setAudioMode(this.isUseAudioTask);
        startLoadingTask();
    }

    public void onAskSomething(View v) {
        stackView.getFront().showInput(Topic.TYPE_WRITING, getCardIcon(Topic.TYPE_WRITING), getResources().getString(R.string.btn_ask_something));
        stackView.doManualSelect();
    }

    public void onSaySomething(View v) {
        stackView.getFront().showInput(Topic.TYPE_SPEAKING, getCardIcon(Topic.TYPE_SPEAKING), getResources().getString(R.string.btn_say_something));
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
                    if (count > 0) {
                        Topic p = AppModel.topics.getData().get(0);
                        stackView.getFront().appendData(p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
                        enableAudioButton(true);
                        stackView.refresh();
                        stackView.unlock();
                    }
                    Topic p = AppModel.topics.getData().get(count > 1 ? 1 : 0);
                    stackView.getMid().showData(p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
                    p = AppModel.topics.getData().get(count>2?2:0);
                    stackView.getBack().showData(p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
                }
            }, 1000);
        }

        @Override
        public void onError(Context context, int code, String message) {
            //TODO: handle error event
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

    public void showTaskDetail(final int type) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                if (type == Topic.TYPE_SPEAKING) {
                    fragment_speaking.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layout = fragment_speaking.getLayoutParams();
                    layout.height = (int) (ActivityBase.getScreenHeight() - ActivityBase.getStatusBarHeight() - stackView.getFront().getHeight() * stackView.getFront().getScaleX());
                    fragment_speaking.setLayoutParams(layout);
                } else {
                    fragment_writing.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layout = fragment_writing.getLayoutParams();
                    layout.height = (int) (ActivityBase.getScreenHeight() - ActivityBase.getStatusBarHeight() - stackView.getFront().getHeight() * stackView.getFront().getScaleX());
                    fragment_writing.setLayoutParams(layout);
                }
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
        ccsv.getFront().appendData(p.getType(), ActivityTaskList.getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
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
        ActivityTaskList act = (ActivityTaskList) ActivityTaskList.sInstance;
        act.showTaskDetail(ccsv.getFront().getStateIntData("type"));
    }

    @Override
    public void onDeselectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onDeselectItem " + index);
        if (index >= 0) {
            Topic p = AppModel.topics.getData().get(index);
            ccsv.getFront().rollback();
        }
    }
}

