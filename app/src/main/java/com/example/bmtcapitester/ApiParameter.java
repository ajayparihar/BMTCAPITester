package com.example.bmtcapitester;

import java.io.Serializable;

public class ApiParameter implements Serializable {
    private String name;
    private String type;
    private String defaultValue;

    public ApiParameter(String name, String type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDefaultValue() { return defaultValue; }
}
