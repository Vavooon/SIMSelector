package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vavooon on 19.11.2015.
 */
public final class Common {

    // our package name
    public static final String PACKAGE_NAME = Common.class.getPackage().getName().replace(".hooks", "");

    public static Activity LAUNCHER_INSTANCE;
    public static Object WORKSPACE_INSTANCE;
    public static Object HOTSEAT_INSTANCE;
    public static View APP_DRAWER_INSTANCE;
    public static ViewGroup DRAG_LAYER;
}