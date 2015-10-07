package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/7/15.
 */
public class FragmentWriting extends FragmentBase {
    EditText input;
    ViewGroup buttons;

    //private int inputHeight;

    public FragmentWriting() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_writing, container, false);
        input = ((EditText) rootView.findViewById(R.id.textAnswer));
        buttons = ((ViewGroup) rootView.findViewById(R.id.textAnswerButtons));

        /*
        final int height = inputHeight - buttons.getHeight() - (int)(16* ActivityBase.density);
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layout = (ViewGroup.LayoutParams) input.getLayoutParams();
                layout.height = (int) (height);
                input.setLayoutParams(layout);
            }
        });
        */
        return rootView;
    }

    /*
    public void show(int view_height) {
        inputHeight = view_height;
    }
    */
}