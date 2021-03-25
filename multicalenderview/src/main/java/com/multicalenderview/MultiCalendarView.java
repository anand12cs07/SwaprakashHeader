package com.multicalenderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.button.MaterialButton;

public class MultiCalendarView extends FrameLayout {
    private final float FLING_SCALE_DOWN_FACTOR = 0.5f;
    private final float DEFAULT_TEXT_SIZE_MONTH_NAME = 14f;
    private final float DEFAULT_TEXT_SIZE_DAY_NUMBER = 24f;
    private final float DEFAULT_TEXT_SIZE_DAY_NAME = 14f;
    private int textColorNormal, textColorSelected, calendarBackground;
    private Drawable selectedDateBackground;
    private int selectorColor;
    private float textSizeMonthName, textSizeDayNumber, textSizeDayName;
    private HorizontalCalendar horizontalCalendar;

    private HorizontalCalendarView calendarView;
    private MaterialButton goToday;
    private AppCompatCheckBox checkBox;

    public MultiCalendarView(@NonNull Context context) {
        super(context);

        init(context);
    }

    public MultiCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HorizontalCalendarView,
                0, 0);

        try {
            textColorNormal = a.getColor(R.styleable.HorizontalCalendarView_textColorNormal, Color.LTGRAY);
            textColorSelected = a.getColor(R.styleable.HorizontalCalendarView_textColorSelected, Color.BLACK);
            selectedDateBackground = a.getDrawable(R.styleable.HorizontalCalendarView_selectedDateBackground);
            selectorColor = a.getColor(R.styleable.HorizontalCalendarView_selectorColor, fetchAccentColor());
            calendarBackground = a.getColor(R.styleable.HorizontalCalendarView_calendarBackGround,Color.WHITE);

            textSizeMonthName = getRawSizeValue(a, R.styleable.HorizontalCalendarView_textSizeMonthName,
                    DEFAULT_TEXT_SIZE_MONTH_NAME);
            textSizeDayNumber = getRawSizeValue(a, R.styleable.HorizontalCalendarView_textSizeDayNumber,
                    DEFAULT_TEXT_SIZE_DAY_NUMBER);
            textSizeDayName = getRawSizeValue(a, R.styleable.HorizontalCalendarView_textSizeDayName,
                    DEFAULT_TEXT_SIZE_DAY_NAME);
        } finally {
            a.recycle();
        }

        init(context);
    }

    public HorizontalCalendarView getCalendarView(){
        return calendarView;
    }

    public MaterialButton getTodayButton(){
        return goToday;
    }

    public AppCompatCheckBox getCheckBox(){
        return checkBox;
    }

    public HorizontalCalendar getHorizontalCalendar() {
        return horizontalCalendar;
    }

    public void setHorizontalCalendar(HorizontalCalendar horizontalCalendar) {

        if (horizontalCalendar.getTextColorNormal() == 0) {
            horizontalCalendar.setTextColorNormal(textColorNormal);
        }
        if (horizontalCalendar.getTextColorSelected() == 0) {
            horizontalCalendar.setTextColorSelected(textColorSelected);
        }
        if (horizontalCalendar.getSelectorColor() == null) { //compare with null because Color.TRANSPARENT == 0
            horizontalCalendar.setSelectorColor(selectorColor);
        }
        if (horizontalCalendar.getCalendarBackgroundColor() == 0){
            horizontalCalendar.setCalendarBackgroundColor(calendarBackground);
        }
        if (horizontalCalendar.getSelectedDateBackground() == null) {
            horizontalCalendar.setSelectedDateBackground(selectedDateBackground);
        }
        if (horizontalCalendar.getTextSizeMonthName() == 0) {
            horizontalCalendar.setTextSizeMonthName(textSizeMonthName);
        }
        if (horizontalCalendar.getTextSizeDayNumber() == 0) {
            horizontalCalendar.setTextSizeDayNumber(textSizeDayNumber);
        }
        if (horizontalCalendar.getTextSizeDayName() == 0) {
            horizontalCalendar.setTextSizeDayName(textSizeDayName);
        }

        this.horizontalCalendar = horizontalCalendar;
        this.calendarView.setHorizontalCalendar(horizontalCalendar);
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_multi_calender_view,this,true);

        calendarView = (HorizontalCalendarView)view.findViewById(R.id.calenderView);
        goToday = (MaterialButton) view.findViewById(R.id.btn);
        checkBox = (AppCompatCheckBox)view.findViewById(R.id.checkBox);

    }

    private float getRawSizeValue(TypedArray a, int index, float defValue) {
        TypedValue outValue = new TypedValue();
        boolean result = a.getValue(index, outValue);
        if (!result) {
            return defValue;
        }

        return TypedValue.complexToFloat(outValue.data);
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
