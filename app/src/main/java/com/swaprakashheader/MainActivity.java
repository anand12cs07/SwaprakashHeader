package com.swaprakashheader;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.multicalenderview.HorizontalCalendar;
import com.multicalenderview.HorizontalCalendarListener;
import com.progressrecyclerview.DefaultItemDecorator;
import com.progressrecyclerview.adapter.ProgressRecyclerViewAdapter;
import com.swaprakashheader.model.HeaderData;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton btnToday, btnSelect;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private HorizontalCalendar horizontalCalendar;
    private ProgressRecyclerViewAdapter adapter;
    private HorizontalCalendar.Builder builderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);


        btnToday = (AppCompatButton) findViewById(R.id.btnToday);
        btnSelect = (AppCompatButton) findViewById(R.id.btnSelect);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DefaultItemDecorator(40, 2));
        adapter = new ProgressRecyclerViewAdapter(HeaderData.getData());
        recyclerView.setAdapter(adapter);

        builderDate = new HorizontalCalendar.Builder(this, R.id.calendarViewDate);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -10);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        horizontalCalendar = builderDate
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .yearFormat("yyyy")
                .textSize(12f, 18f, 12.0f)
                .showDayName(false)
                .showMonthName(true)
                .showYearAndMonth(false)
                .setCalendarMode(HorizontalCalendar.MODE_QUARTERLY)
                .build();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refreshList(HeaderData.getData());
                refreshLayout.setRefreshing(false);
            }
        });

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalCalendar.goToday(false);
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar startDate = Calendar.getInstance();
                startDate.add(Calendar.DATE, -1);
                horizontalCalendar.selectDate(startDate.getTime(), false);
            }
        });

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date startDate, Date endDate, int position) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        switch (item.getItemId()) {
            case R.id.date:
                startDate.add(Calendar.MONTH, -10);
                endDate.add(Calendar.MONTH, 1);
                horizontalCalendar.refresh(HorizontalCalendar.MODE_DAILY, startDate.getTime(), endDate.getTime());
                break;
            case R.id.week:
                startDate.add(Calendar.MONTH, -5);
                endDate.add(Calendar.MONTH, 1);
                horizontalCalendar.refresh(HorizontalCalendar.MODE_WEEKLY, startDate.getTime(), endDate.getTime());
                break;
            case R.id.month:
                startDate.add(Calendar.MONTH,-4);
                endDate.add(Calendar.MONTH,1);
                horizontalCalendar.refresh(HorizontalCalendar.MODE_MONTHLY, startDate.getTime(), endDate.getTime());
                break;
            case R.id.quarter:
                // contains bugs
//                startDate.add(Calendar.MONTH,-15);
//                endDate.add(Calendar.MONTH,3);
//                horizontalCalendar.refresh(HorizontalCalendar.MODE_QUARTERLY, startDate.getTime(), endDate.getTime());
                break;
            case R.id.yearWise:
                startDate.add(Calendar.YEAR,-6);
                endDate.add(Calendar.YEAR,1);
                horizontalCalendar.refresh(HorizontalCalendar.MODE_YEARLY, startDate.getTime(), endDate.getTime());
                break;
            default:
                startDate.add(Calendar.MONTH, -10);
                endDate.add(Calendar.MONTH, 1);
                horizontalCalendar.refresh(HorizontalCalendar.MODE_DAILY, startDate.getTime(), endDate.getTime());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int convertDpToPixel(float dp, Context context) {
        float dimen = dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dimen;
    }
}
