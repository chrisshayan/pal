package vietnamworks.com.pal.components;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/6/15.
 */
public class CustomCardView extends FrameLayout {
    private CustomCardStackView refStack;
    private CardView cardView;
    private int originBackgroundColor;

    public CustomCardView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_card, this);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        cardView = (CardView) this.findViewById(R.id.card_view);
        originBackgroundColor = getResources().getColor(R.color.icons);
    }

    public void setBackgroundColor(int color) {
        cardView.setCardBackgroundColor(color);
    }

    public void resetBackgroundColor() {
        cardView.setCardBackgroundColor(originBackgroundColor);
    }

    public void setDefaultBackgroundColor(int color) {
        originBackgroundColor = color;
    }

    public void setHolderRef(CustomCardStackView ref) {
        this.refStack = ref;
    }
    public CustomCardStackView getHolderRef() {
        return this.refStack;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.refStack != null) {
            this.refStack.onChildTouchEvent(this, event);
        }
        return true;
    }
}
