package org.bbt.kiakoa.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A generic {@link RecyclerView.Adapter} implementing onclick listener to facilitate its implementation
 * To be used in a {@link android.support.v7.widget.RecyclerView}
 *
 * @param <VH> a {@link RecyclerView.ViewHolder} implementing an onclick listener on items
 */
public abstract class ItemClickRecyclerAdapter<VH extends ItemClickRecyclerAdapter.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /**
     * item click listener
     */
    private OnItemClickListener listener;

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * View Holder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * listener
         */
        private OnViewItemClickListener listener;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onViewItemClick();
                    }
                }
            });
        }

        /**
         * set listener on {@link ViewHolder}
         *
         * @param listener listener called when view is clicked
         */
        void setOnViewItemClickListener(OnViewItemClickListener listener) {
            this.listener = listener;
        }

        /**
         * Interface definition for a callback to be invoked when a view item in this {@link ViewHolder} has been clicked.
         */
        private interface OnViewItemClickListener {
            /**
             * Callback method to be invoked when a view item in this {@link ViewHolder} has been clicked
             *
             */
            void onViewItemClick();
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final int p = position;
        holder.setOnViewItemClickListener(new ViewHolder.OnViewItemClickListener() {
            @Override
            public void onViewItemClick() {
                if (listener != null) {
                    listener.onItemClick(p);
                }
            }
        });
    }

    /**
     * Interface definition for a callback to be invoked when an item in this {@link ItemClickRecyclerAdapter} has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in this {@link ItemClickRecyclerAdapter} has been clicked
         *
         * @param position clicked item view position
         */
        void onItemClick(int position);
    }

    /**
     * Setter on {@link OnItemClickListener}
     *
     * @param listener item listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
