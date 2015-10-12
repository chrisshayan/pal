package vietnamworks.com.pal.components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.Callback;
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
                        long dt = Math.min(Math.max((long) (1000.0f / 60.0f) - frameDt, 10), 100);
                        sleep(dt);
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
    public final static int STATE_OPEN_CARD = STATE_PRE_SELECT_ANIM + 1;
    public final static int STATE_CLOSE_CARD = STATE_OPEN_CARD + 1;
    public final static int STATE_SNOOZE = STATE_CLOSE_CARD + 1;
    public final static int STATE_SNOOZE2 = STATE_SNOOZE + 1;

    public final String[] STATE_NAME = {"none", "pre", "idle", "drag", "drag-out", "scroll back", "reorder", "fly-in", "pre-select", "open-card", "close-card", "snooze", "snooze2"};

    public final static float CARD_MARGIN = 10f;
    public final static float SWIPE_MIN_DISTANCE = 50f;
    public final static float SWIPE_MIN_DT = 500;
    public final static float MOVE_MIN_DISTANCE = 3f;
    public final static float CARD_SCALE_STEP = 2f;
    public final static float CARD_TRIGGER_PERCENT = 0.25f;
    public final static float FIRST_CARD_MAX_ROTATE_ANGLE = 5.0f;
    public final static float HIDDEN_CARD_ROTATE_ANGLE = 1.0f;
    public final static float CARD_WIDTH = 0.75f;
    public final static float CARD_HEIGHT = 0.65f;

    private float mDownX;
    private int originLayoutMargin;
    private int state = STATE_NONE;
    private boolean lockState = false;
    private int nextState = STATE_NONE;
    private int lastState = STATE_NONE;
    private int targetScrollX;
    private boolean isFakeDrag = false;
    private long lastTimeTouch = 0;
    private long frameDt;
    private long lastUpdate;
    private float cardScale;

    private void switchState(int state) {nextState = state;}
    public int getState() {
        return state;
    }

    public void closeCard() {
        if (state == STATE_OPEN_CARD) {
            switchState(STATE_CLOSE_CARD);
        }
    }

    public void onChildTouchEvent(CustomCardView child, MotionEvent ev) {
        if (!isLocked && !isFakeDrag && (state == STATE_IDLE || state == STATE_DRAG || state == STATE_DRAG_OUT)) {
            final int action = MotionEventCompat.getActionMasked(ev);
            int distance = (int) (ev.getRawX() - mDownX);

            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                if (state == STATE_DRAG_OUT) {
                    targetScrollX = (int)((front.getWidth()*CARD_TRIGGER_PERCENT*2) * Common.sign(distance));
                    isFakeDrag = true;
                } else {
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
        int[] screen_size = BaseActivity.getScreenSize();

        density = this.getResources().getDisplayMetrics().density;
        int card_width = (int)(screen_size[0]*CARD_WIDTH);
        int card_height = (int)(screen_size[1]*CARD_HEIGHT);

        int card_scale_px = (int)(CARD_SCALE_STEP*density);
        cardScale = 1 - ((card_height - card_scale_px)*1.0f/card_height);

        frontLayout = (FrameLayout.LayoutParams)front.getLayoutParams();
        frontLayout.setMargins(0, 0, 0, 0);
        frontLayout.width = card_width;
        frontLayout.height = card_height;
        front.setScaleX(1.0f);
        front.setScaleY(1.0f);
        front.setLayoutParams(frontLayout);
        //front.setBackgroundColor(getResources().getColor(R.color.icons));
        front.setBackgroundResource(R.drawable.layout_corner_bg);
        front.setRotation(0f);

        midLayout = (FrameLayout.LayoutParams)mid.getLayoutParams();
        //midLayout.setMargins(0, (int) (-CARD_MARGIN * density), 0, 0);
        midLayout.width = card_width;
        midLayout.height = card_height;
        mid.setScaleX(1.0f - cardScale);
        mid.setScaleY(1.0f - cardScale);
        mid.setLayoutParams(midLayout);
        mid.setBackgroundResource(R.drawable.layout_corner_bg);
        //mid.setRotation(HIDDEN_CARD_ROTATE_ANGLE);
        mid.animate().rotation(HIDDEN_CARD_ROTATE_ANGLE).start();

        backLayout = (FrameLayout.LayoutParams) back.getLayoutParams();
        //backLayout.setMargins(backLayout.leftMargin, (int) (-2*CARD_MARGIN * density), 0, 0);
        backLayout.width = card_width;
        backLayout.height = card_height;
        back.setScaleX(1.0f - cardScale * 2.0f);
        back.setScaleY(1.0f - cardScale * 2.0f);
        back.setLayoutParams(backLayout);
        back.setBackgroundResource(R.drawable.layout_corner_bg);
        //back.setRotation(HIDDEN_CARD_ROTATE_ANGLE*2);

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
            final AnimatorSet set = new AnimatorSet();
            ObjectAnimator rotate1 = ObjectAnimator.ofFloat(mid, "rotation", HIDDEN_CARD_ROTATE_ANGLE, 0).setDuration(100);
            ObjectAnimator rotate2 = ObjectAnimator.ofFloat(back, "rotation", HIDDEN_CARD_ROTATE_ANGLE*2, 0).setDuration(200);
            set.play(rotate1).with(rotate2);
            set.addListener(new AnimationEndedEvent(new Callback() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mid.setVisibility(INVISIBLE);
                            back.setVisibility(INVISIBLE);

                        }
                    });
                }
            }));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    set.start();
                }
            });


        } else {
            if (state == STATE_IDLE || state == STATE_CLOSE_CARD) {
                mid.setVisibility(VISIBLE);
                back.setVisibility(VISIBLE);
                final AnimatorSet set = new AnimatorSet();

                ObjectAnimator scaleXAnimator_1 = ObjectAnimator.ofFloat(front, "scaleX", 0.99f).setDuration(100);
                ObjectAnimator scaleXAnimator_2 = ObjectAnimator.ofFloat(front, "scaleX", 1.0f).setDuration(100);

                ObjectAnimator scaleYAnimator_1 = ObjectAnimator.ofFloat(front, "scaleY", 0.99f).setDuration(100);
                ObjectAnimator scaleYAnimator_2 = ObjectAnimator.ofFloat(front, "scaleY", 1.0f).setDuration(100);

                ObjectAnimator rotate1 = ObjectAnimator.ofFloat(mid, "rotation", mid.getRotation(), HIDDEN_CARD_ROTATE_ANGLE).setDuration(100);
                ObjectAnimator rotate2 = ObjectAnimator.ofFloat(back, "rotation", back.getRotation(), HIDDEN_CARD_ROTATE_ANGLE * 2).setDuration(200);

                set.play(scaleXAnimator_1).with(scaleYAnimator_1);
                set.play(scaleXAnimator_2).with(scaleYAnimator_2).after(scaleXAnimator_1);
                set.play(rotate1).with(rotate2).after(scaleXAnimator_2);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        set.start();
                    }
                });
            }
        }
    }

    public CustomCardView getFront() {return this.front;}
    public CustomCardView getBack() {return this.back;}
    public CustomCardView getMid() {return this.mid;}

    public void doManualSelect() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                switchState(STATE_OPEN_CARD);
            }
        });
    }

    private void update() {
        if (lockState) {
            return;
        }
        frameDt = Math.min(System.currentTimeMillis() - lastUpdate, 500);
        lastUpdate = System.currentTimeMillis();
        if (state != nextState) {
            lastState = state;
            state = nextState;
            System.out.println("CHANGE STATE: " + STATE_NAME[lastState] + " -> " + STATE_NAME[state]);
            switch (state) {
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
                    //refresh();
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
                                    delegate.onBeforeChangedActiveItem( (itemIndex + 1) % total, ((itemIndex + 1) + 1) % total,((itemIndex + 1) + 2) % total, CustomCardStackView.this);
                                }
                            });
                        }
                    }
                    break;
                case STATE_OPEN_CARD:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            back.setVisibility(GONE);
                            mid.setVisibility(GONE);
                            int[] screen_size = BaseActivity.getScreenSize();
                            int new_height = (int)(screen_size[0]*9.0f/16.0f);

                            AnimatorSet set = new AnimatorSet();

                            ValueAnimator a1 = ValueAnimator.ofInt(front.getHeight(), new_height).setDuration(100);
                            a1.setInterpolator(new AccelerateInterpolator());
                            a1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    frontLayout.height = (int) animation.getAnimatedValue();
                                    front.setLayoutParams(frontLayout);
                                }
                            });


                            ValueAnimator a2 = ValueAnimator.ofInt(front.getWidth(), screen_size[0]).setDuration(100);
                            a2.setInterpolator(new BounceInterpolator());
                            a2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    frontLayout.width = (int) animation.getAnimatedValue();
                                    front.setLayoutParams(frontLayout);
                                }
                            });

                            ObjectAnimator a3 = ObjectAnimator.ofFloat(front, "y", 0);
                            a3.setInterpolator(new AccelerateInterpolator());

                            set.play(a1).before(a3);
                            set.play(a2).after(a3);

                            set.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (delegate != null) {
                                        int total = delegate.getTotalRecords();
                                        int index = total==0?-1:(itemIndex%total);
                                        delegate.onBeforeSelectItem(index, CustomCardStackView.this);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    front.setBackgroundResource(R.drawable.layout_flat_bg);
                                    if (delegate != null) {
                                        int total = delegate.getTotalRecords();
                                        int index = total==0?-1:(itemIndex%total);
                                        delegate.onSelectItem(index, CustomCardStackView.this);
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
                case STATE_CLOSE_CARD:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            front.setBackgroundResource(R.drawable.layout_corner_bg);
                            int[] screen_size = BaseActivity.getScreenSize();
                            int new_width = (int)(screen_size[0]*CARD_WIDTH);
                            int new_height = (int)(screen_size[1]*CARD_HEIGHT);

                            AnimatorSet set = new AnimatorSet();

                            ValueAnimator a1 = ValueAnimator.ofInt(front.getHeight(), new_height).setDuration(100);
                            a1.setInterpolator(new AccelerateInterpolator());
                            a1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    frontLayout.height = (int) animation.getAnimatedValue();
                                    front.setLayoutParams(frontLayout);
                                }
                            });


                            ValueAnimator a2 = ValueAnimator.ofInt(front.getWidth(), new_width).setDuration(100);
                            a2.setInterpolator(new AccelerateInterpolator());
                            a2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    frontLayout.width = (int) animation.getAnimatedValue();
                                    front.setLayoutParams(frontLayout);
                                }
                            });
                            ObjectAnimator a3 = ObjectAnimator.ofFloat(front, "translationY", 0);
                            a3.setInterpolator(new AccelerateInterpolator());

                            set.play(a2).before(a3);
                            set.play(a1).after(a3);
                            set.addListener(new AnimationEndedEvent(new Callback() {
                                @Override
                                public void run() {
                                    back.setVisibility(VISIBLE);
                                    mid.setVisibility(VISIBLE);
                                    switchState(STATE_IDLE);
                                    refresh();
                                    if (delegate != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int total = delegate.getTotalRecords();
                                                int index = total==0?-1:(itemIndex%total);
                                                delegate.onDeselectItem(index, CustomCardStackView.this);
                                            }
                                        });
                                    }
                                }
                            }));
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
                            set.addListener(new AnimationEndedEvent(new Callback() {
                                @Override
                                public void run() {
                                    switchState(STATE_IDLE);
                                }
                            }));
                            set.start();
                        }
                    });
                    break;
            }
        }

        boolean requiredUpdateLayout = false;
        float movingPercent = 0f, overMovingPercent = 0f;
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
                    float p = Math.abs(frontLayout.leftMargin) / (front.getWidth()*CARD_TRIGGER_PERCENT);
                    movingPercent = Math.min(p, 1.0f);
                    overMovingPercent = Math.max(Math.min(p - 1.0f, 1.0f), 0f);
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
                    switchState(!open_card?STATE_SNOOZE: STATE_OPEN_CARD);
                }
                /*
                if (isFakeDrag && state == STATE_DRAG_OUT) {
                    System.out.println(Math.abs(frontLayout.leftMargin) + " " + front.getWidth()*CARD_TRIGGER_PERCENT*2);
                }
                */
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
                                    delegate.onChangedActiveItem(itemIndex % total, (itemIndex + 1) % total, (itemIndex + 2) % total, CustomCardStackView.this);
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
        final float _overMovingPercent = overMovingPercent;
        final int _state = this.state;

        if (requiredUpdateLayout) {
            lockState = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (_state != STATE_REORDER) {
                        front.setLayoutParams(frontLayout);
                        front.setRotation(_overMovingPercent * FIRST_CARD_MAX_ROTATE_ANGLE * Common.sign(frontLayout.leftMargin));

                        float mid_scalingFactor = (1.0f - cardScale) + cardScale * movingScale;
                        mid.setScaleX(mid_scalingFactor);
                        mid.setScaleY(mid_scalingFactor);
                        mid.setRotation(HIDDEN_CARD_ROTATE_ANGLE * (1 - movingScale));

                        float back_scalingFactor = (1.0f - cardScale * 2.0f) + cardScale * movingScale;
                        back.setScaleX(back_scalingFactor);
                        back.setScaleY(back_scalingFactor);
                        back.setRotation(HIDDEN_CARD_ROTATE_ANGLE + HIDDEN_CARD_ROTATE_ANGLE * (1 - movingScale));
                    } else {
                        holder.removeAllViews();
                        holder.addView(front);
                        holder.addView(back);
                        holder.addView(mid);

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
                    lockState = false;
                }
            });
        }
    }
}

class AnimationEndedEvent implements Animator.AnimatorListener {
    Callback callback;
    public AnimationEndedEvent(Callback callback) {this.callback = callback;}

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        callback.run();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}