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

import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.model.Campionato;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Campionato
 * with a RecyclerView.
 */
public class CampionatoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CAMPIONATO_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onFavoriteButtonPressed(int position);
    }

    private final List<Campionato> campionatoList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public CampionatoRecyclerViewAdapter(List<Campionato> campionatoList, Application application,
                                         OnItemClickListener onItemClickListener) {
        this.campionatoList = campionatoList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (campionatoList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return CAMPIONATO_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = null;

        if (viewType == CAMPIONATO_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.campionato_list_item, parent, false);
            return new NewsViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.loading_item, parent, false);
            return new LoadingCampionatoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder) {
            ((NewsViewHolder) holder).bind(campionatoList.get(position));
        } else if (holder instanceof LoadingCampionatoViewHolder) {
            ((LoadingCampionatoViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if (campionatoList != null) {
            return campionatoList.size();
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

        public void bind(Campionato campionato) {
            textViewTitle.setText(campionato.getNome());
            setImageViewSesso(campionato.getSesso());
            setImageViewFavoriteNews(campionato.isFavorite());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageview_favorite) {
                setImageViewFavoriteNews(!campionatoList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
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

    public static class LoadingCampionatoViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingCampionatoViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_api);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}
