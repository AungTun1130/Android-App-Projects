package com.dji.GSDemo.GoogleMap;

import java.util.List;

public class Parawind_waypoints {
    private int ID;
    private String Part;
    private int SubID;
    private List<Double> Coordinates;
    private boolean IsViewPoint;
    private List<Double> GimbalAngles;
    private List<Double> DroneAngles;
    private String ActioID;
    private String ActioParameter;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPart() {
        return Part;
    }

    public void setPart(String part) {
        Part = part;
    }

    public int getSubID() {
        return SubID;
    }

    public void setSubID(int subID) {
        SubID = subID;
    }

    public List<Double> getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        Coordinates = coordinates;
    }

    public boolean isViewPoint() {
        return IsViewPoint;
    }

    public void setViewPoint(boolean viewPoint) {
        IsViewPoint = viewPoint;
    }

    public List<Double> getGimbalAngles() {
        return GimbalAngles;
    }

    public void setGimbalAngles(List<Double> gimbalAngles) {
        GimbalAngles = gimbalAngles;
    }

    public List<Double> getDroneAngles() {
        return DroneAngles;
    }

    public void setDroneAngles(List<Double> droneAngles) {
        DroneAngles = droneAngles;
    }

    public String getActioID() {
        return ActioID;
    }

    public void setActioID(String actioID) {
        ActioID = actioID;
    }

    public String getActioParameter() {
        return ActioParameter;
    }

    public void setActioParameter(String actioParameter) {
        ActioParameter = actioParameter;
    }

    public String getAllParameter(){
        return getID() + "," + getPart()  + "," +getSubID()   + "," + getCoordinates()  + "," + isViewPoint()  + "," +
                getGimbalAngles()  + "," + getDroneAngles()  + "," + getActioID()  + "," + getActioParameter();
    }
}
