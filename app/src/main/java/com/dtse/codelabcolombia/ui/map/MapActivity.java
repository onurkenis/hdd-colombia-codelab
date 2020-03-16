package com.dtse.codelabcolombia.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dtse.codelabcolombia.R;
import com.dtse.codelabcolombia.utils.PushHelper;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.util.LogM;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = "MapViewDemoActivity";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private HuaweiMap hmap;
    private MapView mMapView;

    //Site
    private String name;
    private Double lat;
    private Double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogM.d(TAG, "map onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        PushHelper pushHelper = PushHelper.getInstance(this);

        name = getIntent().getStringExtra("name");
        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);

        // https://developer.huawei.com/consumer/en/doc/development/HMS-References/push-sendapi
        pushHelper.pushRequest("Marker", "Marker added to: " + name);

        // get mapView by layout view
        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        // get map by async method
        /**
         * this method triggers onMapready method
         */
        mMapView.getMapAsync(this);
    }

    private void setMarkerFromSite() {
        hmap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))).setTitle(name);
        CameraPosition build = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(16).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
        hmap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        Log.d(TAG, "onMapReady: ");

        // after call getMapAsync method ,we can get HuaweiMap hiAnalytics in this call back method
        hmap = map;
        hmap.setMyLocationEnabled(true);
        setMarkerFromSite();

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
