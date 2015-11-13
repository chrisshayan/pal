package vietnamworks.com.pal.activities;

import android.animation.Animator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONObject;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.configurations.AppConfig;
import vietnamworks.com.pal.configurations.AppUiConfig;
import vietnamworks.com.pal.custom_views.UserProfileNavView;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.fragments.AdvisorPreviewFragment;
import vietnamworks.com.pal.fragments.ComposerFragment;
import vietnamworks.com.pal.fragments.PostDetailFragment;
import vietnamworks.com.pal.fragments.PostListFragment;
import vietnamworks.com.pal.fragments.TopicsFragment;
import vietnamworks.com.pal.fragments.WelcomeFragment;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.models.Topics;
import vietnamworks.com.pal.services.AudioMixerService;
import vietnamworks.com.pal.services.FileUploadService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;

public class TimelineActivity extends BaseActivity {
    private PostListFragment allPostsFragment;
    private PostListFragment evaluatedPostsFragment;
    UserProfileNavView navHeaderView;
    Toolbar toolbar;
    View drawer_guide;
    private View quest_view;
    DrawerLayout drawer;

    Query queryTotalUnreadPosts;
    Query queryTotalUnreadEvaluatedPosts;
    Query queryRandomQuest;

    Topic currentQuest;
    TextView txtQuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_image_dehaze);

        //drawer -- header
        navHeaderView = UserProfileNavView.create(this, 0, 0, 0);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(navHeaderView);

        setNumberOfUnreadPostUI(0);
        setNumberOfUnreadEvaluatedPostUI(0);

        //listen to user profile changed then update drawer
        FirebaseService.SetUserProfileListener(new FirebaseService.UserProfileListener() {
            @Override
            public void onChanged(HashMap<String, Object> data) {
                int score_1 = FirebaseService.getUserProfileIntValue("score_1", 0);
                int score_2 = FirebaseService.getUserProfileIntValue("score_2", 0);
                int score_3 = FirebaseService.getUserProfileIntValue("score_3", 0);
                int score_4 = FirebaseService.getUserProfileIntValue("score_4", 0);
                int score_5 = FirebaseService.getUserProfileIntValue("score_5", 0);
                int total = score_1 + score_2 + score_3 + score_4 + score_5;
                float score = 0;
                if (total > 0) {
                    score = Math.round(((score_1 + score_2 * 2 + score_3 * 3 + score_4 * 4 + score_5 * 5) * 10.0f) / total) / 10f;
                }
                if (navHeaderView != null) {
                    navHeaderView.updateStat(
                            FirebaseService.getUserProfileIntValue("total_posts", 0),
                            score,
                            FirebaseService.getUserProfileIntValue("level_completion", 0)
                    );
                    navHeaderView.updateProfile(
                            FirebaseService.getUserProfileStringValue("display_name"),
                            FirebaseService.getUserProfileStringValue("level_name", "Beginner"),
                            FirebaseService.getUserProfileStringValue("avatar")
                    );
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_guide = findViewById(R.id.overlay_with_drawer_guide);
        drawer_guide.setVisibility(View.GONE);

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (drawer_guide.getVisibility() == View.VISIBLE) {
                    drawer_guide.setVisibility(View.GONE);
                    LocalStorage.set(getString(R.string.local_storage_show_drawer_guide), true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int deep = getSupportFragmentManager().getBackStackEntryCount();
                getSupportActionBar().setHomeAsUpIndicator(deep == 0 ? R.drawable.ic_image_dehaze : R.drawable.ic_hardware_keyboard_backspace);

                if (deep == 0 && !LocalStorage.getBool(getString(R.string.local_storage_show_drawer_guide), false)) {
                    drawer_guide.setVisibility(View.VISIBLE);
                    drawer_guide.setAlpha(0);
                    drawer_guide.animate().alpha(AppUiConfig.BASE_OVERLAY_ALPHA).setStartDelay(500).setDuration(500).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawer_guide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    drawer_guide.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            drawer_guide.setVisibility(View.GONE);
                                            ((ViewGroup)drawer_guide.getParent()).removeView(drawer_guide);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });

                                    LocalStorage.set(getString(R.string.local_storage_show_drawer_guide), true);
                                }
                            });
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
                updateToolbar();
            }
        });

        quest_view = findViewById(R.id.challenge_view);
        quest_view.setVisibility(View.GONE);
        txtQuest = (TextView) quest_view.findViewById(R.id.quest);

        if (!LocalStorage.getBool(getString(R.string.local_storage_show_fab_guide), false)) {
            openFragment(new WelcomeFragment(), R.id.fragment_holder);
            setTitle(R.string.title_welcome);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            onOpenAllPosts(null);
            setTitle(R.string.title_timeline);
        }
        queryTotalUnreadPosts = Posts.getUnreadPostsCounterQuery();
        queryTotalUnreadEvaluatedPosts = Posts.getUnreadEvaluatedPostsCounterQuery();
        queryRandomQuest = Topics.getRandomTopicQuery();



        Topics.requestRandomTopics();
    }

    private ValueEventListener onChangedUnreadPostsValue = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setNumberOfUnreadPostUI((int) dataSnapshot.getChildrenCount());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ValueEventListener onChangedUnreadEvaluatedPostsValue = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setNumberOfUnreadEvaluatedPostUI((int) dataSnapshot.getChildrenCount());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ValueEventListener onChangedRandomTask = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount() > 0) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    currentQuest = (Topic) new Topic().importData(snapshot.getValue());
                    txtQuest.setText(currentQuest.getTitle());
                    if (allPostsFragment != null) {
                        allPostsFragment.refresh();
                    }
                    return;
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    @Override
    public void onLayoutChanged(Rect r, final boolean isSoftKeyShown) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        if (f instanceof AdvisorPreviewFragment) {
            ((AdvisorPreviewFragment) f).onLayoutChanged(isSoftKeyShown);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        updateToolbar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
            if (f instanceof ComposerFragment) {
                if (submitTask((ComposerFragment) f)) {
                    hideKeyboard();
                    //onBackPressed();
                    openFragmentAndClean(allPostsFragment, R.id.fragment_holder);
                }
            }

            return true;
        } else if (id == android.R.id.home) {
            int deep = getSupportFragmentManager().getBackStackEntryCount();
            if (deep == 0) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                drawer.openDrawer(navigationView);
            } else {
                getSupportFragmentManager().popBackStackImmediate();
                hideKeyboard();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseService.SetUserProfileListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryTotalUnreadPosts.addValueEventListener(onChangedUnreadPostsValue);
        queryTotalUnreadEvaluatedPosts.addValueEventListener(onChangedUnreadEvaluatedPostsValue);
        queryRandomQuest.addValueEventListener(onChangedRandomTask);

        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        queryTotalUnreadPosts.removeEventListener(onChangedUnreadPostsValue);
        queryTotalUnreadEvaluatedPosts.removeEventListener(onChangedUnreadEvaluatedPostsValue);
        queryRandomQuest.removeEventListener(onChangedRandomTask);

        uploadReceiver.unregister(this);
    }

    private void updateToolbar() {
        Menu menu = toolbar.getMenu();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int id = item.getItemId();
            if (id == R.id.action_send) {
                item.setVisible(f != null && f instanceof ComposerFragment);
            }
        }

        if (f instanceof ComposerFragment) {
            setTitle(R.string.title_composer);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (f instanceof PostListFragment) {
            if (((PostListFragment) f).getFilterType() == PostListFragment.FILTER_ALL) {
                setTitle(R.string.title_timeline);
            } else {
                setTitle(R.string.title_evaluated_posts);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (f instanceof PostDetailFragment) {
            setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (f instanceof TopicsFragment) {
            setTitle(R.string.title_challenge);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (f instanceof AdvisorPreviewFragment) {
            setTitle(R.string.title_advisor_rating);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setNumberOfUnreadPostUI(final int val) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                String txt = val + "";
                if ((val) >= 100) {
                    txt = "99+";
                }
                TextView view = ((TextView) findViewById(R.id.num_of_posts));
                view.setText(txt);
                view.setVisibility(val > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void setNumberOfUnreadEvaluatedPostUI(final int val) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                String txt = val + "";
                if ((val) >= 100) {
                    txt = "99+";
                }
                TextView view = ((TextView) findViewById(R.id.num_of_evaluated_post));
                view.setText(txt);
                view.setVisibility(val > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public void onOpenAllPosts(View v) {
        GaService.trackAction(R.string.ga_action_open_all_posts);
        hideKeyboard();
        closeDrawer();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (!(f instanceof PostListFragment) || ((PostListFragment) f).getFilterType() != PostListFragment.FILTER_ALL) {
                    if (allPostsFragment == null) {
                        allPostsFragment = PostListFragment.createAllPosts();
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    } else {
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    }
                }
                if (f instanceof PostListFragment) {
                    ((PostListFragment) f).refresh();
                }
            }
        }, 500);
    }

    public void onOpenRecentEvaluatedPost(View v) {
        GaService.trackAction(R.string.ga_action_open_recent_evaluated_list);
        hideKeyboard();
        closeDrawer();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (!(f instanceof PostListFragment) || ((PostListFragment) f).getFilterType() != PostListFragment.FILTER_EVALUATED) {
                    if (evaluatedPostsFragment == null) {
                        evaluatedPostsFragment = PostListFragment.createEvaluatedList();
                    }
                    openFragment(evaluatedPostsFragment, R.id.fragment_holder);
                }
                if (f instanceof PostListFragment) {
                    ((PostListFragment) f).refresh();
                }
            }
        }, 500);
    }

    public void onLogout(View v) {
        GaService.trackAction(R.string.ga_action_logout);
        setTimeout(new Runnable() {
            @Override
            public void run() {
                FirebaseService.logout();
                openActivity(AuthActivity.class);
                closeDrawer();
            }
        }, 500);

    }

    public void onOpenWelcomeSaySomethingComposer(View v) {
        GaService.trackAction(R.string.ga_action_open_say_something);
        AudioMixerService.stop();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (f instanceof WelcomeFragment) {
                    if (allPostsFragment == null) {
                        allPostsFragment = PostListFragment.createAllPosts();
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    } else {
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    }
                } else {
                    ((FloatingActionsMenu) findViewById(R.id.fab)).collapseImmediately();
                }
                pushFragment(new ComposerFragment().setTopic(getString(R.string.introduce_yourself), "", null), R.id.fragment_holder);
            }
        }, 100);
    }

    public void onOpenSaySomethingComposer(View v) {
        GaService.trackAction(R.string.ga_action_open_say_something);
        AudioMixerService.stop();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (f instanceof WelcomeFragment) {
                    if (allPostsFragment == null) {
                        allPostsFragment = PostListFragment.createAllPosts();
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    } else {
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    }
                } else {
                    ((FloatingActionsMenu) findViewById(R.id.fab)).collapseImmediately();
                }
                pushFragment(new ComposerFragment(), R.id.fragment_holder);
            }
        }, 100);
    }

    public void onOpenChallengeList(View v) {
        GaService.trackAction(R.string.ga_action_open_topics);
        AudioMixerService.stop();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (f instanceof WelcomeFragment) {
                    if (allPostsFragment == null) {
                        allPostsFragment = PostListFragment.createAllPosts();
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    } else {
                        openFragment(allPostsFragment, R.id.fragment_holder);
                    }
                } else {
                    ((FloatingActionsMenu) findViewById(R.id.fab)).collapseImmediately();
                }
                pushFragment(new TopicsFragment(), R.id.fragment_holder);
            }
        }, 100);
    }

    private boolean submitTask(ComposerFragment f) {
        Topics.requestRandomTopics();
        f.stopRecorder();

        String audio = f.getAudioPath();
        String subject = f.getSubject();
        String topic = f.getTopic();
        String message = f.getMessage().trim();

        if (audio == null && message.length() == 0) {
            toast(R.string.empty_message);
            return false;
        }

        if (audio == null) { //text
            Posts.addText(subject, topic, message);
        } else {
            String post_id = Posts.addAudioAsync(subject, topic, message);
            String server_file_path = Utils.getAudioServerFileName(FirebaseService.authData.getUid(), post_id);
            FileUploadService.upload(
                    this,
                    post_id,
                    AppConfig.AudioUploadURL,
                    audio,
                    server_file_path);
        }
        toast(R.string.create_post_successful);
        return true;
    }

    private final AbstractUploadServiceReceiver uploadReceiver =
            new AbstractUploadServiceReceiver() {
                @Override
                public void onProgress(String uploadId, int progress) {
                    System.out.println("The progress of the upload with ID " + uploadId + " is: " + progress);
                }

                @Override
                public void onError(String uploadId, Exception exception) {
                    System.out.println("Error in upload with ID: " + uploadId + ". " + exception.getLocalizedMessage() + " " + exception);
                    Posts.raiseError(uploadId);
                }

                @Override
                public void onCompleted(String uploadId,
                                        int serverResponseCode,
                                        String serverResponseMessage) {
                    System.out.println("Upload with ID " + uploadId
                            + " has been completed with HTTP " + serverResponseCode
                            + ". Response from server: " + serverResponseMessage);
                    try {
                        JSONObject obj = new JSONObject(serverResponseMessage);
                        Posts.updateAudioLink(uploadId, obj.getString("url"));
                    } catch (Exception E) {
                        Posts.raiseError(uploadId);
                        E.printStackTrace();
                    }
                }
            };

    public void doQuest(View v) {
        if (currentQuest != null) {
            pushFragment(new ComposerFragment().setTopic(currentQuest.getTitle(), currentQuest.getId(), currentQuest.getHint()), R.id.fragment_holder);
        }
    }

    public Topic getCurrentQuest() {
        return currentQuest;
    }

    public View getQuestView() {
        return quest_view;
    }
}
