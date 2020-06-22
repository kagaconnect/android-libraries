package com.kagaconnect.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kagaconnect.core.utils.FontManager;

public class CheckedButton extends LinearLayout {

    private ConstraintLayout root;
    private TextView button_text;
    private TextView button_description;
    private TextView checkbox_text;
    private TextView checkbox_text_bg;
    private Boolean isChecked;

    public CheckedButton(Context context) {
        super(context);
        init(null);
    }

    public CheckedButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CheckedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CheckedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        View view = inflate(getContext(), R.layout.checkedbutton, this);
        if(view != null){
            final Context context = getContext();

            Typeface matfont = FontManager.getTypeface(context, FontManager.MATERIALDESIGNICONS);

            root = findViewById(R.id.root);
            button_text = findViewById(R.id.button_text);
            button_description = findViewById(R.id.button_description);
            checkbox_text = findViewById(R.id.checkbox_text);
            checkbox_text_bg = findViewById(R.id.checkbox_text_bg);

            checkbox_text.setTypeface(matfont);
            checkbox_text_bg.setTypeface(matfont);

            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CheckedButton);
            button_text.setText(ta.getString(R.styleable.CheckedButton_checked_title));
            button_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.CheckedButton_checked_titleSize, 12));

            button_description.setText(ta.getString(R.styleable.CheckedButton_checked_description));
            button_description.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.CheckedButton_checked_descriptionSize, 8));

            changeSelected(ta.getBoolean(R.styleable.CheckedButton_checked_isSelected, false));
            ta.recycle();

        }
    }

    public boolean isSelected(){
        return isChecked;
    }

    public void changeSelected(Boolean isSelected){
        isChecked = isSelected;
        if(isSelected)
        {
            root.setBackground(getResources().getDrawable(R.drawable.checkedbutton_selected));
            //checkbox_text.setText(R.string.mdi_check_circle_outline);
            checkbox_text.setTextColor(getResources().getColor(R.color.arrow_button_active));
            button_text.setTextColor(Color.WHITE);
            button_description.setTextColor(Color.WHITE);
        }
        else
        {
            root.setBackground(getResources().getDrawable(R.drawable.checkedbutton_default));
            //checkbox_text.setText(R.string.mdi_brightness_1);
            checkbox_text.setTextColor(Color.WHITE);
            button_text.setTextColor(Color.BLACK);
            button_description.setTextColor(Color.BLACK);

        }
    }
}
