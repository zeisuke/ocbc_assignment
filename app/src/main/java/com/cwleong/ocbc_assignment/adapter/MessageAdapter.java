package com.cwleong.ocbc_assignment.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cwleong.ocbc_assignment.R;
import com.cwleong.ocbc_assignment.bean.MessagePayloadBean;
import com.cwleong.ocbc_assignment.utils.PredicateUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private MessagePayloadBean mBean;

    private ArrayList<MessagePayloadBean.MessageBean> mItems;
    private ArrayList<MessagePayloadBean.MessageBean> mOriginalItems;
    private ArrayList<MessagePayloadBean.MessageBean> mFilteredItems;

    public MessageAdapter(@NonNull Context context, MessagePayloadBean bean){
        this.mContext = context;
        this.mBean = bean;
        this.mItems = bean.getList();
        this.mOriginalItems = bean.getList();
    }

    public void reloadData(MessagePayloadBean bean){
        this.mBean = bean;
        this.mItems = bean.getList();
        this.mOriginalItems = bean.getList();

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.itemview_message, parent, false);
        MessageAdapter.ViewHolder holder = new MessageAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessagePayloadBean.MessageBean item = mItems.get(position);
        if (item.getSender().equalsIgnoreCase("me")){
            holder.vwMe.setVisibility(View.VISIBLE);
            holder.vwUser.setVisibility(View.GONE);
            holder.tvMeMsg.setText(item.getMessage());
        }else{
            holder.vwMe.setVisibility(View.GONE);
            holder.vwUser.setVisibility(View.VISIBLE);
            holder.tvUserMsg.setText(item.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void clear(){
        mItems.clear();
    }

    public void setItems(ArrayList<MessagePayloadBean.MessageBean> items){
        mItems = items;
        notifyDataSetChanged();
    }

    public void addItem(MessagePayloadBean.MessageBean item){
        mItems.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                FilterResults filterResults = new FilterResults();
                if (charString.isEmpty()) {
                    filterResults.values = mOriginalItems;
                } else {
                    if (mFilteredItems == null){
                        mFilteredItems = new ArrayList<>();
                    }else{
                        mFilteredItems.clear();
                    }
                    mFilteredItems.addAll(mOriginalItems);
                    ArrayList<MessagePayloadBean.MessageBean> filteredWalletItemsByMessage = (ArrayList<MessagePayloadBean.MessageBean>) mFilteredItems.stream().filter(item -> PredicateUtil.getPredicateContainsString(constraint.toString(), item.getMessage())).collect(Collectors.toList());
                    Set<MessagePayloadBean.MessageBean> set = new LinkedHashSet<>(filteredWalletItemsByMessage);
                    List<MessagePayloadBean.MessageBean> combinedList = new ArrayList<>(set);
                    filterResults.values = combinedList;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems = (ArrayList<MessagePayloadBean.MessageBean>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setLooping(true);
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llItemView;
        private LinearLayout vwMe, vwUser;
        private TextView tvMeMsg, tvUserMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemView = itemView.findViewById(R.id.llItemView);

            vwMe = itemView.findViewById(R.id.vwMe);
            tvMeMsg = vwMe.findViewById(R.id.tvMeMsg);

            vwUser = itemView.findViewById(R.id.vwUser);
            tvUserMsg = vwUser.findViewById(R.id.tvUserMsg);
        }
    }
}
