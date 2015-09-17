package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/15/15.
 */
public class MainActivity extends AppCompatActivity {
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
            TalkWithMeFragment fragment = TalkWithMeFragment.create(this);
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public void onSelectTopic(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RecorderFragment next = RecorderFragment.create(this, AppModel.topics.getData().get(this.mCurrentTopicIndex).mTitle);
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onToggleRecorder(View v) {
        RecorderFragment f = (RecorderFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onToggleRecorder();
    }

    public void onCancelRecorder(View v) {
        RecorderFragment f = (RecorderFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.stopPlayer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TalkWithMeFragment next = TalkWithMeFragment.create(this);
        transaction.replace(R.id.main_fragment_container, next);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onSubmitRecord(View v) {

    }

    public void onReplay(View v) {
        RecorderFragment f = (RecorderFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        f.onReplay();
    }

}
