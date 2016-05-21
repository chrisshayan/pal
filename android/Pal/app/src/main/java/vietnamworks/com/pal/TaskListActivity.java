package vietnamworks.com.pal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vietnamworks.com.pal.components.CustomCardStackView;
import vietnamworks.com.pal.components.CustomCardStackViewDelegate;
import vietnamworks.com.pal.components.CustomCardView;
import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.fragments.FragmentRecording;
import vietnamworks.com.pal.fragments.FragmentToolbar;
import vietnamworks.com.pal.fragments.FragmentWriting;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.BaseService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/6/15.
 */
public class TaskListActivity extends BaseActivity {

    public CustomCardStackView stackView;
    public View fragment_writing;
    public View fragment_speaking;
    public ViewGroup groupSaySomething;

    private boolean isUseAudioTask = true;

    public TaskListActivity() {}

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

        stackView.getFront().startLoading();
        this.setTimeout(new Runnable() {
            @Override
            public void run() {
                ((TaskListActivity) (TaskListActivity.sInstance)).showSaySomethingGroup();
                startLoadingTask();
            }
        }, 500);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new DrawerEventListener(drawer));
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    public void onSubmitText(View v) {
        CustomCardView front = stackView.getFront();
        FragmentWriting fragment = (FragmentWriting) getSupportFragmentManager().findFragmentById(R.id.fragment_writing);

        this.hideKeyboard();
        if (front.getTopic().length() == 0) {
            toast(R.string.user_topic_validation_empty_string);
            return;
        } else if (fragment.getText().trim().length() == 0) {
            toast(R.string.user_message_validation_empty_string);
            return;
        }

        AppModel.posts.addText(front.getTopic(), front.getStateStringData("id"), fragment.getText());

        setTimeout(new Runnable() {
            @Override
            public void run() {
                fragment_writing.setVisibility(View.GONE);
                ((FragmentWriting) getSupportFragmentManager().findFragmentById(R.id.fragment_writing)).reset();
                stackView.closeCard();
                showSaySomethingGroup();
                enableAudioButton(true);
                toast(R.string.create_post_successful);
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
        }, 100);
    }

    public void onSubmitAudio(View v) {
        CustomCardView front = stackView.getFront();
        FragmentRecording fragment = (FragmentRecording) getSupportFragmentManager().findFragmentById(R.id.fragment_speaking);

        if (front.getTopic().length() == 0) {
            toast(R.string.user_topic_validation_empty_string);
            return;
        }

        this.fragment_speaking.setVisibility(View.GONE);
        fragment.reset();
        showSaySomethingGroup();
        stackView.closeCard();
        enableAudioButton(true);

        String post_id = AppModel.posts.addAudioAsync(front.getTopic(), front.getStateStringData("id"));

        String server_file_path = Common.getAudioServerFileName(FirebaseService.authData.getUid(), post_id);
        BaseService.PostFile(
                this,
                post_id,
                Config.AudioUploadURL,
                Common.getSampleRecordPath(),
                server_file_path);
        toast(R.string.create_post_successful);
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
                        stackView.getFront().showData(p.getId(), p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());

                        p = AppModel.topics.getData().get(1%count);
                        stackView.getMid().showData(p.getId(), p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
                        p = AppModel.topics.getData().get(2%count);
                        stackView.getBack().showData(p.getId(), p.getType(), getCardIcon(p.getType()), p.getTypeName(), p.getTitle());

                        enableAudioButton(true);
                        stackView.refresh();
                        stackView.unlock();
                    }

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
                AppModel.topics.loadAsync(BaseActivity.sInstance, cnd, loadDataCallback);
                enableAudioButton(false);
            }
        });
    }

    private void enableAudioButton(boolean val) {
        ((FragmentToolbar) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar)).enableAudioButton(val);
    }

    public void showTopicDetail(final int type) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                if (type == Topic.TYPE_SPEAKING) {
                    fragment_speaking.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layout = fragment_speaking.getLayoutParams();
                    layout.height = (int) (BaseActivity.getScreenHeight() - BaseActivity.getStatusBarHeight() - stackView.getFront().getHeight() * stackView.getFront().getScaleX());
                    fragment_speaking.setLayoutParams(layout);
                } else {
                    fragment_writing.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layout = fragment_writing.getLayoutParams();
                    layout.height = (int) (BaseActivity.getScreenHeight() - BaseActivity.getStatusBarHeight() - stackView.getFront().getHeight() * stackView.getFront().getScaleX());
                    fragment_writing.setLayoutParams(layout);
                }
            }
        });

    }


    public void onOpenDrawer(View v) {
        //drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new DrawerEventListener(drawer));
        drawer.openDrawer(navigationView);
    }

    //upload file handler
    private final AbstractUploadServiceReceiver uploadReceiver =
            new AbstractUploadServiceReceiver() {

                // you can override this progress method if you want to get
                // the completion progress in percent (0 to 100)
                // or if you need to know exactly how many bytes have been transferred
                // override the method below this one
                @Override
                public void onProgress(String uploadId, int progress) {
                    System.out.println("The progress of the upload with ID " + uploadId + " is: " + progress);
                }

                @Override
                public void onError(String uploadId, Exception exception) {
                    System.out.println("Error in upload with ID: " + uploadId + ". " + exception.getLocalizedMessage() + " " + exception);
                    AppModel.posts.raiseError(uploadId);
                }

                @Override
                public void onCompleted(String uploadId,
                                        int serverResponseCode,
                                        String serverResponseMessage) {
                    try {
                        JSONObject obj = new JSONObject(serverResponseMessage);
                        AppModel.posts.updateAudioLink(uploadId, obj.getString("url"));
                    }catch (Exception E) {
                        AppModel.posts.raiseError(uploadId);
                        E.printStackTrace();
                    }
                }
            };

}

class MyCustomCardStackViewDelegate implements CustomCardStackViewDelegate {
    @Override
    public void onLaunched(final CustomCardStackView ccsv) {
    }

    @Override
    public void onChangedActiveItem(int front, int mid, int back, CustomCardStackView ccsv) {
        Topic p = AppModel.topics.getData().get(back);
        ccsv.getBack().showData(p.getId(), p.getType(), TaskListActivity.getCardIcon(p.getType()), p.getTypeName(), p.getTitle());

        p = AppModel.topics.getData().get(mid);
        ccsv.getMid().showData(p.getId(), p.getType(), TaskListActivity.getCardIcon(p.getType()), p.getTypeName(), p.getTitle());

        p = AppModel.topics.getData().get(front);
        ccsv.getFront().showData(p.getId(), p.getType(), TaskListActivity.getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
    }

    @Override
    public void onBeforeChangedActiveItem(int front, int mid, int back, CustomCardStackView ccsv) {
        //Topic p = AppModel.topics.getData().get(back);
        //ccsv.getFront().showData(p.getId(), p.getType(), TaskListActivity.getCardIcon(p.getType()), p.getTypeName(), p.getTitle());
    }

    @Override
    public int getTotalRecords() {
        return AppModel.topics.getData().size();
    }


    @Override
    public void onBeforeSelectItem(int index, final CustomCardStackView ccsv) {
        ((TaskListActivity)(TaskListActivity.sInstance)).hideSaySomethingGroup();
    }

    @Override
    public void onSelectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onSelectItem " + index);
        TaskListActivity act = (TaskListActivity) TaskListActivity.sInstance;
        act.showTopicDetail(ccsv.getFront().getStateIntData("type"));
    }

    @Override
    public void onDeselectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onDeselectItem " + index);
        ccsv.getFront().rollback();
    }
}

