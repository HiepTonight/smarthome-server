package com.hieptran.smarthome_server.model;

import java.util.List;

public interface AutoOption {
    Object getGreaterThan();
    Object getLessThan();
    List<DeviceAuto> getHighDevices();
    List<DeviceAuto> getLowDevices();
}