package biz.ddroid.bets.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseResultsRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    private List<T> dataSet;

    public static class VH extends RecyclerView.ViewHolder{

        public VH (View v){
            super(v);
        }
    }

    public BaseResultsRecyclerAdapter() {
        this.dataSet = new ArrayList<>();
    }

    public void setDataSet(List<T> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    protected T get(int position) {
        return dataSet.get(position);
    }

    protected int size() {
        return dataSet.size();
    }
}
