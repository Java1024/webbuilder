package org.webbuilder.utils.file.callback;

import java.io.File;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public interface ScanCallBack extends CanExitCallBack {
    void isFile(int deep, File file);

    void isDir(int deep, File file);

    void error(int deep, File file, Throwable e);

}
