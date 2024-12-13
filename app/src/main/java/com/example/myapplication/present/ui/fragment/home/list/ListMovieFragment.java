package com.example.myapplication.present.ui.fragment.home.list;

import static com.example.myapplication.app.Constants.LAYOUT_GRID;
import static com.example.myapplication.app.Constants.LAYOUT_LIST;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.present.utils.NetworkState;
import com.example.myapplication.present.viewmodel.MyViewModel;

public class ListMovieFragment extends Fragment {
    private ListMovieAdapter listMovieAdapter;
    private RecyclerView recyclerView;
    private int layoutType = LAYOUT_LIST;
    private MyViewModel myViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ListMovieFragment() {
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_movie, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        // Initialize the adapter with the default layout type
        listMovieAdapter = new ListMovieAdapter(requireContext(), layoutType,getViewLifecycleOwner());
        recyclerView.setAdapter(listMovieAdapter);
        listMovieAdapter.setMoviesViewModel(myViewModel);

        setupLayoutManager();
        // Observe the PagingData and submit it to the adapter
        myViewModel
                .getPagingMovies()
                .observe(getViewLifecycleOwner(),moviesPagingData ->
                        listMovieAdapter.submitData(getLifecycle(), moviesPagingData)
                );


        // Add load state listener to handle loading, error, and success states
        listMovieAdapter.addLoadStateListener(loadState -> {
            // Post updates to the main thread to ensure they happen after the layout pass
            recyclerView.post(() -> {
                if (loadState.getAppend() instanceof LoadState.Loading) {
                    listMovieAdapter.setNetworkState(new NetworkState(NetworkState.Status.LOADING, "Loading"));
                } else if (loadState.getAppend() instanceof LoadState.Error) {
                    listMovieAdapter.setNetworkState(new NetworkState(NetworkState.Status.LOADED, "Error"));
                    Toast.makeText(getContext(), "Error loading more data", Toast.LENGTH_SHORT).show();
                } else {
                    listMovieAdapter.setNetworkState(new NetworkState(NetworkState.Status.LOADED, "Success"));
                }
            });
            return null;
        });


        // listen for layout changes
        myViewModel.getTypeLayoutListMovie().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    setLayoutType(integer);
                }
            }
        });

        // listen setting change
        myViewModel.getSettingHasChanged().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    myViewModel.refreshPagingMovies();
                    listMovieAdapter.refresh();
                }
            }
        });

        // action for reload screen
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            myViewModel.refreshPagingMovies();
            listMovieAdapter.refresh();
            swipeRefreshLayout.setRefreshing(false);
        });



        return view;
    }

    private void setupLayoutManager() {
        if (layoutType == LAYOUT_GRID) {
            // StaggeredGridLayoutManager for grid layout
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        } else if(layoutType == LAYOUT_LIST) {
            // LinearLayoutManager for list layout
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public void setLayoutType(int type){
        // current position
        int currentPosition = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                currentPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] firstVisibleItems = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                currentPosition = firstVisibleItems[0];
            }
        }
        //update ui
        this.layoutType = type;
        setupLayoutManager();
        listMovieAdapter.setViewType(layoutType);
        recyclerView.setAdapter(listMovieAdapter);

        //scroll
        recyclerView.scrollToPosition(currentPosition);

    }

}
