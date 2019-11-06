package com.example.heydaf;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

public class Functionality {

    private int functionNumber;
    private int status;
    private String name;
    private String activation;
    private String deactivation;
    private Switch functionalitySwitch;
    private ImageView image;

    public Functionality(int functionNumber, String name, String activation, String deactivation) {
        this.functionNumber = functionNumber;
        this.status = 0;
        this.name = name;
        this.activation = activation;
        this.deactivation = deactivation;
    }

    public Functionality(int functionNumber, String name, String activation, String deactivation, ImageView image) {
        this.functionNumber = functionNumber;
        this.status = 0;
        this.name = name;
        this.activation = activation;
        this.deactivation = deactivation;
        this.image = image;
    }

    void activate() {
        this.status = 1;
        if (functionalitySwitch != null) {
            functionalitySwitch.setChecked(true);
        }
        if (this.image != null) {
            image.setVisibility(View.VISIBLE);
        }
    }

    void deactivate() {
        this.status = 0;
        if (functionalitySwitch != null) {
            functionalitySwitch.setChecked(false);
        }
        if (this.image != null) {
            image.setVisibility(View.INVISIBLE);
        }
    }

    public void setFunctionalitySwitch(Switch functionalitySwitch) {
        this.functionalitySwitch = functionalitySwitch;
    }

    public int getFunctionNumber() {
        return functionNumber;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getActivation() {
        return activation;
    }

    public String getDeactivation() {
        return deactivation;
    }

}
