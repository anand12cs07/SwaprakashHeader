package com.progressrecyclerview.adapter.viewHolder;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.progressrecyclerview.AnimUtils;
import com.progressrecyclerview.R;
import com.progressrecyclerview.model.IData;

import java.util.List;
import java.util.Random;

public class ProgressRecyclerViewViewHolder extends BaseViewHolder {
    private List<IData> dataList;

    public ProgressBar progressBar;
    public AppCompatTextView tvCurrency, tvAmount, tvTitle;

    private int delayImmediate = 500;
    private int delay1 = 1000;
    private int delay2 = 1250;
    private int delay3 = 1500;

    public ProgressRecyclerViewViewHolder(@NonNull View itemView, List<IData> dataList) {
        super(itemView);

        progressBar = itemView.findViewById(R.id.progress);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvAmount = itemView.findViewById(R.id.tv_amount);
        tvCurrency = itemView.findViewById(R.id.tv_currency);

        this.dataList = dataList;
    }

    @Override
    public void onBind(final int position, int data) {
        int midAmount = dataList.get(position).getAmount().intValue() / 2;
        int startAmount = new Random().nextInt(midAmount) + midAmount;
        progressBar.setScaleY((float) getProgressBarScaleY());

        tvCurrency.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvCurrency.setText(dataList.get(position).getCurrency());
            }
        }, delayImmediate);

        AnimUtils.progressUpdateAnimation(progressBar, dataList.get(position).getAmount().intValue(), data, delay1);
        AnimUtils.incrementTextValueAnimation((float) startAmount, dataList.get(position).getAmount().floatValue(), delay2, tvAmount);
        AnimUtils.typeWriterAnimation(tvTitle, dataList.get(position).getTitle(), delay3);
    }

    private int getProgressBarScaleY() {
        int measureHeight = convertDpToPixel(
                progressBar.getContext().getResources().getDimension(R.dimen.progressBarHeight),
                progressBar.getContext()
        );
        int height = convertDpToPixel(
                progressBar.getContext().getResources().getDimension(R.dimen._65sdp),
                progressBar.getContext()
        );
        return height / measureHeight;
    }

    private int convertDpToPixel(float dp, Context context) {
        float dimen = dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dimen;
    }

}
