package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.util.Log;

public class Situation {
    private String ID;
    private String startTime;
    private String publicComment;
    private float latitude;
    private float longitude;
    private String roadName;
    private String trafficType;

    //getter, setter and toString methods

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        try {
            Log.d("Situation", String.format("Start Time: %s ", this.startTime));
        } catch (Throwable ignored) {}
    }

    public String getPublicComment() {
        return publicComment;
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
        try {
            Log.d("Situation", String.format("Public Comment: %s ", this.publicComment));
        } catch (Throwable ignored) {}
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
        try {
            Log.d("Situation", String.format("Road Name: %s ", this.roadName));
        } catch (Throwable ignored) {}
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    @Override
    public String toString() {
        return
                "Situation" + "\n"
                        + "ID='" + ID + '\''
                        + ", startTime='" + startTime + '\''
                        + ", publicComment='" + publicComment + '\''
                        + ", latitude=" + latitude + '\''
                        + ", longitude=" + longitude + '\''
                        + ", roadName='" + roadName + '\''
                        + ", trafficType='" + trafficType + '\''
                        + '}';
    }
}
