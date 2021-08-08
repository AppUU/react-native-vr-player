package com.reactnativevrplayer.listener;


public interface VRModeListener {

    void switchInteractiveMode(int mode);

    void switchDisplayMode(int mode);

    void switchProjectionMode(int mode);

    void setAntiDistortionEnabled(boolean enabled);

}
