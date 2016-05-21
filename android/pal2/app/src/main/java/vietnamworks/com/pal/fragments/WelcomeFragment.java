package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.AnimatorEndListener;
import vietnamworks.com.pal.configurations.AppUiConfig;
import vietnamworks.com.pal.services.Callback;
import vietnamworks.com.pal.services.LocalStorage;

/**
 * Created by duynk on 11/10/15.
 */
public class WelcomeFragment extends BaseFragment {
    boolean preventFabCollapseHintAnim;

    final static int BASE_TRANSLATE = 50;
    final static int BASE_ANIM_DURATION = 300;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_welcome, container, false);

        BaseActivity.applyFont(rootView);

        final View welcome_panel = rootView.findViewById(R.id.welcome_panel);
        welcome_panel.setVisibility(View.INVISIBLE);

        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_appear_anim);
        animation.setDuration(BASE_ANIM_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                welcome_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getView().findViewById(R.id.btn_getStarted).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStarted();
                    }
                });
                /*
                getView().findViewById(R.id.btn_getStarted_Text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStarted();
                    }
                });
                */
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                welcome_panel.startAnimation(animation);
            }
        }, 500);


        preventFabCollapseHintAnim = false;

        FloatingActionsMenu fab = (FloatingActionsMenu) rootView.findViewById(R.id.fab);
        fab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                getView().findViewById(R.id.tutor_1).setVisibility(View.GONE);

                View tutor_2 = getView().findViewById(R.id.tutor_2);
                tutor_2.setAlpha(0);
                tutor_2.setX(BASE_TRANSLATE);
                tutor_2.setVisibility(View.VISIBLE);
                tutor_2.animate().alpha(1f).x(0).setDuration(BASE_ANIM_DURATION).start();
            }

            @Override
            public void onMenuCollapsed() {
                if (!preventFabCollapseHintAnim) {
                    View tutor_1 = getView().findViewById(R.id.tutor_1);
                    tutor_1.setAlpha(0);
                    tutor_1.setX(-BASE_TRANSLATE);
                    tutor_1.setVisibility(View.INVISIBLE);
                    tutor_1.animate().alpha(1f).x(0).setDuration(BASE_ANIM_DURATION).start();
                }
            }
        });

        return rootView;
    }

    private void getStarted() {
        LocalStorage.set(R.string.ls_show_fab_guide, true);
        View overlay = getView().findViewById(R.id.overlay);
        overlay.setAlpha(0);
        overlay.setVisibility(View.VISIBLE);
        overlay.animate().alpha(AppUiConfig.BASE_OVERLAY_ALPHA).setDuration(BASE_ANIM_DURATION).start();

        FloatingActionsMenu fab = (FloatingActionsMenu) getView().findViewById(R.id.fab);
        fab.setScaleX(0);

        preventFabCollapseHintAnim = true;
        fab.collapseImmediately();

        fab.setVisibility(View.VISIBLE);
        fab.animate().scaleX(1).scaleY(1).setDuration(BASE_ANIM_DURATION).setInterpolator(new OvershootInterpolator()).setListener(new AnimatorEndListener(new Callback() {
            @Override
            public void onDone(Context ctx, Object obj) {
                preventFabCollapseHintAnim = false;
                View tutor_1 = getView().findViewById(R.id.tutor_1);
                tutor_1.setAlpha(0);
                tutor_1.setX(-BASE_TRANSLATE);
                tutor_1.setVisibility(View.VISIBLE);
                tutor_1.animate().alpha(1f).x(0).setDuration(BASE_ANIM_DURATION).start();
            }
        })).start();
    }
}
