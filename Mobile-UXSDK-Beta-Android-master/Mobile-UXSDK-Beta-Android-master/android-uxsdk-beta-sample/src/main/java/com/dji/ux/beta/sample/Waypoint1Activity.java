//package com.dji.ux.beta.sample;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.SurfaceTexture;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.Icon;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.TextureView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.RadioGroup;
//import android.widget.SeekBar;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//
//import dji.common.camera.SystemState;
//import dji.common.error.DJIError;
//import dji.common.flightcontroller.FlightControllerState;
//import dji.common.flightcontroller.FlightMode;
//import dji.common.gimbal.CapabilityKey;
//import dji.common.mission.waypoint.Waypoint;
//import dji.common.mission.waypoint.WaypointAction;
//import dji.common.mission.waypoint.WaypointActionType;
//import dji.common.mission.waypoint.WaypointMission;
//import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
//import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
//import dji.common.mission.waypoint.WaypointMissionFinishedAction;
//import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
//import dji.common.mission.waypoint.WaypointMissionHeadingMode;
//import dji.common.mission.waypoint.WaypointMissionUploadEvent;
//import dji.common.product.Model;
//import dji.common.useraccount.UserAccountState;
//import dji.common.util.CommonCallbacks;
//import dji.common.util.DJIParamCapability;
//import dji.keysdk.DJIKey;
//import dji.keysdk.FlightControllerKey;
//import dji.keysdk.KeyManager;
//import dji.sdk.base.BaseProduct;
//import dji.sdk.camera.Camera;
//import dji.sdk.camera.VideoFeeder;
//import dji.sdk.codec.DJICodecManager;
//import dji.sdk.flightcontroller.FlightController;
//import dji.sdk.mission.waypoint.WaypointMissionOperator;
//import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
//import dji.sdk.products.Aircraft;
//import dji.sdk.sdkmanager.DJISDKManager;
//import dji.sdk.useraccount.UserAccountManager;
//
//import dji.sdk.gimbal.Gimbal;
//import dji.common.gimbal.GimbalMode;
//
//public class Waypoint1Activity extends FragmentActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback
//        , GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerDragListener, TextureView.SurfaceTextureListener {
//
//
//    protected static final String TAG = "GSDemoActivity";
//
//    private GoogleMap gMap;
//    private int movingMarker_Index;
//
//    private Switch Take_photo_switch;
//    private Switch Take_photo_dist_interval_switch;
//    private boolean Take_photo_bool = false;
//    private boolean Take_photo_dist_interval_bool = false;
//
//    private EditText Photo_dist_interval_editText;
//
//    private SeekBar Payload_pitch_slider,Payload_roll_slider,Payload_yaw_slider;
//    private SeekBar Aircraft_yaw_slider;
//
//    private TextView wpAltitude_TV;
//    private TextView Waypoint_TextView;
//    private TextView AircraftName,AircraftName2;
//
//    private TextView Payload_pitch_TextView,Payload_roll_TextView,Payload_yaw_TextView;
//    private TextView Aircraft_yaw_TextView;
//
//    private RadioGroup speed_RG;
//    private RadioGroup actionAfterFinished_RG;
//    private RadioGroup heading_RG;
//
//
//    private ImageButton locate, add, clear;
//    private ImageButton upload, start, stop;
//    private Button  config,OpenPanel,btn_EachWaypoint;
//    private Button Previous_waypoint_editMode,Next_waypoint_editMode;
//
//    private LinearLayout wayPointSettings;
//    private LinearLayout PanelInfo;
//    private LinearLayout EachWaypoint_Info;
//    private LinearLayout Config_Info;
//
//    private boolean isOpenPanel;
//    private boolean isAdd = false;
//
//    //Aircraft Info
//    private int Waypoint_index;
//    private double droneLocationLat = 181, droneLocationLng = 181;
//    private double droneHeadingDir;
//    private float Aircraft_yaw_value;
//    private List<Float> Aircraft_yawList = new ArrayList<>();
//    private String ActionID,ActionParam;
//    private List<List<String>> ActionIDList = new ArrayList<>();
//
//    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
//    private Marker droneMarker = null;
//
//    private float altitude = 100.0f;
//    private float mSpeed = 10.0f;
//
//    private List<Parawind_waypoints> parawindWaypointsList = new ArrayList<>();
//    private List<Waypoint> waypointList = new ArrayList<>();
//    private List<String> waypointListString = new ArrayList<>();
//    private List<List<Float>> ActionItems = new ArrayList<>();
//
//    public static WaypointMission.Builder waypointMissionBuilder;
//    private FlightController mFlightController;
//    private WaypointMissionOperator instance;
//    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
//    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;
//
//    //Gimbal Info
//    private Gimbal gimbal = null;
//    private int currentGimbalId = 0;
//    private float gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value;
//
//    // Codec for video live view
//    protected DJICodecManager mCodecManager = null;
//    protected TextureView mVideoSurface = null;
//    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
//
//    //Button img
//    private Drawable arrow_right;
//    private Drawable arrow_left;
//
//
//
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        initFlightController();
//        initPreviewer();
//        //onProductChange();
//
//        if(mVideoSurface == null) {
//            Log.e(TAG, "mVideoSurface is null");
//        }
//    }
//    protected void onProductChange() {
//        initPreviewer();
//        //loginAccount();
//    }
//    @Override
//    protected void onPause(){
//        super.onPause();
//        Log.e(TAG, "onPause");
//        uninitPreviewer();
//    }
//
//    @Override
//    protected void onDestroy(){
//        Log.e(TAG, "onDestroy");
//        uninitPreviewer();
//        unregisterReceiver(mReceiver);
//        removeListener();
//        super.onDestroy();
//    }
//
//    /**
//     * @Description : RETURN Button RESPONSE FUNCTION
//     */
//    public void onReturn(View view){
//        Log.d(TAG, "onReturn");
//        this.finish();
//    }
//
//    private void setResultToToast(final String string){
//        Waypoint1Activity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(Waypoint1Activity.this, string, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void initUI() {
//
//        wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);
//
//        wpAltitude_TV = (TextView) wayPointSettings.findViewById(R.id.altitude);
//        speed_RG = (RadioGroup) wayPointSettings.findViewById(R.id.speed);
//        actionAfterFinished_RG = (RadioGroup) wayPointSettings.findViewById(R.id.actionAfterFinished);
//        heading_RG = (RadioGroup) wayPointSettings.findViewById(R.id.heading);
//
//        //Init Switch
//        Take_photo_switch = (Switch) findViewById(R.id.Take_photo_switch);
//        Take_photo_switch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Take_photo_bool = isChecked;
//            }
//        });
//        Take_photo_dist_interval_switch = (Switch) findViewById(R.id.Take_photo_dist_interval_switch);
//        Take_photo_dist_interval_switch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Take_photo_dist_interval_bool = isChecked;
//            }
//        });
//
//        //Init Edit text
//        Photo_dist_interval_editText = findViewById(R.id.Photo_dist_interval_editText);
//
//        //Init Slider
//        Aircraft_yaw_slider = (SeekBar) findViewById(R.id.slider_AircraftYaw);
//        Payload_pitch_slider = (SeekBar) findViewById(R.id.slider_PayloadPitch);
//        Payload_roll_slider = (SeekBar) findViewById(R.id.slider_PayloadRoll);
//        Payload_yaw_slider = (SeekBar) findViewById(R.id.slider_PayloadYaw);
//
//
//        //init Text View
//        Waypoint_TextView = (TextView) findViewById(R.id.Waypoint_TextView);
//        Aircraft_yaw_TextView = (TextView) findViewById(R.id.textView_AircraftYaw);
//        Payload_pitch_TextView = (TextView) findViewById(R.id.textView_PaylaodPitch);
//        Payload_roll_TextView = (TextView) findViewById(R.id.textView_PaylaodRoll);
//        Payload_yaw_TextView = (TextView) findViewById(R.id.textView_PaylaodYaw);
//
//        AircraftName = findViewById(R.id.DJI_device);
//        AircraftName2 = findViewById(R.id.DJI_device2);
//
//        // init mVideoSurface
//        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);
//
//        if (null != mVideoSurface) {
//            mVideoSurface.setSurfaceTextureListener(this);
//        }
//        //init Button
//        locate =  findViewById(R.id.locate);
//        add = findViewById(R.id.add);
//        clear =  findViewById(R.id.clear);
//        config =  findViewById(R.id.config);
//        upload =  findViewById(R.id.upload);
//        start =findViewById(R.id.start);
//        stop =  findViewById(R.id.stop);
//        OpenPanel = (Button) findViewById(R.id.btn_OpenPanel);
//        btn_EachWaypoint = (Button) findViewById(R.id.btn_EachWaypoint);
//        Previous_waypoint_editMode = (Button) findViewById(R.id.btn_Previous_waypoint_editMode);
//        Next_waypoint_editMode = (Button) findViewById(R.id.btn_Next_waypoint_editMode);
//
//
//        PanelInfo = findViewById(R.id.PanelInfo);
//        //init LinearLayout
//        EachWaypoint_Info = (LinearLayout) findViewById(R.id.EachWaypoint_panel);
//        Config_Info = (LinearLayout) findViewById(R.id.Config_Panel);
//
//
//        locate.setOnClickListener(this);
//        add.setOnClickListener(this);
//        clear.setOnClickListener(this);
//        config.setOnClickListener(this);
//        upload.setOnClickListener(this);
//        start.setOnClickListener(this);
//        stop.setOnClickListener(this);
//        OpenPanel.setOnClickListener(this);
//        btn_EachWaypoint.setOnClickListener(this);
//        Previous_waypoint_editMode.setOnClickListener(this);
//        Next_waypoint_editMode.setOnClickListener(this);
//
//        //Action to disable some button to clean up the mainpage
//        Config_Info.setVisibility(View.VISIBLE);
//        EachWaypoint_Info.setVisibility(View.GONE);
//        OpenPanel.setVisibility(View.GONE);
//        PanelInfo.setVisibility(View.GONE);
//        mVideoSurface.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // When the compile and target version is higher than 22, please request the
//        // following permissions at runtime to ensure the
//        // SDK work well.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
//                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
//                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
//                            Manifest.permission.READ_PHONE_STATE
//                    }
//                    , 1);
//        }
//
//        setContentView(R.layout.activity_waypoint1);
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DJIDemoApplication.FLAG_CONNECTION_CHANGE);
//        registerReceiver(mReceiver, filter);
//        if(getWaypointMissionOperator() == null) {
//            setResultToToast("Not support Waypoint1.0");
//            return;
//        }
//        initUI();
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        edit_eachWaypoint();
//        addListener();
//        //####################################################
//        //Collecting Parawind waypoints
//        //####################################################
//        if(MainActivity.ImportSuccess){
//            parawindWaypointsList = MainActivity.parawindWaypointsList;
//        }
//
//        //####################################################
//        //Initialize Camera - start
//        //####################################################
//        // The callback for receiving the raw H264 video data for camera live view
//        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
//
//            @Override
//            public void onReceive(byte[] videoBuffer, int size) {
//                if (mCodecManager != null) {
//                    mCodecManager.sendDataToDecoder(videoBuffer, size);
//                }
//            }
//        };
//
//        Camera camera = DJIDemoApplication.getCameraInstance();
//
//        if (camera != null) {
//
//            camera.setSystemStateCallback(new SystemState.Callback() {
//                @Override
//                public void onUpdate(SystemState cameraSystemState) {
//                    if (null != cameraSystemState) {
//
//                        Waypoint1Activity.this.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }
//    //####################################################
//    //Setting up Camera video
//    //####################################################
//    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            onProductConnectionChange();
//        }
//    };
//
//    private void onProductConnectionChange()
//    {
//        initFlightController();
//        loginAccount();
//    }
//    private void initPreviewer() {
//
//        BaseProduct product = DJIDemoApplication.getProductInstance();
//
//        if (product == null || !product.isConnected()) {
//            setResultToToast(getString(R.string.disconnected));
//        } else {
//            if (null != mVideoSurface) {
//                mVideoSurface.setSurfaceTextureListener(this);
//            }
//            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
//                VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
//            }
//        }
//    }
//
//    private void uninitPreviewer() {
//        Camera camera = DJIDemoApplication.getCameraInstance();
//        if (camera != null){
//            // Reset the callback
//            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
//        }
//    }
//
//    //####################################################
//    //User login in
//    //####################################################
//    private void loginAccount(){
//
//        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
//                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
//                    @Override
//                    public void onSuccess(final UserAccountState userAccountState) {
//                        Log.e(TAG, "Login Success");
//                    }
//                    @Override
//                    public void onFailure(DJIError error) {
//                        setResultToToast("Login Error:"
//                                + error.getDescription());
//                    }
//                });
//    }
//    //####################################################
//    //Initialize Flight controller
//    //####################################################
//    private void initFlightController() {
//
//        BaseProduct product = DJIDemoApplication.getProductInstance();
//        DJIKey key;
//        key = FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION);
//        Object mproduct = KeyManager.getInstance().getValue(key);
//        if (product != null && product.isConnected()) {
//            if (product instanceof Aircraft) {
//                mFlightController = ((Aircraft) product).getFlightController();
//            }
//        }
//
//
//        AircraftName.setText(mproduct.toString());
//        AircraftName2.setText(String.valueOf(mFlightController.getState().getGPSSignalLevel()));
//        if (mFlightController != null) {
//            mFlightController.getState().setFlightMode(FlightMode.GPS_WAYPOINT);
//            mFlightController.setStateCallback(new FlightControllerState.Callback() {
//                //Here is where the DJI app update the state of the drone to the app -Aung
//                @Override
//                public void onUpdate(FlightControllerState djiFlightControllerCurrentState) {
//                    droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();
//                    droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();
//
//                    droneHeadingDir = djiFlightControllerCurrentState.getAttitude().yaw;
//
//                    updateDroneLocation();
//                }
//            });
//        }
//    }
//
//    //Add Listener for WaypointMissionOperator
//    private void addListener() {
//        if (getWaypointMissionOperator() != null){
//            getWaypointMissionOperator().addListener(eventNotificationListener);
//        }
//    }
//
//    private void removeListener() {
//        if (getWaypointMissionOperator() != null) {
//            getWaypointMissionOperator().removeListener(eventNotificationListener);
//        }
//    }
//
//    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
//        @Override
//        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {
//
//        }
//
//        @Override
//        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {
//
//        }
//
//        @Override
//        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {
//
//        }
//
//        @Override
//        public void onExecutionStart() {
//
//        }
//
//        @Override
//        public void onExecutionFinish(@Nullable final DJIError error) {
//            setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
//        }
//    };
//
//    public WaypointMissionOperator getWaypointMissionOperator() {
//        if (instance == null) {
//            if (DJISDKManager.getInstance().getMissionControl() != null){
//                instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
//            }
//        }
//        return instance;
//    }
//
//
//    //####################################################
//    //Action of each button on the UI
//    //####################################################
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.locate:{
//                updateDroneLocation();
//                AircraftName2.setText(String.valueOf(mFlightController.getState().getGPSSignalLevel()));
//                cameraUpdate(); // Locate the drone's place
//                break;
//            }
//            case R.id.add:{
//                enableDisableAdd();
//                break;
//            }
//            case R.id.clear:{
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        gMap.clear();
//                    }
//
//                });
//                waypointList.clear();
//                ActionItems.clear();
//                waypointListString.clear();
//                Aircraft_yawList.clear();
//                waypointMissionBuilder.waypointList(waypointList);
//                updateDroneLocation();
//                break;
//            }
//            case R.id.config:{
//                EachWaypoint_Info.setVisibility(View.GONE);
//                Config_Info.setVisibility(View.VISIBLE);
//                showSettingDialog();
//                break;
//            }
//            case R.id.upload:{
//                uploadWayPointMission();
//                break;
//            }
//            case R.id.start:{
//                startWaypointMission();
//                break;
//            }
//            case R.id.stop:{
//                stopWaypointMission();
//                break;
//            }
//            case R.id.btn_OpenPanel:
//                OpenPanel_active();
//                break;
//
//            case R.id.btn_EachWaypoint:
//                EachWaypoint_Info.setVisibility(View.VISIBLE);
//                Config_Info.setVisibility(View.GONE);
//                break;
//            case  R.id.btn_Previous_waypoint_editMode:
//                if(waypointList.size()>1 && Waypoint_index> 0){
//                    //Do sth
//                    ChangeWaypoint_edit();
//                    Waypoint_index = Waypoint_index -1;
//                    Update_waypointInfo_panel();
//                    Toast.makeText(this,String.valueOf(Math.toRadians(gimbal_pitch_value)),Toast.LENGTH_SHORT).show();
//
//                }
//                break;
//            case  R.id.btn_Next_waypoint_editMode:
//                if(waypointList.size()>1 && Waypoint_index<waypointList.size()){
//                    ChangeWaypoint_edit();
//                    //Do sth here
//                    Waypoint_index = Waypoint_index +1;
//                    Update_waypointInfo_panel();
//                    Toast.makeText(this,String.valueOf(Math.toRadians(gimbal_pitch_value)),Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    //####################################################
//    //Opening / Closing the panel on the right
//    //####################################################
//    private  void OpenPanel_active(){
//        if(!isOpenPanel){
//            isOpenPanel = true;
//            //Call the arrow img for the button
//            arrow_right = getResources().getDrawable(R.drawable.ic_baseline_arrow_right_24);
//            int h = arrow_right.getIntrinsicHeight();
//            int w = arrow_right.getIntrinsicWidth();
//            arrow_right.setBounds(0,0,w,h);
//
//            OpenPanel.setCompoundDrawablesRelativeWithIntrinsicBounds(arrow_right,null,null,null);
//            //OpenPanel.setCompoundDrawables(arrow_right,null,null,null);
//            PanelInfo.setVisibility(View.VISIBLE);
//        }
//        else{
//            isOpenPanel=false;
//            arrow_left = getResources().getDrawable(R.drawable.ic_baseline_arrow_left_24);
//            int h = arrow_left.getIntrinsicHeight();
//            int w = arrow_left.getIntrinsicWidth();
//            arrow_left.setBounds(0,0,w,h);
//            OpenPanel.setCompoundDrawablesRelativeWithIntrinsicBounds(arrow_left,null,null,null);
//            //OpenPanel.setCompoundDrawables(arrow_left,null,null,null);
//            PanelInfo.setVisibility(View.GONE);
//        }
//
//    }
//
//    //####################################################
//    //Enabling the user to add/save all the waypoints
//    //####################################################
//    private void enableDisableAdd(){
//        if (!isAdd) {
//            isAdd = true;
//            add.setImageResource(R.drawable.ic_baseline_save_24);
//            isOpenPanel = false;
//            OpenPanel_active();
//            mVideoSurface.setVisibility(View.GONE);
//            OpenPanel.setVisibility(View.VISIBLE);
//
//        }else{
//            isAdd = false;
//            add.setImageResource(R.drawable.ic_baseline_add_location_24);
//            isOpenPanel=true;
//            OpenPanel_active();
//            mVideoSurface.setVisibility(View.VISIBLE);
//            OpenPanel.setVisibility(View.GONE);
//            String altitudeString = wpAltitude_TV.getText().toString();
//            altitude = Integer.parseInt(nulltoIntegerDefalt(altitudeString));
//            Log.e(TAG,"altitude "+altitude);
//            Log.e(TAG,"speed "+mSpeed);
//            Log.e(TAG, "mFinishedAction "+mFinishedAction);
//            Log.e(TAG, "mHeadingMode "+mHeadingMode);
//            configWayPointMission();
//        }
//    }
//    //####################################################
//    //Editing each waypoint
//    //####################################################
//    private  void edit_eachWaypoint(){
//        Payload_yaw_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Payload_yaw_TextView.setText(String.valueOf(progress-180));
//                gimbal_yaw_value = progress - 180;
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//        Payload_pitch_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Payload_pitch_TextView.setText(String.valueOf(progress-90));
//                gimbal_pitch_value = progress - 90;
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//        Payload_roll_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Payload_roll_TextView.setText(String.valueOf(progress-90));
//                gimbal_roll_value = progress -90;
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//        Aircraft_yaw_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Aircraft_yaw_TextView.setText(String.valueOf(progress-180));
//                Aircraft_yaw_value = progress -180;
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//    }
//    private void ChangeWaypoint_edit(){
//        int index = Waypoint_index;
//        //ActionItems.remove(index);
//        //Aircraft_yawList.remove(index);
//
//        ActionItems.set(index,Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
//        Aircraft_yawList.set(index,Aircraft_yaw_value);
//
//
//    }
//    private void Update_waypointInfo_panel(){
//        int index = Waypoint_index;
//        Aircraft_yaw_value=Aircraft_yawList.get(index);
//        List<Float> GimbalAction = ActionItems.get(index);
//        gimbal_pitch_value = GimbalAction.get(0);
//        gimbal_roll_value = GimbalAction.get(1);
//        gimbal_yaw_value = GimbalAction.get(2);
//
//        Waypoint_TextView.setText("Waypoint#"+(index+1));
//        Aircraft_yaw_TextView.setText(String.valueOf(Aircraft_yaw_value));
//        Aircraft_yaw_slider.setProgress((int) Aircraft_yaw_value +180);
//        Payload_pitch_TextView.setText(String.valueOf(gimbal_pitch_value));
//        Payload_pitch_slider.setProgress((int) gimbal_pitch_value+90);
//
//
//        if(ActionIDList.get(index).get(0).equals("Take_photo")){
//            Take_photo_switch.setChecked(true);
//            Take_photo_bool = true;
//        }
//        else{
//            Take_photo_switch.setChecked(false);
//            Take_photo_bool = false;
//        }
//        if(ActionIDList.get(index).get(0).equals("Take_photo_interval")){
//            Take_photo_dist_interval_switch.setChecked(true);
//            Photo_dist_interval_editText.setText(ActionIDList.get(index).get(1));
//            Take_photo_dist_interval_bool = true;
//        }
//        else{
//            Take_photo_dist_interval_switch.setChecked(false);
//            Photo_dist_interval_editText.setText(ActionIDList.get(index).get(1));
//            Take_photo_dist_interval_bool = false;
//        }
//
//
//
//    }
//    private  void addNewWaypoint(){
//        Aircraft_yaw_value=0;
//        gimbal_pitch_value=0;
//        gimbal_roll_value=0;
//        gimbal_yaw_value=0;
//        ActionItems.add(Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
//        Aircraft_yawList.add(Aircraft_yaw_value);
//    }
//
//    //####################################################
//    //Configuration of the whole flight plan
//    //####################################################
//    private void showSettingDialog(){
//
//        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.lowSpeed){
//                    mSpeed = 3.0f;
//                } else if (checkedId == R.id.MidSpeed){
//                    mSpeed = 5.0f;
//                } else if (checkedId == R.id.HighSpeed){
//                    mSpeed = 10.0f;
//                }
//            }
//
//        });
//
//        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.d(TAG, "Select finish action");
//                if (checkedId == R.id.finishNone){
//                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
//                } else if (checkedId == R.id.finishGoHome){
//                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
//                } else if (checkedId == R.id.finishAutoLanding){
//                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
//                } else if (checkedId == R.id.finishToFirst){
//                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
//                }
//            }
//        });
//
//        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.d(TAG, "Select heading");
//
//                if (checkedId == R.id.headingNext) {
//                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
//                } else if (checkedId == R.id.headingInitDirec) {
//                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
//                } else if (checkedId == R.id.headingRC) {
//                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
//                } else if (checkedId == R.id.headingWP) {
//                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
//                }
//            }
//        });
//
//    }
//
//    String nulltoIntegerDefalt(String value){
//        if(!isIntValue(value)) value="0";
//        return value;
//    }
//
//    boolean isIntValue(String val)
//    {
//        try {
//            val=val.replace(" ","");
//            Integer.parseInt(val);
//        } catch (Exception e) {return false;}
//        return true;
//    }
//    //####################################################
//    //Initialize Gimbal payload
//    //####################################################
//    private Gimbal getGimbalInstance() {
//        if (gimbal == null) {
//            initGimbal();
//        }
//        return gimbal;
//    }
//    private void initGimbal() {
//        if (DJISDKManager.getInstance() != null) {
//            BaseProduct product = DJISDKManager.getInstance().getProduct();
//            if (product != null) {
//                if (product instanceof Aircraft) {
//                    gimbal = ((Aircraft) product).getGimbals().get(currentGimbalId);
//                } else {
//                    gimbal = product.getGimbal();
//                }
//            }
//        }
//    }
//    private boolean isFeatureSupported() {
//
//        Gimbal gimbal = getGimbalInstance();
//        if (gimbal == null) {
//            return false;
//        }
//
//        DJIParamCapability capability = null;
//        if (gimbal.getCapabilities() != null) {
//            capability = gimbal.getCapabilities().get(CapabilityKey.ADJUST_YAW);
//        }
//
//        if (capability != null) {
//            return capability.isSupported();
//        }
//        return false;
//    }
//
//
//    //####################################################
//    //Finalizing the flight plan
//    //####################################################
//    private void configWayPointMission(){
//        Boolean support = isFeatureSupported();
////        gimbal.setYawSimultaneousFollowEnabled(true,new CommonCallbacks.CompletionCallback() {
////            @Override
////            public void onResult(DJIError error) {
////                if (error == null) {
////                    setResultToToast("Aircraft Heading Set Follow");
////                } else {
////                    setResultToToast("Failed set heading : Follow");
////                }
////            }
////        });
//        //###########################
//        //Change the gimbal mode here
//        //###########################
//        if (getGimbalInstance() != null) {
//            getGimbalInstance().setMode(GimbalMode.YAW_FOLLOW, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError error) {
//
//                }
//            });
//        } else {
//            setResultToToast("Product disconnected");
//        }
//        gimbal.setPitchRangeExtensionEnabled(true ,new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//
//            }
//        });
//        if (waypointMissionBuilder == null){
//
//            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
//                    .headingMode(mHeadingMode)
//                    .autoFlightSpeed(mSpeed)
//                    .maxFlightSpeed(mSpeed)
//                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL)
//                    .setGimbalPitchRotationEnabled(true);
//
//        }else
//        {
//            waypointMissionBuilder.finishedAction(mFinishedAction)
//                    .headingMode(mHeadingMode)
//                    .autoFlightSpeed(mSpeed)
//                    .maxFlightSpeed(mSpeed)
//                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL)
//                    .setGimbalPitchRotationEnabled(true);
//
//        }
//
//        //#################################################################################
//        //Add all the actions for each waypoint here !!
//        //#################################################################################
//        if (waypointMissionBuilder.getWaypointList().size() > 0){
//
//            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++){
//                waypointMissionBuilder.getWaypointList().get(i).altitude = waypointList.get(i).altitude;
//                List<Float> GimbalActions = ActionItems.get(i);
//                List<String> ActionsID = ActionIDList.get(i);
////                waypointMissionBuilder.getWaypointList().get(i).gimbalPitch = GimbalActions.get(0);
////                waypointMissionBuilder.getWaypointList().get(i).heading = (int) Aircraft_yawList.get(i).intValue();
//
//                waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH,GimbalActions.get(0).intValue()));
//                waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,Aircraft_yawList.get(i).intValue()));
//                if(ActionsID.get(0).equals("Take_photo")){
//                    waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,0));
//                }
//                if(ActionsID.get(0).equals("Take_photo_interval")){
//                    waypointMissionBuilder.getWaypointList().get(i).shootPhotoDistanceInterval = Float.parseFloat(ActionsID.get(1));
//                }
//
//            }
//
//        }
//
//        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
//        if (error == null) {
//            setResultToToast("loadWaypoint succeeded");
//        } else {
//            setResultToToast("loadWaypoint failed " + error.getDescription());
//        }
//    }
//
//    //#################################################################################
//    //Uploading the mission to the dji drone
//    //#################################################################################
//    private void uploadWayPointMission(){
//
//        getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                if (error == null) {
//                    setResultToToast("Mission upload successfully!");
//                } else {
//                    setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
//                    getWaypointMissionOperator().retryUploadMission(null);
//                }
//            }
//        });
//
//    }
//
//    private void startWaypointMission(){
//
//        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//    }
//
//    private void stopWaypointMission(){
//
//        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//
//    }
//    //####################################################
//    //Initialize clicker on the GoogleMap
//    //####################################################
//    private void setUpMap() {
//        gMap.setOnMapClickListener(this);// add the listener for click for amap object
//        gMap.setOnPolylineClickListener(this);
//        gMap.setOnMarkerDragListener(this);
//
//    }
//    //####################################################
//    //Click action on the GoogleMap
//    //####################################################
//    @Override
//    public void onMapClick(LatLng point) {
//        if (isAdd){
//            markWaypoint(point,waypointList,waypointListString);
//            Waypoint mWaypoint = new Waypoint(point.latitude, point.longitude, altitude);
//
//            //Add Waypoints to Waypoint arraylist;
//            if (waypointMissionBuilder != null) {
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }else
//            {
//                waypointMissionBuilder = new WaypointMission.Builder();
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }
//            PolylineOptions polylineOptions =new PolylineOptions();
//            for( Waypoint i : waypointList)   {
//                polylineOptions.add(new LatLng(i.coordinate.getLatitude(),i.coordinate.getLongitude()));
//            }
//            gMap.addPolyline(polylineOptions);
//            Waypoint_index = waypointList.size()-1;
//            addNewWaypoint();
//            Update_waypointInfo_panel();
//        }else{
//            setResultToToast("Cannot Add Waypoint");
//        }
//    }
//
//    public static boolean checkGpsCoordination(double latitude, double longitude) {
//        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
//    }
//    //####################################################
//    // Update the drone location based on states from MCU.
//    //####################################################
//    private void updateDroneLocation(){
//
//        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
//        //Create MarkerOptions object
//        final MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.aircraft));
//        //This allow the drone to rotate - edited by Aung
//        markerOptions.anchor(0.5f,0.5f);
//        markerOptions.position(pos);
//        markerOptions.rotation((float)droneHeadingDir);
//
//
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (droneMarker != null) {
//                    droneMarker.remove();
//                }
//
//                if (checkGpsCoordination(droneLocationLat, droneLocationLng)) {
//                    droneMarker = gMap.addMarker(markerOptions);
//                }
//            }
//        });
//    }
//    //####################################################
//    //Creating marker as user click on the map
//    //####################################################
//    private void markWaypoint(LatLng point,List<Waypoint> list,List<String> waypointListString){
//        int lenOfList = Objects.requireNonNull(list.toArray()).length;
//        String name = "WayPoint#" + String.valueOf(lenOfList);
//        waypointListString.add(name);
//        //Create MarkerOptions object
//        MarkerOptions markerOptions = new MarkerOptions().title(name);
//        markerOptions.position(point);
//        //This allow the user to drag the marker - edited by Aung
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        Marker marker = gMap.addMarker(markerOptions);
//        mMarkers.put(mMarkers.size(), marker);
//    }
//
//    private void cameraUpdate(){
//        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
//        float zoomlevel = (float) 18.0;
//        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoomlevel);
//        gMap.moveCamera(cu);
//
//    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        if (gMap == null) {
//            gMap = googleMap;
//            setUpMap();
//        }
//
//        LatLng latLng = new LatLng(0,0);
//
//        for(Parawind_waypoints PW : parawindWaypointsList)
//        {
//            //Extracting the coordinate from csv saved list
//            List<Double> PW_coordinate = PW.getCoordinates();
//            Double PW_latitude         = PW_coordinate.get(1);
//            Double PW_longitude        = PW_coordinate.get(0);
//            Float PW_altitude          = PW_coordinate.get(2).floatValue();
//
//            gimbal_pitch_value         = (float) Math.toDegrees(PW.getGimbalAngles().get(0)*-1);
//            gimbal_roll_value          = (float) Math.toDegrees(PW.getGimbalAngles().get(1));
//            gimbal_yaw_value           = (float) Math.toDegrees(PW.getGimbalAngles().get(2));
//            Aircraft_yaw_value         = (float) Math.toDegrees(PW.getDroneAngles().get(2));
//            ActionID                   = PW.getActioID();
//            ActionParam                = PW.getActioParameter();
//
//            //Creating new Latlng for GoogleMap variable and Waypoint for DJI
//            latLng                      = new LatLng(PW_latitude,PW_longitude);
//            Waypoint mWaypoint          = new Waypoint(PW_latitude, PW_longitude,PW_altitude);
//
//            //Draw a marker on the map
//            markWaypoint(latLng,waypointList,waypointListString);
//
//            //Add Waypoints to Waypoint arraylist;
//            if (waypointMissionBuilder != null) {
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }else
//            {
//                waypointMissionBuilder = new WaypointMission.Builder();
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }
//
//            ActionItems.add(Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
//            ActionIDList.add(Arrays.asList(ActionID,ActionParam));
//            Aircraft_yawList.add(Aircraft_yaw_value);
//
//        }
//        Toast.makeText(getApplicationContext(),String.valueOf(ActionItems.get(0).get(0)),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),String.valueOf(parawindWaypointsList.get(0).getGimbalAngles().get(0)),Toast.LENGTH_SHORT).show();
//        PolylineOptions polylineOptions =new PolylineOptions();
//        for( Waypoint i : waypointList)   {
//            polylineOptions.add(new LatLng(i.coordinate.getLatitude(),i.coordinate.getLongitude()));
//        }
//        gMap.addPolyline(polylineOptions);
//        Waypoint_index = waypointList.size()-1;
//        Update_waypointInfo_panel();
//
//        float zoomlevel = (float) 18.0;
//        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoomlevel);
//        gMap.moveCamera(cu);
//
//    }
//
//    @Override
//    public void onMarkerDragStart(Marker marker) {
//        //Debugging for checking if the index of the selected waypoint is correct
//        //Toast.makeText(getApplicationContext(),String.valueOf(waypointListString.indexOf(marker.getTitle())) ,Toast.LENGTH_SHORT).show();
//        movingMarker_Index = waypointListString.indexOf(marker.getTitle());
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//        float marker_alt = waypointList.get(movingMarker_Index).altitude;
//        waypointList.set(movingMarker_Index,new Waypoint(marker.getPosition().latitude,marker.getPosition().longitude,marker_alt));
//        //Toast.makeText(getApplicationContext(),waypointList.toString(),Toast.LENGTH_SHORT).show();
//        gMap.clear();
//        for(int i =0; i<waypointList.toArray().length;i++){
//            gMap.addMarker(new MarkerOptions()
//                    .draggable(true)
//                    .position(new LatLng(waypointList.get(i).coordinate.getLatitude(),waypointList.get(i).coordinate.getLongitude()))
//                    .title(waypointListString.get(i))
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        }
//
//        PolylineOptions polylineOptions =new PolylineOptions();
//        for( Waypoint i : waypointList)   {
//            polylineOptions.add(new LatLng(i.coordinate.getLatitude(),i.coordinate.getLongitude()));
//        }
//        gMap.addPolyline(polylineOptions);
//    }
//
//    @Override
//    public void onPolylineClick(Polyline polyline) {
//
//    }
//
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        Log.e(TAG, "onSurfaceTextureAvailable");
//        if (mCodecManager == null) {
//            mCodecManager = new DJICodecManager(this, surface, width, height);
//        }
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.e(TAG, "onSurfaceTextureSizeChanged");
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        Log.e(TAG,"onSurfaceTextureDestroyed");
//        if (mCodecManager != null) {
//            mCodecManager.cleanSurface();
//            mCodecManager = null;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }
//}
