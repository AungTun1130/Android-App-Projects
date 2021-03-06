package com.dji.ux.beta.sample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class readCSV extends MainActivity {
    public static boolean ImportSuccess = false;
    private FileInputStream inputStream;

    private List<Parawind_waypoints> waypoints_csv = new ArrayList<Parawind_waypoints>();

    public void setInputStream(FileInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<Parawind_waypoints> getWaypoints_csv() {
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
                    Parawind_waypoints sample = new Parawind_waypoints();

                    sample.setID(Integer.parseInt(csv_data[0].trim()));

                    sample.setPart(csv_data[1].trim());

                    sample.setSubID(Integer.parseInt(csv_data[2].trim()));

                    sample.setSubSubID(csv_data[3].trim());

                    List<Double> point = new ArrayList<>();
                    for(String i : csv_data[4].substring(1,csv_data[4].length() -1).split(" ")){
                        if(i.length()>0){
                            point.add(Double.parseDouble(i.trim()));
                        }
                    }
                    sample.setCoordinates(point);

                    sample.setViewPoint(Boolean.parseBoolean(csv_data[5].trim().toLowerCase()));

                    List<Double> Gimbal= new ArrayList<>();
                    for(String i : csv_data[6].substring(1,csv_data[6].length() -1).split(" ")){
                        if(i.length()>0) {
                            Gimbal.add(Double.parseDouble(i.trim()));
                        }
                    }
                    sample.setGimbalAngles(Gimbal);

                    List<Double> Drone= new ArrayList<>();
                    Log.d("Drone", csv_data[7].substring(1,csv_data[7].length() -1));
                    for(String i : csv_data[7].substring(1,csv_data[7].length() -1).split(" ")){
                        if(i.length()>0) {
                            Drone.add(Double.parseDouble(i.trim()));
                        }
                    }
                    sample.setDroneAngles(Drone);

                    sample.setActioID(csv_data[8]);
                    sample.setActioParameter(csv_data[9]);
                    waypoints_csv.add(sample);

                }
                MainActivity.ImportSuccess = true;
            }
            catch (IOException e){
                Log.d("CSV Read ", "Failed");
                MainActivity.ImportSuccess =  false;
            }
        }
        else {
            Log.d("CSV Import ", "Failed");
            MainActivity.ImportSuccess =  false;
        }

    }
}
