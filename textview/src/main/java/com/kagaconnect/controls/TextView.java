package com.kagaconnect.controls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kagaconnect.core.models.Validator;
import com.kagaconnect.core.utils.DisplayUtils;
import com.kagaconnect.textviewer.R;

public class TextView extends LinearLayout {

    private ConstraintLayout root;
    private android.widget.TextView title_text;
    private android.widget.TextView description_text;

    public TextView(Context context) {
        super(context);
        init(null);
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        View view = inflate(getContext(), R.layout.textview, this);
        if(view != null){
            final Context context = getContext();

            root = findViewById(R.id.root);
            title_text = findViewById(R.id.title_text);
            description_text = findViewById(R.id.description_text);


            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.TextView);
            title_text.setTextColor(ta.getColor(R.styleable.TextView_android_textColor, Color.parseColor("#60000000")));
            description_text.setTextColor(ta.getColor(R.styleable.TextView_textview_description_textColor, Color.parseColor("#60000000")));

            title_text.setText(ta.getString(R.styleable.TextView_textview_hint));
            description_text.setText(ta.getString(R.styleable.TextView_textview_description));

            title_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.TextView_textview_hintSize, (int) DisplayUtils.convertDpToPixel(12,context)));
            description_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.TextView_textview_textSize, (int)DisplayUtils.convertDpToPixel(12,context)));

            ta.recycle();
        }
    }

    public ColorStateList getTextColors(){
        return title_text.getTextColors();
    }

    public final void setTextColor(int color) {
        title_text.setTextColor(color);
    }

    public ColorStateList getDescriptionTextColors(){
        return description_text.getTextColors();
    }

    public final void setDescriptionTextColor(int color) {
        description_text.setTextColor(color);
    }

    public CharSequence getText(){
        return description_text.getText();
    }

    public void addTextChangedListener(TextWatcher addTextChangedListener) {
        description_text.addTextChangedListener(addTextChangedListener);
    }

    public final void setText(CharSequence text) {
        description_text.setText(text);
    }

    public final void setError(CharSequence text) {
        description_text.setError(text);
    }

    public final CharSequence getError() {
        return description_text.getError();
    }

    public void setRawInputType(int type) {
        description_text.setRawInputType(type);
    }

    public boolean validateWith(@NonNull Validator validator) {
        CharSequence text = getText();
        boolean isValid = validator.isValid(text, text.length() == 0);
        if (!isValid) {
            description_text.setError(validator.getErrorMessage());
        }
        postInvalidate();
        return isValid;
    }
}
