package it.unimib.fipavonline.ui.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.R;
import it.unimib.fipavonline.adapter.PartitaRecyclerViewAdapter;
import it.unimib.fipavonline.data.repository.partita.IPartitaRepositoryWithLiveData;
import it.unimib.fipavonline.model.Partita;
import it.unimib.fipavonline.model.PartitaApiResponse;
import it.unimib.fipavonline.model.PartitaResponse;
import it.unimib.fipavonline.model.Result;
import it.unimib.fipavonline.util.Constants;
import it.unimib.fipavonline.util.ErrorMessagesUtil;
import it.unimib.fipavonline.util.ServiceLocator;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Fragment that shows the list of Partita
 */
public class PartitaListFragment extends Fragment {

    private static final String TAG = PartitaListFragment.class.getSimpleName();

    private it.unimib.fipavonline.databinding.FragmentPartitaListBinding FragmentPartitaListBinding;

    private List<Partita> partitaList;
    private PartitaRecyclerViewAdapter partitaRecyclerViewAdapter;
    private PartitaViewModel partitaViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public PartitaListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment PartitaListFragment.
     */
    public static PartitaListFragment newInstance() {
        return new PartitaListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());

        IPartitaRepositoryWithLiveData partitaRepositoryWithLiveData =
                ServiceLocator.getInstance().getPartitaRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        // This is the way to create a ViewModel with custom parameters
        // (see PartitaViewModelFactory class for the implementation details)
        partitaViewModel = new ViewModelProvider(
                requireActivity(),
                new PartitaViewModelFactory(partitaRepositoryWithLiveData)).get(PartitaViewModel.class);

        partitaList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentPartitaListBinding = FragmentPartitaListBinding.inflate(inflater, container, false);
        return FragmentPartitaListBinding.getRoot();

        // TODO: RIMUOVERE codice sotto
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_partita_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        RecyclerView recyclerViewPartita = view.findViewById(R.id.recyclerview_partita_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        partitaRecyclerViewAdapter = new PartitaRecyclerViewAdapter(partitaList,
                requireActivity().getApplication());

        recyclerViewPartita.setLayoutManager(layoutManager);
        recyclerViewPartita.setAdapter(partitaRecyclerViewAdapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(
                Constants.SHARED_PREFERENCES_FILE_NAME, Constants.LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    Constants.SHARED_PREFERENCES_FILE_NAME, Constants.LAST_UPDATE);
        }

        FragmentPartitaListBinding.progressBar.setVisibility(View.VISIBLE);

        // Observe the LiveData associated with the MutableLiveData containing all the Partita
        // returned by the method getPartita(String, long) of PartitaViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        partitaViewModel.getPartita(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {

                        PartitaResponse partitaResponse = ((Result.PartitaResponseSuccess) result).getData();
                        List<Partita> fetchedPartita = partitaResponse.getPartitaList();

                        List<Partita> setPartita = new ArrayList<>();
                        for (Partita p : fetchedPartita) {
                            if (!setPartita.contains(p))
                                setPartita.add(p);
                        }

                        if (!partitaViewModel.isLoading()) {
                            if (partitaViewModel.isFirstLoading()) {
                                partitaViewModel.setResults(((PartitaApiResponse) partitaResponse).getResults());
                                partitaViewModel.setFirstLoading(false);
                                this.partitaList.addAll(setPartita);
                                partitaRecyclerViewAdapter.notifyItemRangeInserted(0,
                                        this.partitaList.size());
                            } else {
                                partitaList.clear();
                                partitaList.addAll(setPartita);
                                partitaRecyclerViewAdapter.notifyItemChanged(0, setPartita.size());
                            }
                            FragmentPartitaListBinding.progressBar.setVisibility(View.GONE);
                        } else {
                            partitaViewModel.setLoading(false);
                            partitaViewModel.setCurrentResults(partitaList.size());
                            partitaRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error)result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                        FragmentPartitaListBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * It checks if the device is connected to Internet.
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
        partitaViewModel.setFirstLoading(true);
        partitaViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentPartitaListBinding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        partitaViewModel.resetPartitaResponseLiveData();
    }

    @Override
    public void onResume() {
        super.onResume();

        IPartitaRepositoryWithLiveData partitaRepositoryWithLiveData =
                ServiceLocator.getInstance().getPartitaRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        partitaViewModel = new ViewModelProvider(
                requireActivity(),
                new PartitaViewModelFactory(partitaRepositoryWithLiveData)).get(PartitaViewModel.class);
    }
}
