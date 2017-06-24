package com.example.android.guardiannewsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(List<News> news, OnItemClickListener listener) {
        newsList = news;
        onItemClickListener = listener;
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        Context context = parent.getContext();
        View newsView = LayoutInflater.from(context).inflate(R.layout.news_list, parent, false);
        return new NewsViewHolder(newsView);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        // Hold the details to be shown
        holder.mSection.setText(news.getSection());
        holder.mTitle.setText(news.getTitle());
        holder.mType.setText(news.getType());
        holder.mDate.setText(news.getDate());
        // Bind those details to the list
        holder.bind(newsList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void clear() {
        newsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<News> list) {
        newsList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(News news);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_textView)
        TextView mTitle;
        @BindView(R.id.section_textView)
        TextView mSection;
        @BindView(R.id.type_textView)
        TextView mType;
        @BindView(R.id.date_textView)
        TextView mDate;

        public NewsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final News news, final OnItemClickListener itemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(news);
                }
            });
        }
    }
}
