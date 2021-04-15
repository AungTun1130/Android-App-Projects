package com.dji.ux.beta.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;

public class start_end_waypoint_dialog extends AppCompatDialogFragment {
    private Spinner start_waypoints_list;
    private Spinner end_waypoints_list;
    private TextView Last_waypoint_textView;

    private WaypointDialogListener listener;
    private List<String> Waypoint_ID;
    private String latest_waypoint;
    private int start_waypoint_index;
    private int end_waypoint_index;


    start_end_waypoint_dialog(List<String> Waypoint_Labels,int latest_wp,int start_wp,int end_wp){
        Waypoint_ID = Waypoint_Labels;
        latest_waypoint= Waypoint_Labels.get(latest_wp);
        if(start_wp != -1 && end_wp != -1){
            start_waypoint_index = start_wp;
            end_waypoint_index = end_wp;
        }
        else{
            start_waypoint_index = 0;
            end_waypoint_index = Waypoint_ID.size()-1;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.start_end_waypoints_dialog,null);
        builder.setView(view)
                .setTitle("Set start & end waypoint")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.set_start_end_waypoint(start_waypoint_index,end_waypoint_index);
                    }
                });

        Last_waypoint_textView = view.findViewById(R.id.Last_waypoint);
        String LWP_text= "Last Waypoint: " + MainActivity.Latest_waypoint;
        Last_waypoint_textView.setText(LWP_text);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,Waypoint_ID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        start_waypoints_list = view.findViewById(R.id.Starting_waypoint_dialog_spinner);
        end_waypoints_list = view.findViewById(R.id.Ending_waypoint_dialog_spinner);
        start_waypoints_list.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,Waypoint_ID.subList(0,Waypoint_ID.size()));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        end_waypoints_list.setAdapter(adapter1);

        start_waypoints_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                start_waypoint_index = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        end_waypoints_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i<= start_waypoint_index){
                    Toast.makeText(getContext(), "Incorrect End Waypoint input!!",Toast.LENGTH_SHORT).show();

                }
                else{
                    end_waypoint_index = i;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(start_waypoint_index != -1 && end_waypoint_index != -1){
            start_waypoints_list.setSelection(start_waypoint_index);
            end_waypoints_list.setSelection(end_waypoint_index);
        }
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (WaypointDialogListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException((context.toString() + "must implement DialogListener"));
        }
    }


    public interface WaypointDialogListener{
        void set_start_end_waypoint(int start_waypoint, int end_waypoint);
    }
}
