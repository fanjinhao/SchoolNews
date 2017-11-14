package com.fayne.android.schoolnews.bean;

/**
 * Created by fan on 2017/11/14.
 */

public class CommonException extends Exception {
    private static final long serialVersionUID = 1L;
    public CommonException() {
        super();
    }
    public CommonException(String message, Throwable arg) {
        super(message, arg);
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable arg) {
        super(arg);
    }
}
