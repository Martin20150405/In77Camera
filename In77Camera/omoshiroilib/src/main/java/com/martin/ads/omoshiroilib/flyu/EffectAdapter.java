package com.martin.ads.omoshiroilib.flyu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.util.EffectUtils;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeData;

import java.util.List;

/**
 * Created by Ads on 2017/6/5.
 */

public class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.EffectHolder> {

    private List<HardCodeData.EffectItem> effectItems;
    private Context context;
    private int selected = 0;

    public EffectAdapter(Context context, List<HardCodeData.EffectItem> effectItems) {
        this.effectItems = effectItems;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public EffectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.effect_item_layout,
                parent, false);
        EffectHolder viewHolder = new EffectHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.effect_thumb_image);
        viewHolder.filterRoot = (LinearLayout) view
                .findViewById(R.id.effect_root);
        viewHolder.filterImg = (FrameLayout) view.findViewById(R.id.effect_img_panel);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EffectHolder holder, final int position) {
        final HardCodeData.EffectItem effectItem = effectItems.get(position);
        holder.thumbImage.setImageBitmap(EffectUtils.getEffectThumbFromFile(context,effectItem));
        if (position == selected) {
            holder.filterImg.setBackgroundResource(R.drawable.effect_item_selected_bg);
        } else {
            holder.filterImg.setBackgroundResource(0);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                if(onEffectChangeListener!=null)
                    onEffectChangeListener.onFilterChanged(effectItems.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return effectItems == null ? 0 : effectItems.size();
    }

    class EffectHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        LinearLayout filterRoot;
        FrameLayout filterImg;

        public EffectHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnEffectChangeListener {
        void onFilterChanged(HardCodeData.EffectItem effectItem);
    }

    private OnEffectChangeListener onEffectChangeListener;

    public void setOnEffectChangeListener(OnEffectChangeListener onEffectChangeListener) {
        this.onEffectChangeListener = onEffectChangeListener;
    }
}
