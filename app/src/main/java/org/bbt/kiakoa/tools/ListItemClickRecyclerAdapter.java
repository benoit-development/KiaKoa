package org.bbt.kiakoa.tools;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;

/**
 * a {@link android.support.v7.widget.RecyclerView.Adapter} managing click listener and item {@link java.util.ArrayList}
 * To be used in a {@link android.support.v7.widget.RecyclerView}
 */
public abstract class ListItemClickRecyclerAdapter<VH extends ListItemClickRecyclerAdapter.ViewHolder, T> extends ItemClickRecyclerAdapter<VH> {

    /**
     * item list
     */
    @NonNull
    private ArrayList<T> itemList = new ArrayList<>();

    /**
     * View Holder
     */
    public static class ViewHolder extends ItemClickRecyclerAdapter.ViewHolder {

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Item getter
     *
     * @param position item position
     * @return item at the position
     */
    protected T getItem(int position) {
        return itemList.get(position);
    }

    /**
     * item list setter
     *
     * @param itemList item list
     */
    public void setItemList(@NonNull ArrayList<T> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     * Interface definition for a callback to be invoked when an item in this {@link ItemClickRecyclerAdapter} has been clicked.
     */
    public interface OnListItemClickListener<T> {
        /**
         * Callback method to be invoked when an item in this {@link ListItemClickRecyclerAdapter} has been clicked
         *
         * @param item clicked item
         */
        void onListItemClick(T item);
    }

    /**
     * Setter on {@link OnListItemClickListener}
     *
     * @param listener item listener
     */
    public void setOnItemClickListener(final OnListItemClickListener<T> listener) {
        setOnItemClickListener(new ItemClickRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onListItemClick(itemList.get(position));
                }
            }
        });
    }


}
