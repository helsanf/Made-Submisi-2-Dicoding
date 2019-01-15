package com.example.helsanf.catalogemovie.activity;

//import android.app.SearchManager;

import android.app.SearchManager;
import android.media.Image;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.helsanf.catalogemovie.adapter.AdapterCardMovie;
import com.example.helsanf.catalogemovie.res.ItemClickSupport;
import com.example.helsanf.catalogemovie.R;
import com.example.helsanf.catalogemovie.adapter.AdapterMovie;
import com.example.helsanf.catalogemovie.getModel.getMovie;
import com.example.helsanf.catalogemovie.model.MovieItem;
import com.example.helsanf.catalogemovie.res.Api;
import com.example.helsanf.catalogemovie.res.ApiInterface;
import com.example.helsanf.catalogemovie.res.SharedManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    private List<MovieItem> listMovie;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    AdapterCardMovie adapterMovie;
    private SearchView searchView;
    Context context;
    SharedManager sharedManager;
    SweetAlertDialog sweetAlertDialog;
    @BindView(R.id.nextPage)
    ImageView nextPage;
    @BindView(R.id.backPage)
    ImageView backPage;
    int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        CircleImageView profileCircleImageView;
        String profileImageUrl = "https://scontent.fcgk6-1.fna.fbcdn.net/v/t1.0-9/16406473_1230864080284708_4980319282913259791_n.jpg?_nc_cat=100&_nc_ht=scontent.fcgk6-1.fna&oh=4aed2d59bfc1fa6155e24232f2250bb1&oe=5C805241";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle("All Movie");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        profileCircleImageView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        Glide.with(MainActivity.this)
                .load(profileImageUrl)
                .into(profileCircleImageView);
        sharedManager = new SharedManager(getApplicationContext());
        mRecyclerView = findViewById(R.id.list_movie);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapterMovie = new AdapterCardMovie(this);
        getAllMovie(page);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.nextPage)
    public void nextPage() {
        page++;
        getAllMovie(page);
    }

    @OnClick(R.id.backPage)
    public void backPage() {
        if (page > 1) {
            page--;
        } else if (page == 1) {
            Toast.makeText(MainActivity.this, "Anda Sudah di Page Pertama",
                    Toast.LENGTH_LONG).show();
        }
        getAllMovie(page);

    }

    private void getAllMovie(Integer page) {

        ApiInterface apiInterface = Api.getUrl().create(ApiInterface.class);
        Call<getMovie> call = apiInterface.getAllMovie(ApiInterface.API_KEY, ApiInterface.BAHASA, page);
        call.enqueue(new Callback<getMovie>() {
            @Override
            public void onResponse(Call<getMovie> call, Response<getMovie> response) {
                List<MovieItem> list = response.body().getResults();
                adapterMovie.setListMovie(list);
                reloadView(adapterMovie, list);
            }
            @Override
            public void onFailure(Call<getMovie> call, Throwable t) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                getMovie(query);
                nextPage.setVisibility(View.GONE);
                backPage.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getMovie(newText);
                nextPage.setVisibility(View.GONE);
                backPage.setVisibility(View.GONE);
                return true;
            }
        });

        return true;
    }


    private void getMovie(String judul) {
        ApiInterface apiInterface = Api.getUrl().create(ApiInterface.class);
        Call<getMovie> call = apiInterface.getMovie(ApiInterface.API_KEY, ApiInterface.BAHASA, judul);
        call.enqueue(new Callback<getMovie>() {
            @Override
            public void onResponse(Call<getMovie> call, Response<getMovie> response) {
                final List<MovieItem> movieItems = response.body().getResults();
                Log.d("TEST RESULT ", "onRespone " + String.valueOf(movieItems.size()));
                if (movieItems.size() == 0) {
                    sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog.getProgressHelper().setSpinSpeed(100);
                    sweetAlertDialog.setTitleText("WARNING");
                    sweetAlertDialog.setContentText("DATA TIDAK DITEMUKAN");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            movieItems.clear();
                            mRecyclerView.setAdapter(null); //clear jika data tidak ditemukan
                        }
                    });
                    sweetAlertDialog.show();
                } else {
                    adapterMovie.setListMovie(movieItems);
                    reloadView(adapterMovie, movieItems);
                }

            }

            @Override
            public void onFailure(Call<getMovie> call, Throwable t) {
                Log.e("onFailure ", "Gagal " + String.valueOf(t.getMessage()));
            }
        });
    }

    private void getNowPlaying() {
        ApiInterface apiInterface = Api.getUrl().create(ApiInterface.class);
        Call<getMovie> call = apiInterface.getNowPlaying(ApiInterface.API_KEY, ApiInterface.BAHASA, 1);

        call.enqueue(new Callback<getMovie>() {
            @Override
            public void onResponse(Call<getMovie> call, Response<getMovie> response) {
                List<MovieItem> list = response.body().getResults();
                adapterMovie.setListMovie(list);
                reloadView(adapterMovie, list);
            }

            @Override
            public void onFailure(Call<getMovie> call, Throwable t) {

            }
        });
    }

    private void getUpComing() {
        ApiInterface apiInterface = Api.getUrl().create(ApiInterface.class);
        Call<getMovie> call = apiInterface.getUpComing(ApiInterface.API_KEY, ApiInterface.BAHASA, 1);
        call.enqueue(new Callback<getMovie>() {
            @Override
            public void onResponse(Call<getMovie> call, Response<getMovie> response) {
                List<MovieItem> list = response.body().getResults();
                adapterMovie.setListMovie(list);
                reloadView(adapterMovie, list);
            }

            @Override
            public void onFailure(Call<getMovie> call, Throwable t) {

            }
        });
    }

    private void clickItemDetail(MovieItem movieItem) {
        Intent detailActivity = new Intent(this, DetailMovie.class);
        detailActivity.putExtra("movieId", movieItem.getId());
        startActivity(detailActivity);
        this.overridePendingTransition(0, 0);
    }

    public void reloadView(RecyclerView.Adapter adapter, final List<MovieItem> movieItems) {
        mRecyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                MovieItem mList = movieItems.get(position);
                int id_movie = mList.getId();
                sharedManager.createIdMovie(String.valueOf(id_movie));
                clickItemDetail(movieItems.get(position));
            }
        });
    }

    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_settings) {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        String title = null;

        if (id == R.id.now_playing) {
            Toast.makeText(this, "NOW PLAYING",
                    Toast.LENGTH_LONG).show();
            title = "Movie Now Playing";
            nextPage.setVisibility(View.GONE);
            backPage.setVisibility(View.GONE);
            getNowPlaying();
            // Handle the camera action
        } else if (id == R.id.upcoming) {
            Toast.makeText(this, "UPCOMING",
                    Toast.LENGTH_LONG).show();
            title = "Movie Up Coming";
            getUpComing();
            nextPage.setVisibility(View.GONE);
            backPage.setVisibility(View.GONE);
        } else if (id == R.id.allmovie) {
            getAllMovie(1);
            title = "All Movie";
            nextPage.setVisibility(View.VISIBLE);
            backPage.setVisibility(View.VISIBLE);
        }

        setActionBarTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
