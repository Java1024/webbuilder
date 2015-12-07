package org.webbuilder.office.excel.config;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class CustomRowStyle {
    private double height = 20;

    public CustomRowStyle() {
    }

    public CustomRowStyle(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
