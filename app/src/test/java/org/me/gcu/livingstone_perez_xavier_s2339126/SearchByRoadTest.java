package org.me.gcu.livingstone_perez_xavier_s2339126;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test cases for the "Search by Road Name" feature.
 * Tests filtering situations by road name (otherName from raw data).
 */
public class SearchByRoadTest {

    private ArrayList<Situation> testSituations;

    @Before
    public void setUp() {
        testSituations = new ArrayList<>();

        Situation s1 = new Situation();
        s1.setID("1");
        s1.setRoadName("M8");
        testSituations.add(s1);

        Situation s2 = new Situation();
        s2.setID("2");
        s2.setRoadName("A89 Edinburgh Road");
        testSituations.add(s2);

        Situation s3 = new Situation();
        s3.setID("3");
        s3.setRoadName(null); // Test null road name handling
        testSituations.add(s3);
    }

    /**
     * Helper method replicating SearchByRoad filtering logic for unit testing.
     */
    private ArrayList<Situation> searchByRoad(ArrayList<Situation> situations, String roadName) {
        ArrayList<Situation> roadSituations = new ArrayList<>();
        if (roadName == null) {
            return roadSituations;
        }
        for (Situation i : situations) {
            if (i.getRoadName() != null && i.getRoadName().toLowerCase().contains(roadName.toLowerCase())) {
                roadSituations.add(i);
            }
        }
        return roadSituations;
    }

    @Test
    public void testSearchExactMatch() {
        ArrayList<Situation> results = searchByRoad(testSituations, "M8");
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).getID());
        assertEquals("M8", results.get(0).getRoadName());
    }

    @Test
    public void testSearchCaseInsensitive() {
        ArrayList<Situation> results = searchByRoad(testSituations, "m8");
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).getID());
    }

    @Test
    public void testSearchPartialMatch() {
        ArrayList<Situation> results = searchByRoad(testSituations, "Edinburgh");
        assertEquals(1, results.size());
        assertEquals("2", results.get(0).getID());
        assertEquals("A89 Edinburgh Road", results.get(0).getRoadName());
    }

    @Test
    public void testSearchNoMatch() {
        ArrayList<Situation> results = searchByRoad(testSituations, "M74");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchNullQuery() {
        ArrayList<Situation> results = searchByRoad(testSituations, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchNullRoadNameHandling() {
        // Should successfully process situations without throwing NullPointerException when roadName is null
        ArrayList<Situation> results = searchByRoad(testSituations, "M8");
        assertNotNull(results);
    }
}
