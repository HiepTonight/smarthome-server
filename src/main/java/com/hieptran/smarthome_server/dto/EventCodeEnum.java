package com.hieptran.smarthome_server.dto;

public enum EventCodeEnum {
    DEVICE_UPDATE_EVENT("DEVICE_UPDATE_EVENT"),
    SENSOR_DATA_UPDATE_EVENT("SENSOR_DATA_UPDATE_EVENT");

    public final String value;

    EventCodeEnum(String i) {
        value = i;
    }
}
