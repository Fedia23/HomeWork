package com.pineapple.softgroup.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pineapple.softgroup.R;
import com.pineapple.softgroup.json.Example;

import java.util.List;

public class WeaterAdapter extends RecyclerView.Adapter<WeaterAdapter.ViewHolder>{

    private List<Example> exampleList;

    public WeaterAdapter(List<Example> exampleList) {
        this.exampleList = exampleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_weater, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
     //   holder.tex1.setText(exampleList.get(position).getCurrent().getIsDay());
        holder.tex2.setText("Hallo");
    }

    @Override
    public int getItemCount() {
        if (exampleList == null) {
            return 1;
        } else {
            return exampleList.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tex1, tex2, tex3;

        public ViewHolder(View itemView) {
            super(itemView);
            tex1 = (TextView) itemView.findViewById(R.id.tex1);
            tex2 = (TextView) itemView.findViewById(R.id.tex2);
            tex3 = (TextView) itemView.findViewById(R.id.tex3);
        }
    }
}
