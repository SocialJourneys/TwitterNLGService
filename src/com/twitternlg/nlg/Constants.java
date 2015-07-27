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
    public static final String KEY_EVENT_REAL_TIME = "RealTime";
    public static final String KEY_EVENT_REAL_TIME5 = "RealTime5";
    public static final String KEY_EVENT_ALL_OK = "ALL_OK";
    public static final String KEY_EVENT_QUESTIONNAIRE = "Questionnaire";

    public static final String KEY_PROBLEM_REASON = "hasFactor";
    public static final String KEY_BUS_SERVICES = "service";
    public static final String KEY_BUS_SERVICES_DIRECTIONS = "bus-services-directions";
    public static final String KEY_PRIMARY_LOCATION = "primaryLocation";
    public static final String KEY_START_TIME = "startsAtDateTime";
    public static final String KEY_END_TIME = "endsAtDateTime";
    public static final String KEY_DIVERTED_ROADS_PLACES = "place";
    public static final String KEY_DELAY_LENGTH = "delayLength";
    public static final String KEY_START_END_DATETIME = "startEndDateTime";

    public static final String KEY_TIMEOFDAY = "timeOfDay";
    public static final String KEY_TODAYS_TIMEOFDAY = "todaysTimeOfDay";

    public static final String KEY_GREETING = "greeting";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_BUS_SERVICE_TIME = "serviceTime";

    public static final String KEYWORD_START_TIME = "start";
    public static final String KEYWORD_END_TIME = "end";
    public static final String KEYWORD_DIVERSION = "diversion";
    public static final String KEYWORD_DELAY = "delay";

    public static final String TEMPLATES_XML_FILENAME = "templates.xml";
    
    public static final String TEMPLATE_EVENT_TYPE_TAG = "event";
    public static final String TEMPLATE_EVENT_DIVERSION_TAG = "diversion";
    public static final String TEMPLATE_EVENT_DELAY_TAG = "delay";
    public static final String TEMPLATE_EVENT_ALL_OK = "all_ok";
    public static final String TEMPLATE_PROBLEM_REASON_TAG = "problem";
    public static final String TEMPLATE_SERVICE_DIRECTION_TAG = "service-direction";
    public static final String TEMPLATE_PRIMARY_LOCATION_TAG = "primaryLocation";
    public static final String TEMPLATE_START_TIME_TAG = "startsAtDateTime";
    public static final String TEMPLATE_END_TIME_TAG = "endsAtDateTime";
    public static final String TEMPLATE_DIVERTED_ROADS_PLACES_TAG = "divertedRoads";
    public static final String TEMPLATE_DELAY_LENGTH_TAG = "delayLength";
    
    public static final long NLG_MESSAGE_TIME_THRESHOLD = 60l;


}