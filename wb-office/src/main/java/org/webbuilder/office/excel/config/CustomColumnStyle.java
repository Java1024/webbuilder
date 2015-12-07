package org.webbuilder.office.excel.config;

/**
 * Created by 浩 on 2015-12-07 0007.
 */
public class CustomColumnStyle {
    private int width = 20;

    public CustomColumnStyle() {
    }

    public CustomColumnStyle(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
