package org.webbuilder.utils.file.callback;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public abstract class AbstractReadCallBack implements ReadCallBack {

    private boolean exit = false;

    @Override
    public void exit() {
        exit = true;
    }

    @Override
    public boolean isExit() {
        return exit;
    }

    @Override
    public void error(Throwable e) {

    }

    @Override
    public void done(int total) {

    }
}
