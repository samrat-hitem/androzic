package com.androzic;

import java.util.Calendar;

import com.androzic.data.Route;
import com.androzic.data.Waypoint;
import com.androzic.navigation.NavigationService;
import com.androzic.overlay.RouteOverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Executes intents from external applications.
 * 
 * @author Andrey Novikov
 */
public class ExternalActions extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		String action = intent.getAction();
		Log.e("ANDROZIC","New intent: "+action);
		
		Androzic application = (Androzic) getApplication();

		if (action.equals("com.androzic.PLOT_ROUTE"))
		{
            double[] wptLat = intent.getExtras().getDoubleArray("targetLat");
            double[] wptLon = intent.getExtras().getDoubleArray("targetLon");
            String[] wptNames = intent.getExtras().getStringArray("targetName");
            if (wptLat != null && wptLon != null && wptLat.length == wptLon.length)
            {
            	Route route = new Route("External route", "", true);
            	for (int i=0; i < wptLat.length; i++)
            	{
            		String name = wptNames != null ? wptNames[i] : "RWPT"+i;
            		route.addWaypoint(name, wptLat[i], wptLon[i]);
            	}
            	int rt = application.addRoute(route);
    			RouteOverlay newRoute = new RouteOverlay(this, route);
    			// FIXME no overlay at this point
    			application.routeOverlays.add(newRoute);
    			startService(new Intent(this, NavigationService.class).setAction(NavigationService.NAVIGATE_ROUTE).putExtra("index", rt));
            }
            else
            {
				Toast.makeText(getBaseContext(), "Bad route data", Toast.LENGTH_LONG).show();
            }
		}
		else if (action.equals("com.google.android.radar.SHOW_RADAR"))
		{
	        double lat = intent.getFloatExtra("latitude", 0);
	        double lon = intent.getFloatExtra("longitude", 0);
	        Waypoint waypoint = new Waypoint("", "", lat, lon);
    		waypoint.date = Calendar.getInstance().getTime();
			int wpt = application.addWaypoint(waypoint);
			waypoint.name = "WPT" + wpt;
			startService(new Intent(this, NavigationService.class).setAction(NavigationService.NAVIGATE_WAYPOINT).putExtra("index", wpt));
		}
        startActivity(new Intent(this, MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
		finish();
	}
}
