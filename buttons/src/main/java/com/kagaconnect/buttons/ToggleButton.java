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

import com.kagaconnect.buttons.interfaces.OnToggledListener;
import com.kagaconnect.core.utils.DisplayUtils;

public class ToggleButton extends LinearLayout {

    private ConstraintLayout root;
    private TextView title_text;
    private LabeledSwitch label_switch;

    public ToggleButton(Context context) {
        super(context);
        init(null);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        View view = inflate(getContext(), R.layout.togglebutton, this);
        if(view != null){
            final Context context = getContext();

            root = findViewById(R.id.root);
            title_text = findViewById(R.id.title_text);
            label_switch = findViewById(R.id.label_switch);

            TypedArray tarr = getContext().obtainStyledAttributes(set, R.styleable.ToggleButton);
            final int N = tarr.getIndexCount();
            for (int i = 0; i < N; ++i) {
                int attr = tarr.getIndex(i);
                if (attr == R.styleable.ToggleButton_toggle_on) {
                    label_switch.setOn(tarr.getBoolean(R.styleable.ToggleButton_toggle_on, false));
                } else if (attr == R.styleable.ToggleButton_toggle_colorOff) {
                    label_switch.setColorOff(tarr.getColor(R.styleable.ToggleButton_toggle_colorOff, Color.parseColor("#FFFFFF")));
                } else if (attr == R.styleable.ToggleButton_toggle_colorBorder) {
                    int accentColor;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        accentColor = getResources().getColor(R.color.colorAccent, getContext().getTheme());
                    } else {
                        accentColor = getResources().getColor(R.color.colorAccent);
                    }
                    label_switch.setColorBorder(tarr.getColor(R.styleable.ToggleButton_toggle_colorBorder, accentColor));
                } else if (attr == R.styleable.ToggleButton_toggle_colorOn) {
                    int accentColor;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        accentColor = getResources().getColor(R.color.colorAccent, getContext().getTheme());
                    } else {
                        accentColor = getResources().getColor(R.color.colorAccent);
                    }
                    label_switch.setColorOn(tarr.getColor(R.styleable.ToggleButton_toggle_colorOn, accentColor));
                } else if (attr == R.styleable.ToggleButton_toggle_colorDisabled) {
                    label_switch.setColorDisabled(tarr.getColor(R.styleable.ToggleButton_toggle_colorOff, Color.parseColor("#D3D3D3")));
                } else if (attr == R.styleable.ToggleButton_toggle_textOff) {
                    label_switch.setLabelOff(tarr.getString(R.styleable.ToggleButton_toggle_textOff));
                } else if (attr == R.styleable.ToggleButton_toggle_textOn) {
                    label_switch.setLabelOn(tarr.getString(R.styleable.ToggleButton_toggle_textOn));
                } else if (attr == R.styleable.ToggleButton_android_textSize) {
                    //int defaultTextSize = (int)(12f * getResources().getDisplayMetrics().scaledDensity);
                    //int textSize = tarr.getDimensionPixelSize(R.styleable.ToggleButton_android_textSize, defaultTextSize);
                    //title_text.setTextSize(textSize);
                    //label_switch.setTextSize(textSize);

                    title_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tarr.getDimensionPixelSize(R.styleable.ToggleButton_android_textSize, (int) DisplayUtils.convertDpToPixel(12,context)));

                } else if(attr == R.styleable.ToggleButton_android_enabled) {
                    label_switch.setEnabled(tarr.getBoolean(R.styleable.ToggleButton_android_enabled, false));
                }
                else if(attr == R.styleable.ToggleButton_android_text) {
                    title_text.setText(tarr.getString(R.styleable.ToggleButton_android_text));
                }else if(attr == R.styleable.ToggleButton_android_textColor) {
                    title_text.setTextColor(tarr.getColor(R.styleable.ToggleButton_android_textColor, Color.parseColor("#60000000")));

                }
            }
            tarr.recycle();
        }
    }

    public void setOnToggledListener(OnToggledListener listener) {
        label_switch.setOnToggledListener(listener);
    }

    public int getColorOn() {
        return label_switch.getColorOn();
    }

    public void setColorOn(int colorOn) {
        label_switch.setColorOn(colorOn);
    }

    public int getColorOff() {
        return label_switch.getColorOff();
    }

    public void setColorOff(int colorOff) {
        label_switch.setColorOff(colorOff);
    }

    public String getLabelOn() {
        return label_switch.getLabelOn();
    }

    public void setLabelOn(String labelOn) {
        label_switch.setLabelOn(labelOn);
    }

    public String getLabelOff() {
        return label_switch.getLabelOff();
    }

    public void setLabelOff(String labelOff) {
        label_switch.setLabelOff(labelOff);
    }

    public Typeface getTypeface() {
        return label_switch.getTypeface();
    }

    public void setTypeface(Typeface typeface) {
        label_switch.setTypeface(typeface);
    }

    public void setOn(boolean on) {
        label_switch.setOn(on);
    }

    public int getColorDisabled() {
        return label_switch.getColorDisabled();
    }

    public void setColorDisabled(int colorDisabled) {
        label_switch.setColorDisabled(colorDisabled);
    }

    public int getColorBorder() {
        return label_switch.getColorBorder();
    }

    public void setColorBorder(int colorBorder) {
        label_switch.setColorBorder(colorBorder);
    }

    public int getTextSize() {
        return label_switch.getTextSize();
    }

    public void setTextSize(int textSize) {
        label_switch.setTextSize(textSize);
    }
}
