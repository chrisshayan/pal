package vietnamworks.com.pal.common;

import android.animation.Animator;

import vietnamworks.com.pal.services.Callback;

/**
 * Created by duynk on 12/9/15.
 */
public class AnimatorEndListener implements Animator.AnimatorListener {
    Callback callback;
    public AnimatorEndListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (callback != null) {
            callback.onDone(null, animation);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
