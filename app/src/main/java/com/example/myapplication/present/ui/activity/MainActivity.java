package com.example.myapplication.present.ui.activity;

import static com.example.myapplication.app.Constants.CHANNEL_ID;
import static com.example.myapplication.app.Constants.LAYOUT_GRID;
import static com.example.myapplication.app.Constants.LAYOUT_LIST;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.NavHeaderProfileBinding;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.domain.model.Result;
import com.example.myapplication.domain.model.User;
import com.example.myapplication.present.ui.fragment.favourite.FavouriteFragment;
import com.example.myapplication.R;
import com.example.myapplication.present.ui.fragment.home.controller.HomeFragment;
import com.example.myapplication.present.ui.fragment.reminder.RemindAdapter;
import com.example.myapplication.present.viewmodel.MyViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivity";
    private int currentTab = 0;
    private boolean switchLayout =true;
    private ViewPagerAdapter viewPagerAdapter;
    private MyViewModel myViewModel;
    private ActivityMainBinding binding;
    private String title;
    private NavController navController;
    private boolean trans = true;
    private Movie lastMovieSelected = new Movie();
    private Boolean onNewIntent =false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        onNewIntent =true;
        if(getIntent().getSerializableExtra("pending_reminder")!=null){
            Reminder r = (Reminder)getIntent().getSerializableExtra("pending_reminder");
            onNewIntent =true;
            assert r != null;
            Log.d("case2","onNewIntent -> "+ r.getMovieModel().toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("case2","onResume -> "+onNewIntent);

        if(onNewIntent)handleIntent(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        if(getIntent().getSerializableExtra("pending_reminder")!=null){
            Reminder r = (Reminder)getIntent().getSerializableExtra("pending_reminder");
            onNewIntent =true;
            assert r != null;
            Log.d("case2","onCreate -> "+ r.getMovieModel().toString());
        }
        setContentView(binding.getRoot());
        // Setup Toolbar
        setSupportActionBar(binding.toolbar);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        // Setup TabLayout and ViewPager2
        setupTabLayout();

        // Setup DrawerLayout and NavigationView
        binding.navigationView.setNavigationItemSelectedListener(this);

        // Setup ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout,  binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // set badge for num of fav movies
        setBadgeFav();
        // setup header ui
        setupHeaderNavigation();

        //
        createNotificationChannel();

    }

    private void setBadgeFav(){
        BadgeDrawable badgeDrawable = Objects.requireNonNull(binding.tabLayout.getTabAt(1)).getOrCreateBadge();
        myViewModel.getFavMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movieList) {
                Log.d("LogLog","change = "+movieList.size());
                int numFav = movieList.size();
                badgeDrawable.setBackgroundColor(Color.RED);
                badgeDrawable.setBadgeTextColor(Color.WHITE);
                badgeDrawable.setNumber(numFav);
            }
        });
    }

    private void setupTabLayout() {


        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
            switch (pos) {
                case 0:
                    tab.setText("Home");
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:

                    tab.setText("Favourite");
                    tab.setIcon(R.drawable.heart);

                    break;
                case 2:
                    tab.setText("Settings");
                    tab.setIcon(R.drawable.settings);
                    break;
                case 3:
                    tab.setText("About");
                    tab.setIcon(R.drawable.info_circle);
                    break;
            }
        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentTab = position;
                invalidateOptionsMenu();
                switch (position) {
                    case 0:
                        binding.toolbar.setTitle("Home");
                        break;
                    case 1:
                        binding.toolbar.setTitle("Favourite");
                        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                        binding.toolbar.setNavigationOnClickListener(view -> {
                            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                            } else {
                                binding.drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                        break;
                    case 2:
                        binding.toolbar.setTitle("Settings");
                        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                        binding.toolbar.setNavigationOnClickListener(view -> {
                            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                            } else {
                                binding.drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                        break;
                    case 3:
                        binding.toolbar.setTitle("About");
                        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                        binding.toolbar.setNavigationOnClickListener(view -> {
                            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                            } else {
                                binding.drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                        break;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_actionbar, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
        // set action for search item
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        setupSearchView(searchView);

        // change item display in toolbar when show detail fragment

        myViewModel.getMovieSelected().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie movie) {
                if(movie!= null){
                    if(movie.getId()!=lastMovieSelected.getId()) {
                        trans =true;
                        lastMovieSelected = movie;
                    }
                    enableToolBarAndTabLayout();
                    title = movie.getTitle();
                    myViewModel.setToolBarForHome(false);
                }
            }
        });

        myViewModel.getToolBarForHome().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){ // ui for home screen
                    trans = true;
                    enableToolBarAndTabLayout();
                    binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                    binding.toolbar.setNavigationOnClickListener(view -> {
                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            binding.drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            binding.drawerLayout.openDrawer(GravityCompat.START);
                        }
                    });
                    switch (currentTab) {
                        case 0: // Tab "Home"
                            binding.toolbar.setTitle("Home");
                            menu.findItem(R.id.action_tab).setVisible(true);
                            menu.findItem(R.id.action_tab).setIcon(switchLayout? R.drawable.layout_grid:R.drawable.list);
                            menu.findItem(R.id.three_dot_menu).setVisible(true);
                            break;
                        case 1: // Tab "Favourite"
                            menu.findItem(R.id.action_search).setVisible(true);
                            break;
                    }

                }else{// ui for child of home fragment
                    if(trans && binding.viewPager.getCurrentItem() !=0) binding.viewPager.setCurrentItem(0);
                    if(binding.viewPager.getCurrentItem() == 0){
                        setupChildFragment(menu);
                        binding.toolbar.setTitle(title);
                        trans =false;
                    }
                }
            }
        });

        return true;
    }
    public void setupChildFragment(Menu menu){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_gray);
        binding.toolbar.setNavigationOnClickListener(view -> {
            if(navController!=null){
                navController.popBackStack(R.id.listMovieFragment,false);
            }
            myViewModel.setMovieSelected(null);
            myViewModel.setToolBarForHome(true);

        });
        menu.findItem(R.id.action_tab).setVisible(false);
        menu.findItem(R.id.action_tab).setVisible(false);
        menu.findItem(R.id.three_dot_menu).setVisible(false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
            if (itemId == R.id.action_tab) {
                if (switchLayout) { // list switch to grid layout
                    item.setIcon(R.drawable.list);
                    myViewModel.setTypeLayoutListMovie(LAYOUT_GRID);

                }else{// grid switch to list
                    item.setIcon(R.drawable.layout_grid);
                    myViewModel.setTypeLayoutListMovie(LAYOUT_LIST);
                }
            switchLayout =!switchLayout;

        }else if (itemId == R.id.three_dot_menu) {
            // Show the popup menu when the "three_dot_menu" item is clicked
            showPopupMenu(binding.toolbar.findViewById(R.id.three_dot_menu));
        }
        else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void showPopupMenu(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.item_display, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                SharedPreferences.Editor editor = preferences.edit();

                if (itemId == R.id.popular) {
                    editor.putString("category", "popular");
                } else if (itemId == R.id.toprated) {
                    editor.putString("category", "top_rated");
                } else if (itemId == R.id.upcoming) {
                    editor.putString("category", "upcoming");
                } else if (itemId == R.id.nowplaying) {
                    editor.putString("category", "now_playing");
                }

                // Commit the changes to SharedPreferences
                editor.apply();
                myViewModel.setSettingHasChanged(true);
                return true;
            }
        });

        popupMenu.show();
    }

    private void setupSearchView(SearchView searchView) {
        FavouriteFragment favouriteFragment = (FavouriteFragment) viewPagerAdapter.getFragment(1);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (favouriteFragment != null) {
                    favouriteFragment.searchMovie(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (favouriteFragment != null) {
                    favouriteFragment.searchMovie(newText);
                }
                return false;
            }
        });
    }


    private void setupHeaderNavigation() {
        setUpBodyNavigationHeader();
        myViewModel.getProfileUseCase().loadUserData(this).observe(this, new Observer<Result<User>>() {
            @Override
            public void onChanged(Result<User> userResult) {
                if(userResult.status == Result.Status.SUCCESS){
                    setViewHeader(userResult.data);
                }else{
                    Toast.makeText(getApplicationContext(),userResult.message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setViewHeader(User u){
        NavHeaderProfileBinding bindingHeader = DataBindingUtil.bind(binding.navigationView.getHeaderView(0));
        assert bindingHeader != null;
        bindingHeader.setUser(u);
        ((HomeFragment)viewPagerAdapter.getFragment(0)).getNavController().observe(this, new Observer<NavController>() {
            @Override
            public void onChanged(NavController nc) {
                navController = nc;
                if(nc==null){
                    Log.d("LogLog","nav controller == null");
                }else{
                    bindingHeader.buttonEdit.setOnClickListener(v->{
                        title = "Edit Profile";
                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            binding.drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        disableToolBarAndTabLayout();
                        myViewModel.setToolBarForHome(false);
                        navController.navigate(R.id.profileSettingFragment);
                    });
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpBodyNavigationHeader(){

        NavHeaderProfileBinding bindingHeader = DataBindingUtil.bind(binding.navigationView.getHeaderView(0));
        RemindAdapter remindAdapter = new RemindAdapter(new ArrayList<>(),myViewModel);
        assert bindingHeader != null;
        bindingHeader.reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingHeader.reminderRecyclerView.setAdapter(remindAdapter);
        myViewModel.getReminderUseCase().getUpcomingReminders().observe(this, new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                Log.d("changeLog","Main > AllReminders "+reminders.size());
                remindAdapter.updateReminders(reminders);
            }
        });
        bindingHeader.buttonShowAll.setOnClickListener(view->{
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
            disableToolBarAndTabLayout();
            myViewModel.setToolBarForHome(false);
            navController.navigate(R.id.reminderListFragment);
        });

    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void handleIntent(Intent intent) {
        onNewIntent = false;
        if (intent != null && intent.getExtras() != null) {
            Reminder reminder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // For API level 34 (TIRAMISU) and above, use the new method
                reminder = intent.getSerializableExtra("pending_reminder", Reminder.class);
            } else {
                reminder = (Reminder) intent.getSerializableExtra("pending_reminder");
            }

            if (reminder != null) {
                myViewModel.getReminderUseCase().deleteReminderByID(reminder.getMovieId());
                myViewModel.setMovieSelected(reminder.getMovieModel());
                Log.d("case2",reminder.getMovieModel().toString());

            }
        } else {
            Log.d("loglog", "Main -> handleIntent -> Intent or Extras are null");
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableToolBarAndTabLayout() {
        binding.toolbar.setVisibility(View.GONE);
        binding.tabLayout.setVisibility(View.GONE);
        if (binding.viewPager.getChildAt(0) != null) {
            binding.viewPager.getChildAt(0).setOnTouchListener((v, event) -> true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void enableToolBarAndTabLayout() {
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.tabLayout.setVisibility(View.VISIBLE);
        if (binding.viewPager.getChildAt(0) != null) {
            binding.viewPager.getChildAt(0).setOnTouchListener((v, event) -> false);
        }
    }



}
