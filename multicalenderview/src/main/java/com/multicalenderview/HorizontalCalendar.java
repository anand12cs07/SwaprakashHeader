package com.multicalenderview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by aman on 11-08-2018.
 */

public final class HorizontalCalendar {
    public static final String MODE_DAILY = "daily";
    public static final String MODE_WEEKLY = "weekly";
    public static final String MODE_MONTHLY = "monthly";
    public static final String MODE_QUARTERLY = "quarterly";
    public static final String MODE_YEARLY = "yearly";

    //endregion
    final RecyclerView.OnScrollListener onScrollListener = new HorizontalCalendarScrollListener();
    private final DateHandler handler;
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private final Calendar dateCalendar = GregorianCalendar.getInstance();
    //RootView
    private final View rootView;
    private final int calendarId;
    //Number of Dates to Show on Screen
    private final int numberOfDatesOnScreen;
    private final String formatDayName;
    private final String formatDayNumber;
    private final String formatMonth;
    private final String formatYear;
    private final boolean showMonthName;
    private final boolean showDayName;

    MultiCalendarView multiCalendarView;
    //region private Fields
    HorizontalCalendarView calendarView;
    HorizontalCalendarAdapter mCalendarAdapter;
    ArrayList<Date> mListDays;
    boolean loading;
    //Interface events
    HorizontalCalendarListener calendarListener;
    // by me

    // private final boolean showYearAndMonth;
    private boolean showYearAndMonth;
    //Start & End Dates
    private Date dateStartCalendar;
    private Date dateEndCalendar;
    //Calendar Modes
    private String calendarMode = MODE_DAILY;
    /* Format, Colors & Font Sizes*/
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateYearFormat;
    private int textColorNormal, textColorSelected, calendarBackground;
    private Drawable selectedDateBackground;
    private Integer selectorColor;
    private float textSizeMonthName, textSizeDayNumber, textSizeDayName;

    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    HorizontalCalendar(Builder builder) {
        this.rootView = builder.rootView;
        this.calendarId = builder.viewId;
        this.textColorNormal = builder.textColorNormal;
        this.textColorSelected = builder.textColorSelected;
        this.selectedDateBackground = builder.selectedDateBackground;
        this.selectorColor = builder.selectorColor;
        this.calendarBackground = builder.calendarBackground;
        this.formatDayName = builder.formatDayName;
        this.formatDayNumber = builder.formatDayNumber;
        this.formatMonth = builder.formatMonth;
        this.formatYear = builder.formatYear;
        this.textSizeMonthName = builder.textSizeMonthName;
        this.textSizeDayNumber = builder.textSizeDayNumber;
        this.textSizeDayName = builder.textSizeDayName;
        this.numberOfDatesOnScreen = builder.numberOfDatesOnScreen;
        this.dateStartCalendar = builder.dateStartCalendar;
        this.dateEndCalendar = builder.dateEndCalendar;
        this.showDayName = builder.showDayName;
        this.showMonthName = builder.showMonthName;
        this.showYearAndMonth = builder.showYearAndMonth;
        this.calendarMode = builder.calendarMode;

        handler = new DateHandler(this, builder.defaultSelectedDate);

    }

    /* Init Calendar View */
    void loadHorizontalCalendar() {

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateYearFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        mListDays = new ArrayList<>();

        multiCalendarView = (MultiCalendarView) rootView.findViewById(calendarId);
        calendarView = multiCalendarView.getCalendarView();
        calendarView.setHasFixedSize(true);
        calendarView.setHorizontalScrollBarEnabled(false);
        multiCalendarView.setHorizontalCalendar(this);
        calendarView.setBackgroundColor(calendarBackground);

        HorizontalSnapHelper snapHelper = new HorizontalSnapHelper();
        snapHelper.attachToHorizontalCalendaar(this);

        multiCalendarView.getTodayButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToday(false);
            }
        });

        hide();
        new InitializeDatesList().execute();
    }

    public HorizontalCalendarListener getCalendarListener() {
        return calendarListener;
    }

    public void setCalendarListener(HorizontalCalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    public void setCalendarMode(String calendarMode) {
        this.calendarMode = calendarMode;
    }

    public String getCalendarMode() {
        return calendarMode;
    }

    /**
     * Select today date and center the Horizontal Calendar to this date
     *
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the date of today
     *                  ,or false to play default scroll animation speed.
     */
    public void goToday(boolean immediate) {
        Calendar calendar = Calendar.getInstance();
        switch (calendarMode) {
            case MODE_WEEKLY:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case MODE_MONTHLY:
            case MODE_QUARTERLY:
                calendar.set(Calendar.DATE, 1);
                break;
            case MODE_YEARLY:
                calendar.set(Calendar.DATE, 1);
                calendar.set(Calendar.MONTH, 1);
                break;
            default:
                break;
        }
        selectDate(calendar.getTime(), immediate);
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date      The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     *                  ,or false to play default scroll animation speed.
     */
    public void selectDate(Date date, boolean immediate) {
        if (loading) {
            handler.date = date;
            handler.immediate = immediate;
        } else {
            if (immediate) {
                int datePosition = positionOfDate(date);
                centerToPositionWithNoAnimation(datePosition);
                if (calendarListener != null) {
                    calendarListener.onDateSelected(getStartDate(date), getEndDate(date), datePosition);
                }
            } else {
                calendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_NORMAL);
                centerCalendarToPosition(positionOfDate(date));
            }
            if (multiCalendarView != null && multiCalendarView.getTodayButton() != null) {
                multiCalendarView.getTodayButton()
                        .setVisibility(isSameDate(date) ? View.GONE : View.VISIBLE);
                multiCalendarView.getTodayButton().setText(
                        mCalendarAdapter.getDayNumberText(date)
                );
            }
        }
    }

    public boolean isSameDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendarMode) {
            case MODE_DAILY:
                return Utils.isSameDate(calendar, Calendar.getInstance());
            case MODE_WEEKLY:
                return Utils.isSameWeek(calendar, Calendar.getInstance());
            case MODE_MONTHLY:
                return Utils.isSameMonth(calendar, Calendar.getInstance());
            case MODE_QUARTERLY:
                return Utils.isSameQuarter(calendar, Calendar.getInstance());
            case MODE_YEARLY:
                return Utils.isSameYear(calendar, Calendar.getInstance());
            default:
                return false;
        }
    }

    public Date getStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        switch (calendarMode) {
            case MODE_DAILY:
                return calendar.getTime();
            case MODE_WEEKLY:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                return calendar.getTime();
            case MODE_MONTHLY:
                calendar.set(Calendar.DATE, 1);
                return calendar.getTime();
            case MODE_QUARTERLY:
                int month = calendar.get(Calendar.MONTH);
                int quarter = month / 3 + 1;
                if (quarter == 1) {
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                } else if (quarter == 2) {
                    calendar.set(Calendar.MONTH, Calendar.APRIL);
                } else if (quarter == 3) {
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                } else if (quarter == 4) {
                    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                }
                calendar.set(Calendar.DATE, 1);
                return calendar.getTime();
            case MODE_YEARLY:
                calendar.set(Calendar.DATE, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                return calendar.getTime();
            default:
                return date;
        }
    }

    public Date getEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        switch (calendarMode) {
            case MODE_DAILY:
                return calendar.getTime();
            case MODE_WEEKLY:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                return calendar.getTime();
            case MODE_MONTHLY:
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                return calendar.getTime();
            case MODE_QUARTERLY:
                int month = calendar.get(Calendar.MONTH);
                int quarter = month / 3 + 1;
                if (quarter == 1) {
                    calendar.set(Calendar.MONTH, Calendar.MARCH);
                } else if (quarter == 2) {
                    calendar.set(Calendar.MONTH, Calendar.JUNE);
                } else if (quarter == 3) {
                    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                } else if (quarter == 4) {
                    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                }
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                return calendar.getTime();
            case MODE_YEARLY:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                return calendar.getTime();
            default:
                return date;
        }
    }

    /**
     * Center the Horizontal Calendar to this position and select the day on this position
     *
     * @param position The position to center the calendar to!
     */
    void centerCalendarToPosition(int position) {
        if (position != -1) {
            int shiftCells = numberOfDatesOnScreen / 2;
            int centerItem = calendarView.getPositionOfCenterItem();

            if (position > centerItem) {
                calendarView.smoothScrollToPosition(position + shiftCells);
            } else if (position < centerItem) {
                calendarView.smoothScrollToPosition(position - shiftCells);
            }
        }
    }

    private void centerToPositionWithNoAnimation(final int position) {
        if (position != -1) {
            int shiftCells = numberOfDatesOnScreen / 2;
            int centerItem = calendarView.getPositionOfCenterItem();

            if (position > centerItem) {
                calendarView.scrollToPosition(position + shiftCells);
            } else if (position < centerItem) {
                calendarView.scrollToPosition(position - shiftCells);
            }

            calendarView.post(new Runnable() {
                @Override
                public void run() {
                    mCalendarAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public boolean isVisible() {
        return calendarView.isShown();
    }

    public void show() {
        calendarView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        calendarView.setVisibility(View.INVISIBLE);
    }

    public void post(Runnable runnable) {
        calendarView.post(runnable);
    }

    public void refresh(String calendarMode, Date dateStartCalendar, Date dateEndCalendar) {
        this.calendarMode = calendarMode;
        this.dateStartCalendar = dateStartCalendar;
        this.dateEndCalendar = dateEndCalendar;

        new InitializeDatesList().execute();
    }

    @TargetApi(21)
    public void setElevation(float elevation) {
        calendarView.setElevation(elevation);
    }

    /**
     * @return the current selected date
     */
    public Date getSelectedDate() {
        return mListDays.get(calendarView.getPositionOfCenterItem());
    }

    /**
     * @return position of selected date in Horizontal Calendar
     */
    public int getSelectedDatePosition() {
        return calendarView.getPositionOfCenterItem();
    }

    /**
     * @param position The position of date
     * @return the date on this index
     * @throws IndexOutOfBoundsException
     */
    public Date getDateAt(int position) throws IndexOutOfBoundsException {
        return mCalendarAdapter.getItem(position);
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    public boolean contains(Date date) {
        return mListDays.contains(date);
    }

    //region Getters & Setters
    public Date getDateStartCalendar() {
        return dateStartCalendar;
    }

    public Date getDateEndCalendar() {
        return dateEndCalendar;
    }

    public String getFormatDayName() {
        return formatDayName;
    }

    public String getFormatDayNumber() {
        return formatDayNumber;
    }

    public String getFormatYear() {
        return formatYear;
    }

    public String getFormatMonth() {
        return formatMonth;
    }

    public boolean isShowDayName() {
        return showDayName;
    }

    public boolean isShowMonthName() {
        return showMonthName;
    }

    public boolean isShowYearAndMonth() {
        return showYearAndMonth;
    }

    // by me
    void setShowYearAndMonth(boolean showYearAndMonth) {
        this.showYearAndMonth = showYearAndMonth;
    }

    public int getNumberOfDatesOnScreen() {
        return numberOfDatesOnScreen;
    }

    public Drawable getSelectedDateBackground() {
        return selectedDateBackground;
    }

    public void setSelectedDateBackground(Drawable selectedDateBackground) {
        this.selectedDateBackground = selectedDateBackground;
    }

    public int getTextColorNormal() {
        return textColorNormal;
    }

    public void setTextColorNormal(int textColorNormal) {
        this.textColorNormal = textColorNormal;
    }

    public int getTextColorSelected() {
        return textColorSelected;
    }

    public void setTextColorSelected(int textColorSelected) {
        this.textColorSelected = textColorSelected;
    }

    public Integer getSelectorColor() {
        return selectorColor;
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    public Integer getCalendarBackgroundColor() {
        return calendarBackground;
    }

    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        this.calendarBackground = calendarBackgroundColor;
    }

    public float getTextSizeMonthName() {
        return textSizeMonthName;
    }

    public void setTextSizeMonthName(float textSizeMonthName) {
        this.textSizeMonthName = textSizeMonthName;
    }

    public float getTextSizeDayNumber() {
        return textSizeDayNumber;
    }

    public void setTextSizeDayNumber(float textSizeDayNumber) {
        this.textSizeDayNumber = textSizeDayNumber;
    }

    public float getTextSizeDayName() {
        return textSizeDayName;
    }

    public void setTextSizeDayName(float textSizeDayName) {
        this.textSizeDayName = textSizeDayName;
    }
    //endregion

    /**
     * @return position of date in Calendar, or -1 if date does not exist
     */
    public int positionOfDate(Date date) {
        dateCalendar.setTime(date);
        startCalendar.setTime(dateStartCalendar);
        endCalendar.setTime(dateEndCalendar);

        if (calendarMode.equals(MODE_WEEKLY)) {
            startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE));
            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
        } else if (calendarMode.equals(MODE_WEEKLY)) {

        } else if (calendarMode.equals(MODE_YEARLY)) {
            startCalendar.set(Calendar.DATE, 1);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE));
            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
        }
        //Está fora do intervalo.
        if (date.after(endCalendar.getTime()) || date.before(startCalendar.getTime())) {
            return -1;
        }
        //Inicio.
        else if (isDatesDaysEquals(date, dateStartCalendar)) {
            return 0;
        }
        //Fim.
        else if (isDatesDaysEquals(date, dateEndCalendar)) {
            return mListDays.size() - 1;
        }
        //Calcular posicao para mes e ano.
        else if (calendarMode.equals(MODE_YEARLY)) {
            int diffYear = dateCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int position = diffYear + 2;
            return position;
        }
        //Calcular posicao para dia e mes.
        else if (calendarMode.equals(MODE_DAILY)) {
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            dateCalendar.set(Calendar.HOUR_OF_DAY, 23);
            dateCalendar.set(Calendar.MINUTE, 59);
            dateCalendar.set(Calendar.SECOND, 59);

            long diff = dateCalendar.getTimeInMillis() - startCalendar.getTimeInMillis(); //result in millis
            long days = (diff / (24 * 60 * 60 * 1000));

            int position = (int) days + 2;

            return position;
        } else if (calendarMode.equals(MODE_WEEKLY)) {
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            dateCalendar.set(Calendar.HOUR_OF_DAY, 23);
            dateCalendar.set(Calendar.MINUTE, 59);
            dateCalendar.set(Calendar.SECOND, 59);

            long diff = dateCalendar.getTimeInMillis() - startCalendar.getTimeInMillis(); //result in millis
            long days = (diff / (24 * 60 * 60 * 1000));
            long weeks = days / 7;

            int position = (int) weeks + 2;

            return position;
        } else if (calendarMode.equals(MODE_MONTHLY)) {
            int diffYear = dateCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + dateCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            int position = diffMonth + 2;
            return position;
        } else if (calendarMode.equals(MODE_QUARTERLY)) {
            int diffYear = dateCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + dateCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            int quarters = diffMonth / 3;
            int position = quarters + 2;
            return position;
        } else {
            return -1;
        }
    }

    /**
     * @return true if dates are equal
     */
    public boolean isDatesDaysEquals(Date date1, Date date2) {
        return isShowYearAndMonth() ? dateYearFormat.format(date1).equals(dateYearFormat.format(date2)) :
                dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    public static class Builder {

        final int viewId;
        final View rootView;

        //Start & End Dates
        Date dateStartCalendar;
        Date dateEndCalendar;

        //Number of Days to Show on Screen
        int numberOfDatesOnScreen;

        /* Format, Colors & Font Sizes*/
        String formatDayName;
        String formatDayNumber;
        String formatMonth;
        String formatYear;
        int textColorNormal, textColorSelected,calendarBackground;
        Drawable selectedDateBackground;
        Integer selectorColor;
        float textSizeMonthName, textSizeDayNumber, textSizeDayName;

        boolean showMonthName = true;
        boolean showDayName = true;
        boolean showYearAndMonth = false;
        Date defaultSelectedDate;
        String calendarMode = HorizontalCalendar.MODE_DAILY;

        /**
         * @param rootView pass the rootView for the Fragment where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */

        public Builder(View rootView, int viewId) {
            this.rootView = rootView;
            this.viewId = viewId;
        }

        /**
         * @param activity pass the activity where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        public Builder(Activity activity, int viewId) {
            this.rootView = activity.getWindow().getDecorView();
            this.viewId = viewId;
        }

        public Builder defaultSelectedDate(Date date) {
            defaultSelectedDate = date;
            return this;
        }

        public Builder startDate(Date dateStartCalendar) {
            this.dateStartCalendar = dateStartCalendar;
            return this;
        }

        public Builder endDate(Date dateEndCalendar) {
            this.dateEndCalendar = dateEndCalendar;
            return this;
        }

        public Builder datesNumberOnScreen(int numberOfItemsOnScreen) {
            this.numberOfDatesOnScreen = numberOfItemsOnScreen;
            return this;
        }

        public Builder dayNameFormat(String format) {
            this.formatDayName = format;
            return this;
        }

        public Builder dayNumberFormat(String format) {
            this.formatDayNumber = format;
            return this;
        }

        public Builder monthFormat(String format) {
            this.formatMonth = format;
            return this;
        }

        public Builder yearFormat(String format) {
            this.formatYear = format;
            return this;
        }

        public Builder textColor(int textColorNormal, int textColorSelected) {
            this.textColorNormal = textColorNormal;
            this.textColorSelected = textColorSelected;
            return this;
        }

        public Builder calendarBackground(int calendarBackground){
            this.calendarBackground = calendarBackground;
            return this;
        }

        public Builder selectedDateBackground(Drawable background) {
            this.selectedDateBackground = background;
            return this;
        }

        public Builder selectorColor(int selectorColor) {
            this.selectorColor = selectorColor;
            return this;
        }

        /**
         * Set the text size of the labels in scale-independent pixels
         *
         * @param textSizeMonthName the month name text size, in SP
         * @param textSizeDayNumber the day number text size, in SP
         * @param textSizeDayName   the day name text size, in SP
         */
        public Builder textSize(float textSizeMonthName, float textSizeDayNumber,
                                float textSizeDayName) {
            this.textSizeMonthName = textSizeMonthName;
            this.textSizeDayNumber = textSizeDayNumber;
            this.textSizeDayName = textSizeDayName;
            return this;
        }

        /**
         * Set the text size of the month name label in scale-independent pixels
         *
         * @param textSizeMonthName the month name text size, in SP
         */
        public Builder textSizeMonthName(float textSizeMonthName) {
            this.textSizeMonthName = textSizeMonthName;
            return this;
        }

        /**
         * Set the text size of the day number label in scale-independent pixels
         *
         * @param textSizeDayNumber the day number text size, in SP
         */
        public Builder textSizeDayNumber(float textSizeDayNumber) {
            this.textSizeDayNumber = textSizeDayNumber;
            return this;
        }

        /**
         * Set the text size of the day name label in scale-independent pixels
         *
         * @param textSizeDayName the day name text size, in SP
         */
        public Builder textSizeDayName(float textSizeDayName) {
            this.textSizeDayName = textSizeDayName;
            return this;
        }

        public Builder showDayName(boolean value) {
            showDayName = value;
            return this;
        }

        public Builder showMonthName(boolean value) {
            showMonthName = value;
            return this;
        }

        public Builder showYearAndMonth(boolean value) {
            showYearAndMonth = value;
            return this;
        }

        public Builder setCalendarMode(String calendarMode) {
            this.calendarMode = calendarMode;
            return this;
        }

        /**
         * @return Instance of {@link HorizontalCalendar} initiated with builder settings
         */
        public HorizontalCalendar build() {
            initDefaultValues();
            // by me
            HorizontalCalendar horizontalCalendar = new HorizontalCalendar(this);
            horizontalCalendar.setShowYearAndMonth(this.showYearAndMonth);
            horizontalCalendar.loadHorizontalCalendar();
            return horizontalCalendar;
        }

        private void initDefaultValues() {
            /* Defaults variables */
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5;
            }

            if (formatDayName == null && showDayName) {
                formatDayName = "EEE";
            }
            if (formatDayNumber == null) {
                formatDayNumber = "dd";
            }
            if (formatMonth == null && (showMonthName || showYearAndMonth)) {
                formatMonth = "MMM";
            }
            if (formatYear == null && showYearAndMonth) {
                formatYear = "yyyy";
            }
            if (dateStartCalendar == null) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                dateStartCalendar = c.getTime();
            }
            if (dateEndCalendar == null) {
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.MONTH, 1);
                dateEndCalendar = c2.getTime();
            }
            if (defaultSelectedDate == null) {
                defaultSelectedDate = new Date();
            }
        }
    }

    private static class DateHandler extends Handler {

        private final WeakReference<HorizontalCalendar> horizontalCalendar;
        public Date date = null;
        public boolean immediate = true;

        public DateHandler(HorizontalCalendar horizontalCalendar, Date defaultDate) {
            this.horizontalCalendar = new WeakReference<>(horizontalCalendar);
            this.date = defaultDate;
        }

        @Override
        public void handleMessage(Message msg) {
            HorizontalCalendar calendar = horizontalCalendar.get();
            if (calendar != null) {
                calendar.loading = false;
                if (date != null) {
                    calendar.selectDate(date, immediate);
                }

            }
        }
    }

    private class InitializeDatesList extends AsyncTask<Void, Void, Void> {

        InitializeDatesList() {
        }

        @Override
        protected void onPreExecute() {
            loading = true;
            mListDays.clear();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ArrayList of dates is set with all the dates between
            //start and end date
            GregorianCalendar calendar = new GregorianCalendar();

            calendar.setTime(dateStartCalendar);
            switch (calendarMode) {
                case MODE_WEEKLY:
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    calendar.add(Calendar.DATE, -7 * (numberOfDatesOnScreen / 2));
                    break;
                case MODE_MONTHLY:
                    calendar.set(Calendar.DATE, 1);
                    calendar.add(Calendar.MONTH, -numberOfDatesOnScreen / 2);
                    break;
                case MODE_QUARTERLY:
                    calendar.set(Calendar.DATE,1);
                    calendar.set(Calendar.MONTH, -3 * numberOfDatesOnScreen / 2);
                    break;
                case MODE_YEARLY:
                    calendar.set(Calendar.DATE, 1);
                    calendar.set(Calendar.MONTH, 1);
                    calendar.add(Calendar.YEAR, -numberOfDatesOnScreen / 2);
                    break;
                default:
                    calendar.add(Calendar.DATE, -numberOfDatesOnScreen / 2);
            }
            Date dateStartBefore = calendar.getTime();
            calendar.setTime(dateEndCalendar);
            switch (calendarMode) {
                case MODE_WEEKLY:
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    calendar.add(Calendar.DATE, 7 * (numberOfDatesOnScreen / 2));
                    break;
                case MODE_MONTHLY:
                    calendar.set(Calendar.DATE, 1);
                    calendar.add(Calendar.MONTH, numberOfDatesOnScreen / 2);
                    break;
                case MODE_QUARTERLY:
                    calendar.set(Calendar.DATE, 1);
                    calendar.add(Calendar.MONTH, 3 * (numberOfDatesOnScreen / 2));
                    break;
                case MODE_YEARLY:
                    calendar.set(Calendar.DATE, 1);
                    calendar.set(Calendar.MONTH, 1);
                    calendar.add(Calendar.YEAR, numberOfDatesOnScreen / 2);
                    break;
                default:
                    calendar.add(Calendar.DATE, numberOfDatesOnScreen / 2);
            }
            Date dateEndAfter = calendar.getTime();

            Date date = dateStartBefore;
            while (!date.after(dateEndAfter)) {
                mListDays.add(date);
                calendar.setTime(date);
//                if (isShowYearAndMonth()) {
//                    calendar.add(Calendar.MONTH, 1);
//                } else {
//                    calendar.add(Calendar.DATE, 1);
//                }
                switch (calendarMode) {
                    case MODE_WEEKLY:
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        calendar.add(Calendar.DATE, 7);
                        break;
                    case MODE_MONTHLY:
                        calendar.set(Calendar.DATE, 1);
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case MODE_QUARTERLY:
                        calendar.set(Calendar.DATE, 1);
                        calendar.add(Calendar.MONTH, 3);
                        break;
                    case MODE_YEARLY:
                        calendar.add(Calendar.YEAR, 1);
                        break;
                    default:
                        calendar.add(Calendar.DATE, 1);
                }
                date = calendar.getTime();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mCalendarAdapter == null) {
                mCalendarAdapter = new HorizontalCalendarAdapter(calendarView, mListDays);
            } else {
                mCalendarAdapter.setDateList(mListDays);
            }
//            mCalendarAdapter.setHasStableIds(true);
            calendarView.setAdapter(mCalendarAdapter);
            calendarView.setLayoutManager(new HorizontalLayoutManager(calendarView.getContext(), false));

            show();
            handler.sendMessage(new Message());
            calendarView.addOnScrollListener(onScrollListener);
        }
    }

    private class HorizontalCalendarScrollListener extends RecyclerView.OnScrollListener {

        final Runnable selectedItemRefresher = new SelectedItemRefresher();
        int lastSelectedItem = -1;

        HorizontalCalendarScrollListener() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //On Scroll, agenda is refresh to update background colors
            post(selectedItemRefresher);

            if (calendarListener != null) {
                calendarListener.onCalendarScroll(calendarView, dx, dy);
            }
        }

        private class SelectedItemRefresher implements Runnable {

            SelectedItemRefresher() {
            }

            @Override
            public void run() {
                final int positionOfCenterItem = calendarView.getPositionOfCenterItem();
                if ((lastSelectedItem == -1) || (lastSelectedItem != positionOfCenterItem)) {
                    //On Scroll, agenda is refresh to update background colors
                    //mCalendarAdapter.notifyItemRangeChanged(getSelectedDatePosition() - 2, 5, "UPDATE_SELECTOR");
                    mCalendarAdapter.notifyItemChanged(positionOfCenterItem, "UPDATE_SELECTOR");
                    if (lastSelectedItem != -1) {
                        mCalendarAdapter.notifyItemChanged(lastSelectedItem, "UPDATE_SELECTOR");
                    }
                    lastSelectedItem = positionOfCenterItem;
                }
            }
        }
    }
}
