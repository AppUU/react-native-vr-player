package com.reactnativevrplayer.utils;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;

public class CustomDirectorFactory extends MD360DirectorFactory {
    @Override
    public MD360Director createDirector(int i) {
        MD360Director.Builder builder = new MD360Director.Builder();
        builder.setPitch(-90).build();
        MD360Director director = new MD360Director(builder){
            @Override
            public void setDeltaX(float mDeltaX) {
                super.setDeltaX(mDeltaX);
            }

            @Override
            public void setDeltaY(float mDeltaY) {
//                super.setDeltaY(mDeltaY);
            }

            @Override
            public void setNearScale(float scale) {
                super.setNearScale(scale);
            }

            @Override
            public void setViewport(int width, int height) {
                super.setViewport(width, height);
            }
        };

        return director;
    }
}
