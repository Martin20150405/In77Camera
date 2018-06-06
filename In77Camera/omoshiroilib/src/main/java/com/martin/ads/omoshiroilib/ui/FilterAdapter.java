package com.martin.ads.omoshiroilib.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    
    private List<FilterType> filterTypeList;
    private Context context;
    private int selected = 0;

    public FilterAdapter(Context context, List<FilterType> filterTypeList) {
        this.filterTypeList = filterTypeList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==1) return -1;
        return super.getItemViewType(position);
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == -1){
            View view = LayoutInflater.from(context).inflate(R.layout.filter_division_layout,
                    parent, false);
            return new FilterHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (LinearLayout)view
                .findViewById(R.id.filter_root);
        viewHolder.filterImg= (FrameLayout) view.findViewById(R.id.filter_img_panel);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder,final int position) {
        if(position==1) return;
        final FilterType filterType=filterTypeList.get(position);
        holder.thumbImage.setImageBitmap(FilterResourceHelper.getFilterThumbFromFile(context,filterType));
        holder.filterName.setText(FilterResourceHelper.getSimpleName(filterType));
        if(position == selected){
            holder.filterImg.setBackgroundResource(R.drawable.effect_item_selected_bg);
            holder.filterName.setTextColor(GlobalConfig.context.getResources().getColor(R.color.app_color));
        }else {
            holder.filterImg.setBackgroundResource(0);
            holder.filterName.setTextColor(Color.WHITE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                if(onFilterChangeListener!=null)
                    onFilterChangeListener.onFilterChanged(filterTypeList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterTypeList == null ? 0 : filterTypeList.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        LinearLayout filterRoot;
        FrameLayout filterImg;
        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnFilterChangeListener{
        void onFilterChanged(FilterType filterType);
    }

    private OnFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(OnFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
