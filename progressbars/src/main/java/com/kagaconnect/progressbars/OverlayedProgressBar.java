package com.kagaconnect.progressbars;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kagaconnect.core.utils.DisplayUtils;
import com.kagaconnect.core.utils.UiUtils;
import com.kagaconnect.loaderspack.CurvesLoader;

public class OverlayedProgressBar extends RelativeLayout {
    @Nullable
    private OnClickListener mClickListener;
    private boolean mClickableOverlay;
    private int mAnimationDuration;
    private CurvesLoader loader;
    private TextView description;
    private String TAG = "OverlayedProgressBar";

    public OverlayedProgressBar(@NonNull Context context) {
        super(context);
    }

    public OverlayedProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OverlayedProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public boolean hasClickableOverlay() {
        return mClickableOverlay;
    }

    public void setClickableOverlay(boolean clickableOverlay) {
        mClickableOverlay = clickableOverlay;
        setOnClickListener(mClickListener);
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void show() {
        show(true);
    }

    public void show(boolean animate) {
        if (animate) {
            UiUtils.fadeInAnim(this);
        } else {
            setVisibility(VISIBLE);
        }
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean animate) {
        if (animate) {
            UiUtils.fadeOutAnim(this);
        } else {
            setVisibility(GONE);
        }
    }

    public void setText(String text){
        description.setText(text);
    }

    public void setText(int resId){
        description.setText(resId);
    }

    public void setTextSize(float size){
        description.setTextSize(size);
    }

    public void setTextColor(@ColorInt int color){
        description.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors){
        description.setTextColor(colors);
    }

    public void setCurveColor(@ColorInt int color){
        loader.setCurveColor(color);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        mClickListener = clickListener;
        super.setOnClickListener(hasClickableOverlay() ? clickListener : null);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.overlayedprogressbar, this);
        if(view != null){

            loader = findViewById(R.id.loader);
            description = findViewById(R.id.description);
            TypedArray attr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.OverlayedProgressBar, 0, 0);

            try {
                loader.setCurveColor(attr.getColor(R.styleable.OverlayedProgressBar_overlayed_progressColor, getResources().getColor(R.color.overlayed_progress_color)));
                setBackgroundColor(attr.getColor(R.styleable.OverlayedProgressBar_overlayed_backgroundColor, getResources().getColor(R.color.overlayed_overlay_color)));

                description.setTextSize(TypedValue.COMPLEX_UNIT_PX, attr.getDimensionPixelSize(R.styleable.OverlayedProgressBar_overlayed_textSize, (int) DisplayUtils.convertDpToPixel(12,context)));
                description.setText(attr.getString(R.styleable.OverlayedProgressBar_overlayed_text));
                description.setTextColor(attr.getColor(R.styleable.OverlayedProgressBar_overlayed_textColor, getResources().getColor(R.color.white)));

            } catch (Exception e) {
                Log.e(TAG, "Failure setting FabOverlayLayout attrs", e);
            } finally {
                attr.recycle();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(getResources().getDimension(R.dimen.opd_elevation));
            }
            setClickable(true);
            setVisibility(View.GONE);
            mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        }
    }
}
