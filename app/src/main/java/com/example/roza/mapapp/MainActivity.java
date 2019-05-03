package com.example.roza.mapapp;

import android.annotation.SuppressLint;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;



import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationEngineListener,PermissionsListener,MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originlocation;

    private Point originpoint;
    private Point DestinationPosition;
    private Marker destinationMarker;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        mapView=findViewById(R.id.mapview);
        button=findViewById(R.id.button);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map=mapboxMap;
        map.addOnMapClickListener(this);
        EnableLocation();
    }

    private void EnableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            initializeengine();
            initializelayer();
        }
        else {
         permissionsManager=new PermissionsManager(this);
         permissionsManager.requestLocationPermissions(this);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void initializeengine(){
        locationEngine=new LocationEngineProvider(this).obtainBestLocationEngineAvailable();

        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);

        locationEngine.activate();



        Location lastlocation=locationEngine.getLastLocation();
       if(lastlocation!=null)
       {
           originlocation=lastlocation;
           setCamera(lastlocation);
       }
       else {
           locationEngine.addLocationEngineListener(this);
       }

    }
    @SuppressWarnings("MissingPermission")
    private void initializelayer(){
        locationLayerPlugin=new LocationLayerPlugin(mapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.GPS);
    }


    private void setCamera(Location location)
    {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13.0));
    }
    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
        {
            originlocation=location;
            setCamera(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
            if(granted)
            {
                EnableLocation();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    @SuppressWarnings("MissingPermission")

    protected void onStart() {
        super.onStart();
        if(locationEngine!=null)
            locationEngine.requestLocationUpdates();
        if(locationLayerPlugin!=null)
            locationLayerPlugin.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(locationEngine!=null)
            locationEngine.removeLocationUpdates();

        if(locationLayerPlugin!=null)
            locationLayerPlugin.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null)
            locationEngine.deactivate();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onMapClick(@NonNull LatLng point) {
        destinationMarker=map.addMarker(new MarkerOptions().position(point));
        DestinationPosition =Point.fromLngLat(point.getLongitude(),point.getLatitude());
        originpoint=Point.fromLngLat(originlocation.getLongitude(),originlocation.getLatitude());
        button.setEnabled(true);
        button.setBackgroundResource(R.color.mapbox_blue);
    }
}
