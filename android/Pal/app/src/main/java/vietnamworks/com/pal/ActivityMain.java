package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.fragments.FragmentRecentTopic;
import vietnamworks.com.pal.fragments.FragmentRecorder;
import vietnamworks.com.pal.fragments.FragmentSubmitTopic;
import vietnamworks.com.pal.fragments.FragmentTalkWithMe;

/**
 * Created by duynk on 9/15/15.
 */
public class ActivityMain extends ActivityBase {
    private Menu menu;
    public int mCurrentTopicIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (findViewById(R.id.main_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            FragmentTalkWithMe fragment = FragmentTalkWithMe.create();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
        }
        else if (id == R.id.action_show_recent_list) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentRecentTopic next = new FragmentRecentTopic();
            transaction.replace(R.id.main_fragment_container, next);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public void hideMenuItem(int index)
    {
        if (menu != null) {
            MenuItem mi = menu.getItem(index);
            if (mi != null) {
                mi.setVisible(false);
            }
        }
    }

    public void showMenuItem(int index)
    {
        if (menu != null) {
            MenuItem mi = menu.getItem(index);
            if (mi != null) {
                mi.setVisible(true);
            }
        }
    }

    public void hideListMenuItem() {
        hideMenuItem(0);
    }

    public void resetMenuItem() {
        showMenuItem(0);
    }

    public Fragment getActiveFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }

    public void onRetryToLoadTopic(View v) {
        FragmentTalkWithMe fragment = (FragmentTalkWithMe) getActiveFragment();
        fragment.refreshTopics();
    }

    public void onSelectTopic(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentRecorder next = FragmentRecorder.create(this, AppModel.topics.getData().get(this.mCurrentTopicIndex).getTitle());
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onSaySomething(View v) {
        mCurrentTopicIndex = -1;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String title = getString(R.string.say_something);
        if (this.mCurrentTopicIndex >= 0) {
            title = AppModel.topics.getData().get(this.mCurrentTopicIndex).getTitle();
        }
        FragmentRecorder next = FragmentRecorder.create(this, title);
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onToggleRecorder(View v) {
        FragmentRecorder f = (FragmentRecorder)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onToggleRecorder();
    }

    public void onCancelRecorder(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (f instanceof FragmentRecorder) {
            ( (FragmentRecorder)f).stopPlayer();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentTalkWithMe next = FragmentTalkWithMe.create();
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onSubmitRecord(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentSubmitTopic next = FragmentSubmitTopic.create(this, mCurrentTopicIndex);
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onReplay(View v) {
        FragmentRecorder f = (FragmentRecorder)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onReplay();
    }

    public void onConfirmTopicAndSubmitData(View v) {
        FragmentSubmitTopic f = (FragmentSubmitTopic)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onSubmit();
    }

    public void onRetrySubmitData(View v) {
        FragmentSubmitTopic f = (FragmentSubmitTopic)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onRetry();
    }

    public void onCancelSubmitData(View v) {
        FragmentSubmitTopic f = (FragmentSubmitTopic)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onCancel();
    }
}
