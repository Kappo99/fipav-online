package it.unimib.fipavonline.adapter;

import android.app.Application;
import android.graphics.Typeface;
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
import it.unimib.fipavonline.model.Partita;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Campionato
 * with a RecyclerView.
 */
public class PartitaRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PARTITA_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;

    private final List<Partita> partitaList;
    private final Application application;

    public PartitaRecyclerViewAdapter(List<Partita> partitaList, Application application) {
        this.partitaList = partitaList;
        this.application = application;
    }

    @Override
    public int getItemViewType(int position) {
        if (partitaList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return PARTITA_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = null;

        if (viewType == PARTITA_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.partita_list_item, parent, false);
            return new PartitaViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.loading_item, parent, false);
            return new LoadingPartitaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PartitaViewHolder) {
            ((PartitaViewHolder) holder).bind(partitaList.get(position));
        } else if (holder instanceof LoadingPartitaViewHolder) {
            ((LoadingPartitaViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if (partitaList != null) {
            return partitaList.size();
        }
        return 0;
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class PartitaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewSesso;
        private final TextView textViewCampionato;
        private final TextView textViewData;
        private final TextView textViewLocali;
        private final TextView textViewOspiti;
        private final TextView textViewSetLocali;
        private final TextView textViewSetOspiti;
        private final TextView textViewSet;

        public PartitaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSesso = itemView.findViewById(R.id.imageview_sex);
            textViewCampionato = itemView.findViewById(R.id.textview_campionato);
            textViewData = itemView.findViewById(R.id.textview_data);
            textViewLocali = itemView.findViewById(R.id.textview_locali);
            textViewOspiti = itemView.findViewById(R.id.textview_ospiti);
            textViewSetLocali = itemView.findViewById(R.id.textview_set_locali);
            textViewSetOspiti = itemView.findViewById(R.id.textview_set_ospiti);
            textViewSet = itemView.findViewById(R.id.textview_set);

            // modifiche grafiche ai testi
            textViewCampionato.setTypeface(null, Typeface.BOLD);
        }

        public void bind(Partita partita) {
            setImageViewSesso(partita.getSesso());
            textViewCampionato.setText(partita.getCampionato());
            textViewData.setText(partita.getData());
            textViewLocali.setText(partita.getLocali());
            textViewOspiti.setText(partita.getOspiti());
            textViewSetLocali.setText(String.valueOf(partita.getSetLocali()));
            textViewSetOspiti.setText(String.valueOf(partita.getSetOspiti()));
            textViewSet.setText(partita.getSet());
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
    }

    public static class LoadingPartitaViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingPartitaViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_api);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}
