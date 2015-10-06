package vietnamworks.com.pal.components;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/6/15.
 */
public class CustomCardView extends FrameLayout {
    private CustomCardStackView refStack;
    private CardView cardView;
    private int originBackgroundColor;

    private ImageView icon;
    private TextView body;
    private TextView title;

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
        icon = (ImageView)this.findViewById(R.id.cc_icon);
        body = (TextView)this.findViewById(R.id.cc_body);
        title = (TextView)this.findViewById(R.id.cc_title);

        originBackgroundColor = getResources().getColor(R.color.icons);
    }

    public void setData(int icon, String title, String text) {
        this.icon.setImageResource(icon);
        this.title.setText(title);
        this.body.setText(text);
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

    public ImageView getIcon() {
        return icon;
    }

    public TextView getBody() {
        return body;
    }

    public TextView getTitle() {
        return title;
    }
}
