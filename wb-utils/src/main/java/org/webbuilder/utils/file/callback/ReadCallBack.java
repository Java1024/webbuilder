package org.webbuilder.utils.file.callback;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public interface ReadCallBack extends CanExitCallBack {

    void readLine(int lineNumber, String line);

    void error(Throwable e);

    void done(int total);

}
