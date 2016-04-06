package org.webbuilder.utils.common;

import java.util.Date;

/**
 * Created by 浩 on 2015-12-30 0030.
 */
public class TestBean {
    private int id;

    private String name;

    private Date date;

    private boolean valid;

    private boolean isTrue;


    public TestBean() {
    }

    public TestBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setIsTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", valid=" + valid +
                ", isTrue=" + isTrue +
                '}';
    }
}
