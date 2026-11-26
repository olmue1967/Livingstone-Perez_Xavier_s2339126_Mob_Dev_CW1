/*  Starter project for Mobile Platform Development - 1st diet 26/27
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required source and displays the
    raw data in a TextField
*/

//
// Name                 Xavier Livingstone-Perez
// Student ID           s2339126
// Programme of Study   Software Development
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.location.Location;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.me.gcu.livingstone_perez_xavier_s2339126.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Fragment latestFragment;
    private Fragment nearbyFragment;
    private Fragment incidentMapFragment;
    private Fragment fr;

    private Button latestButton;
    private Button nearbyButton;
    private Button incidentMapButton;
    private Button refreshDataButton;


    private ArrayList<Situation> allSituations;

    private float userLatitude = 55.604505f;
    private float userLongitude = -2.78722f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    //DOWNLOADING FROM TRAFFIC SCOTLAND
     String clientId = "";
     String clientKey = "";
    final String unplannedEventsURL = "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    final String currentRoadWorksURL = "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadWorks/Content.xml";   //SLP


    public ArrayList<Situation> getAllSituations() {
        return allSituations;
    }

    public float getUserLatitude() {
        return userLatitude;
    }

    public float getUserLongitude() {
        return userLongitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //reading in the hidden api keys

        Bundle metaData = null;
        try {
            metaData = getPackageManager().
                    getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA).metaData;
            clientId = metaData.getString("org.me.gcu.livingstone_perez_xavier_s2339126.API_ID");
            clientKey = metaData.getString("org.me.gcu.livingstone_perez_xavier_s2339126.API_KEY");


        } catch (PackageManager.NameNotFoundException e) { throw new RuntimeException(e); }
        Log.d("My ID: ", clientId); //use the key as needed
        Log.d("My key: ", clientKey); //use the key as needed


        latestButton = (Button)findViewById(R.id.latestbutton);
        nearbyButton = (Button)findViewById(R.id.nearyoubutton);
        incidentMapButton = (Button)findViewById(R.id.mapbutton);
        refreshDataButton = (Button)findViewById(R.id.refreshDataButton);


        latestButton.setOnClickListener(this);
        nearbyButton.setOnClickListener(this);
        incidentMapButton.setOnClickListener(this);
        refreshDataButton.setOnClickListener(this);


        latestFragment = new LatestIncidentsFragment();
        nearbyFragment = new NearbyIncidentsFragment();
        incidentMapFragment = new IncidentMapFragment();

        fr = latestFragment;

        // Set fr as the current Fragment to show
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragholder, fr);
            transaction.commit();
        }

        allSituations = new ArrayList<>();
        fetchUserLocation();
        startProgress();
        //taken from line 50 in complete pull parser code
    }

    private void fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
                if (location != null) {
                    userLatitude = (float) location.getLatitude();
                    userLongitude = (float) location.getLongitude();
                    Log.d("Location", "Fetched User Coarse Location: " + userLatitude + ", " + userLongitude);
                }
            }
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            fetchUserLocation();
        }
    }

    public void onClick(View aview)
    {
        if (aview == latestButton) {
            fr = latestFragment;
            Log.d("MyTag", "Latest Incidents Fragment selected");
            if (allSituations.isEmpty()) {
                startProgress();
            } else if (fr instanceof LatestIncidentsFragment) {
                ((LatestIncidentsFragment) fr).updateData(allSituations);
            }
        } else if (aview == nearbyButton) {
            fr = nearbyFragment;
            Log.d("MyTag", "Nearby Incidents Fragment selected");
            if (allSituations.isEmpty()) {
                startProgress();
            } else if (fr instanceof NearbyIncidentsFragment) {
                String closeResult = "";
                ArrayList<Situation> closeSituations = GetCloseSituations(userLatitude, userLongitude);
                if (closeSituations.isEmpty()) {
                    closeSituations = allSituations;
                }

                ((NearbyIncidentsFragment) fr).updateData(closeSituations);
            }
        } else if (aview == incidentMapButton) {
            fr = incidentMapFragment;
            Log.d("MyTag", "Incident Map Fragment selected");
            if (allSituations.isEmpty()) {
                startProgress();
            } else if (fr instanceof IncidentMapFragment) {
                ((IncidentMapFragment) fr).updateData(allSituations);
            }
            else if (aview == refreshDataButton) {
                Log.d("MyTag", "refreshDataButton selected");
            }
        }

        if (fr != null) {
            // Update fragment view
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragholder, fr);
            transaction.commit();
        }
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(unplannedEventsURL)).start();
    } //


    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String result="";
        private String myurl;
        BufferedReader in = null;
        String inputLine = "";
        public Task(String aurl){
            myurl = aurl;
        }
        @Override
        public void run(){

            // get the UnplannedEvents
            ProcessXML(unplannedEventsURL);

            // add the current roadworks
            ProcessXML(currentRoadWorksURL);


            // Now update the TextView to display raw XML data
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    if (fr instanceof LatestIncidentsFragment) {

                        ((LatestIncidentsFragment) fr).updateData(allSituations);
                    } else if (fr instanceof NearbyIncidentsFragment) {
                        String closeResult = "";
                        ArrayList<Situation> closeSituations = GetCloseSituations(userLatitude, userLongitude);
                        if (closeSituations.isEmpty()) {
                            closeSituations = allSituations;
                        }
                        ((NearbyIncidentsFragment) fr).updateData(closeSituations);
                    } else if (fr instanceof IncidentMapFragment) {
                        ((IncidentMapFragment) fr).updateData(allSituations);
                    }

                    // update events every 90 seconds
                    TimerTask updateEvents = new TimerTask(){

                        public void run() {
                            UpdateEvents();
                        }
                    };

                    Timer eventsTimer = new Timer("EventTimer");    // SLP
                    eventsTimer.schedule(updateEvents, 90000);
                }

                public void UpdateEvents()
                {
                    //updates with existing events
                    // get the UnplannedEvents
                    ProcessXML(unplannedEventsURL);

                    // add the current roadworks
                    ProcessXML(currentRoadWorksURL);
                }

                });
        }

        private void ProcessXML(String myurl)
        {
            try {
                result = "";    //reset
                URL url = new URL(myurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //ADD AUTHORISATION (string with clientIID:clientKey)
                urlConnection.setRequestProperty("Authorization",Base64.getEncoder().encodeToString((clientId + ":" + clientKey).getBytes()));
                try {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((inputLine = in.readLine()) != null){
                        result = result + inputLine;
                    }
                    in.close();
                } catch (IOException e) {
                    String error = urlConnection.getErrorStream().toString();
                    Log.e("urlConnection", "ioexception");
                }
                finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("URL", "ioexception");
            }

            //Clean up any leading garbage characters
            int i = result.indexOf("<?"); //initial tag
            result = result.substring(i);

            // Now that you have the xml data into result, you can parse it
            Situation newSituation = null;
            try
            {
                boolean insideASituation = false;

                XmlPullParserFactory factory =
                        XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader(result) );

                //YOUR PARSING HERE!!!

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("situation"))
                        {
                            insideASituation = true;
                            //Start a new Thing object
                            newSituation = new Situation();
                            Log.d("Situation","New Situation found!");

                            newSituation.setID(xpp.getAttributeValue(null,"id"));
                        }
                        else if (xpp.getName().equalsIgnoreCase("generalPublicComment"))
                        {
                            Log.d("Situation","Public Comment found!11");
                            if (insideASituation)
                            {
                                boolean insideAPublicComment = true;
                                while (insideAPublicComment)
                                {
                                    if (eventType == XmlPullParser.TEXT
                                            && xpp.getText().trim().length() > 0)
                                    {
                                        String value = xpp.getText().trim();
                                        newSituation.setPublicComment(value);

                                        Log.d("Situation","Public Comment found!");
                                        insideAPublicComment = false;
                                    }
                                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("generalPublicComment"))
                                    {
                                        insideAPublicComment = false;
                                    }
                                    eventType = xpp.next(); // Get the next event  before looping again
                                }
                            }

                        }
                        else if (xpp.getName().equalsIgnoreCase("overallStartTime"))
                        {
                            insideASituation = true;
                            Log.d("Situation","Start Time found!");
                            newSituation.setStartTime(xpp.nextText());
                        }
                        else if (xpp.getName().equalsIgnoreCase("otherName"))
                        {
                            Log.d("Situation","Road Name found!");
                            if (insideASituation)
                            {
                                boolean insideOtherName = true;
                                while (insideOtherName)
                                {
                                    if (eventType == XmlPullParser.TEXT
                                            && xpp.getText().trim().length() > 0)
                                    {
                                        String value = xpp.getText().trim();
                                        newSituation.setRoadName(value);
                                        insideOtherName = false;


                                    }
                                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("otherName"))
                                    {
                                        insideOtherName = false;
                                    }
                                    eventType = xpp.next(); // Get the next event  before looping again
                                }
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("latitude"))
                        {
                            insideASituation = true;
                            Log.d("Situation","Latitude found!");
                            newSituation.setLatitude(Float.parseFloat(xpp.nextText()));
                        }
                        else if (xpp.getName().equalsIgnoreCase("longitude"))
                        {
                            insideASituation = true;
                            Log.d("Situation","Longitude found!");
                            newSituation.setLongitude(Float.parseFloat((xpp.nextText())));
                        }
                        else if (xpp.getName().equalsIgnoreCase("abnormalTrafficType"))
                        {
                            insideASituation = true;
                            Log.d("Situation","Traffic Type found!");
                            newSituation.setTrafficType(xpp.nextText());
                        }

                    }


                    else if(eventType == XmlPullParser.END_TAG) // Found an end tag
                    {
                        if (xpp.getName().equalsIgnoreCase("situation"))
                        {
                            boolean alreadyHaveSituation = false;

                            // check if we have this id and add if we don't // SLP
                            for (Situation situation : allSituations) {
                                if (Objects.equals(situation.getID(), newSituation.getID())) {

                                    // say we have it
                                    alreadyHaveSituation = true;

                                    // break out as we have a match
                                    break;
                                }
                            }

                            // only add it if it is new
                            if (!alreadyHaveSituation) {


                                allSituations.add(newSituation); //add to collection
                            }

                            insideASituation = false;
                            Log.d("MyTag","Situation parsing completed!");
                        }
                    }

                    eventType = xpp.next(); // Get the next event  before looping again

                }

            }

            catch (XmlPullParserException e)
            {
                Log.e("Parsing","EXCEPTION" + e);
                //throw new RuntimeException(e);
            }

            catch (IOException ae1)
            {
                Log.e("MyTag","IO error during parsing");
            }
        }

    }


    /**
     * Calculates the distance between two (latitude, longitude) coordinates in miles using Android's Location API.
     */
    private float calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2)
    {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        // Convert meters to miles (1 meter = 0.000621371 miles)
        return results[0] * 0.000621371f;
    }

    protected ArrayList<Situation> GetCloseSituations(float lat, float lon)
    {
        final float maxDistanceInMiles = 10.0f;

        ArrayList<Situation> closeSituations = new ArrayList<>();

        for (Situation i : allSituations)
        {
            float distance = calculateDistanceInMiles(lat, lon, i.getLatitude(), i.getLongitude());
            if (distance <= maxDistanceInMiles)
            {
                closeSituations.add(i);
            }
        }
        return closeSituations;
    }

    protected ArrayList<Situation> SearchByRoad(String roadName)
    {
        ArrayList<Situation> roadSituations = new ArrayList<>();
        for (Situation i : allSituations)
        {
            if (i.getRoadName() != null && i.getRoadName().toLowerCase().contains(roadName.toLowerCase()))
            {
                roadSituations.add(i);
            }
        }
        return roadSituations;
    }

    private Situation GetSituation(String id)
    {
        for (Situation i : allSituations)
        {
            if (i.getID().equals(id))
            {
               return i;
            }
        }
        return null;
    }

}