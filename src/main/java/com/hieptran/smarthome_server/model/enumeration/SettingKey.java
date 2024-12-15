package com.hieptran.smarthome_server.model.enumeration;

public enum SettingKey {
    HIGHTEMP("highTemp"),
    LOWTEMP("lowTemp"),
    HIGHTEMPDEVICES("highTempDevices"),
    LOWTEMPDEVICES("lowTempDevices"),
    HIGHHUMI("highHumi"),
    LOWHUMI("lowHumi"),
    HIGHHUMIDEVICES("highHumiDevices"),
    LOWHUMIDEVICES("lowHumiDevices"),
    HIGHLIGHT("highLight"),
    LOWLIGHT("lowLight"),
    HIGHLIGHTDEVICES("highLightDevices"),
    LOWLIGHTDEVICES("lowLightDevices");;

    private final String name;

    SettingKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
