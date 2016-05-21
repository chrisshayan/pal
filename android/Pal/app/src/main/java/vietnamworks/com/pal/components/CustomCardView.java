package vietnamworks.com.pal.components;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.StateObject;

/**
 * Created by duynk on 10/6/15.
 */
public class CustomCardView extends FrameLayout {
    public final static int STATE_LOADING = 0;
    public final static int STATE_NORMAL = 1;
    public final static int STATE_INPUT = 2;
    public final static int STATE_MESSAGE = 3;

    public StateObject stateData = new StateObject();


    private CustomCardStackView refStack;
    private ViewGroup cardView;
    private View header;
    private View hr;
    ProgressBar progressBar;

    private ImageView icon;
    private TextView body;
    private TextView title;
    private EditText input;

    private int state = STATE_NORMAL;

    public CustomCardView(Context context) {
        super(context);
        initializeViews(context);
        setupDefaultData();
        setupUI();
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        setupDefaultData();
        setupUI();
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        setupDefaultData();
        setupUI();
    }

    private void setupDefaultData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("icon", R.mipmap.ic_launcher);
        map.put("state", STATE_NORMAL);
        map.put("body", "");
        map.put("title", "");
        map.put("id", "");
        map.put("type", -1);
        stateData.pushState(map);
    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_card, this);
        BaseActivity.applyFont(this);
    }

    @Override
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
        BaseActivity.applyFont(this);

        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    BaseActivity.sInstance.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    BaseActivity.sInstance.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
        });
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

    public String getTopic() {
        if (body.getText().length() == 0) {
            return input.getText().toString();
        } else {
            return body.getText().toString().trim();
        }
    }

    public String getAnswer() {
        if (body.getText().length() == 0) {
            return input.getText().toString();
        } else {
            return body.getText().toString().trim();
        }
    }

    public int getState() {
        return stateData.getIntParam("state");
    }

    private void setData(String id, int state, int type, int icon, String title, String text) {
        stateData.setParam("id", id);
        stateData.setParam("icon", icon);
        stateData.setParam("title", title);
        stateData.setParam("state", state);
        stateData.setParam("body", text);
        stateData.setParam("type", type);
    }

    private void setData(HashMap<String, Object> stateData, String id, int state, int type, int icon, String title, String text) {
        stateData.put("id", id);
        stateData.put("icon", icon);
        stateData.put("title", title);
        stateData.put("state", state);
        stateData.put("body", text);
        stateData.put("type", type);
    }

    public void startLoading() {
        if (getState() == STATE_INPUT) {
            HashMap<String, Object> state = stateData.cloneState();
            setData(state, "", STATE_LOADING, -1, R.drawable.ic_search_grey, "", getResources().getString(R.string.message_loading));
            stateData.setState(1, state);
        } if (getState() != STATE_LOADING) {
            setData("", STATE_LOADING, -1, R.drawable.ic_search_grey, "", getResources().getString(R.string.message_loading));
            setupUI();
        }
    }

    public void showData(String id, int type, int icon, String title, String text) {
        if (getState() == STATE_INPUT) {
            HashMap<String, Object> state = stateData.cloneState();
            setData(state, id, STATE_NORMAL, type, icon, title, text);
            stateData.setState(1, state);
        } else {
            setData(id, STATE_NORMAL, type, icon, title, text);
            setupUI();
        }
    }

    public void showMessage(String message) {
        if (getState() == STATE_INPUT) {
            HashMap<String, Object> state = stateData.cloneState();
            setData(state, "", STATE_MESSAGE, -1, R.drawable.ic_launcher, "Message", message);
            stateData.setState(1, state);
        } else {
            setData("", STATE_MESSAGE, -1, R.drawable.ic_launcher, "Message", message);
            setupUI();
        }
    }

    public void showInput(int type, int icon, String title) {
        stateData.cloneAndPushState();
        this.input.setText("");
        setData("", STATE_INPUT, type, icon, title, "");
        setupUI();
    }

    public void rollback() {
        stateData.popState();
        setupUI();
    }

    private void setupUI() {
        if (stateData.size() > 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    int state = (int) stateData.getIntParam("state");
                    int iconId = (int) stateData.getIntParam("icon");
                    String titleTxt = (String) stateData.getStringParam("title");
                    String text = (String) stateData.getStringParam("body");

                    icon.setImageResource(iconId);
                    title.setText(titleTxt);
                    body.setText(text);

                    if (state == STATE_NORMAL) {
                        body.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        input.setVisibility(GONE);
                    } else if (state == STATE_INPUT) {
                        body.setVisibility(GONE);
                        progressBar.setVisibility(GONE);
                        input.setVisibility(VISIBLE);
                    } else if (state == STATE_LOADING) {
                        icon.setImageResource(R.drawable.ic_search_grey);
                        body.setVisibility(GONE);
                        progressBar.setVisibility(VISIBLE);
                        input.setVisibility(GONE);
                    } else if (state == STATE_MESSAGE) {
                        icon.setImageResource(R.drawable.ic_search_grey);
                        body.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        input.setVisibility(GONE);
                    }
                }
            });
        }
    }

    public Object getStateData(String key) {
        return stateData.getParam(key);
    }

    public int getStateIntData(String key) {
        return (int)stateData.getParam(key);
    }

    public String getStateStringData(String key) {
        return stateData.getParam(key).toString();
    }
}
