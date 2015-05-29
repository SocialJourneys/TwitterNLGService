/*
 * Constants file for TwitterNLG project
 */

package com.twitternlg.nlg;

public final class Constants {

    private Constants() {
            // restrict instantiation
    }

  
    public static final String KEY_EVENT_TYPE = "type";
    public static final String KEY_EVENT_DIVERSION = "PublicTransportDiversion";
    public static final String KEY_EVENT_DELAY = "PublicTransportDelay";
    
    public static final String KEY_PROBLEM_REASON = "hasFactor";
    public static final String KEY_BUS_SERVICES = "service";
    public static final String KEY_PRIMARY_LOCATION = "primaryLocation";
    public static final String KEY_START_TIME = "startsAtTime";
    public static final String KEY_END_TIME = "endsAtTime";
    public static final String KEY_DIVERTED_ROADS_PLACES = "place";
    public static final String KEY_DELAY_LENGTH = "delayLength";

    public static final String KEYWORD_START_TIME = "start";
    public static final String KEYWORD_END_TIME = "end";
    public static final String KEYWORD_DIVERSION = "diversion";
    public static final String KEYWORD_DELAY = "delay";

    public static final String TEMPLATES_XML_FILENAME = "templates.xml";
    
    public static final String TEMPLATE_EVENT_TYPE_TAG = "event";
    public static final String TEMPLATE_EVENT_DIVERSION_TAG = "diversion";
    public static final String TEMPLATE_EVENT_DELAY_TAG = "delay";
    public static final String TEMPLATE_PROBLEM_REASON_TAG = "problem";
    public static final String TEMPLATE_BUS_SERVICES_TAG = "busServices";
    public static final String TEMPLATE_PRIMARY_LOCATION_TAG = "primaryLocation";
    public static final String TEMPLATE_START_TIME_TAG = "startsAtTime";
    public static final String TEMPLATE_END_TIME_TAG = "endsAtTime";
    public static final String TEMPLATE_DIVERTED_ROADS_PLACES_TAG = "divertedRoads";
    public static final String TEMPLATE_DELAY_LENGTH_TAG = "delayLength";

}