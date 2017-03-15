package com.hp.wxcontrol;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

public class GpsMockManager {
	private Context mContext;

    Handler mHandler1;
    public String mMockProviderName = "spoof";
    private int mPostDelayed = 10000;

    GpsFakePointProvider mGpsFakePointProvider;

    public GpsMockManager(String mockProviderName, Context context,
            Handler handler) {
        super();
        mMockProviderName = mockProviderName;
        mContext = context;
        mHandler1 = handler;

        // mGpsFakePointProvider = new ChangedPointProvider();
        mGpsFakePointProvider = new StaticPointProvider();
    }

    public String getmMockProviderName() {
        return mMockProviderName;
    }

    Runnable fakeProc = new Runnable() {

        @Override
        public void run() {
            GpsFakePoint pnt = mGpsFakePointProvider.getGpsFakePoint();
            setLocation(pnt.longitude, pnt.latitude);
            mHandler1.postDelayed(fakeProc, mPostDelayed);
        }
    };

    public void RunFake() {
        mHandler1.postDelayed(fakeProc, mPostDelayed);
    }

    public void StopFake() {
        mHandler1.removeCallbacks(fakeProc);
    }

    private void setLocation(double longitude, double latitude) {
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.getProvider(mMockProviderName) == null) {
            locationManager.addTestProvider(mMockProviderName, false, true,
                    false, false, false, false, false, 0, 5);
            locationManager.setTestProviderEnabled(mMockProviderName, true);
        }
        Location loc = new Location(mMockProviderName);
        loc.setTime(System.currentTimeMillis());
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        locationManager.setTestProviderLocation(mMockProviderName, loc);

        Log.i("gps", String.format("once: x=%s y=%s", longitude, latitude));
    }

    public class GpsFakePoint {

        public GpsFakePoint(double longitude, double latitude) {
            super();
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public double latitude;
        public double longitude;
    }

    public interface GpsFakePointProvider {
        public GpsFakePoint getGpsFakePoint();
    }

    public class ChangedPointProvider implements GpsFakePointProvider {
        private double mLatitude = 1;
        private double mLongitude = 1;

        @Override
        public GpsFakePoint getGpsFakePoint() {
            return new GpsFakePoint(mLatitude++, mLongitude++);
        }

    }

    public class StaticPointProvider implements GpsFakePointProvider {
        public GpsFakePoint getGpsFakePoint() {
            return new GpsFakePoint(116.9152, 39.4435);
            //return new GpsFakePoint(116.8737, 39.4328);//偏左
            //return new GpsFakePoint(116.886,39.4257);
            
        }
    }
}
