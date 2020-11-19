package com.example.googlemap;

import java.util.List;

public class Parawind_waypoint {
    private int ID;
    private String Part;
    private int SubID;
    private List<Double> Coordinates;
    private boolean IsViewPoint;
    private List<Double> GimbalAngles;

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
}
