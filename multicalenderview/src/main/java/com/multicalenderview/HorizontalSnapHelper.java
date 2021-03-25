package com.multicalenderview;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by aman on 11-08-2018.
 */

public class HorizontalSnapHelper extends LinearSnapHelper {

    private HorizontalCalendar horizontalCalendar;

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View snapView = super.findSnapView(layoutManager);

        if (horizontalCalendar.calendarView.getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING) {
            int selectedItemPosition;
            if (snapView == null) {
                // no snapping required
                selectedItemPosition = horizontalCalendar.getSelectedDatePosition();
            } else {
                int[] snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView);
                if ((snapDistance[0] != 0) || (snapDistance[1] != 0)) {
                    return snapView;
                }
                selectedItemPosition = layoutManager.getPosition(snapView);
            }
            if (horizontalCalendar.calendarListener != null) {
                Date date = horizontalCalendar.getDateAt(selectedItemPosition);

                if (horizontalCalendar.multiCalendarView != null && horizontalCalendar.multiCalendarView.getTodayButton() != null) {
                    horizontalCalendar.multiCalendarView.getTodayButton()
                            .setVisibility(
                                    horizontalCalendar.isSameDate(date) ? View.GONE : View.VISIBLE
                            );
                }
                horizontalCalendar.calendarListener.onDateSelected(
                        horizontalCalendar.getStartDate(date),
                        horizontalCalendar.getEndDate(date),
                        selectedItemPosition
                );
            }
        }

        return snapView;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        // Do nothing
    }

    public void attachToHorizontalCalendaar(@Nullable HorizontalCalendar horizontalCalendar) throws IllegalStateException {
        this.horizontalCalendar = horizontalCalendar;
        attachToRecyclerView();
    }

    private void attachToRecyclerView() {
        horizontalCalendar.calendarView.setOnFlingListener(null);
        super.attachToRecyclerView(horizontalCalendar.calendarView);
    }
}