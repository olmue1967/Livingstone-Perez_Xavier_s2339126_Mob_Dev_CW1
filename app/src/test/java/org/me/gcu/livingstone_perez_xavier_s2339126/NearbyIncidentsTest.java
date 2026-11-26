package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test cases for checking if incidents/roadworks are nearby to the user
 * based on latitude and longitude coordinates.
 */
public class NearbyIncidentsTest {

    private ArrayList<Situation> testSituations;
    // Sample user coarse coordinates (e.g. Glasgow center: 55.8642, -4.2518)
    private final float userLat = 55.8642f;
    private final float userLon = -4.2518f;

    @Before
    public void setUp() {
        testSituations = new ArrayList<>();

        // Situation 1: Very close to user (approx 1-2 miles away)
        Situation s1 = new Situation();
        s1.setID("1");
        s1.setRoadName("M8 Near Glasgow");
        s1.setLatitude(55.8650f);
        s1.setLongitude(-4.2400f);
        testSituations.add(s1);

        // Situation 2: Far away from user (e.g., in Edinburgh, approx 40+ miles away)
        Situation s2 = new Situation();
        s2.setID("2");
        s2.setRoadName("Edinburgh City Bypass");
        s2.setLatitude(55.9533f);
        s2.setLongitude(-3.1883f);
        testSituations.add(s2);
    }

    /**
     * Helper method replicating distance calculation logic using Haversine / Location formula.
     */
    private float calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        try {
            Location.distanceBetween(lat1, lon1, lat2, lon2, results);
            return results[0] * 0.000621371f;
        } catch (Throwable t) {
            // Fallback approximation for unit tests if Android Location class is not mocked
            double earthRadius = 3958.8;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                       Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return (float) (earthRadius * c);
        }
    }

    /**
     * Helper method replicating GetCloseSituations filtering logic.
     */
    private ArrayList<Situation> getCloseSituations(ArrayList<Situation> situations, float lat, float lon) {
        final float maxDistanceInMiles = 5.0f;
        ArrayList<Situation> closeSituations = new ArrayList<>();

        for (Situation i : situations) {
            float distance = calculateDistanceInMiles(lat, lon, i.getLatitude(), i.getLongitude());
            if (distance <= maxDistanceInMiles) {
                closeSituations.add(i);
            }
        }
        return closeSituations;
    }

    @Test
    public void testIncidentWithinRangeIsNearby() {
        ArrayList<Situation> nearby = getCloseSituations(testSituations, userLat, userLon);
        assertEquals(1, nearby.size());
        assertEquals("1", nearby.get(0).getID());
        assertEquals("M8 Near Glasgow", nearby.get(0).getRoadName());
    }

    @Test
    public void testIncidentOutsideRangeIsFilteredOut() {
        ArrayList<Situation> nearby = getCloseSituations(testSituations, userLat, userLon);
        // Situation 2 (Edinburgh) should be filtered out because it exceeds 5 miles
        for (Situation s : nearby) {
            assertNotEquals("2", s.getID());
        }
    }

    @Test
    public void testDistanceCalculationAccuracy() {
        // Distance between same point should be 0
        float distSame = calculateDistanceInMiles(userLat, userLon, userLat, userLon);
        assertEquals(0.0f, distSame, 0.01f);

        // Distance between Glasgow and Edinburgh should be greater than 35 miles
        float distGlasgowEdinburgh = calculateDistanceInMiles(55.8642, -4.2518, 55.9533, -3.1883);
        assertTrue(distGlasgowEdinburgh > 35.0f);
    }
}
