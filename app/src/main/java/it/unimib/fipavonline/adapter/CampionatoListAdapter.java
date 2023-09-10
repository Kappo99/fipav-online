package it.unimib.fipavonline.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.model.Campionato;

/**
 * Custom adapter that extends ArrayAdapter to show an ArrayList of Campionato.
 */
public class CampionatoListAdapter extends ArrayAdapter<Campionato> {

    private final List<Campionato> campionatoList;
    private final Application application;
    private final int layout;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;

    /**
     * Interface to associate a listener to other elements defined in the layout
     * chosen for the ListView item (e.g., a Button).
     */
    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(Campionato campionato);
    }

    public CampionatoListAdapter(@NonNull Context context, Application application, int layout, @NonNull List<Campionato> campionatoList,
                                 OnFavoriteButtonClickListener onDeleteButtonClickListener) {
        super(context, layout, campionatoList);
        this.layout = layout;
        this.campionatoList = campionatoList;
        this.application = application;
        this.onFavoriteButtonClickListener = onDeleteButtonClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }

        TextView textViewNome = convertView.findViewById(R.id.textview_nome);
        ImageView imageViewSesso = convertView.findViewById(R.id.imageview_sex);
        ImageView imageViewFavorite = convertView.findViewById(R.id.imageview_favorite);

        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteButtonClickListener.onFavoriteButtonClick(campionatoList.get(position));
            }
        });

        textViewNome.setText(campionatoList.get(position).getNome());
        setImageViewSesso(imageViewSesso, campionatoList.get(position).getSesso());

        return convertView;
    }

    private void setImageViewSesso(ImageView imageViewSesso, String sesso) {
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
