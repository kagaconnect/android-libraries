package com.kagaconnect.controls;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.kagaconnect.spinner.R;

import java.util.Arrays;
import java.util.List;

public class Spinner extends AppCompatTextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";
    private static final String IS_ARROW_HIDDEN = "is_arrow_hidden";
    private static final String ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id";
    public static final int VERTICAL_OFFSET = 1;

    private int selectedIndex;
    private Drawable arrowDrawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private SpinnerBaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHidden;
    private int textColor;
    private int backgroundSelector;
    private int arrowDrawableTint;
    private int displayHeight;
    private int parentVerticalOffset;
    private int dropDownListPaddingBottom;
    private @DrawableRes
    int arrowDrawableResId;
    private SpinnerTextFormatter spinnerTextFormatter = new SimpleSpinnerTextFormatter();
    private SpinnerTextFormatter selectedTextFormatter = new SimpleSpinnerTextFormatter();
    private PopUpTextAlignment horizontalAlignment;

    @Nullable
    private ObjectAnimator arrowAnimator = null;

    public Spinner(Context context) {
        super(context);
        init(context, null);
    }

    public Spinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, selectedIndex);
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden);
        bundle.putInt(ARROW_DRAWABLE_RES_ID, arrowDrawableResId);
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow.isShowing());
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt(SELECTED_INDEX);

            if (adapter != null) {
                setTextInternal(selectedTextFormatter.format(adapter.getItemInDataset(selectedIndex)).toString());
                adapter.setSelectedIndex(selectedIndex);
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showDropDown();
                        }
                    });
                }
            }

            isArrowHidden = bundle.getBoolean(IS_ARROW_HIDDEN, false);
            arrowDrawableResId = bundle.getInt(ARROW_DRAWABLE_RES_ID);
            savedState = bundle.getParcelable(INSTANCE_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Spinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), 0, defaultPadding, 0);
        //setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding, defaultPadding);
        setClickable(true);

        backgroundSelector = typedArray.getResourceId(R.styleable.Spinner_spin_backgroundSelector, R.drawable.spin_selector);
        setBackgroundResource(backgroundSelector);
        textColor = typedArray.getColor(R.styleable.Spinner_spin_textTint, getDefaultTextColor(context));
        setTextColor(textColor);

        listView = new ListView(context);
        // Set the spinner's id into the listview to make it pretend to be the right parent in
        // onItemClick
        listView.setId(getId());
        listView.setDivider(null);
        listView.setItemsCanFocus(true);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The selected item is not displayed within the list, so when the selected position is equal to
                // the one of the currently selected item it gets shifted to the next item.
                int offsetPosition = position;
                if (position >= selectedIndex && position < adapter.getCount()) {
                    // offsetPosition++;
                }

                selectedIndex = offsetPosition;

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }

                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                }

                adapter.setSelectedIndex(offsetPosition);
                setTextInternal(selectedTextFormatter.format(adapter.getItemInDataset(offsetPosition)).toString());
                dismissDropDown();
                setError(null);
            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(listView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(DEFAULT_ELEVATION);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.spinner_drawable));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down_shadow));
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!isArrowHidden) {
                    animateArrow(false);
                }
            }
        });

        isArrowHidden = typedArray.getBoolean(R.styleable.Spinner_spin_hideArrow, false);
        arrowDrawableTint = typedArray.getColor(R.styleable.Spinner_spin_arrowTint, Integer.MAX_VALUE);
        arrowDrawableResId = typedArray.getResourceId(R.styleable.Spinner_spin_arrowDrawable, R.drawable.arrow);
        dropDownListPaddingBottom =
                typedArray.getDimensionPixelSize(R.styleable.Spinner_spin_dropDownListPaddingBottom, 0);
        horizontalAlignment = PopUpTextAlignment.fromId(
                typedArray.getInt(R.styleable.Spinner_popupTextAlignment, PopUpTextAlignment.CENTER.ordinal())
        );

        CharSequence[] entries = typedArray.getTextArray(R.styleable.Spinner_spin_entries);
        if (entries != null) {
            attachDataSource(Arrays.asList(entries));
        }

        typedArray.recycle();

        measureDisplayHeight();
    }

    private void measureDisplayHeight() {
        displayHeight = getContext().getResources().getDisplayMetrics().heightPixels;
    }

    private int getParentVerticalOffset() {
        if (parentVerticalOffset > 0) {
            return parentVerticalOffset;
        }
        int[] locationOnScreen = new int[2];
        getLocationOnScreen(locationOnScreen);
        return parentVerticalOffset = locationOnScreen[VERTICAL_OFFSET];
    }

    @Override
    protected void onDetachedFromWindow() {
        if (arrowAnimator != null) {
            arrowAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            onVisibilityChanged(this, getVisibility());
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        arrowDrawable = initArrowDrawable(arrowDrawableTint);
        setArrowDrawableOrHide(arrowDrawable);
    }

    private Drawable initArrowDrawable(int drawableTint) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), arrowDrawableResId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            if (drawableTint != Integer.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint);
            }
        }
        return drawable;
    }

    private void setArrowDrawableOrHide(Drawable drawable) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private int getDefaultTextColor(Context context) {
        if(isEnabled()) {
            TypedValue typedValue = new TypedValue();
            context.getTheme()
                    .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            TypedArray typedArray = context.obtainStyledAttributes(typedValue.data,
                    new int[]{android.R.attr.textColorPrimary});
            int defaultTextColor = typedArray.getColor(0, Color.BLACK);
            typedArray.recycle();
            return defaultTextColor;
        }
        else return Color.parseColor("#60000000");
    }

    public Object getSelectedItem() {
        return adapter.getItemInDataset(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setArrowDrawable(@DrawableRes @ColorRes int drawableId) {
        arrowDrawableResId = drawableId;
        arrowDrawable = initArrowDrawable(R.drawable.arrow);
        setArrowDrawableOrHide(arrowDrawable);
    }

    public void setArrowDrawable(Drawable drawable) {
        arrowDrawable = drawable;
        setArrowDrawableOrHide(arrowDrawable);
    }

    public void setTextInternal(String text) {
        if (selectedTextFormatter != null) {
            //if(getHint() != null)
            //    setText(getHint()+" : "+selectedTextFormatter.format(text));
            //else
            setText(selectedTextFormatter.format(text));
        } else {
            setText(text);
        }
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.setSelectedIndex(position);
                selectedIndex = position;
                setTextInternal(selectedTextFormatter.format(adapter.getItemInDataset(position)).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void setFastScrollEnabled(boolean isEnabled) {
        this.listView.setFastScrollEnabled(isEnabled);
    }

    public void addOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public <T> void attachDataSource(@NonNull List<T> list) {
        adapter = new SpinnerAdapter<>(getContext(), list, textColor, backgroundSelector, spinnerTextFormatter, horizontalAlignment);
        setAdapterInternal(adapter);
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = new SpinnerAdapterWrapper(getContext(), adapter, textColor, backgroundSelector,
                spinnerTextFormatter, horizontalAlignment);
        setAdapterInternal(this.adapter);
    }

    public PopUpTextAlignment getPopUpTextAlignment() {
        return horizontalAlignment;
    }

    private void setAdapterInternal(SpinnerBaseAdapter adapter) {
        if (adapter.getCount() > 0) {
            // If the adapter needs to be set again, ensure to reset the selected index as well
            selectedIndex = 0;
            listView.setAdapter(adapter);
            //setTextInternal(selectedTextFormatter.format(adapter.getItemInDataset(selectedIndex)).toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && event.getAction() == MotionEvent.ACTION_UP) {
            if (!popupWindow.isShowing()) {
                showDropDown();
            } else {
                dismissDropDown();
            }
        }
        return super.onTouchEvent(event);
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        arrowAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        arrowAnimator.start();
    }

    public void dismissDropDown() {
        if (!isArrowHidden) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!isArrowHidden) {
            animateArrow(true);
        }
        measurePopUpDimension();
        popupWindow.showAsDropDown(this);
    }

    private void measurePopUpDimension() {
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(getPopUpHeight(), MeasureSpec.AT_MOST);
        listView.measure(widthSpec, heightSpec);
        popupWindow.setWidth(listView.getMeasuredWidth());
        popupWindow.setHeight(listView.getMeasuredHeight() - dropDownListPaddingBottom);
    }

    private int getPopUpHeight() {
        return Math.max(verticalSpaceBelow(), verticalSpaceAbove());
    }

    private int verticalSpaceAbove() {
        return getParentVerticalOffset();
    }

    private int verticalSpaceBelow() {
        return displayHeight - getParentVerticalOffset() - getMeasuredHeight();
    }

    public void setTintColor(@ColorRes int resId) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable, ContextCompat.getColor(getContext(), resId));
        }
    }

    public void setArrowTintColor(int resolvedColor) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable, resolvedColor);
        }
    }

    public void hideArrow() {
        isArrowHidden = true;
        setArrowDrawableOrHide(arrowDrawable);
    }

    public void showArrow() {
        isArrowHidden = false;
        setArrowDrawableOrHide(arrowDrawable);
    }

    public boolean isArrowHidden() {
        return isArrowHidden;
    }

    public void setDropDownListPaddingBottom(int paddingBottom) {
        dropDownListPaddingBottom = paddingBottom;
    }

    public int getDropDownListPaddingBottom() {
        return dropDownListPaddingBottom;
    }

    public void setSpinnerTextFormatter(SpinnerTextFormatter spinnerTextFormatter) {
        this.spinnerTextFormatter = spinnerTextFormatter;
    }

    public void setSelectedTextFormatter(SpinnerTextFormatter textFormatter) {
        this.selectedTextFormatter = textFormatter;
    }

    public void lockView(boolean locked){
        this.setEnabled(!locked);
    }

    public interface SpinnerTextFormatter {
        Spannable format(String text);

        Spannable format(Object item);
    }

    public class SimpleSpinnerTextFormatter implements SpinnerTextFormatter {

        @Override
        public final Spannable format(String text) {
            return new SpannableString(text);
        }

        @Override
        public Spannable format(Object item) {
            return new SpannableString(item.toString());
        }
    }

    public enum PopUpTextAlignment {
        START(0),
        END(1),
        CENTER(2);

        private final int id;

        PopUpTextAlignment(int id) {
            this.id = id;
        }

        static PopUpTextAlignment fromId(int id) {
            for (PopUpTextAlignment value : values()) {
                if (value.id == id) return value;
            }
            return CENTER;
        }
    }

    @SuppressWarnings("unused")
    public abstract class SpinnerBaseAdapter<T> extends BaseAdapter {

        private final PopUpTextAlignment horizontalAlignment;
        private final SpinnerTextFormatter spinnerTextFormatter;

        private int textColor;
        private int backgroundSelector;

        int selectedIndex;

        SpinnerBaseAdapter(
                Context context,
                int textColor,
                int backgroundSelector,
                SpinnerTextFormatter spinnerTextFormatter,
                PopUpTextAlignment horizontalAlignment
        ) {
            this.spinnerTextFormatter = spinnerTextFormatter;
            this.backgroundSelector = backgroundSelector;
            this.textColor = textColor;
            this.horizontalAlignment = horizontalAlignment;
        }

        @Override
        public View getView(int position, @Nullable View convertView, ViewGroup parent) {
            Context context = parent.getContext();
            TextView textView;

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.spinner_list_item, null);
                textView = convertView.findViewById(R.id.text_view_spinner);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(ContextCompat.getDrawable(context, backgroundSelector));
                }
                convertView.setTag(new ViewHolder(textView));
            } else {
                textView = ((ViewHolder) convertView.getTag()).textView;
            }

            textView.setText(spinnerTextFormatter.format(getItem(position)));
            textView.setTextColor(textColor);

            setTextHorizontalAlignment(textView);

            return convertView;
        }

        private void setTextHorizontalAlignment(TextView textView) {
            switch (horizontalAlignment) {
                case START:
                    textView.setGravity(Gravity.START);
                    break;
                case END:
                    textView.setGravity(Gravity.END);
                    break;
                case CENTER:
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    break;
            }
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        void setSelectedIndex(int index) {
            selectedIndex = index;
        }

        public abstract T getItemInDataset(int position);

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public abstract T getItem(int position);

        @Override
        public abstract int getCount();

        class ViewHolder {
            TextView textView;

            ViewHolder(TextView textView) {
                this.textView = textView;
            }
        }
    }

    public class SpinnerAdapter<T> extends SpinnerBaseAdapter {

        private final List<T> items;

        SpinnerAdapter(
                Context context,
                List<T> items,
                int textColor,
                int backgroundSelector,
                SpinnerTextFormatter spinnerTextFormatter,
                PopUpTextAlignment horizontalAlignment
        ) {
            super(context, textColor, backgroundSelector, spinnerTextFormatter, horizontalAlignment);
            this.items = items;
        }

        @Override
        public int getCount() {
            //return items.size() - 1;
            return items.size();
        }

        @Override
        public T getItem(int position) {
        /*if (position >= selectedIndex) {
            return items.get(position + 1);
        } else {
            return items.get(position);
        }*/

            return items.get(position);
        }

        @Override
        public T getItemInDataset(int position) {
            return items.get(position);
        }
    }

    public class SpinnerAdapterWrapper extends SpinnerBaseAdapter {

        private final ListAdapter baseAdapter;

        SpinnerAdapterWrapper(
                Context context,
                ListAdapter toWrap,
                int textColor,
                int backgroundSelector,
                SpinnerTextFormatter spinnerTextFormatter,
                PopUpTextAlignment horizontalAlignment
        ) {
            super(context, textColor, backgroundSelector, spinnerTextFormatter, horizontalAlignment);
            baseAdapter = toWrap;
        }

        @Override public int getCount() {
            //return baseAdapter.getCount() - 1;
            return baseAdapter.getCount() ;
        }

        @Override public Object getItem(int position) {
            //return baseAdapter.getItem(position >= selectedIndex ? position + 1 : position);
            return baseAdapter.getItem(position);
        }

        @Override public Object getItemInDataset(int position) {
            return baseAdapter.getItem(position);
        }
    }
}