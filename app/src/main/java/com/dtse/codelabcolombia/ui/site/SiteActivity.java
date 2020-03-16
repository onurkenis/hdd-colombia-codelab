package com.dtse.codelabcolombia.ui.site;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dtse.codelabcolombia.MainApplication;
import com.dtse.codelabcolombia.ui.map.MapActivity;
import com.dtse.codelabcolombia.R;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class SiteActivity extends AppCompatActivity implements SiteAdapter.ISiteAdapter {
    private static final String TAG = "Site Activity";

    private SearchService searchService;
    private SiteAdapter adapter;
    private TextView emptyText;
    private AVLoadingIndicatorView progress;

    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);

        emptyText = findViewById(R.id.emptyText);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SiteAdapter(this);
        recyclerView.setAdapter(adapter);
        progress = findViewById(R.id.progress);
        progress.smoothToHide();

        searchService = SearchServiceFactory.create(this);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                progress.smoothToShow();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.smoothToHide();
    }

    /**
     * with the set radius method we are narrowing down our search result base on distiance
     */
    public void search(String query) {
        NearbySearchRequest nearbySearchRequest = new NearbySearchRequest();
        nearbySearchRequest.setQuery(query);
        nearbySearchRequest.setLocation(new Coordinate(lat, lng));
        nearbySearchRequest.setRadius(10000);
        searchService.nearbySearch(nearbySearchRequest, new SearchResultListener<NearbySearchResponse>() {
            @Override
            public void onSearchResult(NearbySearchResponse nearbySearchResponse) {

                StringBuilder response = new StringBuilder("\n");
                response.append("success\n");
                int count = 1;
                AddressDetail addressDetail;
                for (Site site : nearbySearchResponse.getSites()) {
                    addressDetail = site.getAddress();
                    response.append(String.format(
                            "[%s]  name: %s, formatAddress: %s, country: %s, countryCode: %s \r\n",
                            "" + (count++), site.getName(), site.getFormatAddress(),
                            (addressDetail == null ? "" : addressDetail.getCountry()),
                            (addressDetail == null ? "" : addressDetail.getCountryCode())));
                }
                Log.d(TAG, "search result is : " + response);
                //  resultTextView.setText(response.toString());
                emptyText.setVisibility(View.GONE);
                progress.smoothToHide();
                adapter.setList((ArrayList<Site>) nearbySearchResponse.getSites());
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                Log.e(TAG, "onSearchError is: " + searchStatus.getErrorCode());
            }
        });
    }

    @Override
    public void itemClicked(Site site) {
        progress.smoothToShow();

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("name", site.getName());
        intent.putExtra("lat", site.getLocation().getLat());
        intent.putExtra("lng", site.getLocation().getLng());

        // Log Place Select Event
        MainApplication.hiAnalytics.onEvent("placeSelect", intent.getExtras());

        // Start Map Activity
        startActivity(intent);
    }
}
