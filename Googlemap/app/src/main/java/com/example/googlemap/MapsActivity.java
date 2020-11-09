package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.strictmode.CleartextNetworkViolation;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback , GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
    private List<LatLng> waypointList = new ArrayList<>();
    private List<String> waypointListString = new ArrayList<>();
    private int movingMarker_Index;

    private Button Edit;

    private boolean isAdd = false;

    private void initUI(){
        Edit = findViewById(R.id.Edit);

        Edit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        initUI();
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        setUpMap();
    }

    /**
     *
     * @param newConfig
     * This show a popup on the screen when the orientation of the phone changes
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(getApplicationContext(),"Portrait mode",Toast.LENGTH_SHORT).show();
        }
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE){
            Toast.makeText(getApplicationContext(),"Landscape mode",Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMap() {
        mMap.setOnMapClickListener(this);// add the listener for click for amap object
        mMap.setOnPolylineClickListener(this);
        mMap.setOnMarkerDragListener(this);

    }
    @Override
    public void onMapClick(LatLng latLng) {
        if(isAdd==true){

            markWaypoint(latLng,waypointList);
            waypointList.add(latLng);
            PolylineOptions polyline =new PolylineOptions();
            for( LatLng i : waypointList)   {

                polyline.add(i);
            }
            mMap.addPolyline(polyline);

        }
    }
    private void markWaypoint(LatLng point,List<LatLng> list){
        int lenOfList = list.toArray().length;
        String name = "WayPoint#" + String.valueOf(lenOfList);
        waypointListString.add(name);
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        //Create MarkerOptions object
        MarkerOptions markerOptions = new MarkerOptions().title(name);
        markerOptions.position(point);
        //This allow the user to drag the marker - edited by Aung
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = mMap.addMarker(markerOptions);
        mMarkers.put(mMarkers.size(), marker);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Edit:
                enableDisableAdd();

                break;
            default:
                break;

        }

    }
    private void enableDisableAdd(){
        if (isAdd == false) {
            isAdd = true;
            Edit.setText("Exit");
        }else{
            isAdd = false;
            Edit.setText("Add");
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        //Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).addAll(waypointList));

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Debugging for checking if the index of the selected waypoint is correct
        //Toast.makeText(getApplicationContext(),String.valueOf(waypointListString.indexOf(marker.getTitle())) ,Toast.LENGTH_SHORT).show();
        movingMarker_Index = waypointListString.indexOf(marker.getTitle());
    }

    @Override
    public void onMarkerDrag(Marker marker) {


    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        waypointList.set(movingMarker_Index,marker.getPosition());
        Toast.makeText(getApplicationContext(),waypointList.toString(),Toast.LENGTH_SHORT).show();
        mMap.clear();
        for(int i =0; i<waypointList.toArray().length;i++){
            mMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .position(waypointList.get(i))
                    .title(waypointListString.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        mMap.addPolyline(new PolylineOptions().clickable(true).addAll(waypointList));

    }
}