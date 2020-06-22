package com.kagaconnect.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kagaconnect.core.utils.DisplayUtils;
import com.kagaconnect.core.utils.FontManager;

public class ArrowedButton extends LinearLayout {

    Button button;
    TextView description_text;

    public ArrowedButton(Context context) {
        super(context);
        init(null);
    }

    public ArrowedButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ArrowedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrowedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        View view = inflate(getContext(), R.layout.arrowedbutton, this);
        if(view != null){
            final Context context = getContext();

            Typeface matfont = FontManager.getTypeface(context, FontManager.MATERIALDESIGNICONS);

            button = findViewById(R.id.button);
            TextView icon = findViewById(R.id.icon);
            description_text = findViewById(R.id.description_text);

            icon.setTypeface(matfont);

            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.ArrowedButton);
            button.setText(ta.getString(R.styleable.ArrowedButton_arrow_text));
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ArrowedButton_arrow_textSize, (int) DisplayUtils.convertDpToPixel(12,context)));

            description_text.setText(ta.getString(R.styleable.ArrowedButton_arrow_description));
            description_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ArrowedButton_arrow_descriptionSize, (int)DisplayUtils.convertDpToPixel(12,context)));

            ta.recycle();

        }
    }

    public void setText(CharSequence text){
        button.setText(text);
    }

    public void setText(int resid){
        button.setText(resid);
    }

    public void setDescription(CharSequence text){
        description_text.setText(text);
    }

    public void setDescription(int resid){
        description_text.setText(resid);
    }

    public void setOnClickListener(@Nullable OnClickListener l){
        button.setOnClickListener(l);
    }
}