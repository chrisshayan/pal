package vietnamworks.com.pal.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.configurations.AppUiConfig;

/**
 * Created by duynk on 11/10/15.
 */
public class WelcomeFragment extends BaseFragment {
    boolean preventFabCollapseHintAnim;

    final static int BASE_TRANSLATE = 200;
    final static int BASE_ANIM_DURATION = 500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_welcome, container, false);

        BaseActivity.applyFont(rootView);

        View welcome_panel = rootView.findViewById(R.id.welcome_panel);
        welcome_panel.setAlpha(0);
        welcome_panel.setY(BASE_TRANSLATE);
        welcome_panel.animate().alpha(1f).y(0).setDuration(BASE_ANIM_DURATION*2).setStartDelay(BASE_ANIM_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((ImageButton) getView().findViewById(R.id.btn_getStarted)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStarted();
                    }
                });
                ((Button) getView().findViewById(R.id.btn_getStarted_Text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStarted();
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
        View overlay = getView().findViewById(R.id.overlay);
        overlay.setAlpha(0);
        overlay.setVisibility(View.VISIBLE);
        overlay.animate().alpha(AppUiConfig.BASE_OVERLAY_ALPHA).setDuration(BASE_ANIM_DURATION).start();

        FloatingActionsMenu fab = (FloatingActionsMenu) getView().findViewById(R.id.fab);
        fab.setScaleX(0);

        preventFabCollapseHintAnim = true;
        fab.collapseImmediately();

        fab.setVisibility(View.VISIBLE);
        fab.animate().scaleX(1).scaleY(1).setDuration(BASE_ANIM_DURATION).setInterpolator(new OvershootInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                preventFabCollapseHintAnim = false;
                View tutor_1 = getView().findViewById(R.id.tutor_1);
                tutor_1.setAlpha(0);
                tutor_1.setX(-BASE_TRANSLATE);
                tutor_1.setVisibility(View.VISIBLE);
                tutor_1.animate().alpha(1f).x(0).setDuration(BASE_ANIM_DURATION).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
