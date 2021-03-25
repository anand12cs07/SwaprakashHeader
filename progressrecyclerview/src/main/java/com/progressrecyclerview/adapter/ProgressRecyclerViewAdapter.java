package com.progressrecyclerview.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.progressrecyclerview.R;
import com.progressrecyclerview.adapter.viewHolder.ProgressRecyclerViewViewHolder;
import com.progressrecyclerview.model.IData;

import org.reactivestreams.Subscriber;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.observables.MathObservable;

public class ProgressRecyclerViewAdapter extends RecyclerView.Adapter<ProgressRecyclerViewViewHolder> {
    private List<IData> list;
    private OnItemClickListener listener;

    private int progressMax = 0;
    private IData maxIData;
    private int maxProgress;

    public ProgressRecyclerViewAdapter(List<IData> list) {
        this.list = list;
        maxIData = ((IData) Collections.max(list, new Comparator<IData>() {
            @Override
            public int compare(IData data, IData t1) {
                return data.getAmount().intValue() - t1.getAmount().intValue();
            }
        }));
        maxProgress = maxAmount(maxIData.getAmount().intValue());
    }

    @NonNull
    @Override
    public ProgressRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new ProgressRecyclerViewViewHolder(view, list);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgressRecyclerViewViewHolder holder, final int position) {
        holder.onBind(position,maxProgress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener == null)
                    return;
                listener.onItemClick(list.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    public void refreshList(List<IData> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private int maxAmount(int amount){
        double units = 0;
        while(amount >= 10){
            amount /=10;
            units++;
        }

        return (amount + 1) * (int)Math.pow(10.0,units);
    }
}
