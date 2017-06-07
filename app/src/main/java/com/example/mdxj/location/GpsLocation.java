package com.example.mdxj.location;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

public class GpsLocation {
	private Context mContext = null;
	private Handler mh = null;
    
	private double latitude = 0;
	private double longitude= 0;
	private double altitude = 0;
	private double accuracy = 0;
	private int satiCount = 0;

	private LocationManager mLm = null;
	private GpsStatus.Listener statusListener;
	private GpsStatus gpsStatus;
	private Location mCurLocation = null;

    public static final int GPS_SUCCESS = 1;
    public static final int GPS_STOP = 2;
    
	public GpsLocation(Context ct) {
		mContext = ct;
	}
	
	public boolean isGpsOpen() {
		LocationManager lm = (LocationManager) mContext.getSystemService(
				Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(
				LocationManager.GPS_PROVIDER);
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	
	public int getSatelliteCount() {
		return satiCount;
	}
	
	public String toString(String format) {
		return String.format(format, latitude, longitude);
	}
	
	public boolean startLoaction() {
		mLm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setCostAllowed(false);
//		criteria.setSpeedRequired(false);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		
//		String provider = mLm.getBestProvider(criteria, true);
//		if (null == provider) {
//			return false;
//		}		
		
		String provider = LocationManager.GPS_PROVIDER;
		mCurLocation = mLm.getLastKnownLocation(provider);
		
		mLm.requestLocationUpdates(provider, 500, 1, locationListener);

		statusListener = new GpsStatus.Listener() 
		{
		    public void onGpsStatusChanged(int event)
		    {
		    	if (mLm == null) {
		    		return;
		    	}
		    	gpsStatus = mLm.getGpsStatus(null);
		    
		    	switch(event)
		    	{
		    	case GpsStatus.GPS_EVENT_FIRST_FIX:
					break;	     
		    	case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					Iterable<GpsSatellite> allSatellites;
					allSatellites = gpsStatus.getSatellites();
					 
					Iterator<GpsSatellite> it = allSatellites.iterator(); 
					satiCount = 0;
					while(it.hasNext())   
					{
						 satiCount++;
						 GpsSatellite gs = it.next();
					}
					break;	     
		    	case GpsStatus.GPS_EVENT_STARTED:
					break;	     
		    	case GpsStatus.GPS_EVENT_STOPPED:
					break;	     
		    	default :
		    	 	break;
		    	}
		    }
		};
		mLm.addGpsStatusListener(statusListener);
		return true;
	}
	
	public void stopLocation(){
		if(mLm != null){
			mLm.removeUpdates(locationListener);
			mLm.removeGpsStatusListener(statusListener);
			mh.sendEmptyMessage(GPS_STOP);
		}
	}

	public boolean isAllowedArea() {
//		boolean isAllowed =  false;
//		
//		double lat = this.getLatitude();
//		double lng = this.getLongitude();
//		
//		if (isDalian(lat, lng) || isDandong(lat, lng) || isTieling(lat, lng)) {
//			isAllowed = true;
//		}
		
		return true;
	}
	
	private boolean isDalian(double lat, double lng) {
		boolean result = false; 
		
		if (lat > 38.64451248 && lat < 40.24204848 && lng > 120.8012353 && lng < 123.5861492) {
			result = true;
		}
		
		return result;
	}
	
	private boolean isDandong(double lat, double lng) {
		boolean result = false; 
		
		if (lat > 39.61271682 && lat < 41.21025138 && lng > 123.3324580 && lng < 125.7771863) {
			result = true;
		}
		
		return result;
	}
	
	private boolean isTieling(double lat, double lng) {
		boolean result = false; 
		
		if (lat > 41.91396858 && lat < 43.57488042 && lng > 123.3151610 && lng < 125.2524910) {
			result = true;
		}
		
		return result;
	}
	
    
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void updateWithNewLocation(Location location) {
		if (location == null) {
		} else {
			if (!isBetterLocation(location, mCurLocation)) {
				return;
			}
			mCurLocation = location;
			
			if (!isBetterAccuracy(location)) {
				return;
			}
						
	        latitude = location.getLatitude();
	        longitude= location.getLongitude();
	        altitude = location.getAltitude();
	        accuracy = location.getAccuracy();
	        
			mh.sendEmptyMessage(GPS_SUCCESS);
		}
	}

	public void setMh(Handler mHandler) {
		this.mh = mHandler;
	}

	private boolean isBetterAccuracy(Location location) {
		return location.getAccuracy() < 50;
	}
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
