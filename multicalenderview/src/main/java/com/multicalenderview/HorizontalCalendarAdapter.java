package com.multicalenderview;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by aman on 11-08-2018.
 */

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.DayViewHolder> {

    private final Context context;
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private ArrayList<Date> datesList;
    private int widthCell;
    private HorizontalCalendar horizontalCalendar;
    private int numberOfDates;
    private HorizontalCalendarView horizontalCalendarView;


    HorizontalCalendarAdapter(HorizontalCalendarView horizontalCalendarView, ArrayList<Date> datesList) {
        this.horizontalCalendarView = horizontalCalendarView;
        this.context = horizontalCalendarView.getContext();
        this.datesList = datesList;
        this.horizontalCalendar = horizontalCalendarView.getHorizontalCalendar();
        this.numberOfDates = horizontalCalendar.getNumberOfDatesOnScreen();
        calculateCellWidth();
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_horizontal_calendar, viewGroup, false);

        convertView.setMinimumWidth(widthCell);

        final DayViewHolder holder = new DayViewHolder(convertView);
        final Integer selectorColor = horizontalCalendar.getSelectorColor();
        if (selectorColor != null) {
            holder.selectionView.setBackgroundColor(selectorColor);
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == -1)
                    return;

                Date date = datesList.get(holder.getAdapterPosition());

                startCalendar.setTime(horizontalCalendar.getDateStartCalendar());
                endCalendar.setTime(horizontalCalendar.getDateEndCalendar());

                if (horizontalCalendar.isShowYearAndMonth()) {
                    startCalendar.set(Calendar.DATE, 1);
                    startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    startCalendar.set(Calendar.MINUTE, 0);
                    startCalendar.set(Calendar.SECOND, 0);
                    endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE));
                    endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                    endCalendar.set(Calendar.MINUTE, 59);
                    endCalendar.set(Calendar.SECOND, 59);
                }

                if (!date.before(startCalendar.getTime())
                        && !date.after(endCalendar.getTime())) {
                    horizontalCalendarView.setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_SLOW);
                    horizontalCalendar.centerCalendarToPosition(holder.getAdapterPosition());
                }
            }
        });

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Date date = datesList.get(holder.getAdapterPosition());
                HorizontalCalendarListener calendarListener = horizontalCalendar.getCalendarListener();
                if ((calendarListener != null) && !date.before(horizontalCalendar.getDateStartCalendar())
                        && !date.after(horizontalCalendar.getDateEndCalendar())) {
                    return calendarListener.onDateLongClicked(date, holder.getAdapterPosition());
                }
                return false;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        Date day = datesList.get(position);
        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        // Selected Day
        if (position == selectedItemPosition) {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorSelected());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(horizontalCalendar.getSelectedDateBackground());
            } else {
                holder.layoutBackground.setBackgroundDrawable(horizontalCalendar.getSelectedDateBackground());
            }
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorNormal());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(null);
            } else {
                holder.layoutBackground.setBackgroundDrawable(null);
            }
            holder.selectionView.setVisibility(View.INVISIBLE);
        }

        holder.txtDayNumber.setText(getDayNumberText(day));
        holder.txtMonthName.setText(getMonthText(day));

        if (horizontalCalendar.isShowYearAndMonth()) {
//            holder.txtDayNumber.setText(DateFormat.format(horizontalCalendar.getFormatMonth(), day).toString());
//            holder.txtMonthName.setText(DateFormat.format(horizontalCalendar.getFormatYear(), day).toString());
        } else {
//            holder.txtDayNumber.setText(DateFormat.format(horizontalCalendar.getFormatDayNumber(), day).toString());
            if (horizontalCalendar.isShowMonthName()) {
//                holder.txtMonthName.setText(DateFormat.format(horizontalCalendar.getFormatMonth(), day).toString());
                holder.txtMonthName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        horizontalCalendar.getTextSizeMonthName());
            } else {
                holder.txtMonthName.setVisibility(View.GONE);
            }
        }

        holder.txtDayNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                horizontalCalendar.getTextSizeDayNumber());

        if (!horizontalCalendar.isShowYearAndMonth() && horizontalCalendar.isShowDayName()) {
            holder.txtDayName.setText(DateFormat.format(horizontalCalendar.getFormatDayName(), day).toString());
            holder.txtDayName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    horizontalCalendar.getTextSizeDayName());
        } else {
            holder.txtDayName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position, List<Object> payloads) {
        if ((payloads == null) || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        // Selected Day
        if (position == selectedItemPosition) {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorSelected());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorSelected());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(horizontalCalendar.getSelectedDateBackground());
            } else {
                holder.layoutBackground.setBackgroundDrawable(horizontalCalendar.getSelectedDateBackground());
            }
            holder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            holder.txtDayNumber.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtMonthName.setTextColor(horizontalCalendar.getTextColorNormal());
            holder.txtDayName.setTextColor(horizontalCalendar.getTextColorNormal());
            if (Build.VERSION.SDK_INT >= 16) {
                holder.layoutBackground.setBackground(null);
            } else {
                holder.layoutBackground.setBackgroundDrawable(null);
            }
            holder.selectionView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Date getItem(int position) {
        return datesList.get(position);
    }

    void setDateList(ArrayList<Date> list) {
        this.datesList = list;
        notifyDataSetChanged();
    }

    public String getDayNumberText(Date date) {
        switch (horizontalCalendar.getCalendarMode()) {
            case HorizontalCalendar.MODE_DAILY:
                return DateFormat.format(horizontalCalendar.getFormatDayNumber(), date).toString();
            case HorizontalCalendar.MODE_WEEKLY:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return String.valueOf("W").concat(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
            case HorizontalCalendar.MODE_MONTHLY:
                return DateFormat.format(horizontalCalendar.getFormatMonth(), date).toString();
            case HorizontalCalendar.MODE_QUARTERLY:
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date);
                return String.valueOf("Q").concat(String.valueOf(calendar1.get(Calendar.MONTH) / 3 + 1));
            case HorizontalCalendar.MODE_YEARLY:
                return DateFormat.format(horizontalCalendar.getFormatYear(), date).toString();
            default:
                return "";
        }
    }

    private String getMonthText(Date date){
        switch (horizontalCalendar.getCalendarMode()){
            case HorizontalCalendar.MODE_DAILY:
            case HorizontalCalendar.MODE_WEEKLY:
                return DateFormat.format(horizontalCalendar.getFormatMonth(), date).toString();
            case HorizontalCalendar.MODE_MONTHLY:
            case HorizontalCalendar.MODE_QUARTERLY:
                return DateFormat.format(horizontalCalendar.getFormatYear(), date).toString();
            case HorizontalCalendar.MODE_YEARLY:
                return "Year";
            default:
                return "";
        }
    }
    /**
     * calculate each item width depends on {@link HorizontalCalendar#numberOfDatesOnScreen}
     */
    private void calculateCellWidth() {

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        int widthScreen;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);

            widthScreen = size.x;
        } else {
            widthScreen = display.getWidth();
        }

        widthCell = widthScreen / numberOfDates;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView txtDayNumber;
        TextView txtDayName;
        TextView txtMonthName;
        View selectionView;
        View layoutBackground;
        View rootView;

        public DayViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            txtDayNumber = (TextView) rootView.findViewById(R.id.dayNumber);
            txtDayName = (TextView) rootView.findViewById(R.id.dayName);
            txtMonthName = (TextView) rootView.findViewById(R.id.monthName);
            layoutBackground = rootView.findViewById(R.id.layoutBackground);
            selectionView = rootView.findViewById(R.id.selection_view);
        }
    }
}
