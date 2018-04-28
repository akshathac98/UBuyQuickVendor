package com.ubuyquick.vendor;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ShopLocationActivity";

    private final float DEFAULT_ZOOM = 15.0f;
    private double delivery_radius = 100.0D;

    private GoogleMap shopLocationMap;
    private EditText et_delivery_radius, et_search;
    private Button btn_set_radius, btn_set_location, btn_search;

    private double lat = 12.9716, lng = 77.5946;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_location);

//        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_delivery_radius = (EditText) findViewById(R.id.et_delivery_radius);
        et_search = (EditText) findViewById(R.id.et_search);
        btn_set_radius = (Button) findViewById(R.id.btn_set_radius);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_set_location = (Button) findViewById(R.id.btn_set_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_set_radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopLocationMap.clear();
                if (!TextUtils.isEmpty(et_delivery_radius.getText())) {
                    delivery_radius = Double.parseDouble(et_delivery_radius.getText().toString());
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .draggable(true)
                        .title(lat + "," + lng);
                shopLocationMap.addMarker(markerOptions);
                shopLocationMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lng)).radius(delivery_radius));
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: geolocating");
                Geocoder geocoder = new Geocoder(ShopLocationActivity.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(et_search.getText().toString(), 1);
                } catch (IOException e) {
                    Log.d(TAG, "onClick: " + e.getLocalizedMessage());
                }

                if (list.size() > 0) {
                    Address address = list.get(0);
                    Log.d(TAG, "onClick: location found; " + address.toString());
                    shopLocationMap.clear();
                    lat = address.getLatitude();
                    lng = address.getLongitude();
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .title(address.getLocality());

                    if (!TextUtils.isEmpty(et_delivery_radius.getText())) {
                        delivery_radius = Double.parseDouble(et_delivery_radius.getText().toString());
                    }

                    shopLocationMap.addMarker(markerOptions);
                    shopLocationMap.addCircle(new CircleOptions().radius(delivery_radius)
                            .center(latLng));
                    shopLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                }
            }
        });

        btn_set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("radius", delivery_radius);
                returnIntent.putExtra("result", new double[]{lat, lng});
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        shopLocationMap = googleMap;
        LatLng india = new LatLng(lat, lng);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ShopLocationActivity.this);
            dialog.setMessage("Please enable GPS to use My Location");
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ShopLocationActivity.this.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
        }

        shopLocationMap.setMyLocationEnabled(true);
        shopLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 15.0f));

        shopLocationMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lat = latLng.latitude;
                lng = latLng.longitude;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(lat + "," + lng);

                if (!TextUtils.isEmpty(et_delivery_radius.getText())) {
                    delivery_radius = Double.parseDouble(et_delivery_radius.getText().toString());
                }

                shopLocationMap.clear();
                shopLocationMap.addMarker(markerOptions);
                shopLocationMap.addCircle(new CircleOptions().radius(delivery_radius)
                        .center(latLng));
            }
        });

        shopLocationMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                lat = latLng.latitude;
                lng = latLng.longitude;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(lat + "," + lng);

                if (!TextUtils.isEmpty(et_delivery_radius.getText())) {
                    delivery_radius = Double.parseDouble(et_delivery_radius.getText().toString());
                }

                shopLocationMap.clear();
                shopLocationMap.addMarker(markerOptions);
                shopLocationMap.addCircle(new CircleOptions().radius(delivery_radius)
                        .center(latLng));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
