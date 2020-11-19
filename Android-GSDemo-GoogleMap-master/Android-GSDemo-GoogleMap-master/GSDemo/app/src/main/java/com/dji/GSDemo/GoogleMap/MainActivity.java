package com.dji.GSDemo.GoogleMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static  final int PERMISSION_REQUEST_STORAGE =1000;
    private static  final int READ_REQUEST_CODE =42;


    static List<Parawind_waypoints> parawindWaypointsList = new ArrayList<>();
    static boolean ImportSuccess;
    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_waypoint1:
                startActivity(MainActivity.this, Waypoint1Activity.class);
                break;
            case R.id.btn_waypoint2:
                startActivity(MainActivity.this, Waypoint2Activity.class);
                break;
            case R.id.btn_importKML:
                break;
            case R.id.btn_importCSV:
                performFileSearch();
                break;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_waypoint1).setOnClickListener(clickListener);
        findViewById(R.id.btn_waypoint2).setOnClickListener(clickListener);
        findViewById(R.id.btn_importCSV).setOnClickListener(clickListener);

    }

    public static void startActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }
    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Toast.makeText(this, "Choose a csv file", Toast.LENGTH_SHORT).show();
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
                readCSV file= new readCSV();
                path =System.getenv("EXTERNAL_STORAGE") +"/"+path.substring(path.indexOf(":") + 1);
                FileInputStream inputStream = null;
                try {
                    File f = new File(path);
                    inputStream = new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                file.setInputStream(inputStream);
                parawindWaypointsList = file.getWaypoints_csv();
                ImportSuccess = true;

//                String r = parawindWaypointsList.get(1).getPart();
//                Toast.makeText(this, r, Toast.LENGTH_SHORT).show();


            }

        }

    }


}
