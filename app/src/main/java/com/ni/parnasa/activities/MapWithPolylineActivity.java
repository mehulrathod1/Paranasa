package com.ni.parnasa.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.Modules.DirectionFinder;
import com.ni.parnasa.activities.Modules.DirectionFinderListener;
import com.ni.parnasa.activities.Modules.Route;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MapWithPolylineActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private Context mContext;

    private GoogleMap mMap;
    private ImageView imgBack;

    private double cur_lat, cur_lng, other_lat, other_lng;
    private ProgressDialog progressDialog;

    //    private List<Marker> originMarkers;
//    private List<Marker> destinationMarkers;
//    private List<Polyline> polylinePaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_with_polyline);

        mContext = MapWithPolylineActivity.this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        cur_lat = Double.parseDouble(intent.getStringExtra("cur_lat"));
        cur_lng = Double.parseDouble(intent.getStringExtra("cur_lng"));
        other_lat = Double.parseDouble(intent.getStringExtra("lat_job"));
        other_lng = Double.parseDouble(intent.getStringExtra("lng_job"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));*/

        drawPolyline();
    }

    private void drawPolyline() {

        String origin = cur_lat + "," + cur_lng;
        String destination = other_lat + "," + other_lng;

        try {
            new DirectionFinder(MapWithPolylineActivity.this, this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(mContext, getString(R.string.please_wait),
                getString(R.string.find_direction), true);

        mMap.clear();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

        progressDialog.dismiss();
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(cur_lat, cur_lng))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(other_lat, other_lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        for (Route routes : route) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(ContextCompat.getColor(mContext, R.color.c_btn_blue))
                    .width(10)
                    .zIndex(101);

            for (int i = 0; i < routes.points.size(); i++)
                polylineOptions.add(routes.points.get(i));

            mMap.addPolyline(polylineOptions);

//            polylinePaths.add();
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cur_lat, cur_lng), 14f));
    }
}
