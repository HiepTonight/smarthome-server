package com.hieptran.smarthome_server.dto;

public enum StatusCodeEnum {
    //EXCEPTION
    EXCEPTION("EXCEPTION"), // Exception
    EXCEPTION0400("EXCEPTION0400"), // Bad request
    EXCEPTION0404("EXCEPTION0404"), // Not found
    EXCEPTION0503("EXCEPTION0503"), // Http message not readable
    EXCEPTION0504("EXCEPTION0504"), // Missing servlet request parameter
    EXCEPTION0505("EXCEPTION0505"), // Access Denied/Not have permission

    //LOGIN
    LOGIN1200("LOGIN1200"), // Login successfully

    LOGIN0201("LOGIN0201"), // Login failed because verify authorization code failed


    //ORDER
    DEVICE1200("DEVICE1200"), // Order created successfully
    DEVICE1300("DEVICE1300"),//Find device success

    DEVICE0200("DEVICE0200"), // Order get/saved failed
    DEVICE0300("DEIVDE0300"), //Cannot find device

    USER1200("USER1200"), // User created successfully
    USER0200("USER0200"), // User get/saved failed

    HOME1200("HOME1200"), // Home created successfully
    HOME0200("HOME0200"), // Home get/saved failed

    SENSOR1200("SENSOR1200"),
    SENSOR0200("SENSOR1200");

    public final String value;

    StatusCodeEnum(String i) {
        value = i;
    }
}
