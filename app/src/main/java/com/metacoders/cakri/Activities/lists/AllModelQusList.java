package com.metacoders.cakri.Activities.lists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.metacoders.cakri.Activities.Details.ModelQustionDetails;
import com.metacoders.cakri.Adapter.JobCircularAdaper;
import com.metacoders.cakri.Adapter.ModelQustionListAdapter;
import com.metacoders.cakri.Models.ModelQustionListResponse;
import com.metacoders.cakri.Activities.Details.PostDetailActivity;
import com.metacoders.cakri.R;
import com.metacoders.cakri.Service.RetrofitClient;
import com.metacoders.cakri.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllModelQusList extends AppCompatActivity implements ModelQustionListAdapter.ItemClickLisnter {
    RecyclerView recyclerView;
    int currentPage = 1;
    int totalPage = 1;
    String nextPage;
    ModelQustionListAdapter adaper;
    List<ModelQustionListResponse.singleModelQus> circularList = new ArrayList<>();
    List<ModelQustionListResponse.singleModelQus> mcircularList = new ArrayList<>();
    boolean isScrolling = false;
    LinearLayoutManager manager;
    int currentItems, totalItems, scrollOutItems;
    SpinKitView progress;
    RelativeLayout loadingPanel;
    LottieAnimationView animationView;
    String cat_id, sub_cat_id, qus_type;
    int qus_name ; // bank = 0  or bcs = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_model_qus_list);
        recyclerView = findViewById(R.id.recyclerView);
        progress = (SpinKitView) findViewById(R.id.spin_kit);
        loadingPanel = findViewById(R.id.loadingPanel);
        animationView = findViewById(R.id.lav_actionBar);

        // getting  the data
        cat_id = getIntent().getStringExtra("cat_id");
        sub_cat_id = getIntent().getStringExtra("sub_cat_id");
        qus_type = getIntent().getStringExtra("qus_type");
        qus_name = getIntent().getIntExtra("qus_name" , 0 ) ;
        manager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(manager);

        adaper = new ModelQustionListAdapter(getApplicationContext(), circularList, this);

        recyclerView.setAdapter(adaper);


        loadList(currentPage);

        initScrollListener();


    }

    private void loadList(int page) {
        if (page == 1) {
            progress.setVisibility(View.GONE);
        } else progress.setVisibility(View.VISIBLE);


        // making two call  one for whole question other for subjective
        // whole qus
        Call<ModelQustionListResponse> WholeCallList = RetrofitClient.getInstance()
                .getApi().getWholeQustionList(cat_id, page);
        // subjective qus
        Call<ModelQustionListResponse> SubjectiveCallKist = RetrofitClient.getInstance()
                .getApi().getSubjectiveQustionList(cat_id, sub_cat_id, page);

        // whole logic for model qusttion slection
        if (qus_type.equals("whole")) {
            // load the whole qus
            WholeCallList.enqueue(new Callback<ModelQustionListResponse>() {
                @Override
                public void onResponse(Call<ModelQustionListResponse> call, Response<ModelQustionListResponse> response) {

                    Log.d(Constants.TAG, "onResponse: " + response.raw());
                    if (response.code() == 200) {

                        currentPage = response.body().getCurrentPage();
                        totalPage = response.body().getLastPage();
                        // now make a list

                        mcircularList = response.body().getData();
                        // circularList = response.body().getData() ;

                        progress.setVisibility(View.GONE);
                        circularList.addAll(mcircularList);
                        adaper.notifyDataSetChanged();


                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            if (currentPage == 1) {
                                //shimmerFrameLayout.setVisibility(View.GONE);
                                animationView.pauseAnimation();
                                loadingPanel.setVisibility(View.GONE);

                            }
                        }, 500);


                    } else {
                        Log.d(Constants.TAG, response.code() + "");
                    }
                }

                @Override
                public void onFailure(Call<ModelQustionListResponse> call, Throwable t) {

                    Log.d(Constants.TAG, t.getMessage() + "");

                }
            });
        } else {
            // load the subjective qustion

            SubjectiveCallKist.enqueue(new Callback<ModelQustionListResponse>() {
                @Override
                public void onResponse(Call<ModelQustionListResponse> call, Response<ModelQustionListResponse> response) {

                    Log.d(Constants.TAG, "onResponse: " + response.raw());
                    if (response.code() == 200) {

                        currentPage = response.body().getCurrentPage();
                        totalPage = response.body().getLastPage();
                        // now make a list

                        mcircularList = response.body().getData();
                        // circularList = response.body().getData() ;

                        progress.setVisibility(View.GONE);
                        circularList.addAll(mcircularList);
                        adaper.notifyDataSetChanged();


                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            if (currentPage == 1) {
                                //shimmerFrameLayout.setVisibility(View.GONE);
                                animationView.pauseAnimation();
                                loadingPanel.setVisibility(View.GONE);

                            }
                        }, 500);


                    } else {
                        Log.d(Constants.TAG, response.code() + "");
                    }
                }

                @Override
                public void onFailure(Call<ModelQustionListResponse> call, Throwable t) {

                    Log.d(Constants.TAG, t.getMessage() + "");

                }
            });
        }


    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    loadMore();
                }
            }
        });


    }

    private void loadMore() {


        if (currentPage != totalPage) {
            loadList(currentPage + 1);
            ;
            isScrolling = false;

        } else {
            isScrolling = false;
            Toast.makeText(getApplicationContext(), "You Are At The Last PAge", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onItemClick(ModelQustionListResponse.singleModelQus model) {
        Intent p = new Intent(getApplicationContext(), ModelQustionDetails.class);
        p.putExtra("MODEL", model);
        p.putExtra("qus_name" , qus_name) ;
        startActivity(p);

    }
}