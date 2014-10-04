package com.mobile.android.trafficlock.datagrabber;

/**
 * Created with IntelliJ IDEA.
 * User: Rniemo
 * Date: 10/4/14
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DataGrabber {

    /**
     *
     * @return The time interval between data updates
     */
    public int getTimeInterval();

    /**
     *
     * @return How much this data factors into the time calculation from 0-1.0, 0 being not at all
     * and 1 being a significant amount. negative numbers indicate no data available
     */
    public double getData();


}
