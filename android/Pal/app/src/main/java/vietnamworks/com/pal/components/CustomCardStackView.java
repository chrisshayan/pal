package vietnamworks.com.pal.components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import vietnamworks.com.pal.ActivityBase;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/6/15.
 */
public class CustomCardStackView extends FrameLayout {
    CustomCardView front, mid, back;
    FrameLayout holder;
    FrameLayout.LayoutParams frontLayout;
    FrameLayout.LayoutParams backLayout;
    FrameLayout.LayoutParams midLayout;
    private boolean isLocked = false;
    private boolean _preventOpenCard = false;

    private int itemIndex = 0;
    private int totalItem = 0;
    private CustomCardStackViewDelegate delegate;

    private float density;

    Thread thread;
    private boolean isAlive = true;
    private android.os.Handler handler = new android.os.Handler();

    public CustomCardStackView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CustomCardStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CustomCardStackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_card_stack, this);
    }

    public void lock() {this.isLocked = true;}
    public void unlock() {this.isLocked = false;}
    public void preventOpenCard(boolean val) {this._preventOpenCard = val;}
    public void snooze() {
        if (state == STATE_IDLE || state == STATE_SNOOZE) {
            this.switchState(STATE_SNOOZE2);
        }
    }

    public void setDelegate(CustomCardStackViewDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onFinishInflate() {
        float d = this.getResources().getDisplayMetrics().density;

        super.onFinishInflate();
        front = (CustomCardView) this.findViewById(R.id.ccs_front);
        mid = (CustomCardView) this.findViewById(R.id.ccs_mid);
        back = (CustomCardView) this.findViewById(R.id.ccs_back);
        holder = (FrameLayout) this.findViewById(R.id.ccs_holder);

        front.setVisibility(INVISIBLE);
        mid.setVisibility(INVISIBLE);
        back.setVisibility(INVISIBLE);

        front.setHolderRef(this);

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(isAlive) {
                        update();
                        sleep(40);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        itemIndex = 0;
        switchState(STATE_PRE_INIT);

        thread.start();
    }

    @Override protected void onDetachedFromWindow() {
        isAlive = false;
        super.onDetachedFromWindow();
    }

    public final static int STATE_NONE = 0;
    public final static int STATE_PRE_INIT = STATE_NONE + 1;
    public final static int STATE_IDLE = STATE_PRE_INIT + 1;
    public final static int STATE_DRAG = STATE_IDLE + 1;
    public final static int STATE_DRAG_OUT = STATE_DRAG + 1;
    public final static int STATE_SCROLL_BACK = STATE_DRAG_OUT + 1;
    public final static int STATE_REORDER = STATE_SCROLL_BACK + 1;
    public final static int STATE_FLY_IN = STATE_REORDER + 1;
    public final static int STATE_PRE_SELECT_ANIM = STATE_FLY_IN + 1;
    public final static int STATE_TRANS_OPEN_CARD = STATE_PRE_SELECT_ANIM + 1;
    public final static int STATE_TRANS_CLOSE_CARD = STATE_TRANS_OPEN_CARD + 1;
    public final static int STATE_SNOOZE = STATE_TRANS_CLOSE_CARD + 1;
    public final static int STATE_SNOOZE2 = STATE_SNOOZE + 1;

    public final String[] STATE_NAME = {"none", "pre", "idle", "drag", "drag-out", "scroll back", "reorder", "fly-in", "pre-select", "open-card", "close-card", "snooze", "snooze2"};

    public final static float CARD_MARGIN = 10f;
    public final static float SWIPE_MIN_DISTANCE = 50f;
    public final static float SWIPE_MIN_DT = 500;
    public final static float MOVE_MIN_DISTANCE = 3f;
    public final static float CARD_SCALE_STEP = 0.05f;
    public final static float CARD_TRIGGER_PERCENT = 0.25f;
    public final static float MAX_ROTATE_ANGLE = 5.0f;


    private float mDownX;
    private int originLayoutMargin;
    private int state = STATE_NONE;
    private int nextState = STATE_NONE;
    private int lastState = STATE_NONE;
    private int targetScrollX;
    private boolean isFakeDrag = false;
    private long lastTimeTouch = 0;
    private long frameDt;
    private long lastUpdate;

    private void switchState(int state) {nextState = state;}

    public void closeCard() {
        if (state == STATE_TRANS_OPEN_CARD) {
            switchState(STATE_TRANS_CLOSE_CARD);
        }
    }

    public void onChildTouchEvent(CustomCardView child, MotionEvent ev) {
        if (!isLocked && !isFakeDrag && (state == STATE_IDLE || state == STATE_DRAG || state == STATE_DRAG_OUT)) {
            final int action = MotionEventCompat.getActionMasked(ev);
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                if (state == STATE_DRAG_OUT) {
                    switchState(STATE_REORDER);
                } else {
                    int distance = (int) (ev.getRawX() - mDownX);
                    long dt = System.currentTimeMillis() - lastTimeTouch;
                    if (dt < SWIPE_MIN_DT && Math.abs(distance) > density*SWIPE_MIN_DISTANCE) {
                        //is swipe
                        targetScrollX = (int)((front.getWidth()*CARD_TRIGGER_PERCENT*2) * Common.sign(distance));
                        switchState(STATE_DRAG);
                        isFakeDrag = true;
                    } else {
                        if (Math.abs(distance) < MOVE_MIN_DISTANCE*density) {
                            switchState(STATE_PRE_SELECT_ANIM);
                        } else {
                            switchState(STATE_SCROLL_BACK);
                        }
                    }
                }
            }
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    lastTimeTouch = System.currentTimeMillis();
                    mDownX = ev.getRawX();
                    originLayoutMargin = frontLayout.leftMargin;
                    if (state != STATE_DRAG && state != STATE_DRAG_OUT) {
                        switchState(STATE_DRAG);
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    targetScrollX = originLayoutMargin + (int) (ev.getRawX() - mDownX);
                    break;
                }
            }
        }
    }

    private void initLayout() {
        int[] screen_size = ActivityBase.getScreenSize();

        density = this.getResources().getDisplayMetrics().density;
        int card_width = (int)(screen_size[0]*0.9f);
        int card_height = (int)(card_width*9.0f/16.0f);

        frontLayout = (FrameLayout.LayoutParams)front.getLayoutParams();
        frontLayout.setMargins(0, 0, 0, 0);
        frontLayout.width = card_width;
        frontLayout.height = card_height;
        front.setScaleX(1.0f);
        front.setScaleY(1.0f);
        front.setLayoutParams(frontLayout);
        //front.setBackgroundColor(getResources().getColor(R.color.icons));
        front.setBackgroundResource(R.drawable.layout_corner_bg);

        midLayout = (FrameLayout.LayoutParams)mid.getLayoutParams();
        midLayout.setMargins(0, (int) (-CARD_MARGIN * density), 0, 0);
        midLayout.width = card_width;
        midLayout.height = card_height;
        mid.setScaleX(1.0f - CARD_SCALE_STEP);
        mid.setScaleY(1.0f - CARD_SCALE_STEP);
        mid.setLayoutParams(midLayout);
        mid.setBackgroundResource(R.drawable.layout_corner_bg);

        backLayout = (FrameLayout.LayoutParams) back.getLayoutParams();
        backLayout.setMargins(backLayout.leftMargin, (int) (-2*CARD_MARGIN * density), 0, 0);
        backLayout.width = card_width;
        backLayout.height = card_height;
        back.setScaleX(1.0f - CARD_SCALE_STEP * 2.0f);
        back.setScaleY(1.0f - CARD_SCALE_STEP * 2.0f);
        back.setLayoutParams(backLayout);
        back.setBackgroundResource(R.drawable.layout_corner_bg);


        front.setVisibility(VISIBLE);
        if (this.totalItem < 1) {
            mid.setVisibility(INVISIBLE);
            back.setVisibility(INVISIBLE);
        } else {
            mid.setVisibility(VISIBLE);
            back.setVisibility(VISIBLE);
        }
    }

    public void refresh() {
        if (delegate != null) {
            this.totalItem = this.delegate.getTotalRecords();
        }
        if (this.totalItem < 1) {
            mid.setVisibility(INVISIBLE);
            back.setVisibility(INVISIBLE);
        } else {
            mid.setVisibility(VISIBLE);
            back.setVisibility(VISIBLE);
        }
    }

    public CustomCardView getFront() {return this.front;}
    public CustomCardView getBack() {return this.back;}
    public CustomCardView getMid() {return this.mid;}

    private void update() {
        frameDt = Math.min(System.currentTimeMillis() - lastUpdate, 100);
        lastUpdate = System.currentTimeMillis();
        if (state != nextState) {
            System.out.println("CHANGE TO STATE ... " + STATE_NAME[nextState]);
            switch (nextState) {
                case STATE_PRE_INIT:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initLayout();
                            if (delegate != null) {
                                delegate.onLaunched(CustomCardStackView.this);
                                refresh();
                            }
                            switchState(STATE_IDLE);
                        }
                    });
                    break;
                case STATE_IDLE:
                    isFakeDrag = false;
                    break;
                case STATE_SCROLL_BACK:
                case STATE_PRE_SELECT_ANIM:
                    targetScrollX = 0;
                    break;
                case STATE_FLY_IN:
                    backLayout.leftMargin = targetScrollX;
                    targetScrollX = 0;
                    if (delegate != null) {
                        final int total = delegate.getTotalRecords();
                        if (total > 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    delegate.onBeforeChangedActiveItem( (itemIndex + 1) % total, ((itemIndex + 1) + 2) % total, CustomCardStackView.this);
                                }
                            });
                        }
                    }
                    break;
                case STATE_TRANS_OPEN_CARD:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            back.setVisibility(GONE);
                            mid.setVisibility(GONE);
                            int[] screen_size = ActivityBase.getScreenSize();
                            float scale = screen_size[0]*1.0f/front.getWidth();

                            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mid, "translationY", CARD_MARGIN*density);
                            ObjectAnimator anim2 = ObjectAnimator.ofFloat(back, "translationY", 2*CARD_MARGIN*density);
                            ObjectAnimator anim3 = ObjectAnimator.ofFloat(front, "translationY", -screen_size[1]/2 + (front.getHeight()/scale)/2 + ActivityBase.sInstance.getStatusBarHeight());
                            ObjectAnimator anim4 = ObjectAnimator.ofFloat(front, "scaleX", scale);
                            ObjectAnimator anim5 = ObjectAnimator.ofFloat(front, "scaleY", scale);
                            AnimatorSet set = new AnimatorSet();
                            set.play(anim1).with(anim2).with(anim3);
                            set.play(anim4).with(anim5).after(anim3);
                            set.setDuration(250);
                            set.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (delegate != null) {
                                        delegate.onBeforeSelectItem(itemIndex % delegate.getTotalRecords(), CustomCardStackView.this);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    front.setBackgroundResource(R.drawable.layout_flat_bg);
                                    if (delegate != null) {
                                        delegate.onSelectItem(itemIndex % delegate.getTotalRecords(), CustomCardStackView.this);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            });
                            set.start();
                        }
                    });
                    break;
                case STATE_TRANS_CLOSE_CARD:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            front.setBackgroundResource(R.drawable.layout_corner_bg);
                            ObjectAnimator anim1 = ObjectAnimator.ofFloat(front, "translationY", 0);
                            ObjectAnimator anim2 = ObjectAnimator.ofFloat(front, "scaleX", 1);
                            ObjectAnimator anim3 = ObjectAnimator.ofFloat(front, "scaleY", 1);
                            AnimatorSet set = new AnimatorSet();
                            set.play(anim2).with(anim3).before(anim1);
                            set.setDuration(250);
                            set.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {}

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    back.setVisibility(VISIBLE);
                                    mid.setVisibility(VISIBLE);
                                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(mid, "translationY", 0);
                                    anim1.setDuration(100);
                                    anim1.start();

                                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(back, "translationY", 0);
                                    anim2.setDuration(100);
                                    anim2.start();

                                    switchState(STATE_IDLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            });
                            set.start();
                        }
                    });
                    break;
                case STATE_SNOOZE:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorSet set = new AnimatorSet();
                            ObjectAnimator scaleXAnimator_1 = ObjectAnimator.ofFloat(front, "scaleX", 1.01f);
                            ObjectAnimator scaleXAnimator_2 = ObjectAnimator.ofFloat(front, "scaleX", 1.0f);
                            ObjectAnimator scaleXAnimator_3 = ObjectAnimator.ofFloat(front, "scaleX", 1.01f);
                            ObjectAnimator scaleXAnimator_4 = ObjectAnimator.ofFloat(front, "scaleX", 1.0f);

                            ObjectAnimator scaleYAnimator_1 = ObjectAnimator.ofFloat(front, "scaleY", 1.01f);
                            ObjectAnimator scaleYAnimator_2 = ObjectAnimator.ofFloat(front, "scaleY", 1.0f);
                            ObjectAnimator scaleYAnimator_3 = ObjectAnimator.ofFloat(front, "scaleY", 1.01f);
                            ObjectAnimator scaleYAnimator_4 = ObjectAnimator.ofFloat(front, "scaleY", 1.0f);

                            set.play(scaleXAnimator_1).with(scaleYAnimator_1);
                            set.play(scaleXAnimator_2).with(scaleYAnimator_2).after(scaleXAnimator_1);
                            set.play(scaleXAnimator_3).with(scaleYAnimator_3).after(scaleXAnimator_2);
                            set.play(scaleXAnimator_4).with(scaleYAnimator_4).after(scaleXAnimator_3);
                            set.setDuration(100);
                            set.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (delegate != null) {
                                        int count = delegate.getTotalRecords();
                                        if (count == 0) {
                                            delegate.onBeforeSelectItem(-1, CustomCardStackView.this);
                                        } else {
                                            delegate.onBeforeSelectItem(itemIndex % delegate.getTotalRecords(), CustomCardStackView.this);
                                        }
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (delegate != null) {
                                        int count = delegate.getTotalRecords();
                                        if (count == 0) {
                                            delegate.onSelectItem(-1, CustomCardStackView.this);
                                        } else {
                                            delegate.onSelectItem(itemIndex % delegate.getTotalRecords(), CustomCardStackView.this);
                                        }
                                        switchState(nextState != state?nextState:STATE_IDLE);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            set.start();
                        }
                    });
                    break;
                case STATE_SNOOZE2:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorSet set = new AnimatorSet();
                            ObjectAnimator anim1 = ObjectAnimator.ofFloat(front, "rotation", 0.0f, 5.0f);
                            anim1.setDuration(50);
                            ObjectAnimator anim2 = ObjectAnimator.ofFloat(front, "rotation", 5.0f, -5.0f);
                            anim2.setDuration(100);
                            ObjectAnimator anim3 = ObjectAnimator.ofFloat(front, "rotation", -5.0f, 5.0f);
                            anim3.setDuration(100);
                            ObjectAnimator anim4 = ObjectAnimator.ofFloat(front, "rotation", 5.0f, -5.0f);
                            anim4.setDuration(100);
                            ObjectAnimator anim5 = ObjectAnimator.ofFloat(front, "rotation", -5.0f, 0.0f);
                            anim5.setDuration(50);
                            set.play(anim1).before(anim2);
                            set.play(anim2).before(anim3);
                            set.play(anim3).before(anim4);
                            set.play(anim4).before(anim5);
                            set.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    switchState(STATE_IDLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            set.start();
                        }
                    });
                    break;
            }

            lastState = state;
            state = nextState;
        }

        boolean requiredUpdateLayout = false;
        float movingPercent = 0f;
        switch (state) {
            case STATE_PRE_INIT:
                break;
            case STATE_DRAG:
            case STATE_SCROLL_BACK:
            case STATE_DRAG_OUT:
            case STATE_PRE_SELECT_ANIM:
                if (frontLayout.leftMargin != targetScrollX) {
                    frontLayout.leftMargin = Common.lerp(frontLayout.leftMargin, targetScrollX, 0.8f);
                    requiredUpdateLayout = true;
                }
                if (requiredUpdateLayout) {
                    movingPercent = Math.min(Math.abs(frontLayout.leftMargin) / (front.getWidth()*CARD_TRIGGER_PERCENT), 1.0f);
                    if (movingPercent >= 1.0f) {
                        switchState(STATE_DRAG_OUT);
                    } else if (movingPercent > 0 && state == STATE_DRAG_OUT) {
                        switchState(STATE_DRAG);
                    }
                }

                if (frontLayout.leftMargin == 0 && state == STATE_SCROLL_BACK) {
                    switchState(STATE_IDLE);
                }
                if (frontLayout.leftMargin == 0 && state == STATE_PRE_SELECT_ANIM) {
                    boolean open_card = true;
                    if (delegate != null) {
                        open_card = (delegate.getTotalRecords() > 0);
                    }
                    switchState(!open_card?STATE_SNOOZE:STATE_TRANS_OPEN_CARD);
                }

                if (isFakeDrag && state == STATE_DRAG_OUT) {
                    System.out.println(Math.abs(frontLayout.leftMargin) + " " + front.getWidth()*CARD_TRIGGER_PERCENT*2);
                }
                if ( isFakeDrag && state == STATE_DRAG_OUT && Math.abs(frontLayout.leftMargin) >= (int)(front.getWidth()*CARD_TRIGGER_PERCENT*2)) {
                    switchState(STATE_REORDER);
                }

                break;
            case STATE_FLY_IN:
                if (backLayout.leftMargin != targetScrollX) {
                    backLayout.leftMargin = Common.lerp(backLayout.leftMargin, targetScrollX, 0.8f);
                    requiredUpdateLayout = true;
                }
                if (targetScrollX == backLayout.leftMargin) {
                    switchState(STATE_IDLE);
                    if (delegate != null) {
                        itemIndex ++;
                        final int total = delegate.getTotalRecords();
                        if (total > 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    delegate.onChangedActiveItem(itemIndex % total, (itemIndex + 2) % total, CustomCardStackView.this);
                                }
                            });
                        }

                    }
                }
                break;
            case STATE_REORDER:
                requiredUpdateLayout = true;
                break;
            default:
                break;
        }

        final float movingScale = movingPercent;
        final int _state = this.state;

        if (requiredUpdateLayout) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (_state != STATE_REORDER) {
                        final float density = getResources().getDisplayMetrics().density;
                        front.setLayoutParams(frontLayout);

                        /*
                        if (movingScale >= 1.0f) {
                            front.setBackgroundColor(getResources().getColor(android.support.design.R.color.material_grey_50));
                        } else {
                            front.resetBackgroundColor();
                        }
                        */
                        front.setRotation(movingScale * MAX_ROTATE_ANGLE * Common.sign(frontLayout.leftMargin));

                        float mid_scalingFactor = (1.0f - CARD_SCALE_STEP) + CARD_SCALE_STEP * movingScale;
                        mid.setScaleX(mid_scalingFactor);
                        mid.setScaleY(mid_scalingFactor);
                        midLayout.setMargins(0, (int) ((-CARD_MARGIN + CARD_MARGIN * movingScale) * density), 0, 0);
                        mid.setLayoutParams(midLayout);

                        float back_scalingFactor = (1.0f - CARD_SCALE_STEP * 2.0f) + CARD_SCALE_STEP * movingScale;
                        back.setScaleX(back_scalingFactor);
                        back.setScaleY(back_scalingFactor);
                        backLayout.setMargins(backLayout.leftMargin, (int) ((-2 * CARD_MARGIN + CARD_MARGIN * movingScale) * density), 0, 0);
                        back.setLayoutParams(backLayout);
                    } else {
                        holder.removeAllViews();
                        holder.addView(front);
                        holder.addView(back);
                        holder.addView(mid);

                        //front.resetBackgroundColor();
                        front.setRotation(0);
                        mid.setHolderRef(front.getHolderRef());
                        front.setHolderRef(null);

                        CustomCardView tmp = front;
                        front = mid;
                        mid = back;
                        back = tmp;

                        initLayout();
                        switchState(STATE_FLY_IN);
                    }
                }
            });
        }
    }
}

