package vietnamworks.com.pal.components;

import android.animation.Animator;
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

    public void Lock() {this.isLocked = true;}
    public void Unlock() {this.isLocked = false;}

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
        this.Lock();

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

    public final String[] STATE_NAME = {"none", "pre", "idle", "drag", "drag-out", "scroll back", "reorder", "fly-in", "pre-select", "select", "open-card", "close-card"};

    public final static float CARD_MARGIN = 10f;
    public final static float CARD_MARGIN_DELTA = 0f;
    public final static float SWIPE_MIN_DISTANCE = 50f;
    public final static float SWIPE_MIN_DT = 500;
    public final static float MOVE_MIN_DISTANCE = 3f;
    public final static float CARD_SCALE_STEP = 0.05f;
    public final static float CARD_TRIGGER_PERCENT = 0.25f;
    public final static float MAX_ROTATE_ANGLE = 10.0f;


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
        frontLayout = (FrameLayout.LayoutParams)front.getLayoutParams();
        frontLayout.setMargins(0, (int) (2*CARD_MARGIN*density), 0, 0);
        frontLayout.width = (int)(screen_size[0]*0.9f);
        frontLayout.height = (int)(frontLayout.width*3.0f/4.0f);
        front.setScaleX(1.0f);
        front.setScaleY(1.0f);
        front.setLayoutParams(frontLayout);
        front.setBackgroundColor(getResources().getColor(R.color.icons));

        midLayout = (FrameLayout.LayoutParams)mid.getLayoutParams();
        midLayout.setMargins(0, (int) (CARD_MARGIN * density), 0, 0);
        midLayout.width = (int)(screen_size[0]*0.9f);
        midLayout.height = (int)(frontLayout.width*3.0f/4.0f);
        mid.setScaleX(1.0f - CARD_SCALE_STEP);
        mid.setScaleY(1.0f - CARD_SCALE_STEP);
        mid.setLayoutParams(midLayout);

        backLayout = (FrameLayout.LayoutParams) back.getLayoutParams();
        backLayout.setMargins(backLayout.leftMargin, 0, 0, 0);
        backLayout.width = (int)(screen_size[0]*0.9f);
        backLayout.height = (int)(frontLayout.width*3.0f/4.0f);
        back.setScaleX(1.0f - CARD_SCALE_STEP * 2.0f);
        back.setScaleY(1.0f - CARD_SCALE_STEP * 2.0f);
        back.setLayoutParams(backLayout);


        front.setVisibility(VISIBLE);
        if (this.totalItem < 1) {
            mid.setVisibility(INVISIBLE);
            back.setVisibility(INVISIBLE);
            this.Lock();
        } else {
            mid.setVisibility(VISIBLE);
            back.setVisibility(VISIBLE);
            this.Unlock();
        }
    }

    public void refresh() {
        if (delegate != null) {
            this.totalItem = this.delegate.getTotalRecords();
        }
        if (this.totalItem < 1) {
            mid.setVisibility(INVISIBLE);
            back.setVisibility(INVISIBLE);
            this.Lock();
        } else {
            mid.setVisibility(VISIBLE);
            back.setVisibility(VISIBLE);
            this.Unlock();
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
                            front.animate().translationY(-screen_size[1]/2 + front.getHeight()/2).setDuration(100).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {}
                                @Override
                                public void onAnimationCancel(Animator animation) {}
                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    int[] screen_size = ActivityBase.getScreenSize();
                                    float scale = screen_size[0]*1.0f/front.getWidth();
                                    front.animate().scaleX(scale).scaleY(scale).setDuration(100).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {}
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (delegate != null) {
                                                delegate.onSelectItem(itemIndex % delegate.getTotalRecords(), CustomCardStackView.this);
                                            }
                                        }
                                        @Override
                                        public void onAnimationCancel(Animator animation) {}
                                        @Override
                                        public void onAnimationRepeat(Animator animation) {}
                                    }).start();
                                }
                            }).start();
                        }
                    });
                    break;
                case STATE_TRANS_CLOSE_CARD:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            front.animate().scaleX(1).scaleY(1).setDuration(100).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    front.animate().translationY(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {}
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            back.setVisibility(VISIBLE);
                                            mid.setVisibility(VISIBLE);
                                            switchState(STATE_IDLE);
                                        }
                                        @Override
                                        public void onAnimationCancel(Animator animation) {}
                                        @Override
                                        public void onAnimationRepeat(Animator animation) {}
                                    }).start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            }).start();
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
                requiredUpdateLayout = true;
                break;
            case STATE_DRAG:
            case STATE_SCROLL_BACK:
            case STATE_DRAG_OUT:
            case STATE_PRE_SELECT_ANIM:
                if (frontLayout.leftMargin != targetScrollX) {
                    frontLayout.leftMargin = Common.lerp(frontLayout.leftMargin, targetScrollX, 0.5f);
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
                    switchState(STATE_TRANS_OPEN_CARD);
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
                    backLayout.leftMargin = Common.lerp(backLayout.leftMargin, targetScrollX, 0.5f);
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
                    if (_state == STATE_PRE_INIT) {
                        initLayout();
                        if (delegate != null) {
                            delegate.onLaunched(CustomCardStackView.this);
                            refresh();
                        }
                        switchState(STATE_IDLE);
                        return;
                    }
                    if (_state != STATE_REORDER) {
                        final float density = getResources().getDisplayMetrics().density;
                        front.setLayoutParams(frontLayout);
                        if (movingScale >= 1.0f) {
                            front.setBackgroundColor(getResources().getColor(android.support.design.R.color.material_grey_50));
                        } else {
                            front.resetBackgroundColor();
                        }
                        front.setRotation(movingScale * MAX_ROTATE_ANGLE * Common.sign(frontLayout.leftMargin));

                        float mid_scalingFactor = (1.0f - CARD_SCALE_STEP) + CARD_SCALE_STEP * movingScale;
                        mid.setScaleX(mid_scalingFactor);
                        mid.setScaleY(mid_scalingFactor);
                        midLayout.setMargins(0, (int) ((CARD_MARGIN + (CARD_MARGIN - CARD_MARGIN_DELTA) * movingScale) * density), 0, 0);
                        mid.setLayoutParams(midLayout);

                        float back_scalingFactor = (1.0f - CARD_SCALE_STEP * 2.0f) + CARD_SCALE_STEP * movingScale;
                        back.setScaleX(back_scalingFactor);
                        back.setScaleY(back_scalingFactor);
                        backLayout.setMargins(backLayout.leftMargin, (int) (((CARD_MARGIN - CARD_MARGIN_DELTA) * movingScale) * density), 0, 0);
                        back.setLayoutParams(backLayout);
                    } else {
                        holder.removeAllViews();
                        holder.addView(front);
                        holder.addView(back);
                        holder.addView(mid);

                        front.resetBackgroundColor();
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

