package vietnamworks.com.pal.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import vietnamworks.com.pal.ActivityBase;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/6/15.
 */
public class CustomCardView extends FrameLayout {
    private CustomCardStackView refStack;
    private ViewGroup cardView;
    private View header;
    private View hr;
    ProgressBar progressBar;

    private ImageView icon;
    private TextView body;
    private TextView title;
    private EditText input;

    private int customType;

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
        cardView = (ViewGroup) this.findViewById(R.id.card_view);
        icon = (ImageView)this.findViewById(R.id.cc_icon);
        body = (TextView)this.findViewById(R.id.cc_body);
        title = (TextView)this.findViewById(R.id.cc_title);
        header = (View)this.findViewById(R.id.cc_header);
        hr = (View)this.findViewById(R.id.cc_hr);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        input = (EditText)findViewById(R.id.cc_input);


        ActivityBase.applyFont(this);
        progressBar.setVisibility(GONE);
    }

    public void startLoading() {
        icon.setImageResource(R.drawable.ic_search_grey);
        title.setText(getResources().getString(R.string.message_loading));
        body.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public void setData(int type, int icon, String title, String text) {
        this.customType = type;
        this.icon.setImageResource(icon);
        this.title.setText(title);
        this.body.setText(text);
        hr.setVisibility(VISIBLE);
        header.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);

        if (text == null || text.trim().length() == 0) {
            body.setVisibility(GONE);
            input.setVisibility(VISIBLE);
        } else {
            input.setVisibility(GONE);
            body.setVisibility(VISIBLE);
        }
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

    public ViewGroup getCardView() {
        return cardView;
    }

    public int getCustomType() {
        return customType;
    }

    public void setCustomType(int customType) {
        this.customType = customType;
    }
}
