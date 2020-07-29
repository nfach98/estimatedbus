package com.example.eta.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eta.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton extends ConstraintLayout {

    private TextView text;
    private ProgressBar progress;

    public ProgressButton(Context context) {
        this(context,null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(),R.layout.layout_button_progress,this);

        text = findViewById(R.id.text);
        progress = findViewById(R.id.progress);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0);
        String buttonText = a.getString(R.styleable.ProgressButton_text);
        boolean loading = a.getBoolean(R.styleable.ProgressButton_loading, false);
        boolean enable = a.getBoolean(R.styleable.ProgressButton_enabled, true);

        setText(buttonText);
        setLoading(loading);
        setEnabled(enable);
    }

    public void setText(CharSequence text) {
        this.text.setText(text);
    }

    public void setLoading(boolean state){
        if(isEnabled()) {
            if (state) {
                text.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                setEnabled(false);
            } else {
                text.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                setEnabled(true);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
