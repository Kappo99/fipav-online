package it.unimib.fipavonline.ui.main;

import static it.unimib.fipavonline.util.Constants.LAST_UPDATE;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_COUNTRY_OF_INTEREST;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.fipavonline.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.adapter.CampionatoRecyclerViewAdapter;
import it.unimib.fipavonline.databinding.FragmentCampionatoListBinding;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.NewsApiResponse;
import it.unimib.fipavonline.model.NewsResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.data.repository.news.INewsRepositoryWithLiveData;
import it.unimib.fipavonline.util.ErrorMessagesUtil;
import it.unimib.fipavonline.util.ServiceLocator;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Fragment that shows the news associated with a Country.
 */
public class CampionatoListFragment extends Fragment {

    private static final String TAG = CampionatoListFragment.class.getSimpleName();

    private FragmentCampionatoListBinding fragmentCampionatoListBinding;

    private List<Campionato> campionatoList;
    private CampionatoRecyclerViewAdapter campionatoRecyclerViewAdapter;
    private NewsViewModel newsViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of news
    private int lastVisibleItem; // The position of the last visible news item
    private int visibleItemCount; // Number or total visible news items

    // Based on this value, the process of loading more news is anticipated or postponed
    // Look at the if condition at line 237 to see how it is used
    private final int threshold = 1;

    public CampionatoListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment CampionatoListFragment.
     */
    public static CampionatoListFragment newInstance() {
        return new CampionatoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());

        INewsRepositoryWithLiveData newsRepositoryWithLiveData =
                ServiceLocator.getInstance().getNewsRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        if (newsRepositoryWithLiveData != null) {
            // This is the way to create a ViewModel with custom parameters
            // (see NewsViewModelFactory class for the implementation details)
            newsViewModel = new ViewModelProvider(
                    requireActivity(),
                    new NewsViewModelFactory(newsRepositoryWithLiveData)).get(NewsViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
        campionatoList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentCampionatoListBinding = FragmentCampionatoListBinding.inflate(inflater, container, false);
        return fragmentCampionatoListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String country = sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_COUNTRY_OF_INTEREST);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        RecyclerView recyclerViewCountryNews = view.findViewById(R.id.recyclerview_country_news);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        campionatoRecyclerViewAdapter = new CampionatoRecyclerViewAdapter(campionatoList,
                requireActivity().getApplication(),
                new CampionatoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onFavoriteButtonPressed(int position) {
                campionatoList.get(position).setFavorite(!campionatoList.get(position).isFavorite());
                newsViewModel.updateNews(campionatoList.get(position));
            }
        });
        recyclerViewCountryNews.setLayoutManager(layoutManager);
        recyclerViewCountryNews.setAdapter(campionatoRecyclerViewAdapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(
                        SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        fragmentCampionatoListBinding.progressBar.setVisibility(View.VISIBLE);

        // Observe the LiveData associated with the MutableLiveData containing all the news
        // returned by the method getNews(String, long) of NewsViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        newsViewModel.getNews(country, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
            result -> {
                if (result.isSuccess()) {

                    NewsResponse newsResponse = ((Result.NewsResponseSuccess) result).getData();
                    List<Campionato> fetchedNews = newsResponse.getNewsList();

                    if (!newsViewModel.isLoading()) {
                        if (newsViewModel.isFirstLoading()) {
                            newsViewModel.setTotalResults(((NewsApiResponse) newsResponse).getTotalResults());
                            newsViewModel.setFirstLoading(false);
                            this.campionatoList.addAll(fetchedNews);
                            campionatoRecyclerViewAdapter.notifyItemRangeInserted(0,
                                    this.campionatoList.size());
                        } else {
                            // Updates related to the favorite status of the news
                            campionatoList.clear();
                            campionatoList.addAll(fetchedNews);
                            campionatoRecyclerViewAdapter.notifyItemChanged(0, fetchedNews.size());
                        }
                        fragmentCampionatoListBinding.progressBar.setVisibility(View.GONE);
                    } else {
                        newsViewModel.setLoading(false);
                        newsViewModel.setCurrentResults(campionatoList.size());

                        int initialSize = campionatoList.size();

                        for (int i = 0; i < campionatoList.size(); i++) {
                            if (campionatoList.get(i) == null) {
                                campionatoList.remove(campionatoList.get(i));
                            }
                        }
                        int startIndex = (newsViewModel.getPage()*TOP_HEADLINES_PAGE_SIZE_VALUE) -
                                                                    TOP_HEADLINES_PAGE_SIZE_VALUE;
                        for (int i = startIndex; i < fetchedNews.size(); i++) {
                            campionatoList.add(fetchedNews.get(i));
                        }
                        campionatoRecyclerViewAdapter.notifyItemRangeInserted(initialSize, campionatoList.size());
                    }
                } else {
                    ErrorMessagesUtil errorMessagesUtil =
                            new ErrorMessagesUtil(requireActivity().getApplication());
                    Snackbar.make(view, errorMessagesUtil.
                                    getErrorMessage(((Result.Error)result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
                    fragmentCampionatoListBinding.progressBar.setVisibility(View.GONE);
                }
            });

        recyclerViewCountryNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();

                if (isConnected && totalItemCount != newsViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    // Condition to enable the loading of other news while the user is scrolling the list
                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !newsViewModel.isLoading()
                            ) &&
                            newsViewModel.getNewsResponseLiveData().getValue() != null &&
                            newsViewModel.getCurrentResults() != newsViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> newsListMutableLiveData = newsViewModel.getNewsResponseLiveData();

                        if (newsListMutableLiveData.getValue() != null &&
                                newsListMutableLiveData.getValue().isSuccess()) {

                            newsViewModel.setLoading(true);
                            campionatoList.add(null);
                            campionatoRecyclerViewAdapter.notifyItemRangeInserted(campionatoList.size(),
                                    campionatoList.size() + 1);

                            int page = newsViewModel.getPage() + 1;
                            newsViewModel.setPage(page);
                            newsViewModel.fetchNews(country);
                        }
                    }
                }
            }
        });
    }

    /**
     * Checks if the device is connected to Internet.
     * See: https://developer.android.com/training/monitoring-device-state/connectivity-status-type#DetermineConnection
     * @return true if the device is connected to Internet; false otherwise.
     */
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newsViewModel.setFirstLoading(true);
        newsViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCampionatoListBinding = null;
    }
}
