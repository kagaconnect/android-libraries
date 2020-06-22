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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kagaconnect.edittext.R;
import com.kagaconnect.core.models.Validator;
import com.kagaconnect.core.utils.DisplayUtils;

public class EditText extends LinearLayout {

    private ConstraintLayout root;
    private TextView title_text;
    private android.widget.EditText description_text;

    public EditText(Context context) {
        super(context);
        init(null);
    }

    public EditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        View view = inflate(getContext(), R.layout.edittext, this);
        if(view != null){
            final Context context = getContext();

            root = findViewById(R.id.root);
            title_text = findViewById(R.id.title_text);
            description_text = findViewById(R.id.description_text);


            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.EditText);
            title_text.setText(ta.getString(R.styleable.EditText_editor_hint));
            title_text.setHintTextColor(ta.getColor(R.styleable.EditText_editor_hintColor, Color.parseColor("#20000000")));
            title_text.setTextColor(ta.getColor(R.styleable.EditText_android_textColor, Color.parseColor("#60000000")));

            title_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.EditText_editor_hintSize, (int) DisplayUtils.convertDpToPixel(12,context)));
            description_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.EditText_editor_textSize, (int) DisplayUtils.convertDpToPixel(12,context)));
            description_text.setEnabled(ta.getBoolean(R.styleable.EditText_android_enabled, true));

            description_text.setHint(ta.getString(R.styleable.EditText_editor_descriptionHint));
            description_text.setHintTextColor(ta.getColor(R.styleable.EditText_editor_descriptionHintColor, Color.parseColor("#20000000")));
            ta.recycle();
        }
    }

    public CharSequence getHint(){
        return title_text.getText();
    }

    public final void setHint(CharSequence text) {
        title_text.setText(text);
    }

    public ColorStateList getHintTextColors(){
        return title_text.getHintTextColors();
    }

    public final void setHintTextColor(int color) {
        title_text.setHintTextColor(color);
    }

    public final void setHintTextColor(ColorStateList color) {
        title_text.setHintTextColor(color);
    }

    public CharSequence getDescriptionHint(){
        return description_text.getHint();
    }

    public final void setDescriptionHint(CharSequence text) {
        description_text.setHint(text);
    }

    public ColorStateList getDescriptionHintTextColors(){
        return description_text.getHintTextColors();
    }

    public final void setDescriptionHintTextColor(int color) {
        description_text.setHintTextColor(color);
    }

    public final void setDescriptionHintTextColor(ColorStateList color) {
        description_text.setHintTextColor(color);
    }

    public CharSequence getText(){
        return description_text.getText();
    }

    public void addTextChangedListener(TextWatcher addTextChangedListener) {
        description_text.addTextChangedListener(addTextChangedListener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        description_text.setOnFocusChangeListener(listener);
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

    public void setSelection(int index) {
        description_text.setSelection(index);
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

    public boolean focus(){
        description_text.setFocusable(true);
        description_text.setFocusableInTouchMode(true);
        return description_text.requestFocus();
    }

    public void lockView(boolean locked){
        description_text.setEnabled(!locked);
    }

    public void readOnly(boolean value){
        description_text.setFocusable(!value);
        description_text.setFocusableInTouchMode(!value);
    }
}
