package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;



import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapsActivity extends FragmentActivity implements
        SeekBar.OnSeekBarChangeListener ,View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback , GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerDragListener{

    private static  final int PERMISSION_REQUEST_STORAGE =1000;
    private static  final int READ_REQUEST_CODE =42;

    private GoogleMap mMap;
    private boolean mIsRestore;
    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
    private List<LatLng> waypointList = new ArrayList<>();
    private List<String> waypointListString = new ArrayList<>();
    private int movingMarker_Index;

    private TextView KML_path;
    private TextView KML_file;

    private SeekBar seekBar1;
    private EditText editText1;

    private Button load_kml;
    private Button Edit;
    private Button switch_screen;
    private Button OpenPanel;
    private LinearLayout Panel;
    private ConstraintLayout Camera;

    private boolean isAdd = false;
    private boolean isOpenPanel = false;

    private void initUI(){

        KML_path = findViewById(R.id.KML_path);
        seekBar1 = findViewById(R.id.seekBar1);
        editText1 = findViewById(R.id.editText1);
        Edit = findViewById(R.id.Edit);
        OpenPanel = findViewById(R.id.btn_open_panel);
        Panel = findViewById(R.id.include2);
        Camera = findViewById(R.id.camera);
        Edit.setOnClickListener(this);
        OpenPanel.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        load_kml = findViewById(R.id.Load_kml);
        load_kml.setOnClickListener(this);
        switch_screen = findViewById(R.id.switch_screen_btn);
        switch_screen.setOnClickListener(this);
        gMap = findViewById(R.id.include);
        //KML_file = findViewById(R.id.KML_file);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsRestore = savedInstanceState != null;
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        initUI();
        mapFragment.getMapAsync(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }
    }

    private void readKML(Uri input) {
        StringBuilder sb = new StringBuilder();
        try{

            // Shared object managers - used to support multiple layer types on the map simultaneously
            // [START maps_multilayer_demo_init1]
            MarkerManager markerManager = new MarkerManager(mMap);
            GroundOverlayManager groundOverlayManager = new GroundOverlayManager(mMap);
            PolygonManager polygonManager = new PolygonManager(mMap);
            PolylineManager polylineManager = new PolylineManager(mMap);
            // [END maps_multilayer_demo_init1]

            //Sources for converting uri to InputStream
            //https://stackoverflow.com/questions/43774287/how-to-convert-the-uri-to-inputstream-data-and-upload-the-stream-data-into-serve

            InputStream inputStream = getContentResolver().openInputStream(input);
            KmlLayer layer = new KmlLayer(mMap,inputStream, this, markerManager, polygonManager, polylineManager, groundOverlayManager,null);
            addKmlToMap(layer);
        }
        catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Import Failed", Toast.LENGTH_SHORT).show();
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed reading the KML file", Toast.LENGTH_SHORT).show();
        }

    }
    private void addKmlToMap(KmlLayer kmlLayer) {
        if (kmlLayer != null) {
            kmlLayer.addLayerToMap();
            kmlLayer.setOnFeatureClickListener(feature -> {Toast.makeText(this,
                    "Feature clicked: " + feature.getId(),
                    Toast.LENGTH_SHORT).show();});
            moveCameraToKml(kmlLayer);
        }
    }
    private void moveCameraToKml(KmlLayer kmlLayer) {
        if (mIsRestore) return;
        try {
            String oo ="1";

            Toast.makeText(this,String.valueOf( kmlLayer.hasPlacemarks()), Toast.LENGTH_SHORT).show();
            if(kmlLayer.hasPlacemarks()){
                oo+=String.valueOf(kmlLayer.hasPlacemarks());

                for(KmlPlacemark i : kmlLayer.getPlacemarks()) {
                    oo+= Arrays.asList(i.getPolygonOptions().getPoints());
                }
            }
            for(KmlContainer container: kmlLayer.getContainers()){
                oo += container.getProperties().toString();
                oo+=container.getContainers().toString();
                oo+=container.getPlacemarks().toString();
                oo+=kmlLayer.getFeatures().toString();
                //oo+= Arrays.toString(kmlLayer.getDefaultLineStringStyle().getGeometryType());
                if(container.hasProperty("name")){

                }
            }
            for(KmlPlacemark placemark1 : kmlLayer.getPlacemarks()){
                oo += placemark1.getProperties().toString();

            }
            //Retrieve the first container in the KML layer
            KmlContainer container = kmlLayer.getContainers().iterator().next();
            //Retrieve a nested container within the first container
            //container = container.getContainers().iterator().next();
            //Retrieve the first placemark in the nested container
            KmlPlacemark placemark = container.getPlacemarks().iterator().next();
            KML_file.setText(oo);
            //Retrieve a polygon object in a placemark
//            List<LatLng> latLng = placemark.getPolylineOptions().getPoints();
//
//            KML_file.setText(container.getPlacemarks().iterator().next().toString());
//
//            PolylineOptions polylines = placemark.getPolylineOptions();
//            mMap.addPolyline(polylines);
            //Create LatLngBounds of the outer coordinates of the polygon
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (LatLng latLng : polygon.getOuterBoundaryCoordinates()) {
//                builder.include(latLng);
//            }
//
//            int width = getResources().getDisplayMetrics().widthPixels;
//            int height = getResources().getDisplayMetrics().heightPixels;
//            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, 1));
        } catch (Exception e) {
            // may fail depending on the KML being shown
            e.printStackTrace();
        }
    }
    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                //readKML(uri);
                read_csv file= new read_csv();

                path =System.getenv("EXTERNAL_STORAGE") +"/"+path.substring(path.indexOf(":") + 1);



//                FileInputStream inputStream = null;
//                try {
//                    File f = new File(path);
//                    inputStream = new FileInputStream(f);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                InputStream inputStream = getResources().openRawResource(R.raw.inspection_trajectory_edited);
                file.setInputStream(inputStream);
                List<Parawind_waypoint> waypointList_csv = file.getWaypoints_csv();
                for(Parawind_waypoint i: waypointList_csv){
                    Log.e("CSV",i.getAllParameter());
                }


            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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

            case R.id.btn_open_panel:
                OpenPanel_active();
                break;
            case R.id.config:{
                showSettingDialog();
                break;
            }
            case R.id.Load_kml:
                performFileSearch();
                break;
            case R.id.switch_screen_btn;

            default:
                break;

        }

    }

    private void enableDisableAdd(){
        if (isAdd == false) {
            isAdd = true;
            Edit.setBackground(getResources().getDrawable(R.drawable.ic_baseline_save_24));
            isOpenPanel = false;
            OpenPanel_active();
            Camera.setVisibility(View.GONE);
            OpenPanel.setVisibility(View.VISIBLE);

        }else{
            isAdd = false;
            Edit.setBackground(getResources().getDrawable(R.drawable.ic_baseline_add_box_24));
            isOpenPanel=true;
            OpenPanel_active();
            Camera.setVisibility(View.VISIBLE);
            OpenPanel.setVisibility(View.GONE);

        }
    }
    private  void OpenPanel_active(){
        if(!isOpenPanel){
            isOpenPanel = true;

            Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_arrow_right_24);
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            drawable.setBounds(0,0,w,h);
            OpenPanel.setCompoundDrawables(drawable,null,null,null);
            Panel.setVisibility(View.VISIBLE);
        }
        else{
            isOpenPanel=false;
            OpenPanel.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_baseline_arrow_left_24),null,null,null);
            Panel.setVisibility(View.GONE);
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
    private void showSettingDialog(){
        LinearLayout wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);

        final TextView wpAltitude_TV = (TextView) wayPointSettings.findViewById(R.id.altitude);
        RadioGroup speed_RG = (RadioGroup) wayPointSettings.findViewById(R.id.speed);
        RadioGroup actionAfterFinished_RG = (RadioGroup) wayPointSettings.findViewById(R.id.actionAfterFinished);
        RadioGroup heading_RG = (RadioGroup) wayPointSettings.findViewById(R.id.heading);

        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lowSpeed){
                    //mSpeed = 3.0f;
                } else if (checkedId == R.id.MidSpeed){
                    //mSpeed = 5.0f;
                } else if (checkedId == R.id.HighSpeed){
                    //mSpeed = 10.0f;
                }
            }

        });

        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.d(TAG, "Select finish action");
                if (checkedId == R.id.finishNone){
                    //mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
                } else if (checkedId == R.id.finishGoHome){
                    //mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
                } else if (checkedId == R.id.finishAutoLanding){
                   // mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
                } else if (checkedId == R.id.finishToFirst){
                    //mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                }
            }
        });

        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.d(TAG, "Select heading");

                if (checkedId == R.id.headingNext) {
                    //mHeadingMode = WaypointMissionHeadingMode.AUTO;
                } else if (checkedId == R.id.headingInitDirec) {
                    //mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                } else if (checkedId == R.id.headingRC) {
                    //mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                } else if (checkedId == R.id.headingWP) {
                    //mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                }
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(wayPointSettings)
                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                        String altitudeString = wpAltitude_TV.getText().toString();
                        //altitude = Integer.parseInt(nulltoIntegerDefalt(altitudeString));
                        //Log.e(TAG,"altitude "+altitude);
                        //Log.e(TAG,"speed "+mSpeed);
                        //Log.e(TAG, "mFinishedAction "+mFinishedAction);
                        //Log.e(TAG, "mHeadingMode "+mHeadingMode);
                        //configWayPointMission();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                })
                .create()
                .show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        editText1.setText(String.valueOf(i));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}