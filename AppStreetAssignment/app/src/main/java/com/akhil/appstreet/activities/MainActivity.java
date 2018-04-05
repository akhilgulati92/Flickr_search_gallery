package com.akhil.appstreet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.akhil.appstreet.R;
import com.akhil.appstreet.adapters.FlickrImageAdapter;
import com.akhil.appstreet.constants.Constants;
import com.akhil.appstreet.model.FlickrImage;
import com.akhil.appstreet.FlickrApplication;
import com.akhil.appstreet.utils.CommonUtils;
import com.akhil.appstreet.utils.DatabaseHelper;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    final int SDCARD_PERMISSION = 1;
    final String TAG = this.getClass().getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private RelativeLayout rootView;

    //flickrImagesList gets all data from gson, then it is split into two sets for two column view
    private ArrayList<FlickrImage> flickrImagesList;

    //varying url based on constant flickr api url for searching
    private String URL = Constants.URL;

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private SearchView.OnQueryTextListener listener;
    private boolean isLoading, isLastPage;
    private int PAGE_SIZE = 30;
    private String finalQuery = "";
    private int PAGE_NO = 1;
    private FlickrImageAdapter imagesAdapter;
    private int NUM_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //all things of this activity initialized here
    void init() {

        //initialized to avoid null
        flickrImagesList = new ArrayList<>();

        //views
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        imagesAdapter = new FlickrImageAdapter(MainActivity.this, flickrImagesList);
        recyclerView.setAdapter(imagesAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        rootView = (RelativeLayout) findViewById(R.id.relativeLayout);
        initializeLayoutManager();
        setQueryTextListener();
        //search listener from toolbar

    }

    private void setQueryTextListener() {
        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                try {
                    CommonUtils.hideSoftKeyboard(MainActivity.this);

                    //replaces spaces
                    finalQuery = query.replaceAll("\\s", "+");
                    flickrImagesList.clear();
                    if (CommonUtils.isOnline(MainActivity.this)) {
                        Log.d(TAG, "newURL =" + URL);
                        showLoading();
                        requestData(finalQuery, PAGE_NO);
                    } else {
                        List<FlickrImage> cachedData = DatabaseHelper.getInstance().getCache(FlickrImage.class, query);
                        flickrImagesList.addAll(cachedData);
                        if (flickrImagesList != null && flickrImagesList.size() > 0) {
                            imagesAdapter.notifyDataSetChanged();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

    }

    public void initializeLayoutManager() {
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (imagesAdapter.getItemViewType(position)) {
                    case FlickrImageAdapter.ITEM_VIEW_TYPE_FOOTER:
                        return NUM_COLUMNS;
                    case FlickrImageAdapter.ITEM_VIEW_TYPE_BASIC:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        requestStoragePermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper.getInstance().close();
    }


    void showLoading() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(listener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_two:
                NUM_COLUMNS = 2;
                layoutManager = new GridLayoutManager(MainActivity.this, NUM_COLUMNS);
                initializeLayoutManager();
                return true;
            case R.id.action_three:
                NUM_COLUMNS = 3;
                layoutManager = new GridLayoutManager(MainActivity.this, NUM_COLUMNS);
                initializeLayoutManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void requestStoragePermission() {
        // For api Level 23 and above.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SDCARD_PERMISSION);

        }
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage && CommonUtils.isOnline(MainActivity.this)) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    isLoading = true;
                    PAGE_NO++;
                    loadMoreItems();
                    imagesAdapter.setLoading(isLoading);
                    imagesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void loadMoreItems() {
        requestData(finalQuery, PAGE_NO);
        CommonUtils.hideSoftKeyboard(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_FULL_SCREEN) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra(Constants.CURRENT_IMAGE_POSITION)) {
                    int position = data.getIntExtra(Constants.CURRENT_IMAGE_POSITION, -1);
                    if (position != -1) {
                        recyclerView.scrollToPosition(position);
                    }
                }
            }
        }
    }


    void requestData(String keyword, int page_no) {

        //constructs search url
        URL = Constants.URL.replace("keyword", keyword).replace("pageno", String.valueOf(page_no));


        //request data from flickr api using volley library
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading = false;
                Log.d(TAG, "onResponse= " + response.toString());
                new parseTask(response).execute();
                imagesAdapter.setLoading(false);
                imagesAdapter.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                isLoading = false;
                Log.d(TAG, "onErrorResponse= " + error.getMessage());

                if (error.getMessage().contains("UnknownHostException")) {
                    Snackbar.make(rootView, "No Network", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(rootView, "Error - " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }

                imagesAdapter.setLoading(false);
                imagesAdapter.notifyDataSetChanged();


            }
        }
        );

        FlickrApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }//getData


    //background task for parsing data
    class parseTask extends AsyncTask<Void, Void, JSONArray> {

        JSONObject jsonObject;

        public parseTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public JSONArray doInBackground(Void... args) {

            try {

                if (this.jsonObject.has("photos")) {

                    JSONObject jsonObject = this.jsonObject.getJSONObject("photos");
                    JSONArray jsonArray = jsonObject.getJSONArray("photo");

                    if (jsonArray.length() > 0) {
                        return jsonArray;


                    } else {
                        Snackbar.make(rootView, "Found no results", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(rootView, "Found no photos", Snackbar.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(rootView, "Error - " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        public void onPostExecute(JSONArray result) {
            super.onPostExecute(result);

            //  DatabaseHelper.getInstance().processApiResult(result, FlickrImage[].class);

            //gson library for faster and easier serialization
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FlickrImage>>() {
            }.getType();
            List<FlickrImage> list = gson.fromJson(result.toString(), listType);
            flickrImagesList.addAll(list);
            for (FlickrImage obj : flickrImagesList) {
                obj.setTag(finalQuery);
            }

            Log.d("serData", "flickrImagesList.size=" + flickrImagesList.size());
            DatabaseHelper.getInstance().processApiResult(flickrImagesList, FlickrImage[].class);


            hideLoading();
        }

    }//parseTask


}