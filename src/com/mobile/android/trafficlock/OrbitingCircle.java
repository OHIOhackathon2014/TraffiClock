package com.mobile.android.trafficlock;

import android.view.View;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/4/14
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrbitingCircle {

    private ImageView img;
    private float angle;
    private View planet;

    public OrbitingCircle(ImageView img, View planet){
        this.img = img;
        this.planet = planet;
    }

    public void setAngle(float angle){
        this.angle = angle;
        int radiusx = planet.getMinimumWidth() + planet.getPaddingLeft() + planet.getPaddingRight() + img.getPaddingLeft() * 2
                         + img.getPaddingRight() * 2 + img.getMinimumWidth();
        int radiusy = planet.getMinimumHeight() + planet.getPaddingBottom() + planet.getPaddingTop() +
                img.getPaddingBottom() + img.getPaddingTop() + img.getMinimumHeight();
        img.setTranslationX((float) (radiusx * Math.cos(angle)));
        img.setTranslationY((float) (radiusx * -Math.sin(angle)));

    }

    public float getAngle(){
        return angle;
    }


}
