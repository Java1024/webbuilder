package org.webbuilder.utils.file.callback;

import java.io.File;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public abstract class AbstractScanCallBack implements ScanCallBack {

    private boolean exit = false;

    @Override
    public void exit() {
        exit=true;
    }

    @Override
    public boolean isExit() {
        return exit;
    }

    @Override
    public void isDir(int deep, File file) {

    }

    @Override
    public void error(int deep, File file, Throwable e) {

    }
}
