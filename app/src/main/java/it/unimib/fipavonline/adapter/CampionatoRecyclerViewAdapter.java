package it.unimib.fipavonline.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.model.News;
import it.unimib.fipavonline.util.DateTimeUtil;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of News
 * with a RecyclerView.
 */
public class CampionatoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NEWS_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onNewsItemClick(News news);
        void onFavoriteButtonPressed(int position);
    }

    private final List<News> newsList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public CampionatoRecyclerViewAdapter(List<News> newsList, Application application,
                                         OnItemClickListener onItemClickListener) {
        this.newsList = newsList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (newsList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return NEWS_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = null;

        if (viewType == NEWS_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.campionato_list_item, parent, false);
            return new NewsViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.news_loading_item, parent, false);
            return new LoadingNewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder) {
            ((NewsViewHolder) holder).bind(newsList.get(position));
        } else if (holder instanceof LoadingNewsViewHolder) {
            ((LoadingNewsViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if (newsList != null) {
            return newsList.size();
        }
        return 0;
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageViewSesso;
        private final TextView textViewTitle;
        private final ImageView imageViewFavoriteNews;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSesso = itemView.findViewById(R.id.imageview_sex);
            textViewTitle = itemView.findViewById(R.id.textview_nome);
            imageViewFavoriteNews = itemView.findViewById(R.id.imageview_favorite);
            itemView.setOnClickListener(this);
            imageViewFavoriteNews.setOnClickListener(this);
        }

        public void bind(News news) {
            textViewTitle.setText(news.getNome());
            setImageViewSesso(news.getSesso());
            setImageViewFavoriteNews(news.isFavorite());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageview_favorite_news) {
                setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onNewsItemClick(newsList.get(getAdapterPosition()));
            }
        }

        private void setImageViewSesso(String sesso) {
            if (sesso.equals("M")) {
                imageViewSesso.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_man_24));
            } else {
                imageViewSesso.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_woman_24));
            }
        }

        private void setImageViewFavoriteNews(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavoriteNews.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavoriteNews.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }

    public static class LoadingNewsViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingNewsViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_news);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}
