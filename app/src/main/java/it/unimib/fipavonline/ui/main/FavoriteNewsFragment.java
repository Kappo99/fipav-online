package it.unimib.fipavonline.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.adapter.CampionatoListAdapter;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.ErrorMessagesUtil;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Fragment that shows the favorite news of the user.
 */
public class FavoriteNewsFragment extends Fragment {

    private static final String TAG = FavoriteNewsFragment.class.getSimpleName();

    private List<Campionato> campionatoList;
    private CampionatoListAdapter campionatoListAdapter;
    private ProgressBar progressBar;
    private NewsViewModel newsViewModel;

    public FavoriteNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment FavoriteNewsFragment.
     */
    public static FavoriteNewsFragment newInstance() {
        return new FavoriteNewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        campionatoList = new ArrayList<>();
        newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                // It adds the menu item in the toolbar
                menuInflater.inflate(R.menu.top_app_bar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.delete) {
                    newsViewModel.deleteAllFavoriteNews();
                }
                return false;
            }
            // Use getViewLifecycleOwner() to avoid that the listener
            // associated with a menu icon is called twice
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        progressBar = view.findViewById(R.id.progress_bar);

        ListView listViewFavNews = view.findViewById(R.id.listview_fav_news);

        campionatoListAdapter =
                new CampionatoListAdapter(requireContext(), requireActivity().getApplication(),
                        R.layout.favorite_news_list_item, campionatoList,
                        news -> {
                            news.setFavorite(false);
                            newsViewModel.removeFromFavorite(news);
                        });
        listViewFavNews.setAdapter(campionatoListAdapter);

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING);

        // Observe the LiveData associated with the MutableLiveData containing the favorite news
        // returned by the method getFavoriteNewsLiveData() of NewsViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        newsViewModel.getFavoriteNewsLiveData(isFirstLoading).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    campionatoList.clear();
                    campionatoList.addAll(((Result.CampionatoResponseSuccess)result).getData().getCampionatoList());
                    campionatoListAdapter.notifyDataSetChanged();
                    if (isFirstLoading) {
                        sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                Constants.SHARED_PREFERENCES_FIRST_LOADING, false);
                    }
                } else {
                    ErrorMessagesUtil errorMessagesUtil =
                            new ErrorMessagesUtil(requireActivity().getApplication());
                    Snackbar.make(view, errorMessagesUtil.
                                    getErrorMessage(((Result.Error)result).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
