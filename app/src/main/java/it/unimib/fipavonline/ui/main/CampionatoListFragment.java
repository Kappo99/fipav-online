package it.unimib.fipavonline.ui.main;

import static it.unimib.fipavonline.util.Constants.LAST_UPDATE;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_COUNTRY_OF_INTEREST;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import it.unimib.fipavonline.data.repository.campionato.ICampionatoRepositoryWithLiveData;
import it.unimib.fipavonline.databinding.FragmentCampionatoListBinding;
import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.CampionatoApiResponse;
import it.unimib.fipavonline.model.CampionatoResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.util.ErrorMessagesUtil;
import it.unimib.fipavonline.util.ServiceLocator;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Fragment that shows the campionato associated with a Country.
 */
public class CampionatoListFragment extends Fragment {

    private static final String TAG = CampionatoListFragment.class.getSimpleName();

    private FragmentCampionatoListBinding fragmentCampionatoListBinding;

    private List<Campionato> campionatoList;
    private CampionatoRecyclerViewAdapter campionatoRecyclerViewAdapter;
    private CampionatoViewModel campionatoViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

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

        ICampionatoRepositoryWithLiveData campionatoRepositoryWithLiveData =
                ServiceLocator.getInstance().getCampionatoRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        if (campionatoRepositoryWithLiveData != null) {
            // This is the way to create a ViewModel with custom parameters
            // (see CampionatoViewModelFactory class for the implementation details)
            campionatoViewModel = new ViewModelProvider(
                    requireActivity(),
                    new CampionatoViewModelFactory(campionatoRepositoryWithLiveData)).get(CampionatoViewModel.class);
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

        RecyclerView recyclerViewCampionatoList = view.findViewById(R.id.recyclerview_campionato_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        campionatoRecyclerViewAdapter = new CampionatoRecyclerViewAdapter(campionatoList,
                requireActivity().getApplication(),
                new CampionatoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onFavoriteButtonPressed(int position) {
                campionatoList.get(position).setFavorite(!campionatoList.get(position).isFavorite());
                campionatoViewModel.updateCampionato(campionatoList.get(position));
            }
        });
        recyclerViewCampionatoList.setLayoutManager(layoutManager);
        recyclerViewCampionatoList.setAdapter(campionatoRecyclerViewAdapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(
                        SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        fragmentCampionatoListBinding.progressBar.setVisibility(View.VISIBLE);

        // Observe the LiveData associated with the MutableLiveData containing all the campionato
        // returned by the method getCampionato(long) of CampionatoViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        campionatoViewModel.getCampionato(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
            result -> {
                if (result.isSuccess()) {

                    CampionatoResponse campionatoResponse = ((Result.CampionatoResponseSuccess) result).getData();
                    List<Campionato> fetchedCampionato = campionatoResponse.getCampionatoList();

                    if (!campionatoViewModel.isLoading()) {
                        if (campionatoViewModel.isFirstLoading()) {
                            campionatoViewModel.setTotalResults(((CampionatoApiResponse) campionatoResponse).getResults());
                            campionatoViewModel.setFirstLoading(false);
                            this.campionatoList.addAll(fetchedCampionato);
                            campionatoRecyclerViewAdapter.notifyItemRangeInserted(0,
                                    this.campionatoList.size());
                        } else {
                            // Updates related to the favorite status of the campionato
                            campionatoList.clear();
                            campionatoList.addAll(fetchedCampionato);
                            campionatoRecyclerViewAdapter.notifyItemChanged(0, fetchedCampionato.size());
                        }
                        fragmentCampionatoListBinding.progressBar.setVisibility(View.GONE);
                    } else {
                        campionatoViewModel.setLoading(false);
                        campionatoViewModel.setCurrentResults(campionatoList.size());
                        campionatoRecyclerViewAdapter.notifyDataSetChanged();
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
        campionatoViewModel.setFirstLoading(true);
        campionatoViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCampionatoListBinding = null;
    }
}
