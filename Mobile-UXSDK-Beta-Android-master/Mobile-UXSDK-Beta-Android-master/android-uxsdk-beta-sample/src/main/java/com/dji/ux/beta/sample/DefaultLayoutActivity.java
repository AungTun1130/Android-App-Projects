/*
 * Copyright (c) 2018-2020 DJI
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.dji.ux.beta.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dji.mapkit.core.camera.DJICameraUpdateFactory;
import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJIBitmapDescriptor;
import com.dji.mapkit.core.models.DJIBitmapDescriptorFactory;
import com.dji.mapkit.core.models.DJICameraPosition;
import com.dji.mapkit.core.models.DJILatLng;
import com.dji.mapkit.core.models.annotations.DJIMarker;
import com.dji.mapkit.core.models.annotations.DJIMarkerOptions;
import com.dji.mapkit.core.models.annotations.DJIPolylineOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dji.common.airlink.PhysicalSource;
import dji.common.flightcontroller.flyzone.FlyZoneCategory;
import dji.common.gimbal.Axis;
import dji.common.gimbal.ResetDirection;
import dji.common.util.CommonCallbacks;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.thirdparty.io.reactivex.android.schedulers.AndroidSchedulers;
import dji.thirdparty.io.reactivex.disposables.CompositeDisposable;
import dji.ux.beta.cameracore.widget.fpvinteraction.FPVInteractionWidget;
import dji.ux.beta.core.extension.ViewExtensions;
import dji.ux.beta.core.panelwidget.systemstatus.SystemStatusListPanelWidget;
import dji.ux.beta.core.panelwidget.topbar.TopBarPanelWidget;
import dji.ux.beta.core.util.SettingDefinitions;
import dji.ux.beta.core.widget.fpv.FPVWidget;
import dji.ux.beta.core.widget.gpssignal.GPSSignalWidget;
import dji.ux.beta.core.widget.simulator.SimulatorIndicatorWidget;
import dji.ux.beta.core.widget.systemstatus.SystemStatusWidget;
import dji.ux.beta.hardwareaccessory.widget.rtk.RTKWidget;
import dji.ux.beta.map.widget.map.MapWidget;
import dji.ux.beta.training.widget.simulatorcontrol.SimulatorControlWidget;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightMode;
import dji.common.gimbal.CapabilityKey;
import dji.sdk.base.BaseProduct;
import dji.common.util.DJIParamCapability;
import dji.sdk.products.Aircraft;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.gimbal.Gimbal;
import dji.common.gimbal.GimbalMode;



/**
 * Displays a sample layout of widgets similar to that of the various DJI apps.
 */
public class DefaultLayoutActivity extends AppCompatActivity implements start_end_waypoint_dialog.WaypointDialogListener {

    //region Fields
    private final static String TAG = "DefaultLayoutActivity";

    @BindView(R.id.widget_fpv)
    protected FPVWidget fpvWidget;
    @BindView(R.id.widget_fpv_interaction)
    protected FPVInteractionWidget fpvInteractionWidget;
    @BindView(R.id.widget_map)
    protected MapWidget mapWidget;
    @BindView(R.id.widget_secondary_fpv)
    protected FPVWidget secondaryFPVWidget;
    @BindView(R.id.root_view)
    protected ConstraintLayout parentView;
    @BindView(R.id.widget_panel_system_status_list)
    protected SystemStatusListPanelWidget systemStatusListPanelWidget;

    @BindView(R.id.widget_rtk)
    protected RTKWidget rtkWidget;
    @BindView(R.id.widget_simulator_control)
    protected SimulatorControlWidget simulatorControlWidget;

    private boolean isMapMini = true;
    private int widgetHeight;
    private int widgetWidth;
    private int widgetMargin;
    private int deviceWidth;
    private int deviceHeight;
    private CompositeDisposable compositeDisposable;
    //endregion
    // Parawind edit

    // Map drawing
    private Bitmap marker_icon_unselected;
    private Bitmap marker_icon_selected;
    private List<DJIMarker> markerList;
    private List<DJILatLng> djiLatLngList;
    private int current_map_type =0;
    private boolean flyzone_enable=false;
    // mission waypoints
    private float altitude = 100.0f;
    private float mSpeed = 10.0f;
    public static WaypointMission.Builder waypointMissionBuilder;
    private WaypointMissionOperator instance;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;
    // UI variable
    private ConstraintLayout Parawind_layout;
    private Button map_setting_btn;
    private Button waypoint_setting_btn;
    private Button mission_setting_btn;
    private Button altitude_increase_btn;
    private Button altitude_decrease_btn;
    private Button del_waypoint_btn;


    private ImageButton upload_btn;
    private ImageButton clear_waypoints_btn;
    private ImageButton add_waypoint_btn;
    private ImageButton flyzone_btn;
    private ImageButton map_type_btn;
    private ImageButton start_mission_btn;
    private ImageButton stop_mission_btn;
    private ImageButton open_close_panel_btn;
    private ImageButton setting_btn;
    private ImageButton Close_all_setting_btn_1;
    private ImageButton Close_all_setting_btn_2;


    private TextView CurrentPos;
    private EditText Photo_dist_interval_editText;
    //private ViewStub viewStub_map;
    private ViewStub viewStub_waypoints;
    private ViewStub viewStub_mission;
    private ViewStub viewStub_all_setting;


    private LinearLayout wayPointSettings;
    private LinearLayout PanelInfo;
    private LinearLayout EachWaypoint_Info;
    private LinearLayout Config_Info;

    private Button next_waypoint_btn;
    private Button previous_waypoint_btn;
    private TextView Aircraft_altitude_TextView;
    private TextView Waypoint_TextView;
    private TextView Payload_pitch_TextView,Payload_roll_TextView,Payload_yaw_TextView;
    private TextView Aircraft_yaw_TextView;
    private SeekBar Payload_pitch_slider,Payload_roll_slider,Payload_yaw_slider;
    private SeekBar Aircraft_yaw_slider;

    private Switch Take_photo_switch;
    private Switch Take_photo_dist_interval_switch;
    private boolean Take_photo_bool = false;
    private boolean Take_photo_dist_interval_bool = false;

    private RadioGroup speed_RG;
    private RadioGroup actionAfterFinished_RG;
    private RadioGroup heading_RG;

    LinearLayout editing_panel;

    private boolean isOpenPanel;
    private boolean isAdd;
    //Gimbal Info
    private Gimbal gimbal = null;
    private int currentGimbalId = 0;
    private float gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value;
    //Aircraft Info
    private FlightController mFlightController;
    private int Waypoint_index;

    private double droneHeadingDir;
    private float Aircraft_yaw_value;
    private List<Float> Aircraft_yawList = new ArrayList<>();
    private String ActionID,ActionParam;
    private List<List<String>> ActionIDList = new ArrayList<>();
    private List<Parawind_waypoints> parawindWaypointsList = new ArrayList<>();
    private volatile List<Waypoint> waypointList = new ArrayList<>();
    private volatile List<String> waypointList_SubsubID = new ArrayList<>();
    private List<String> waypointListString = new ArrayList<>();
    private List<List<Float>> ActionItems = new ArrayList<>();

    // Waypoint Tracking
    private volatile double droneLocationLat , droneLocationLng , droneLocationAlt;
    private Button Set_start_end_waypoint;
    private Button Reset_start_end_waypoint;
    private Handler mainHandler = new Handler();
    private volatile boolean mission_start = false;
    private volatile int Passed_waypoint = 0;
    private int starting_waypoint_index = -1;
    private int ending_waypoint_index = -1;

    // All setting
    private ViewStub viewStub_rtk_setting;
    //Flight controller setting
    private ImageButton FlightController_btn;
    // Obstacle sensing setting
    private ImageButton ObstacleSensing_btn;
    // Remote controller setting
    private ImageButton RemoteController_btn;
    //Image Transmission setting
    private  ImageButton ImageTransmission_btn;
    // Aircraft battery setting
    private  ImageButton Battery_btn;
    // Gimbal setting
    private Button GimbalSetting_btn;
    // Common setting
    private  Button CommonSetting_btn;
    //RTK setting
    //private Button RtkSetting_btn;
    //private Spinner rtk_service_type;

    private void parawind_ui_addition_init(){
        marker_icon_unselected = BitmapFactory.decodeResource(getResources(),R.drawable.ic_waypoint_off);
        marker_icon_selected = BitmapFactory.decodeResource(getResources(),R.drawable.ic_waypoint_on);
        // =================================================================================================
        //                               Initialize Parawind layout
        // =================================================================================================
        Parawind_layout = findViewById(R.id.Parawind_widget);
        // Action button for the mission and map
        upload_btn = findViewById(R.id.upload_btn);
        upload_btn.setOnClickListener(view -> upload_mission());
        clear_waypoints_btn = findViewById(R.id.clear_waypoints_btn);
        clear_waypoints_btn.setOnClickListener(view -> clear_all_waypoints());
        add_waypoint_btn = findViewById(R.id.add_waypoint_btn);
        add_waypoint_btn.setOnClickListener(view -> add_and_edit_waypoints());
        flyzone_btn = findViewById(R.id.flyzone_btn);
        flyzone_btn.setOnClickListener(view -> flyzone_view());
        map_type_btn = findViewById(R.id.map_type_btn);
        map_type_btn.setOnClickListener(view -> change_map_type());

        start_mission_btn= findViewById(R.id.start_mission_btn);
        start_mission_btn.setOnClickListener(view -> start_mission_func());
        stop_mission_btn = findViewById(R.id.stop_mission_btn);
        stop_mission_btn.setOnClickListener(view -> stop_mission_func());

        CurrentPos = findViewById(R.id.Current_pos);
        Set_start_end_waypoint = findViewById(R.id.Set_starting_waypoint);
        Set_start_end_waypoint.setOnClickListener(view -> Set_start_end_waypoint_func());
        Reset_start_end_waypoint = findViewById(R.id.reset_starting_waypoint);
        Reset_start_end_waypoint.setOnClickListener(view -> {starting_waypoint_index = -1; ending_waypoint_index = -1; MainActivity.Latest_waypoint = null;});

        // Editing panels for editing
        // Initialize the panel for editing itself
        editing_panel = findViewById(R.id.editing_panel);

        // Map setting edit
//        viewStub_map = findViewById(R.id.viewStub_map_setting_panel);
//        viewStub_map.inflate();
//        map_setting_btn = findViewById(R.id.map_setting_btn);
//        map_setting_btn.setOnClickListener(view -> change_to_map_setting_panel());
        // all setting edit
        viewStub_all_setting = findViewById(R.id.viewStub_all_setting_panel);
        viewStub_all_setting.inflate();
        viewStub_all_setting.setVisibility(View.GONE);
        Close_all_setting_btn_1 = findViewById(R.id.close_all_setting_btn_1);
        Close_all_setting_btn_1.setOnClickListener(view -> {viewStub_all_setting.setVisibility(View.GONE);});
        Close_all_setting_btn_2 = findViewById(R.id.close_all_setting_btn_2);
        Close_all_setting_btn_2.setOnClickListener(view -> {viewStub_all_setting.setVisibility(View.GONE);});

        // RTK setting edit
        viewStub_rtk_setting = findViewById(R.id.rtk_viewStub);
        viewStub_rtk_setting.inflate();


        // Waypoints setting edit
        viewStub_waypoints = findViewById(R.id.viewStub_waypoints_setting_panel);
        viewStub_waypoints.inflate();
        waypoint_setting_btn = findViewById(R.id.waypoint_setting_btn);
        waypoint_setting_btn.setOnClickListener(view -> change_to_waypoint_setting_panel());

        // Mission setting Edit
        viewStub_mission = findViewById(R.id.viewStub_mission_setting_panel);
        viewStub_mission.inflate();
        mission_setting_btn = findViewById(R.id.mission_config_btn);
        mission_setting_btn.setOnClickListener(view -> change_to_mission_setting_panel());

        // Button for opening and closing the editing panel
        open_close_panel_btn = findViewById(R.id.close_panel_btn);
        open_close_panel_btn.setOnClickListener(view -> open_close_editing_panel());

        // Variables for when editing panel is close and open
        isOpenPanel = false;
        // Variable for allowing user to add/edit waypoints
        isAdd = false;
        // Hiding all the editing panel and widget when app start
        Parawind_layout.setVisibility(View.GONE);
        open_close_panel_btn.setVisibility(View.GONE);
        editing_panel.setVisibility(View.GONE);


        // All widget in the editing Panel
        // =================================================================================================
        // =========================             Waypoint edit               ===============================
        // =================================================================================================
        // Textview
        Aircraft_altitude_TextView = findViewById(R.id.altitude_texview);
        Waypoint_TextView = findViewById(R.id.Waypoint_TextView);
        Payload_pitch_TextView = findViewById(R.id.textView_PaylaodPitch);
        Payload_roll_TextView = findViewById(R.id.textView_PaylaodRoll);
        Payload_yaw_TextView = findViewById(R.id.textView_PayloadYaw);
        Aircraft_yaw_TextView = findViewById(R.id.textView_AircraftYaw);
        // Button UI
        setting_btn = findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(view -> {
            viewStub_all_setting.setVisibility(View.VISIBLE);
            Spinner rtk_service_type = findViewById(R.id.RTK_services_spinner);

            //// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.RTK_services, android.R.layout.simple_spinner_item);
            //// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //// Apply the adapter to the spinner
            rtk_service_type.setAdapter(adapter);
            rtk_service_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#4DA3FF"));
                    if(i==0){
                        findViewById(R.id.Custom_network_rtk_panel).setVisibility(View.GONE);
                        findViewById(R.id.rtk_search_btn).setVisibility(View.GONE);
                    }
                    if(i ==1){
                        findViewById(R.id.Custom_network_rtk_panel).setVisibility(View.GONE);
                        findViewById(R.id.rtk_search_btn).setVisibility(View.VISIBLE);
                    }
                    if(i == 3){
                        findViewById(R.id.rtk_search_btn).setVisibility(View.GONE);
                        findViewById(R.id.Custom_network_rtk_panel).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //Button RtkSetting_btn = findViewById(R.id.RTK_setting);
//            SystemStatusListPanelWidget systemStatusListPanelWidget= findViewById(R.id.widget_panel_system_status_list);
//            if(systemStatusListPanelWidget.getVisibility() == View.GONE){
//                systemStatusListPanelWidget.setVisibility(View.VISIBLE);
//                Parawind_layout.setVisibility(View.GONE);
//            }
//            else {
//                systemStatusListPanelWidget.setVisibility(View.GONE);
//                if(!isMapMini){
//                    Parawind_layout.setVisibility(View.VISIBLE);
//                }
//
//            }
        });

        //##########################################
        // Next waypoint Button
        //##########################################
        next_waypoint_btn = findViewById(R.id.btn_Next_waypoint_editMode);
        next_waypoint_btn.setOnClickListener(new Switch.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(waypointList.size()>1 && Waypoint_index<waypointList.size()-1){
                    ChangeWaypoint_edit();
                    //Do sth here
                    Waypoint_index = Waypoint_index +1;
                    update_waypoint_markers(Waypoint_index);
                    update_waypointInfo_panel();
                    Toast.makeText(DefaultLayoutActivity.this,String.valueOf(Math.toRadians(gimbal_pitch_value)),Toast.LENGTH_SHORT).show();
                }
            }
        });
        //##########################################
        // Previous waypoint Button
        //##########################################
        previous_waypoint_btn = findViewById(R.id.btn_Previous_waypoint_editMode);
        previous_waypoint_btn.setOnClickListener(new Switch.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(waypointList.size()>1 && Waypoint_index>0){
                    ChangeWaypoint_edit();
                    //Do sth here
                    Waypoint_index = Waypoint_index -1;
                    update_waypoint_markers(Waypoint_index);
                    update_waypointInfo_panel();
                    Toast.makeText(DefaultLayoutActivity.this,String.valueOf(Math.toRadians(gimbal_pitch_value)),Toast.LENGTH_SHORT).show();
                }
            }
        });
        //##########################################
        // Increase waypoint altitude Button
        //##########################################
        altitude_increase_btn = findViewById(R.id.altitude_up_btn);
        altitude_increase_btn.setOnClickListener(view -> {
            DJIMap map = mapWidget.getMap();
            waypointList.get(Waypoint_index).altitude +=1.0f;
            Aircraft_altitude_TextView.setText(String.valueOf(waypointList.get(Waypoint_index).altitude));
        });

        //##########################################
        // Decrease waypoint altitude Button
        //##########################################
        altitude_decrease_btn = findViewById(R.id.altitude_down_btn);
        altitude_decrease_btn.setOnClickListener(view -> {
            DJIMap map = mapWidget.getMap();
            waypointList.get(Waypoint_index).altitude -=1.0f;
            Aircraft_altitude_TextView.setText(String.valueOf(waypointList.get(Waypoint_index).altitude));
        });
        //##########################################
        // Delete waypoint Button
        //##########################################
        del_waypoint_btn = findViewById(R.id.del_waypoint_btn);
        del_waypoint_btn.setOnClickListener(view -> {
            int index = Waypoint_index;
            waypointList.remove(index);
            waypointList_SubsubID.remove(index);
            waypointMissionBuilder.getWaypointList().remove(index);
            ActionIDList.remove(index);
            Aircraft_yawList.remove(index);
            ActionItems.remove(index);
            djiLatLngList.remove(index);
            markerList.remove(index);

            if(index>0 && waypointList.size()>0){
                Waypoint_index -=1;
            }
            update_waypoint_markers(index);
            update_waypointInfo_panel();

        });
        //Init Switch
        Take_photo_switch = (Switch) findViewById(R.id.Take_photo_switch);
        Take_photo_switch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Take_photo_bool = b;
            }

        });
        Take_photo_dist_interval_switch = (Switch) findViewById(R.id.Take_photo_dist_interval_switch);
        Take_photo_dist_interval_switch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Take_photo_dist_interval_bool = isChecked;
            }
        });
        //Init Edit text
        Photo_dist_interval_editText = findViewById(R.id.Photo_dist_interval_editText);
        //Init Slider
        Aircraft_yaw_slider = (SeekBar) findViewById(R.id.slider_AircraftYaw);
        Payload_pitch_slider = (SeekBar) findViewById(R.id.slider_PayloadPitch);
        Payload_roll_slider = (SeekBar) findViewById(R.id.slider_PayloadRoll);
        Payload_yaw_slider = (SeekBar) findViewById(R.id.slider_PayloadYaw);

        edit_eachWaypoint();

        // ======================================================
        // Accessing the mission control from DJI SDK
        // ======================================================
        addListener();
        // Check if the SDK support waypoint control mission and initializing the missionOpeartor
        // This allow the developer to upload, start ,and stop the mission
        if(getWaypointMissionOperator() == null) {
            setResultToToast("Not support Waypoint1.0");
        }
        else{
            setResultToToast("Support Waypoint1.0");
        }

    }
    // ================================================================================================================
    //                                                   Parawind UI function
    // ================================================================================================================
    /**
     * Parawind UI function for:
     * upload
     * clear all waypoints
     * add waypoints
     * show my_location
     * change map_type
     * start_mission
     * stop mission
     * open/clase editing panel
     * open mission setting panel
     * open waypoint setting panel
     * open map setting panel
     * Set start and end waypoint
     */
    private void Set_start_end_waypoint_func(){
        start_end_waypoint_dialog start_end_waypoint_dialog = new start_end_waypoint_dialog(waypointList_SubsubID,Passed_waypoint,starting_waypoint_index,ending_waypoint_index);
        start_end_waypoint_dialog.show(getSupportFragmentManager(),"Set start end waypoint dialog");
    }
    private void upload_mission(){
        Toast.makeText(DefaultLayoutActivity.this, "Uploading mission...",Toast.LENGTH_SHORT).show();
        uploadWayPointMission();
    }

    private void clear_all_waypoints(){
        Toast.makeText(DefaultLayoutActivity.this, "Clear",Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waypointList.clear();
                waypointList_SubsubID.clear();
                waypointMissionBuilder.getWaypointList().clear();
                ActionIDList.clear();
                Aircraft_yawList.clear();
                ActionItems.clear();
                djiLatLngList.clear();
                markerList.clear();
                mapWidget.getMap().clear();
            }
        });
    }

    private void add_and_edit_waypoints(){

        if(isAdd){
            open_close_panel_btn.setVisibility(View.GONE);
            add_waypoint_btn.setImageResource(R.drawable.ic_baseline_add_location);
            isOpenPanel = true;
            configWayPointMission();
            isAdd = false;
        }
        else{
            open_close_panel_btn.setVisibility(View.VISIBLE);
            add_waypoint_btn.setImageResource(R.drawable.ic_baseline_save);
            isOpenPanel = false;
            isAdd = true;
        }

        open_close_editing_panel();
    }

    private void flyzone_view(){
        //Toast.makeText(DefaultLayoutActivity.this, "My location",Toast.LENGTH_SHORT).show();

        if(mapWidget.getFlyZoneHelper().isTapToUnlockEnabled()){
            mapWidget.getFlyZoneHelper().setTapToUnlockEnabled(false);
            flyzone_enable = false;
            Toast.makeText(DefaultLayoutActivity.this, "false",Toast.LENGTH_SHORT).show();
        }
        else{
            mapWidget.getFlyZoneHelper().setTapToUnlockEnabled(true);
            flyzone_enable = true;
            Toast.makeText(DefaultLayoutActivity.this, "true",Toast.LENGTH_SHORT).show();
        }


    }

    private void change_map_type(){
        Toast.makeText(DefaultLayoutActivity.this, "ChangeMap",Toast.LENGTH_SHORT).show();
        if (mapWidget.getMap() != null) {
            switch (current_map_type) {
                case 0:
                    mapWidget.getMap().setMapType(DJIMap.MapType.SATELLITE);
                    current_map_type +=1;
                    break;
                case 1:
                    mapWidget.getMap().setMapType(DJIMap.MapType.HYBRID);
                    current_map_type +=1;
                    break;
                case 2:
                    mapWidget.getMap().setMapType(DJIMap.MapType.NORMAL);
                    current_map_type +=1;
                    break;
                default:
                    mapWidget.getMap().setMapType(DJIMap.MAP_TYPE_NONE);
                    current_map_type = 0;
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_map_not_initialized, Toast.LENGTH_SHORT).show();
        }
    }

    private void start_mission_func(){
        Toast.makeText(DefaultLayoutActivity.this, "Mission Start",Toast.LENGTH_SHORT).show();
        startWaypointMission();
        mission_start = true;
    }

    private void stop_mission_func(){
        Toast.makeText(DefaultLayoutActivity.this, "Stop Mission",Toast.LENGTH_SHORT).show();
        stopWaypointMission();
        mission_start = false;
    }

    private void open_close_editing_panel(){
        editing_panel = findViewById(R.id.editing_panel);
        if(isOpenPanel){
            open_close_panel_btn.setImageResource(R.drawable.ic_baseline_chevron_left_24);
            editing_panel.setVisibility(View.GONE);
            isOpenPanel = false;
        }
        else {
            open_close_panel_btn.setImageResource(R.drawable.ic_baseline_chevron_right_24);
            editing_panel.setVisibility(View.VISIBLE);
            isOpenPanel = true;
        }
    }
    private void change_to_mission_setting_panel(){
        mission_setting_btn.setBackgroundResource(R.color.colorDarkBackground);
        waypoint_setting_btn.setBackgroundResource(R.color.white);
        //map_setting_btn.setBackgroundResource(R.color.white);

        viewStub_mission.setVisibility(View.VISIBLE);
        //viewStub_map.setVisibility(View.GONE);
        viewStub_waypoints.setVisibility(View.GONE);

        wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.mission_setting_layout, null);
        speed_RG = (RadioGroup) findViewById(R.id.speed);
        actionAfterFinished_RG = (RadioGroup) findViewById(R.id.actionAfterFinished);
        heading_RG = (RadioGroup)findViewById(R.id.heading);
        showSettingDialog();
    }
    private void change_to_waypoint_setting_panel(){
        mission_setting_btn.setBackgroundResource(R.color.white);
        waypoint_setting_btn.setBackgroundResource(R.color.colorDarkBackground);
        //map_setting_btn.setBackgroundResource(R.color.white);
        viewStub_waypoints.setVisibility(View.VISIBLE);
        viewStub_mission.setVisibility(View.GONE);
        //viewStub_map.setVisibility(View.GONE);
    }
//    private void change_to_map_setting_panel(){
//        mission_setting_btn.setBackgroundResource(R.color.white);
//        waypoint_setting_btn.setBackgroundResource(R.color.white);
//        map_setting_btn.setBackgroundResource(R.color.colorDarkBackground);
//        viewStub_map.setVisibility(View.VISIBLE);
//        viewStub_mission.setVisibility(View.GONE);
//        viewStub_waypoints.setVisibility(View.GONE);
//    }

    //####################################################
    //Editing each waypoint on sliders
    //####################################################
    private  void edit_eachWaypoint(){
        Payload_yaw_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Payload_yaw_TextView.setText(String.valueOf(progress-180));
                gimbal_yaw_value = progress - 180;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Payload_pitch_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Payload_pitch_TextView.setText(String.valueOf(progress-90));
                gimbal_pitch_value = progress - 90;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Payload_roll_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Payload_roll_TextView.setText(String.valueOf(progress-90));
                gimbal_roll_value = progress -90;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Aircraft_yaw_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Aircraft_yaw_TextView.setText(String.valueOf(progress-180));
                Aircraft_yaw_value = progress -180;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    // Saving the current waypoint parameter from UI widget before changing to next/previous waypoints
    private void ChangeWaypoint_edit(){
        int index = Waypoint_index;
        ActionItems.set(index,Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
        Aircraft_yawList.set(index,Aircraft_yaw_value);
    }
    // Refresh the waypoint setting panel and its UI for the selected waypoint
    private void update_waypointInfo_panel(){
        if(waypointList.size()>0) {
            int index = Waypoint_index;
            Log.e(TAG,String.valueOf(index) +"|" +String.valueOf(Aircraft_yawList.size())+"|"+String.valueOf(waypointList.size()));

            Aircraft_yaw_value = Aircraft_yawList.get(index);
            List<Float> GimbalAction = ActionItems.get(index);
            gimbal_pitch_value = GimbalAction.get(0);
            gimbal_roll_value = GimbalAction.get(1);
            gimbal_yaw_value = GimbalAction.get(2);

            Waypoint_TextView.setText("Waypoint#" + (index + 1));
            Aircraft_altitude_TextView.setText(String.valueOf(waypointList.get(index).altitude));
            Aircraft_yaw_TextView.setText(String.valueOf(Aircraft_yaw_value));
            Aircraft_yaw_slider.setProgress((int) Aircraft_yaw_value + 180);
            Payload_pitch_TextView.setText(String.valueOf(gimbal_pitch_value));
            Payload_pitch_slider.setProgress((int) gimbal_pitch_value + 90);

            if (ActionIDList.get(index).get(0).equals("Take_photo")) {
                Take_photo_switch.setChecked(true);
                Take_photo_bool = true;
            } else {
                Take_photo_switch.setChecked(false);
                Take_photo_bool = false;
            }
            if (ActionIDList.get(index).get(0).equals("Take_photo_interval")) {
                Take_photo_dist_interval_switch.setChecked(true);
                Photo_dist_interval_editText.setText(ActionIDList.get(index).get(1));
                Take_photo_dist_interval_bool = true;
            } else {
                Take_photo_dist_interval_switch.setChecked(false);
                Photo_dist_interval_editText.setText(ActionIDList.get(index).get(1));
                Take_photo_dist_interval_bool = false;
            }
        }
    }

    // Add new variables when user add new waypoint on the map
    private  void addNewWaypoint(){
        Aircraft_yaw_value=0;
        gimbal_pitch_value=0;
        gimbal_roll_value=0;
        gimbal_yaw_value=0;
        ActionIDList.add(Arrays.asList("None","None"));
        ActionItems.add(Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
        Aircraft_yawList.add(Aircraft_yaw_value);
    }
    //=========================================================================================================
    //                                      DJI SDK related
    //=========================================================================================================
    //Add Listener for WaypointMissionOperator
    private void addListener() {
        if (getWaypointMissionOperator() != null){
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }
    // Remove listener for WaypointMissionOperator
    private void removeListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {

        }

        @Override
        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {

        }

        @Override
        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {
            assert executionEvent.getProgress() != null;
            Passed_waypoint =  executionEvent.getProgress().targetWaypointIndex;
        }

        @Override
        public void onExecutionStart() {

        }

        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {
            setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
        }
    };

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }
    private void setResultToToast(final String string){
        DefaultLayoutActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DefaultLayoutActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //#################################################################################
    //Uploading the mission to the dji drone
    //#################################################################################
    private void uploadWayPointMission(){

        getWaypointMissionOperator().uploadMission(error -> {
            if (error == null) {
                setResultToToast("Mission upload successfully!");
            } else {
                setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
                getWaypointMissionOperator().retryUploadMission(null);
            }
        });

    }
    //#################################################################################
    // Starting the mission to the dji drone
    //#################################################################################
    private void startWaypointMission(){
        getWaypointMissionOperator().startMission(error -> setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription())));

    }
    //#################################################################################
    // Stopping the mission to the dji drone
    //#################################################################################
    private void stopWaypointMission(){

        getWaypointMissionOperator().stopMission(error -> setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription())));

    }
    //####################################################
    //Configuration of the whole flight plan
    //####################################################
    private void showSettingDialog(){

        speed_RG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lowSpeed){
                mSpeed = 3.0f;
            } else if (checkedId == R.id.MidSpeed){
                mSpeed = 5.0f;
            } else if (checkedId == R.id.HighSpeed){
                mSpeed = 10.0f;
            }
            setResultToToast("Speed:" +mSpeed);
        });

        actionAfterFinished_RG.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Select finish action");
            if (checkedId == R.id.finishNone){
                mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
            } else if (checkedId == R.id.finishGoHome){
                mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
            } else if (checkedId == R.id.finishAutoLanding){
                mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
            } else if (checkedId == R.id.finishToFirst){
                mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
            }
        });

        heading_RG.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Select heading");

            if (checkedId == R.id.headingNext) {
                mHeadingMode = WaypointMissionHeadingMode.AUTO;
            } else if (checkedId == R.id.headingInitDirec) {
                mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
            } else if (checkedId == R.id.headingRC) {
                mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
            } else if (checkedId == R.id.headingWP) {
                mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
            }
        });

    }
    //####################################################
    //Initialize Gimbal payload
    //####################################################
    private Gimbal getGimbalInstance() {
        if (gimbal == null) {
            initGimbal();
        }
        return gimbal;
    }
    private void initGimbal() {
        if (DJISDKManager.getInstance() != null) {
            BaseProduct product = DJISDKManager.getInstance().getProduct();
            if (product != null) {
                if (product instanceof Aircraft) {
                    gimbal = ((Aircraft) product).getGimbals().get(currentGimbalId);
                } else {
                    gimbal = product.getGimbal();
                }
            }
        }
    }
    private boolean isFeatureSupported() {

        Gimbal gimbal = getGimbalInstance();
        if (gimbal == null) {
            return false;
        }

        DJIParamCapability capability = null;
        if (gimbal.getCapabilities() != null) {
            capability = gimbal.getCapabilities().get(CapabilityKey.ADJUST_YAW);
        }

        if (capability != null) {
            return capability.isSupported();
        }
        return false;
    }
    //####################################################
    //Initialize Flight controller
    //####################################################
    private void initFlightController() {

        if (DJISDKManager.getInstance() != null) {
            BaseProduct product = DJISDKManager.getInstance().getProduct();

            if (product != null && product.isConnected()) {
                if (product instanceof Aircraft) {
                    mFlightController = ((Aircraft) product).getFlightController();
                }
            }

            if (mFlightController != null) {
                mFlightController.getState().setFlightMode(FlightMode.GPS_WAYPOINT);
                mFlightController.setStateCallback(new FlightControllerState.Callback() {
                    //Here is where the DJI app update the state of the drone to the app -Aung
                    @Override
                    public void onUpdate(FlightControllerState djiFlightControllerCurrentState) {
                        droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();
                        droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();
                        droneLocationAlt = djiFlightControllerCurrentState.getAircraftLocation().getAltitude();

                        droneHeadingDir = djiFlightControllerCurrentState.getAttitude().yaw;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String lat = String.format("%.6f",droneLocationLat);
                                String lng = String.format("%.6f",droneLocationLng);
                                String alt = String.format("%.3f",droneLocationAlt);
                                String dir = String.format("%.2f",droneHeadingDir);
                                String text = lat +"|"+ lng+"|" + alt + "|"+dir;
                                CurrentPos.setText(text);
                                if(mission_start && starting_waypoint_index != -1 && ending_waypoint_index !=-1){
                                    TextView te = findViewById(R.id.current_pos_stat);
                                    int wp = Passed_waypoint;

                                    try {
                                        List<String> wp_labels = waypointList_SubsubID.subList(starting_waypoint_index,ending_waypoint_index+1);
                                        te.setText(wp_labels.get(wp));
                                        MainActivity.Latest_waypoint = wp_labels.get(wp);
                                    } catch (Exception e) {
                                        setResultToToast("Wrong indexes");
                                    }
                                }


                            }
                        });
                    }
                });
            }
        }

    }


    //####################################################
    //Finalizing the flight plan
    //####################################################
    private void configWayPointMission(){
        //###########################
        //Change the gimbal mode here
        //###########################
        if (getGimbalInstance() != null) {
            getGimbalInstance().setMode(GimbalMode.YAW_FOLLOW, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    //setResultToToast("Yaw follow mode");
                }
            });
            gimbal.setPitchRangeExtensionEnabled(true ,new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                }
            });
            getGimbalInstance().reset(Axis.YAW_AND_PITCH, ResetDirection.CENTER, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
        } else {
            setResultToToast("Product disconnected");
        }

        if (waypointMissionBuilder == null){

            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL)
                    .setGimbalPitchRotationEnabled(true);

        }else
        {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL)
                    .setGimbalPitchRotationEnabled(true);

        }
//        if(waypointMissionBuilder.getHeadingMode() == WaypointMissionHeadingMode.USING_WAYPOINT_HEADING){
//            setResultToToast("Waypoint heading mode");
//        }
//        else{
//            setResultToToast("Not waypoint heading mode");
//        }
        //#################################################################################
        //Add all the actions for each waypoint here !!
        //#################################################################################
        if (waypointList.size() > 0){
            int start_wp_index;
            int end_wp_index;
            if(waypointMissionBuilder.getWaypointList().size()>0){
                waypointMissionBuilder.getWaypointList().clear();
            }
            if(starting_waypoint_index != -1 && ending_waypoint_index != -1){
                start_wp_index = starting_waypoint_index;
                end_wp_index = ending_waypoint_index;
            }
            else{
                start_wp_index = 0;
                end_wp_index = waypointMissionBuilder.getWaypointList().size()-1;
            }
            String text = "Start Saypoint: " +waypointList_SubsubID.get(start_wp_index) +"\n"+
                    "End Waypoint: "+waypointList_SubsubID.get(end_wp_index);
            setResultToToast(text);
//            List<Waypoint> wp_temp = waypointList.subList(start_wp_index,end_wp_index);
//            waypointMissionBuilder.waypointList(wp_temp).waypointCount(wp_temp.size());
            for (int i=start_wp_index; i<= end_wp_index; i++){
                Waypoint temp = waypointList.get(i);

                //waypointMissionBuilder.getWaypointList().get(i).altitude = waypointList.get(i).altitude;
                temp.altitude = waypointList.get(i).altitude;

                int yaw = Aircraft_yawList.get(i).intValue();

                List<Float> GimbalActions = ActionItems.get(i);
                List<String> ActionsID = ActionIDList.get(i);

//                waypointMissionBuilder.getWaypointList().get(i).gimbalPitch = GimbalActions.get(0);
//                waypointMissionBuilder.getWaypointList().get(i).heading = (int) yaw;
                temp.gimbalPitch = GimbalActions.get(0);
                temp.heading = (int) yaw;

//                waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH,GimbalActions.get(0).intValue()));
//                waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,yaw));
                if(ActionsID.get(0).equals("Take_photo_interval")){
                    //waypointMissionBuilder.getWaypointList().get(i).shootPhotoDistanceInterval = Float.parseFloat(ActionsID.get(1));
                    temp.shootPhotoDistanceInterval = Float.parseFloat(ActionsID.get(1));

                    //
                }
                else{
                    if(ActionsID.get(0).equals("Take_photo")){
                        //waypointMissionBuilder.getWaypointList().get(i).addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,0));
                        temp.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,0));
;
                    }
                }
                waypointMissionBuilder.addWaypoint(temp);
            }
        }
        waypointMissionBuilder.waypointCount(waypointMissionBuilder.getWaypointList().size());
        setResultToToast(String.valueOf( waypointMissionBuilder.getWaypointList().size()));
        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            setResultToToast("loadWaypoint succeeded");
        } else {
            setResultToToast("loadWaypoint failed " + error.getDescription());
        }
    }
    // =================================================================================================
    //                                         Map action related
    // =================================================================================================
    //####################################################
    //Creating marker as on the map
    //####################################################
    private DJIMarker markWaypoint(DJIMap map, DJILatLng point,List<DJILatLng> list){
        String name = "WayPoint#" + String.valueOf(list.indexOf(point)+1);
        //Create MarkerOptions object
        DJIMarkerOptions markerOptions = new DJIMarkerOptions().title(name).draggable(true).icon(DJIBitmapDescriptorFactory.fromBitmap(marker_icon_unselected));
        markerOptions.position(point);
        // changeing the icon for marker
        //markerOptions.icon(DJIBitmapDescriptorFactory.defaultMarker());
        DJIMarker marker= map.addMarker(markerOptions);
        return marker;
    }
    //####################################################
    //Colored marker for selected waypoint as on the map
    //####################################################
    private void update_waypoint_markers(int selected_waypoint_index){
        DJIMarker mMarker = null;
        DJIMap map = mapWidget.getMap();
        map.clear();
        markerList.clear();
        for(DJILatLng latLng : djiLatLngList){
            String name = "WayPoint#" + String.valueOf(djiLatLngList.indexOf(latLng)+1);
            DJIMarkerOptions djiMarkerOptions = new DJIMarkerOptions().position(latLng).draggable(true).title(name);
            // Color unselected waypoint blue and selected waypoint red
            if(djiLatLngList.indexOf(latLng) != selected_waypoint_index){
                djiMarkerOptions.icon(DJIBitmapDescriptorFactory.fromBitmap(marker_icon_unselected));
            }
            else{
                djiMarkerOptions.icon(DJIBitmapDescriptorFactory.fromBitmap(marker_icon_selected));
            }
            mMarker = mapWidget.getMap().addMarker(djiMarkerOptions);
            markerList.add(mMarker);
        }
        //Drawing polyline
        DJIPolylineOptions polylineOptions = new DJIPolylineOptions().color(Color.BLUE)
                .width(5)
                .addAll(djiLatLngList);
        map.addPolyline(polylineOptions);

    }
    // =================================================================================================
    //                   Ploting all the parawind waypoints and saving all the data
    // =================================================================================================
    private void add_waypoint_from_csv(DJIMap map, List<Parawind_waypoints> parawindWaypointsList){
        for(Parawind_waypoints PW : parawindWaypointsList)
        {
            //Extracting the coordinate from csv saved list
            List<Double> PW_coordinate = PW.getCoordinates();
            Double PW_latitude         = PW_coordinate.get(1);
            Double PW_longitude        = PW_coordinate.get(0);
            float PW_altitude          = PW_coordinate.get(2).floatValue();

            gimbal_pitch_value         = (float) (PW.getGimbalAngles().get(1).floatValue());
            gimbal_roll_value          = (float) PW.getGimbalAngles().get(0).floatValue();
            gimbal_yaw_value           = (float) PW.getGimbalAngles().get(2).floatValue();
            Aircraft_yaw_value         = (float) PW.getDroneAngles().get(2).floatValue();

            ActionID                   = PW.getActioID();
            ActionParam                = PW.getActioParameter();

            //Creating new Latlng for GoogleMap variable and Waypoint for DJI
            DJILatLng latLng            = new DJILatLng(PW_latitude,PW_longitude,PW_altitude);
            Waypoint mWaypoint          = new Waypoint(PW_latitude, PW_longitude,PW_altitude);
            waypointList.add(mWaypoint);
            waypointList_SubsubID.add(PW.getID()+"|"+PW.getPart() +"|"+ PW.getSubSubID());

            //Draw a marker on the map
            djiLatLngList.add(latLng);
            markerList.add(markWaypoint(map,latLng,djiLatLngList));
            // Draw polyline on the map
            DJIPolylineOptions polylineOptions = new DJIPolylineOptions().color(Color.BLUE).width(5);
            map.addPolyline(polylineOptions.addAll(djiLatLngList));
            //Add Waypoints to Waypoint arraylist;
//            if (waypointMissionBuilder != null) {
//                waypointMissionBuilder.waypointCount(waypointList.size());
//            }else
//            {
//                waypointMissionBuilder = new WaypointMission.Builder();
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }

            ActionItems.add(Arrays.asList(gimbal_pitch_value,gimbal_roll_value,gimbal_yaw_value));
            ActionIDList.add(Arrays.asList(ActionID,ActionParam));
            Aircraft_yawList.add(Aircraft_yaw_value);
        }
        Waypoint_index = waypointList.size() -1;
        update_waypointInfo_panel();

    }
    // Parawind edit------
    //region Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_layout);

        parawind_ui_addition_init();

        widgetHeight = (int) getResources().getDimension(R.dimen.mini_map_height);
        widgetWidth = (int) getResources().getDimension(R.dimen.mini_map_width);
        widgetMargin = (int) getResources().getDimension(R.dimen.mini_map_margin);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        ButterKnife.bind(this);
        markerList = new ArrayList<>();

        // Parawind edit -------------------------------------------------------------------------------------------
        // This here is all the action happen when the user interact on the map
        // such as: Drag the waypoint, click on the waypoint
        djiLatLngList = new ArrayList<>();
        MapWidget.OnMapReadyListener onMapReadyListener = map -> {
            if(MainActivity.ImportSuccess){
                add_waypoint_from_csv(map,parawindWaypointsList);
                cameraUpdate();
            }
            map.setMapType(DJIMap.MapType.NORMAL);

            //Add toasts when a marker is dragged
            map.setOnMarkerDragListener(new DJIMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(DJIMarker djiMarker) {
                    if (markerList.contains(djiMarker) && !isMapMini && isAdd) {
//                        Toast.makeText(DefaultLayoutActivity.this,
//                                getString(R.string.marker_drag_started, markerList.indexOf(djiMarker)),
//                                Toast.LENGTH_SHORT).show();
                        setResultToToast(djiMarker.getTitle() + "drag started");
                        int index = markerList.indexOf(djiMarker);
                        // Draw Marker drawing on the map
                        //update_waypoint_markers(index);
                    }
                }

                @Override
                public void onMarkerDrag(DJIMarker djiMarker) {

                    //do nothing
                }

                @Override
                public void onMarkerDragEnd(DJIMarker djiMarker) {
                    if (markerList.contains(djiMarker) &&  !isMapMini && isAdd) {
                        Toast.makeText(DefaultLayoutActivity.this,
                                getString(R.string.marker_drag_ended, markerList.indexOf(djiMarker)),
                                Toast.LENGTH_SHORT).show();
                        int index = markerList.indexOf(djiMarker);
                        // Update the waypoint position
                        djiLatLngList.get(index).setLatitude(djiMarker.getPosition().latitude);
                        djiLatLngList.get(index).setLongitude(djiMarker.getPosition().longitude);

                        // Draw Marker drawing on the map
                        update_waypoint_markers(index);

                        //Update the data on the info panel
                        Waypoint_index = index;
                        update_waypointInfo_panel();
                    }
                }
            });
            //Add toasts when a marker is clicked
            mapWidget.setOnMarkerClickListener(djiMarker -> {
                if(!isMapMini) {
//                    Toast.makeText(DefaultLayoutActivity.this, getString(R.string.marker_clicked, markerList.indexOf(djiMarker)),
//                            Toast.LENGTH_SHORT).show();
                    try {
                        setResultToToast(djiMarker.getTitle());
                        int index = markerList.indexOf(djiMarker);
                        Waypoint_index = index;
                        update_waypoint_markers(index);
                        update_waypointInfo_panel();
                    }catch (Exception e){
                        setResultToToast("Waypoint does not exist");
                    }
                }
                else{
                    onViewClick(mapWidget);
                }
                return true;
            });
            //Add a marker to the map when the map is clicked
            map.setOnMapClickListener(djiLatLng -> {
                if(!isMapMini) {
                    if(isAdd) {
                        if(getWaypointMissionOperator() == null) {
                            setResultToToast("Not support Waypoint1.0");
                        }
                        assert djiLatLng != null;
                        Waypoint mWaypoint = new Waypoint(djiLatLng.latitude, djiLatLng.longitude, 0);
                        waypointList.add(mWaypoint);
                        waypointList_SubsubID.add("None");
                        // add new djiLatlng to list
                        djiLatLngList.add(djiLatLng);
                        // add marker
                        markWaypoint(map,djiLatLng,djiLatLngList);
                        update_waypoint_markers(djiLatLngList.indexOf(djiLatLng));

                        //Add Waypoints to Mission;

//                        if (waypointMissionBuilder != null) {
//                            waypointMissionBuilder.waypointCount(waypointList.size());
//                        } else {
//                            waypointMissionBuilder = new WaypointMission.Builder();
//                            waypointMissionBuilder.waypointCount(waypointList.size());
//                        }
//                        waypointMissionBuilder.addWaypoint(mWaypoint);
                        Waypoint_index = djiLatLngList.indexOf(djiLatLng);
                        addNewWaypoint();
                        update_waypointInfo_panel();
                    }
                    else{
                        Toast.makeText(DefaultLayoutActivity.this,"Cannot add waypoint",Toast.LENGTH_SHORT).show();
                    }
                }
                onViewClick(mapWidget);
            });

        };

        //####################################################
        //Collecting Parawind waypoints
        //####################################################
        if(MainActivity.ImportSuccess){
            parawindWaypointsList = MainActivity.parawindWaypointsList;
        }

        mapWidget.initGoogleMap(onMapReadyListener);
        // Parawind edit -------------------------------------------------------------------------------------------
        mapWidget.getUserAccountLoginWidget().setVisibility(View.GONE);
        mapWidget.onCreate(savedInstanceState);

        // Setup top bar state callbacks
        TopBarPanelWidget topBarPanel = findViewById(R.id.panel_top_bar);
        SystemStatusWidget systemStatusWidget = topBarPanel.getSystemStatusWidget();
        if (systemStatusWidget != null) {
            systemStatusWidget.setStateChangeCallback(findViewById(R.id.widget_panel_system_status_list));
        }


        SimulatorIndicatorWidget simulatorIndicatorWidget = topBarPanel.getSimulatorIndicatorWidget();
        if (simulatorIndicatorWidget != null) {
            simulatorIndicatorWidget.setStateChangeCallback(findViewById(R.id.widget_simulator_control));
        }

        GPSSignalWidget gpsSignalWidget = topBarPanel.getGPSSignalWidget();
        if (gpsSignalWidget != null) {
            gpsSignalWidget.setStateChangeCallback(findViewById(R.id.widget_rtk));
        }
        mapWidget.getFlyZoneHelper().setFlyZoneVisible(FlyZoneCategory.WARNING, true);
        mapWidget.getFlyZoneHelper().setFlyZoneVisible(FlyZoneCategory.ENHANCED_WARNING, true);
        mapWidget.getFlyZoneHelper().setFlyZoneVisible(FlyZoneCategory.AUTHORIZATION, true);
        mapWidget.getFlyZoneHelper().setFlyZoneVisible(FlyZoneCategory.RESTRICTED, true);

//        Waypoint_tracking tracking = new Waypoint_tracking(waypointList);
//        new Thread(tracking).start();
    }




    @Override
    protected void onDestroy() {
        mapWidget.onDestroy();
        super.onDestroy();
        // parawind edit-------------------------------------------------------
        // remove all the WaypointMissionOperator and waypoints when not used
        removeListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapWidget.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapWidget.onLowMemory();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapWidget.onResume();
        initFlightController();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(secondaryFPVWidget.getCameraName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateSecondaryVideoVisibility));

        compositeDisposable.add(systemStatusListPanelWidget.closeButtonPressed()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pressed -> {
                    if (pressed) {
                        ViewExtensions.hide(systemStatusListPanelWidget);
                        if(!isMapMini){
                            Parawind_layout.setVisibility(View.VISIBLE);
                        }
                    }
                }));

        compositeDisposable.add(rtkWidget.getWidgetStateUpdate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rtkWidgetState -> {
                    if (rtkWidgetState instanceof RTKWidget.RTKWidgetState.VisibilityUpdate) {
                        if (((RTKWidget.RTKWidgetState.VisibilityUpdate) rtkWidgetState).isVisible()) {
                            hideOtherPanels(rtkWidget);
                        }
                    }
                }));
        compositeDisposable.add(simulatorControlWidget.getUIStateUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simulatorControlWidgetState -> {
                    if (simulatorControlWidgetState instanceof SimulatorControlWidget.SimulatorControlWidgetUIUpdate.VisibilityToggled) {
                        if (((SimulatorControlWidget.SimulatorControlWidgetUIUpdate.VisibilityToggled) simulatorControlWidgetState).isVisible()) {
                            hideOtherPanels(simulatorControlWidget);
                        }
                    }
                }));
    }

    @Override
    protected void onPause() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        mapWidget.onPause();
        super.onPause();
    }
    //endregion

    //region Utils

    private void hideOtherPanels(@Nullable View widget) {
        View[] panels = {
                rtkWidget,
                simulatorControlWidget
        };

        for (View panel : panels) {
            if (widget != panel) {
                panel.setVisibility(View.GONE);
            }
        }
    }



    /**
     * Handles a click event on the FPV widget
     */
    @OnClick(R.id.widget_fpv)
    public void onFPVClick() {
        onViewClick(fpvWidget);
    }

    /**
     * Handles a click event on the secondary FPV widget
     */
    @OnClick(R.id.widget_secondary_fpv)
    public void onSecondaryFPVClick() {
        swapVideoSource();
    }

    /**
     * Swaps the FPV and Map Widgets.
     *
     * @param view The thumbnail view that was clicked.
     */
    // Parawind edit -----------------------------------------------------
    private void onViewClick(View view) {
        if (view == fpvWidget && !isMapMini) {
            //reorder widgets
            parentView.removeView(fpvWidget);
            parentView.addView(fpvWidget, 0);
            //resize widgets
            resizeViews(fpvWidget, mapWidget);
            //enable interaction on FPV
            fpvInteractionWidget.setInteractionEnabled(true);
            //disable user login widget on map
            //mapWidget.getUserAccountLoginWidget().setVisibility(View.GONE);

            // Update the camera view on the map when Map is turn into mini version
            cameraUpdate();
            Parawind_layout.setVisibility(View.GONE);
            isMapMini = true;
        } else if (view == mapWidget && isMapMini) {
            //reorder widgets
            parentView.removeView(fpvWidget);
            parentView.addView(fpvWidget, parentView.indexOfChild(mapWidget) + 1);
            //resize widgets
            resizeViews(mapWidget, fpvWidget);
            //disable interaction on FPV
            fpvInteractionWidget.setInteractionEnabled(false);
            //enable user login widget on map
            //mapWidget.getUserAccountLoginWidget().setVisibility(View.VISIBLE);

            // Update open the Parawind layout when Map is turn into large version
            Parawind_layout.setVisibility(View.VISIBLE);
            isMapMini = false;
        }
    }
    private void cameraUpdate() {
        if (waypointList.size() > 0 && mapWidget.getMap() != null) {
            DJILatLng djiLatLng = new DJILatLng(waypointList.get(0).coordinate.getLatitude(), waypointList.get(0).coordinate.getLongitude());
            int zoom_level = 15;
            mapWidget.getMap().moveCamera(DJICameraUpdateFactory.newCameraPosition(
                    new DJICameraPosition(djiLatLng, zoom_level)));
        }
    }
    // Parawind edit -----------------------------------------------------
    /**
     * Helper method to resize the FPV and Map Widgets.
     *
     * @param viewToEnlarge The view that needs to be enlarged to full screen.
     * @param viewToShrink  The view that needs to be shrunk to a thumbnail.
     */
    private void resizeViews(View viewToEnlarge, View viewToShrink) {
        //enlarge first widget
        ResizeAnimation enlargeAnimation = new ResizeAnimation(viewToEnlarge, widgetWidth, widgetHeight, deviceWidth, deviceHeight, 0);
        viewToEnlarge.startAnimation(enlargeAnimation);

        //shrink second widget
        ResizeAnimation shrinkAnimation = new ResizeAnimation(viewToShrink, deviceWidth, deviceHeight, widgetWidth, widgetHeight, widgetMargin);
        viewToShrink.startAnimation(shrinkAnimation);
    }

    /**
     * Swap the video sources of the FPV and secondary FPV widgets.
     */
    private void swapVideoSource() {
        if (secondaryFPVWidget.getVideoSource() == SettingDefinitions.VideoSource.SECONDARY) {
            fpvWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
            secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
        } else {
            fpvWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
            secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
        }
    }

    /**
     * Hide the secondary FPV widget when there is no secondary camera.
     *
     * @param cameraName The name of the secondary camera.
     */
    private void updateSecondaryVideoVisibility(String cameraName) {
        if (cameraName.equals(PhysicalSource.UNKNOWN.name())) {
            secondaryFPVWidget.setVisibility(View.GONE);
        } else {
            secondaryFPVWidget.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void set_start_end_waypoint(int start_waypoint, int end_waypoint) {
        starting_waypoint_index = start_waypoint;
        ending_waypoint_index = end_waypoint;
        String text = "Start Saypoint: " +waypointList_SubsubID.get(start_waypoint) +"\n"+
                "End Waypoint: "+waypointList_SubsubID.get(end_waypoint);
        setResultToToast(text);
    }
    //endregion

    //region classes

    /**
     * Animation to change the size of a view.
     */
    private static class ResizeAnimation extends Animation {

        private static final int DURATION = 300;

        private View view;
        private int toHeight;
        private int fromHeight;
        private int toWidth;
        private int fromWidth;
        private int margin;

        private ResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
            this.toHeight = toHeight;
            this.toWidth = toWidth;
            this.fromHeight = fromHeight;
            this.fromWidth = fromWidth;
            view = v;
            this.margin = margin;
            setDuration(DURATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height = (toHeight - fromHeight) * interpolatedTime + fromHeight;
            float width = (toWidth - fromWidth) * interpolatedTime + fromWidth;
            ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            p.rightMargin = margin;
            p.bottomMargin = margin;
            view.requestLayout();
        }
    }
    //endregion

}
