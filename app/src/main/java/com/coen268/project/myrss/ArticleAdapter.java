package com.coen268.project.myrss;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleRowHolder>{

    private ArrayList<Article> data;
    private Context mContext;
    private onItemClickListener itemClickListener;
    private onItemLongClickListener itemLongClickListener;

    @Override
    public ArticleRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_row,null);
        mContext = parent.getContext();
        return new ArticleRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ArticleRowHolder holder, int position) {

        Article article = data.get(position);
        holder.title.setText("Title: " + article.getTitle());
        holder.author.setText("Author: " + article.getAuthor());

        holder.date_added.setText(formatTime(article.getPublishedTime()));
        //holder.date_added.setText("Published at: " + article.getPublishedTime());
        Picasso.with(mContext).load(article.getImageLinks()).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Article getItem(int position){ return data.get(position); }

    public class ArticleRowHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener,
            AdapterView.OnLongClickListener{

        private ImageView cover;
        private TextView title;
        private TextView author;
        private TextView date_added;

        public ArticleRowHolder(View itemView) {

            super(itemView);

            cover = (ImageView) itemView.findViewById(R.id.cover);
            title = (TextView) itemView.findViewById(R.id.article_title);
            author = (TextView) itemView.findViewById(R.id.article_author);
            date_added = (TextView) itemView.findViewById(R.id.article_published_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(itemLongClickListener != null){
                itemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    public void setData(ArrayList<Article> articlesData){ data = articlesData; }

    public interface onItemClickListener{
        void onItemClick(View v, int position);
    }

    public interface onItemLongClickListener{
        void onItemLongClick(View v, int position);
    }

    public void setOnItemLongClickListener(final onItemLongClickListener mItemLongClickListener){
        this.itemLongClickListener = mItemLongClickListener;
    }

    public void setOnItemClickListener(final onItemClickListener mItemClickListener){
        this.itemClickListener = mItemClickListener;
    }

    /*
    this function is used to change time's format to 2017-05-28 17:00
     */
    private String formatTime(String time) {
        String resultTime = "";
        if (time != null && time.length() != 0) {
            String[] dates = time.split("T");
            dates[1] = dates[1].substring(0, 5);
            resultTime = dates[0].trim() + " " + dates[1];
        }
        return resultTime;
    }

}