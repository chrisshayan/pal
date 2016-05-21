package vietnamworks.com.pal.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.AnimatorEndListener;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.configurations.AppUiConfig;
import vietnamworks.com.pal.custom_views.UserProfileNavView;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.entities.UserProfile;
import vietnamworks.com.pal.fragments.AdvisorPreviewFragment;
import vietnamworks.com.pal.fragments.BaseFragment;
import vietnamworks.com.pal.fragments.ChangePasswordFragment;
import vietnamworks.com.pal.fragments.ComposerFragment;
import vietnamworks.com.pal.fragments.PostDetailFragment;
import vietnamworks.com.pal.fragments.PostListFragment;
import vietnamworks.com.pal.fragments.ProfileFragment;
import vietnamworks.com.pal.fragments.TopicsFragment;
import vietnamworks.com.pal.fragments.UpdateProfileFragment;
import vietnamworks.com.pal.fragments.WelcomeFragment;
import vietnamworks.com.pal.models.CurrentUserProfile;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.models.Topics;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.AudioMixerService;
import vietnamworks.com.pal.services.Callback;
import vietnamworks.com.pal.services.ExceptionReportService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;
import vietnamworks.com.pal.services.ParseService;

public class TimelineActivity extends BaseActivity {

    public final static int REQUEST_CAMERA = 7000;
    public final static int SELECT_FILE = 7001;

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

    public static String resumeFromPushWithPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        displayHomeAsUpButton(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_image_dehaze);
        }

        //drawer -- header
        navHeaderView = UserProfileNavView.create(this, 0, 0, 0);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(navHeaderView);
        applyFont(navigationView);

        setNumberOfUnreadPostUI(0);
        setNumberOfUnreadEvaluatedPostUI(0);

        //listen to user profile changed then update drawer
        FirebaseService.SetUserProfileListener(new FirebaseService.UserProfileListener() {
            @Override
            public void onChanged(HashMap<String, Object> data) {
                UserProfile p = UserProfile.getCurrentUserProfile();
                if (navHeaderView != null) {
                    navHeaderView.updateStat(
                            p.getTotalPosts(),
                            p.getScore(),
                            p.getLevelCompletion()
                    );
                    navHeaderView.updateProfile(
                            p.getDisplayName(),
                            p.getLevelName(),
                            p.getAvatar()
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
                removeDrawerGuide();
                LocalStorage.set(R.string.ls_show_drawer_guide, true);
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
                int current_session = FirebaseService.getUserProfileIntValue("total_sessions", 0);

                getSupportActionBar().setHomeAsUpIndicator(deep == 0 ? R.drawable.ic_image_dehaze : R.drawable.ic_hardware_keyboard_backspace);

                if (current_session > 5 && deep == 0 && !LocalStorage.getBool(R.string.ls_show_drawer_guide, false)) {
                    drawer_guide.setVisibility(View.VISIBLE);
                    drawer_guide.setAlpha(0);
                    drawer_guide.animate().alpha(AppUiConfig.BASE_OVERLAY_ALPHA).setStartDelay(500).setDuration(500).setListener(new AnimatorEndListener(new Callback() {
                        @Override
                        public void onDone(Context ctx, Object obj) {
                            drawer_guide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeDrawerGuide();
                                    LocalStorage.set(R.string.ls_show_drawer_guide, true);
                                }
                            });
                        }
                    })).start();
                }

                hideKeyboard();
                updateToolbar();
            }
        });

        quest_view = findViewById(R.id.challenge_view);
        applyFont(quest_view);
        quest_view.setVisibility(View.GONE);
        txtQuest = (TextView) quest_view.findViewById(R.id.quest);

        quest_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quest_view.setVisibility(View.GONE);
                doQuest(v);
            }
        });

        if (!LocalStorage.getBool(R.string.ls_show_fab_guide, false)) {
            openFragment(new WelcomeFragment(), R.id.fragment_holder);
            setTitle(R.string.title_welcome);
            displayHomeAsUpButton(false);
        } else {
            handlePushNotification(null);
        }
        queryTotalUnreadPosts = Posts.getUnreadPostsCounterQuery();
        queryTotalUnreadEvaluatedPosts = Posts.getUnreadEvaluatedPostsCounterQuery();
        queryRandomQuest = Topics.getRandomTopicQuery();

        Topics.requestRandomTopics();

        //show version:
        try {
            TextView versionView = ((TextView) findViewById(R.id.version));
            String version = "Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " - build " + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionView.setText(version);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public void handlePushNotification(Bundle ext) {
        //read push content
        Bundle extras = ext;
        if (extras == null) {
            Intent intentNotification = getIntent();
            extras = intentNotification.getExtras();
        }
        if (extras!=null) {
            String jsonData = extras.getString( "com.parse.Data" );
            if (jsonData != null && !jsonData.isEmpty()) {
                try {
                    JSONObject object = new JSONObject(jsonData);
                    String post_id = object.getString("post_id");
                    if (post_id != null && !post_id.isEmpty()) {
                        TimelineActivity.resumeFromPushWithPostId = post_id;
                    } else {
                        TimelineActivity.resumeFromPushWithPostId = null;
                    }
                }catch (Exception E) {
                    E.printStackTrace();
                }
            }
        }
        if (TimelineActivity.resumeFromPushWithPostId != null) {
            autoLogin();
        }
        if (resumeFromPushWithPostId != null) {
            final String postID = resumeFromPushWithPostId;
            Posts.markAsRead(postID);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    Bundle b = new Bundle();
                    b.putString("id", postID);
                    allPostsFragment = PostListFragment.createAllPosts();
                    openFragment(allPostsFragment, R.id.fragment_holder);
                    openFragment(PostDetailFragment.create(b), R.id.fragment_holder, true);
                }
            });
        } else {
            onOpenAllPosts(null);
            setTitle(R.string.title_timeline);
        }
        resumeFromPushWithPostId = null;
    }


    public void removeDrawerGuide() {
        try {
            if (drawer_guide != null && drawer_guide.getParent() != null) {
                ((ViewGroup) drawer_guide.getParent()).removeView(drawer_guide);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
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
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                currentQuest = (Topic) new Topic().importData(snapshot.getValue());
                txtQuest.setText(currentQuest.getTitle());
                if (allPostsFragment != null) {
                    allPostsFragment.refresh();
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
        } else if (f instanceof ChangePasswordFragment) {
            ((ChangePasswordFragment) f).onLayoutChanged(isSoftKeyShown);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (!isBackKeyLocked()) {
                getSupportFragmentManager().popBackStack();
            }
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

        if (id == android.R.id.home) {
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
        CurrentUserProfile.increaseSessionCounter();

        autoLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();

        queryTotalUnreadPosts.removeEventListener(onChangedUnreadPostsValue);
        queryTotalUnreadEvaluatedPosts.removeEventListener(onChangedUnreadEvaluatedPostsValue);
        queryRandomQuest.removeEventListener(onChangedRandomTask);

        FirebaseService.setOnConnectionChanged(null);
    }

    private void updateToolbar() {
        Menu menu = toolbar.getMenu();
        Fragment f = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        /*
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int id = item.getItemId();
        }
        */

        if (f instanceof ComposerFragment) {
            showActionBar(R.string.title_composer, true);;
        } else if (f instanceof PostListFragment) {
            if (((PostListFragment) f).getFilterType() == PostListFragment.FILTER_ALL) {
                showActionBar(R.string.title_timeline, true);
            } else {
                showActionBar(R.string.title_evaluated_posts, true);
            }
        } else if (f instanceof PostDetailFragment) {
            showActionBar("", true);
        } else if (f instanceof TopicsFragment) {
            showActionBar(R.string.title_challenge, true);
        } else if (f instanceof AdvisorPreviewFragment) {
            showActionBar(R.string.title_advisor_rating, true);
        } else if (f instanceof UpdateProfileFragment) {
            showActionBar(R.string.title_update_profile, true);
        }

        if (f instanceof BaseFragment) {((BaseFragment)f).onResumeFromBackStack();}

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

    public void highlightAllPostMenuItem(boolean val) {
        TextView v = (TextView)findViewById(R.id.nav_item_allposts);
        if (v != null) {
            if (val) {
                applyFont(v, RobotoB, true);
                if (Utils.isLollipopOrLater()) {
                    v.setTextColor(getResources().getColor(R.color.colorTextPrimary, getTheme()));
                } else {
                    v.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                }
            } else {
                applyFont(v, RobotoR, true);
                if (Utils.isLollipopOrLater()) {
                    v.setTextColor(getResources().getColor(R.color.colorActionItem, getTheme()));
                } else {
                    v.setTextColor(getResources().getColor(R.color.colorActionItem));
                }
            }
        }
    }

    public void highlightEvaluatedMenuItem(boolean val) {
        TextView v = (TextView)findViewById(R.id.nav_item_evaluated);
        if (v != null) {
            if (val) {
                applyFont(v, RobotoB, true);
                if (Utils.isLollipopOrLater()) {
                    v.setTextColor(getResources().getColor(R.color.colorTextPrimary, getTheme()));
                } else {
                    v.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                }
            } else {
                applyFont(v, RobotoR, true);
                if (Utils.isLollipopOrLater()) {
                    v.setTextColor(getResources().getColor(R.color.colorActionItem, getTheme()));
                } else {
                    v.setTextColor(getResources().getColor(R.color.colorActionItem));
                }
            }
        }
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
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        if (!(f instanceof PostListFragment) || ((PostListFragment) f).getFilterType() != PostListFragment.FILTER_ALL) {
            if (allPostsFragment == null) {
                allPostsFragment = PostListFragment.createAllPosts();
            }
            openFragment(allPostsFragment, R.id.fragment_holder);
        }
        if (f instanceof PostListFragment) {
            ((PostListFragment) f).refresh();
        }
    }

    public void onOpenRecentEvaluatedPost(View v) {
        GaService.trackAction(R.string.ga_action_open_recent_evaluated_list);
        hideKeyboard();
        closeDrawer();
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

    private void logout() {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                FirebaseService.logout();
                ParseService.unRegisterUser();
                openActivity(AuthActivity.class);
                closeDrawer();
            }
        }, 500);
    }

    public void onLogout(View v) {
        GaService.trackAction(R.string.ga_action_logout);
        logout();
    }

    public void onOpenChangePasswordForm(View v) {
        GaService.trackAction(R.string.ga_action_change_password);
        setTimeout(new Runnable() {
            @Override
            public void run() {
                pushFragment(new ChangePasswordFragment(), R.id.fragment_holder, true);
                closeDrawer();
            }
        }, 500);

    }

    public void onOpenUserProfile(View v) {
        GaService.trackAction(R.string.ga_action_change_password);
        setTimeout(new Runnable() {
            @Override
            public void run() {
                pushFragment(new ProfileFragment(), R.id.fragment_holder);
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

    public void doQuest(View v) {
        if (currentQuest != null) {
            pushFragment(new ComposerFragment().setTopic(currentQuest.getTitle(), currentQuest.getId(), currentQuest.getHint()), R.id.fragment_holder);
        }
    }

    public void doNothing(View v) {}

    public Topic getCurrentQuest() {
        return currentQuest;
    }

    public View getQuestView() {
        return quest_view;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(getString(R.string.avatar_picker_take_photo_temp_file))) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                    if (fragment instanceof ProfileFragment) {
                        ((ProfileFragment)fragment).onSelectedAvatar(Utils.getFixOrientationBitmap(f.getAbsolutePath(), 256, 256));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getRealPathFromURI(selectedImageUri);

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (fragment instanceof ProfileFragment) {
                    ((ProfileFragment)fragment).onSelectedAvatar(Utils.getFixOrientationBitmap(tempPath, 256, 256));
                }
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    public void resetToMainTimeline() {
        hideKeyboard();
        setTitle(R.string.title_timeline);
        openFragmentAndClean(allPostsFragment, R.id.fragment_holder);
    }


    private boolean isProcessAutoLogin = false;
    private void autoLogin() {
        if (isProcessAutoLogin) {
            return;
        }
        isProcessAutoLogin = true;

        long last_login = LocalStorage.getLong(R.string.ls_last_login, 0);

        if (Utils.getMillis() - last_login > 60*60*1000) {
            LocalStorage.set(R.string.ls_last_login, Utils.getMillis());
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            String email = LocalStorage.getString(R.string.ls_last_success_email, "");
            String pwd = LocalStorage.getString(R.string.ls_last_success_password, "");
            if (email.isEmpty() || pwd.isEmpty()) {
                isProcessAutoLogin = false;
                dialog.hide();
                logout();
            } else {
                FirebaseService.login(email, Utils.r13(pwd), new AsyncCallback() {
                    @Override
                    public void onSuccess(Context ctx, Object obj) {
                        isProcessAutoLogin = false;
                        dialog.hide();
                    }

                    @Override
                    public void onError(Context ctx, int error_code, String message) {
                        isProcessAutoLogin = false;
                        dialog.hide();
                        ExceptionReportService.report("Fail to auto login from last success");
                        logout();
                    }
                });
            }
        }
    }
}
