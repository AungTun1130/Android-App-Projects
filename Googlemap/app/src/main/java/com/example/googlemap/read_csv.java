package com.example.googlemap;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class read_csv extends MapsActivity{



    private InputStream inputStream;

    private List<Parawind_waypoint> waypoints_csv = new ArrayList<Parawind_waypoint>();

//    public void setInputStream(FileInputStream inputStream) {
//        this.inputStream = inputStream;
//    }
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<Parawind_waypoint> getWaypoints_csv() {
        readCSVData();
        return waypoints_csv;
    }


    private void readCSVData(){
        if(inputStream != null){

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            String line;

            try{
                //Skipping the first line
                bufferedReader.readLine();
                //Start reading next line
                while ((line = bufferedReader.readLine())!= null){
                    String[] csv_data = line.split(",");
                    Log.e("Tag",csv_data[0]);
                    Parawind_waypoint sample = new Parawind_waypoint();

                    sample.setID(Integer.parseInt(csv_data[0].trim()));

                    sample.setPart(csv_data[1].trim());

                    sample.setSubID(Integer.parseInt(csv_data[2].trim()));

                    List<Double> point = new ArrayList<>();
                    for(String i : csv_data[3].substring(2,csv_data[3].length() -1).split(" ")){
                        if(i.length()>0){
                            point.add(Double.parseDouble(i.trim()));
                        }

                    }
                    sample.setCoordinates(point);

                    sample.setViewPoint(Boolean.parseBoolean(csv_data[4].trim().toLowerCase()));

                    List<Double> Gimbal= new ArrayList<>();
                    for(String i : csv_data[5].substring(2,csv_data[5].length() -1).split(" ")){
                        if(i.length()>1) {
                            Gimbal.add(Double.parseDouble(i.trim()));
                        }
                    }
                    sample.setGimbalAngles(Gimbal);

                    List<Double> Drone= new ArrayList<>();
                    Log.d("Drone", csv_data[6].substring(1,csv_data[6].length() -1));
                    for(String i : csv_data[6].substring(1,csv_data[6].length() -1).split(" ")){
                        if(i.length()>0) {
                            Drone.add(Double.parseDouble(i.trim()));
                        }
                    }
                    sample.setDroneAngles(Drone);

                    sample.setActioID(csv_data[7]);
                    sample.setActioParameter(csv_data[8]);
                    waypoints_csv.add(sample);

                }
            }
            catch (IOException e){
                Toast.makeText(this,"Read csv error",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"File does not exist",Toast.LENGTH_SHORT).show();
        }

    }
}
