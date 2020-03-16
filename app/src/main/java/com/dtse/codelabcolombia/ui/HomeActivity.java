package com.dtse.codelabcolombia.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dtse.codelabcolombia.R;
import com.dtse.codelabcolombia.ui.ml.FaceDetectionActivity;
import com.dtse.codelabcolombia.ui.site.SiteActivity;
import com.dtse.codelabcolombia.utils.PushHelper;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "Home";
    private AVLoadingIndicatorView progress;
    PushHelper pushHelper;
    private boolean isLocationActive = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Location
     */
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    private SettingsClient settingsClient;
    private TextView site;

    private double latitute;
    private double longitute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progress = findViewById(R.id.progress);
        pushHelper = PushHelper.getInstance(this);

        if (checkPermission()){
            getLocation();
            requestLocationUpdatesWithCallback();
        }

        // site
        site = findViewById(R.id.siteButton);
        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationActive) {
                    Intent i = new Intent(HomeActivity.this, SiteActivity.class);
                    i.putExtra("lat", latitute);
                    i.putExtra("lng", longitute);
                    startActivity(i);
                }
            }
        });

        TextView mlButton = findViewById(R.id.mlButton);
        mlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FaceDetectionActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocationActive)
            site.setBackgroundColor(getResources().getColor(R.color.green));
        else
            site.setBackgroundColor(getResources().getColor(R.color.red));
    }

    /**
     * if yo want to this method work you need to call requestLocationUpdatesWithCallback
     */
    private void getLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // create settingsClient
        settingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Set the interval for location updates, in milliseconds.
        mLocationRequest.setInterval(10000);
        // set the priority of the request
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                latitute = location.getLatitude();
                                longitute = location.getLongitude();
                                progress.smoothToHide();
                                isLocationActive = true;
                                site.setBackgroundColor(getResources().getColor(R.color.green));
                                Log.i(TAG,
                                        "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                                + "," + location.getLatitude() + "," + location.getAccuracy());

                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                    }
                }


            };

            checkPermission();
        }
    }

    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }else
                return true;
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }else
                return true;
        }
        return false;
    }

    /**
     * function：Requests location updates with a callback on the specified Looper thread.
     * first：use SettingsClient object to call
     * checkLocationSettings(LocationSettingsRequest locationSettingsRequest)
     * method to check device settings.
     * second： use  FusedLocationProviderClient object to call
     * requestLocationUpdates (LocationRequest request, LocationCallback callback, Looper looper) method.
     *
     * this method makes yo subscribe to location kit and trigger onLocationResult method every ten seconds.
     */
    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "check location settings success");
                            // request location updates
                            fusedLocationProviderClient
                                    .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG,
                                                    "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            // LocationLog.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        // ResolvableApiException rae = (ResolvableApiException) e;
                                        // rae.startResolutionForResult(RequestLocationUpdatesWithCallbackActivity.this, 0);
                                    } catch (Exception ex) {
                                        Log.e(TAG, ex.getMessage());
                                    }
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocation();
        requestLocationUpdatesWithCallback();
    }
}
