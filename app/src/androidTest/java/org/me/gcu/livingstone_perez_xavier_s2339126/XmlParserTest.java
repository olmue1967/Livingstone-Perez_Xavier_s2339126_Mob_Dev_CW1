package org.me.gcu.livingstone_perez_xavier_s2339126;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Instrumented test for the XML parsing function which generates a list of situations.
 */
@RunWith(AndroidJUnit4.class)
public class XmlParserTest {

    private ArrayList<Situation> parseXml(String xmlData) {
        ArrayList<Situation> situations = new ArrayList<>();
        Situation newSituation = null;
        try {
            boolean insideASituation = false;

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("situation")) {
                        insideASituation = true;
                        newSituation = new Situation();
                        newSituation.setID(xpp.getAttributeValue(null, "id"));
                    } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                        if (insideASituation) {
                            boolean insideAPublicComment = true;
                            while (insideAPublicComment) {
                                if (eventType == XmlPullParser.TEXT && xpp.getText().trim().length() > 0) {
                                    newSituation.setPublicComment(xpp.getText().trim());
                                    insideAPublicComment = false;
                                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                                    insideAPublicComment = false;
                                }
                                eventType = xpp.next();
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("overallStartTime")) {
                        insideASituation = true;
                        newSituation.setStartTime(xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("otherName")) {
                        if (insideASituation) {
                            boolean insideOtherName = true;
                            while (insideOtherName) {
                                if (eventType == XmlPullParser.TEXT && xpp.getText().trim().length() > 0) {
                                    newSituation.setRoadName(xpp.getText().trim());
                                    insideOtherName = false;
                                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("otherName")) {
                                    insideOtherName = false;
                                }
                                eventType = xpp.next();
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("latitude")) {
                        insideASituation = true;
                        newSituation.setLatitude(Float.parseFloat(xpp.nextText()));
                    } else if (xpp.getName().equalsIgnoreCase("longitude")) {
                        insideASituation = true;
                        newSituation.setLongitude(Float.parseFloat(xpp.nextText()));
                    } else if (xpp.getName().equalsIgnoreCase("abnormalTrafficType")) {
                        insideASituation = true;
                        newSituation.setTrafficType(xpp.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("situation")) {
                        boolean alreadyHaveSituation = false;
                        for (Situation situation : situations) {
                            if (Objects.equals(situation.getID(), newSituation.getID())) {
                                alreadyHaveSituation = true;
                                break;
                            }
                        }
                        if (!alreadyHaveSituation) {
                            situations.add(newSituation);
                        }
                        insideASituation = false;
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return situations;
    }

    @Test
    public void testParseValidXml() {
        String sampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<payload>\n" +
                "    <situation id=\"S9999\">\n" +
                "        <overallStartTime>2023-11-01T12:00:00Z</overallStartTime>\n" +
                "        <generalPublicComment>Road maintenance work</generalPublicComment>\n" +
                "        <otherName>M8</otherName>\n" +
                "        <latitude>55.8642</latitude>\n" +
                "        <longitude>-4.2518</longitude>\n" +
                "        <abnormalTrafficType>roadworks</abnormalTrafficType>\n" +
                "    </situation>\n" +
                "</payload>";

        ArrayList<Situation> result = parseXml(sampleXml);

        assertEquals(1, result.size());
        Situation s = result.get(0);
        assertEquals("S9999", s.getID());
        assertEquals("2023-11-01T12:00:00Z", s.getStartTime());
        assertEquals("Road maintenance work", s.getPublicComment());
        assertEquals("M8", s.getRoadName());
        assertEquals(55.8642f, s.getLatitude(), 0.0001f);
        assertEquals(-4.2518f, s.getLongitude(), 0.0001f);
        assertEquals("roadworks", s.getTrafficType());
    }

    @Test
    public void testParseDuplicateSituationsIgnored() {
        String sampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<payload>\n" +
                "    <situation id=\"S1111\">\n" +
                "        <overallStartTime>2023-11-01T12:00:00Z</overallStartTime>\n" +
                "        <otherName>M8</otherName>\n" +
                "    </situation>\n" +
                "    <situation id=\"S1111\">\n" +
                "        <overallStartTime>2023-11-01T12:00:00Z</overallStartTime>\n" +
                "        <otherName>M8 Duplicate</otherName>\n" +
                "    </situation>\n" +
                "</payload>";

        ArrayList<Situation> result = parseXml(sampleXml);

        assertEquals(1, result.size());
        assertEquals("S1111", result.get(0).getID());
        assertEquals("M8", result.get(0).getRoadName());
    }
}
