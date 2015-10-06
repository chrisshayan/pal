package vietnamworks.com.pal;

import android.content.Context;
import android.os.Bundle;

import vietnamworks.com.pal.components.CustomCardStackView;
import vietnamworks.com.pal.components.CustomCardStackViewDelegate;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.AsyncCallback;

/**
 * Created by duynk on 10/6/15.
 */
public class ActivityTaskList extends ActivityBase {
    private CustomCardStackView stackView;

    public ActivityTaskList() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        stackView = (CustomCardStackView)this.findViewById(R.id.cards_stack_view);
        stackView.setDelegate(new MyCustomCardStackViewDelegate());
    }
}

class MyCustomCardStackViewDelegate implements CustomCardStackViewDelegate {
    @Override
    public void onLaunched(final CustomCardStackView ccsv) {
        System.out.println("onLaunched");
        //start loading
        ccsv.getFront().setData(R.drawable.ic_launcher, "", "Loading");
        ccsv.lock();
        AppModel.topics.loadAsync(ActivityBase.sInstance, new AsyncCallback() {
            @Override
            public void onSuccess(Context context, Object obj) {
                System.out.println("onSuccess");
                int count = AppModel.topics.getData().size();
                if (count > 0) {
                    Topic p = AppModel.topics.getData().get(0);
                    ccsv.getFront().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone:R.drawable.ic_conversation, p.getTypeName(), p.getTitle());
                }
                if (count > 1) {
                    Topic p = AppModel.topics.getData().get(1);
                    ccsv.getMid().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone:R.drawable.ic_conversation, p.getTypeName(), p.getTitle());
                }
                if (count > 2) {
                    Topic p = AppModel.topics.getData().get(2);
                    ccsv.getBack().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone:R.drawable.ic_conversation, p.getTypeName(), p.getTitle());
                } else {
                    Topic p = AppModel.topics.getData().get(0);
                    ccsv.getBack().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone:R.drawable.ic_conversation, p.getTypeName(), p.getTitle());
                }
                ccsv.refresh();
                ccsv.unlock();
            }

            @Override
            public void onError(Context context, int code, String message) {
                System.out.println("Fail to load data " + message);
                ccsv.getFront().setData(R.drawable.ic_launcher, "", "Fail to load data. Touch to retry");
                ccsv.unlock();
                ccsv.snooze();
            }
        });
    }

    @Override
    public void onChangedActiveItem(int front, int back, CustomCardStackView ccsv) {
    }

    @Override
    public void onBeforeChangedActiveItem(int front, int back, CustomCardStackView ccsv) {
        Topic p = AppModel.topics.getData().get(back);
        ccsv.getBack().setData(p.getType()==Topic.TYPE_SPEAKING?R.drawable.ic_microphone:R.drawable.ic_conversation, p.getTypeName(), p.getTitle());
    }

    @Override
    public int getTotalRecords() {
        return AppModel.topics.getData().size();
    }

    @Override
    public void onSelectItem(int index, final CustomCardStackView ccsv) {
        System.out.println("onSelectItem " + index);
        if (index >= 0) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ccsv.closeCard();
                }
            }, 3000);
        } else {
            this.onLaunched(ccsv);
        }
    }
}

