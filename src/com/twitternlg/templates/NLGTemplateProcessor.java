package com.twitternlg.templates;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import static com.twitternlg.nlg.Constants.KEY_EVENT_TYPE;
import static com.twitternlg.nlg.Constants.KEY_EVENT_DIVERSION;
import static com.twitternlg.nlg.Constants.KEY_EVENT_DELAY;
import static com.twitternlg.nlg.Constants.KEY_EVENT_UPCOMING;
import static com.twitternlg.nlg.Constants.KEY_PROBLEM_REASON;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES_DIRECTIONS;
import static com.twitternlg.nlg.Constants.KEY_PRIMARY_LOCATION;
import static com.twitternlg.nlg.Constants.KEY_START_TIME;
import static com.twitternlg.nlg.Constants.KEY_END_TIME;
import static com.twitternlg.nlg.Constants.KEY_DELAY_LENGTH;
import static com.twitternlg.nlg.Constants.KEY_DURATION;
import static com.twitternlg.nlg.Constants.KEY_DIVERTED_ROADS_PLACES;
import static com.twitternlg.nlg.Constants.KEYWORD_DIVERSION;
import static com.twitternlg.nlg.Constants.KEYWORD_DELAY;
import static com.twitternlg.nlg.Constants.TEMPLATE_EVENT_DIVERSION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_EVENT_DELAY_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_PROBLEM_REASON_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_SERVICE_DIRECTION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_PRIMARY_LOCATION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_START_TIME_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_END_TIME_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_DIVERTED_ROADS_PLACES_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_DELAY_LENGTH_TAG;


public class NLGTemplateProcessor {
	// core variables
	private Lexicon lexicon = null;
	private NLGFactory nlgFactory = null;
	private Realiser realiser = null;

	public NLGTemplateProcessor() {

		// initialize core variables
		lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);

		// Map <String,Object> RDFdata = createRDFData();

		// SPhraseSpec tweet = generateTweet((Map
		// <String,Object>)RDFdata.get("diversion_tweet_1"));
		// SPhraseSpec tweet = generateTweet( (Map
		// <String,Object>)RDFdata.get("delay_tweet"));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		// HTTPManager.getInstance().sendPost();

	}

	public String generateTweetString(Map<String, Object> RDFdata) {
		// Map <String,Object> RDFdata = createRDFData();
		// SPhraseSpec tweet = generateTweet((Map
		// <String,Object>)RDFdata.get("diversion_tweet_1"));

		SPhraseSpec tweet = generateTweet(RDFdata);
		String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return output;
	}

	/*
	 * Decide which tweet to generate depending on type of event
	 */
	private SPhraseSpec generateTweet(Map<String, Object> RDFdata) {
		SPhraseSpec tweet = null;

		switch (RDFdata.get(KEY_EVENT_TYPE).toString()) {
		case KEY_EVENT_DIVERSION:
			tweet = generateDiversionTweetTemplate1(RDFdata);
			break;
		case KEY_EVENT_DELAY:
			tweet = generateDelayTweetTemplate1(RDFdata);
			break;
		case "accident":
			// tweet = generateDelayTweet(RDFdata);
			break;
		case "greeting":
			// tweet = generateDelayTweet(RDFdata);
			break;
		default:
			break;
		}

		return tweet;

	}

	public ArrayList<String> generateTweets(Map<String, Object> RDFdata, ServletContext context) {
		ArrayList<String> tweets = new ArrayList<String>();

		switch (RDFdata.get(KEY_EVENT_TYPE).toString()) {
		case KEY_EVENT_DIVERSION:
			tweets.add(/*
					 * "<strong>T4:</strong>  delays (of | of up to) < number > mins (to | on) < route > [and [< number > mins on] < route >] < time-interval ><br/>"
					 * + "<strong>Message:</strong> "+
					 */
		//realiser.realiseSentence(generateDiversionTweetTemplate44(RDFdata))
		realiser.realiseSentence(testXPath(context,RDFdata))
		/* + "<br/><br/>" */);
			break;
		case KEY_EVENT_DELAY:
			tweets.add(/*
						 * "<strong>T4:</strong>  delays (of | of up to) < number > mins (to | on) < route > [and [< number > mins on] < route >] < time-interval ><br/>"
						 * + "<strong>Message:</strong> "+
						 */
			realiser.realiseSentence(generateDelayTweetTemplate44(RDFdata))
			/* + "<br/><br/>" */);

			break;
		case KEY_EVENT_UPCOMING:
			tweets.add(/*
						 * "<strong>T4:</strong>  delays (of | of up to) < number > mins (to | on) < route > [and [< number > mins on] < route >] < time-interval ><br/>"
						 * + "<strong>Message:</strong> "+
						 */
					realiser.realiseSentence(testXPath(context,RDFdata))
			/* + "<br/><br/>" */);

			break;
		case "accident":
			// tweet = generateDelayTweet(RDFdata);
			break;
		default:
			break;
		}

		return tweets;

	}
	
	private SPhraseSpec generateDiversionTweet2(Map<String, Object> RDFdata) {

		/*
		 * Assumption: Every tweet will have two parts - info about the bus and
		 * place, info about the problem and date
		 * 
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();
		// add bus info
		ArrayList<String> bus_services = (ArrayList<String>) RDFdata
				.get("service");
		CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
		for (String bus : bus_services) {
			NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
			buses.addCoordinate(bus_obj);
		}

		tweet.setSubject(buses);

		tweet.setObject(RDFdata.get(KEYWORD_DIVERSION).toString());
		// p.setVerb("effect"); //minimizing the characters by dropping out the
		// verb

		// add the location info
		NPPhraseSpec place = nlgFactory.createNounPhrase(RDFdata.get(
				"place").toString());
		PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
		pp.addComplement(place);
		pp.setPreposition("at");

		tweet.addComplement(pp);

		/*
		 * 
		 * create the second part - problem and date
		 */

		// add the problem info
		// NPPhraseSpec q = nlgFactory.createNounPhrase(RDFdata.get("reason"));
		SPhraseSpec q = nlgFactory.createClause();
		q.setObject(RDFdata.get("reason").toString());
		q.setFeature(Feature.COMPLEMENTISER, "due to");
		q.setFeature(Feature.TENSE, Tense.PRESENT);

		tweet.addComplement(q);

		// add the start end date info

		SPhraseSpec date = createDatePhrase(RDFdata);

		tweet.addComplement(date);

		String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	private SPhraseSpec createDateTimeIntervalPhrase(Map<String, Object> RDFdata) {
		Tense tense = (Tense) determineClauseTense(RDFdata);

		SPhraseSpec date_phrase = null;

		String start_date = createDateString(RDFdata, "start");
		String end_date = createDateString(RDFdata, "end");

		if (start_date.length() > 0 || end_date.length() > 0) {

			date_phrase = nlgFactory.createClause();

			String full_date = start_date;

			if (start_date.length() > 0 && end_date.length() > 0)
				full_date = full_date + " to " + end_date;
			else if (start_date.length() <= 0 && end_date.length() > 0)
				full_date = end_date;

			date_phrase.setObject(full_date);

			switch (tense) {
			case PAST: {
				date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				break;
			}
			case PRESENT: {
				if (end_date.length() > 0 && start_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				else if (end_date.length() > 0 && start_date.length() < 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "until");
				else if (start_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "since");
				break;
			}
			case FUTURE: {
				if (start_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				else
					date_phrase.setFeature(Feature.COMPLEMENTISER, "until");

				break;
			}
			default:
				break;

			}
		}
		// date_phrase.setObject(tense.toString());

		return date_phrase;
	}

	private SPhraseSpec createDatePhrase(Map<String, Object> RDFdata) {
		Tense tense = (Tense) determineClauseTense(RDFdata);

		SPhraseSpec date_phrase = null;

		String start_date = createDateString(RDFdata, "start");
		String end_date = createDateString(RDFdata, "end");

		if (start_date.length() > 0 || end_date.length() > 0) {

			date_phrase = nlgFactory.createClause();

			String full_date = start_date;

			if (start_date.length() > 0 && end_date.length() > 0)
				full_date = full_date + " to " + end_date;
			else if (start_date.length() <= 0 && end_date.length() > 0)
				full_date = end_date;

			date_phrase.setObject(full_date);

			switch (tense) {
			case PAST: {
				if (start_date.length() <= 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "until");
				else
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				break;
			}
			case PRESENT: {
				if (end_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				else if (start_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "since");
				break;
			}
			case FUTURE: {
				if (start_date.length() > 0)
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
				else
					date_phrase.setFeature(Feature.COMPLEMENTISER, "until");

				break;
			}
			default:
				break;

			}
		}

		// date_phrase.setObject(tense.toString());

		return date_phrase;
	}

	/*
	 * Determines past,present or future tense based on given date/time in the
	 * annotation
	 */
	private Object determineClauseTense(Map<String, Object> RDFdata) {
		Date current_date = new Date();
		Date start_date = new Date();
		Date end_date = new Date();

		ArrayList<Object> arr_start = createDateFormat(RDFdata, "start");
		ArrayList<Object> arr_end = createDateFormat(RDFdata, "end");

		if (arr_start.size() > 0)
			start_date = (Date) arr_start.get(0);
		if (arr_end.size() > 0)
			end_date = (Date) arr_end.get(0);

		long start_diff = start_date.getTime() - current_date.getTime();
		long end_diff = end_date.getTime() - current_date.getTime();

		if (start_diff > 0) {
			// System.out.println("future");
			return Tense.FUTURE;
		}
		 else if (start_diff <=0 && end_diff>0) {
				// System.out.println("past");
			 return Tense.FUTURE;
		 }
		 else if (start_diff <=0) {
			// System.out.println("past");
			return Tense.PAST;
		} else if (end_diff <=0){
			
			return Tense.PAST;
				}
		else{
			// System.out.println("present");
			return Tense.PRESENT;
		}

		// if end date is past
		// return Tense.PAST
	}

	/*
	 * Determines past,present or future tense based on given date/time in the
	 * annotation
	 */
	private Map<String, String> determineTodayTomorrowWeekend(
			Map<String, Object> RDFdata, String start_end_date) {
		Date current_date = new Date();
		Date start_date = new Date();

		ArrayList<Object> arr_start = createDateFormat(RDFdata, start_end_date);

		Map<String, String> results = new HashMap<String, String>();

		if (arr_start.size() > 0)
			start_date = (Date) arr_start.get(0);

		// check if today
		// check if morning,evening,night
		// check if tomorrow
		// check if morning,evening,night
		// check if weekend
		// check if morning,evening,night

		long seconds_difference = start_date.getTime() - current_date.getTime();
		int days_difference = start_date.getDate() - current_date.getDate();
		// days_difference = days_difference/(1000*60*60*24);
		long minutes_difference = seconds_difference / (60 * 1000);
		int hour = start_date.getHours();

		String phrase = "";

		if (days_difference == 1 || days_difference == 0) {
			if (days_difference == 1)
				phrase = "tomorrow";
			else if (days_difference == 0)
				phrase = "today";

			if (hour >= 6 && hour <= 11) {
				if (phrase == "today")
					phrase = "this morning";
				else
					phrase = phrase + " " + "morning";
			} else if (hour >= 12 && hour <= 15) {
				if (phrase == "today")
					phrase = "this afternoon";
				else
					phrase = phrase + " " + "afternoon";
			} else if (hour >= 17 && hour <= 19) {
				if (phrase == "today")
					phrase = "this evening";
				else
					phrase = phrase + " " + "evening";
			}

			else if (hour >= 21) {
				if (phrase == "today")
					phrase = "tonight";
				else
					phrase = phrase + " " + "night";
			}

		} else {
			if (days_difference > 0 && days_difference <= 8) {
				if (start_date.getDay() == 6 || start_date.getDay() == 0)
					phrase = "this weekend";
				else
					phrase = createDateString(RDFdata, start_end_date);

			} else
				phrase = createDateString(RDFdata, start_end_date);
		}

		results.put("minutes", String.valueOf(minutes_difference));
		results.put("days", String.valueOf(days_difference));
		results.put("hours", String.valueOf(hour));
		results.put("phrase", phrase);

		return results;
	}

	private String createDateString(Map<String, Object> RDFdata,
			String start_end) {
		ArrayList<Object> arr = createDateFormat(RDFdata, start_end);
		DateFormat format = null;
		String date = "";
		SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd MMMM");
		if (arr.size() > 0) {
			format = new SimpleDateFormat((String) arr.get(1));
			date = outputFormat.format((Date) arr.get(0));
		}

		return date;
	}

	/*
	 * creates date formatter for the input date : e.g mm, dd, yyyy or just dd,
	 * yyyy
	 */
	private ArrayList<Object> createDateFormat(Map<String, Object> RDFdata,
			String start_end) {
		String formatter_pattern = "";
		String date_str = "";
		System.out.println(start_end);
		if (RDFdata.get(start_end + "sAtDateTime") != null){
			formatter_pattern = "yyyy-MM-dd'T'HH:mm:ss";
			date_str += RDFdata.get(start_end + "sAtDateTime"); 
//			date_str = date_str + RDFdata.get(start_end + "-day") + " ";
		}
		if (RDFdata.get(start_end + "-day") != null) {
			formatter_pattern = formatter_pattern + "E" + " ";
			date_str = date_str + RDFdata.get(start_end + "-day") + " ";
		}
		if (RDFdata.get(start_end + "-time") != null) {
			formatter_pattern = formatter_pattern + "HH:mm" + " ";
			date_str = date_str + RDFdata.get(start_end + "-time") + " ";
		}
		if (RDFdata.get(start_end + "-date") != null) {
			formatter_pattern = formatter_pattern + "d" + "/";
			date_str = date_str + RDFdata.get(start_end + "-date") + "/";
		}
		if (RDFdata.get(start_end + "-month") != null) {
			formatter_pattern = formatter_pattern + "MM" + "/";
			date_str = date_str + RDFdata.get(start_end + "-month") + "/";
		}
		if (RDFdata.get(start_end + "-year") != null) {
			formatter_pattern = formatter_pattern + "yyyy" + " ";
			date_str = date_str + RDFdata.get(start_end + "-year") + " ";
		}

		date_str = date_str.replaceAll("\\s+$", "");
		formatter_pattern = formatter_pattern.replaceAll("\\s+$", "");

		ArrayList<Object> arr = new ArrayList<Object>();

		if (date_str.length() > 0 && formatter_pattern.length() > 0)
			try {
				DateFormat format = new SimpleDateFormat(formatter_pattern);
				Date date = format.parse(date_str);
				arr.add(date);
				arr.add(formatter_pattern);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		System.out.println(arr);
		return arr;
	}

	/*
	 * Generate bus services phrase
	 */
	private CoordinatedPhraseElement generateBusServicesPhrase(
			String buses_string) {
		
		List<String> buses_list = new ArrayList<String>();
		if (buses_string!=null)
				buses_list.addAll(Arrays.asList(buses_string.split(",")));

		CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
		for (String bus : buses_list) {
			NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
			buses.addCoordinate(bus_obj);
		}

		return buses;
	}

	private CoordinatedPhraseElement generateBusServicesWithDirections(
			Map<String, Object> RDFdata) {
		String buses_string = (String) RDFdata.get(KEY_BUS_SERVICES);
		CoordinatedPhraseElement buses_with_directions = null;

		if (buses_string != null && buses_string.length() > 0) {

			String buses_directions_string = (String) RDFdata
					.get(KEY_BUS_SERVICES_DIRECTIONS);
			List<String> buses_list = new ArrayList<String>(
					Arrays.asList(buses_string.split(",")));
			List<String> buses_directions_list = new ArrayList<String>();

			if (buses_directions_string != null
					&& buses_directions_string.length() > 0)
				buses_directions_list = Arrays.asList(buses_directions_string
						.split(","));

			buses_with_directions = nlgFactory.createCoordinatedPhrase();
			for (int i = 0; i < buses_list.size(); i++) {
				String bus = buses_list.get(i);
				if (buses_directions_list.size() > i)
					bus = bus + " " + buses_directions_list.get(i).trim();

				NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
				buses_with_directions.addCoordinate(bus_obj);
			}

		}
		return buses_with_directions;
	}

	/*
	 * Generate diversion secondary locations phrase
	 */

	private PPPhraseSpec generateDiversionSecondaryLocationPhrase(
			String secondary_location_string, String preposition) {
		PPPhraseSpec secondary_location_phrase = null;

		if (secondary_location_string.length() > 0) {
			NPPhraseSpec place = nlgFactory
					.createNounPhrase(secondary_location_string);
			secondary_location_phrase = nlgFactory.createPrepositionPhrase();
			secondary_location_phrase.addComplement(place);
			secondary_location_phrase.setPreposition(preposition); // todo
		}

		return secondary_location_phrase;
	}

	/*
	 * Generate locations phrase
	 */

	private PPPhraseSpec generatePrimaryLocationPhrase(
			String primary_location_string, String preposition) {
		PPPhraseSpec primary_location_phrase = null;

		if (primary_location_string.length() > 0) {
			NPPhraseSpec place = nlgFactory
					.createNounPhrase(primary_location_string);
			primary_location_phrase = nlgFactory.createPrepositionPhrase();
			primary_location_phrase.addComplement(place);
			primary_location_phrase.setPreposition(preposition); // todo
		}

		return primary_location_phrase;
	}

	/*
	 * Generate duration phrase
	 */

	private PPPhraseSpec generateDurationPhrase(String duration_string,
			String preposition) {
		PPPhraseSpec duration_phrase = null;

		if (duration_string.length() > 0) {
			NPPhraseSpec duration = nlgFactory
					.createNounPhrase(duration_string);
			duration_phrase = nlgFactory.createPrepositionPhrase();
			duration_phrase.addComplement(duration);
			// duration_phrase.addComplement("only");
			duration_phrase.setPreposition(preposition); // todo
		}

		return duration_phrase;
	}

	private PPPhraseSpec generateDiversionRoadsPhrase(
			String diversion_roads_string, String preposition) {
		PPPhraseSpec diversion_roads_phrase = null;

		List<String> roads_list = new ArrayList<String>(
				Arrays.asList(diversion_roads_string.split(",")));

		// if there are roads
		if (roads_list.size() > 0) {

			CoordinatedPhraseElement roads = nlgFactory
					.createCoordinatedPhrase();
			for (String road : roads_list) {
				NPPhraseSpec road_obj = nlgFactory.createNounPhrase(road);
				roads.addCoordinate(road_obj);
			}

			diversion_roads_phrase = nlgFactory.createPrepositionPhrase();
			diversion_roads_phrase.addComplement(roads);
			diversion_roads_phrase.setPreposition(preposition);
		}
		/*
		 * if(diversion_roads_string.length()>0){ NPPhraseSpec roads =
		 * nlgFactory.createNounPhrase(diversion_roads_string);
		 * diversion_roads_phrase = nlgFactory.createPrepositionPhrase();
		 * diversion_roads_phrase.addComplement(roads);
		 * diversion_roads_phrase.setPreposition(preposition); }
		 */
		return diversion_roads_phrase;
	}

	private PPPhraseSpec generateDiversionRoadsPhraseOnly(
			String diversion_roads_string) {
		PPPhraseSpec diversion_roads_phrase = null;

		List<String> roads_list = new ArrayList<String>(
				Arrays.asList(diversion_roads_string.split(",")));

		// if there are roads
		if (roads_list.size() > 0) {

			CoordinatedPhraseElement roads = nlgFactory
					.createCoordinatedPhrase();
			for (String road : roads_list) {
				NPPhraseSpec road_obj = nlgFactory.createNounPhrase(road);
				roads.addCoordinate(road_obj);
			}

			diversion_roads_phrase = nlgFactory.createPrepositionPhrase();
			diversion_roads_phrase.addComplement(roads);
		}

		return diversion_roads_phrase;
	}
	
	private PPPhraseSpec generatePrimaryLocationPhraseOnly(
			String primary_location_string) {
		PPPhraseSpec primary_location_phrase = null;

		if (primary_location_string.length() > 0) {
			NPPhraseSpec place = nlgFactory
					.createNounPhrase(primary_location_string);
			primary_location_phrase = nlgFactory.createPrepositionPhrase();
			primary_location_phrase.addComplement(place);
		}

		return primary_location_phrase;
	}
	
	/*
	 * Generate problem reason phrase
	 */

	private PPPhraseSpec generateProblemReasonPhrase(String delay_length_string) {
		PPPhraseSpec delay_length_phrase = null;

		if (delay_length_string.length() > 0) {
			NPPhraseSpec delay = nlgFactory.createNounPhrase(delay_length_string);
			delay_length_phrase = nlgFactory.createPrepositionPhrase();
			delay_length_phrase.addComplement(delay);
		}
		
		return delay_length_phrase;
	}

	private PPPhraseSpec generateProblemReasonPhraseOnly(String problem_reason_string) {
		PPPhraseSpec problem_phrase = null;

		if (problem_reason_string.length() > 0) {
			NPPhraseSpec problem = nlgFactory
					.createNounPhrase(problem_reason_string);
			problem_phrase = nlgFactory.createPrepositionPhrase();
			problem_phrase.addComplement(problem);
		}

		return problem_phrase;
	}

	
	private SPhraseSpec generateDiversionTweet(Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);

		tweet.setSubject(buses);

		// add the reason phrase - diversion,delay etc
		tweet.setObject(RDFdata.get(KEYWORD_DIVERSION).toString());

		// add the location phrase

		if (RDFdata.containsKey("place") && generatePrimaryLocationPhrase(RDFdata.get("place")
				.toString(), "at") != null)
			tweet.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("place").toString(), "at"));

		// add the problem phrase
		if (generateProblemReasonPhrase(RDFdata.get("problem").toString()) != null)
			tweet.addComplement(generateProblemReasonPhrase(RDFdata.get(
					"problem").toString()));

		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * Template #
	 * 
	 * <route> <primary_location> diversion - 2nd - <Date> <route>
	 * <primary_location> diversion from <time-interval> <route>
	 * <primary_location> diversion this wkend at <secondary_location> - <Date>
	 * <route> <primary_location< diversion is in place until <Date> <route>
	 * <primary_location< diversion in place <timeInterval> until <Date> <route>
	 * diversion in place on <date> for <timeInterval>
	 */

	/*
	 * <route> is being diverted (into | along) <location> and then (right along
	 * | into) < road >
	 */
	private SPhraseSpec generateDiversionTweetTemplate1(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);

		tweet.setSubject(buses);

		VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase("is");
		tweet.setVerbPhrase(verb_phrase);
		tweet.setObject("diverted");

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), " around") != null)
			tweet.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), "at"));

		if (RDFdata.containsKey("place")
				&& generateDiversionRoadsPhrase(RDFdata.get("place")
						.toString(), "around ") != null)
			tweet.addComplement(generateDiversionRoadsPhrase(
					RDFdata.get("place").toString(), "around"));

		
		 PPPhraseSpec secondary_location_phrase = null;
		  if(((String)RDFdata.get("place")).length()>0){ //secondary
		  //location phrase goes at the end of message
		  tweet.addComplement(generateDiversionSecondaryLocationPhrase
		  (RDFdata.get("place").toString(),"into"));
		  
		  if(generateDiversionSecondaryLocationPhrase(RDFdata.get(
		  "secondary-location").toString(),"at")!=null)
		  secondary_location_phrase =
		  generateDiversionSecondaryLocationPhrase(RDFdata
		  .get("secondary-location").toString(),"at"); }
		 

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * 2<route> <primary_location> diversion starts <timeInterval> until <date>
	 */
	private SPhraseSpec generateDiversionTweetTemplate2(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "") != null)
			buses.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), ""));

		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject(buses);
		first_part.setObject("diversion");

		tweet.setSubject(first_part);

		VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("start");
		tweet.setVerbPhrase(start_verb);

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		/*
		 * PPPhraseSpec secondary_location_phrase = null;
		 * if(((String)RDFdata.get("place")).length()>0){ //secondary
		 * location phrase goes at the end of message
		 * tweet.addComplement(generateDiversionSecondaryLocationPhrase
		 * (RDFdata.get("place").toString(),"into"));
		 * 
		 * if(generateDiversionSecondaryLocationPhrase(RDFdata.get(
		 * "secondary-location").toString(),"at")!=null)
		 * secondary_location_phrase =
		 * generateDiversionSecondaryLocationPhrase(RDFdata
		 * .get("secondary-location").toString(),"at"); }
		 */

		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);
		System.out.println(tweet);
		return tweet;
	}

	/*
	 * 3 <route> diversion starts <timeInterval> for <timeInterval> only -
	 * <location>
	 */
	private SPhraseSpec generateDiversionTweetTemplate3(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject(buses);
		first_part.setObject("diversion");

		tweet.setSubject(first_part);

		VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("start");
		tweet.setVerbPhrase(start_verb);

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));
		

		if (RDFdata.get("delayLength") != null
				&& generateDurationPhrase(RDFdata.get("delayLength").toString(),
						"for") != null)
			tweet.addComplement(generateDurationPhrase(RDFdata.get("delayLength")
					.toString(), "for"));

		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "-") != null)
			tweet.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), "-"));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * <route> <route> <route> from <primary_location> are being diverted along
	 * < road >,< road >,< road >,< road > and < road >
	 */
	private SPhraseSpec generateDiversionTweetTemplate4(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject(buses);
		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "from") != null)
			first_part.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), "at"));

		tweet.setSubject(first_part);
		VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("is");
		tweet.setVerbPhrase(start_verb);

		tweet.setObject("diverted");

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		if (RDFdata.containsKey("place")
				&& generateDiversionRoadsPhrase(RDFdata.get("place")
						.toString(), "around") != null)
			tweet.addComplement(generateDiversionRoadsPhrase(
					RDFdata.get("place").toString(), "around"));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * <route> <route> <route> from <primary_location> are being diverted along
	 * < road >,< road >,< road >,< road > and < road >
	 */
	private SPhraseSpec generateDiversionTweetTemplate44(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		SPhraseSpec phrase1 = nlgFactory.createClause();
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		
  		VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase("is");

  		
		phrase1.setSubject(buses);
		phrase1.setVerbPhrase(verb_phrase);
		phrase1.setObject("diverted");
		
		Tense tense = (Tense) determineClauseTense(RDFdata);
		tweet.setFeature(Feature.TENSE, tense);
		
		PPPhraseSpec phrase2 = nlgFactory.createPrepositionPhrase();

		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "from") != null)
			phrase2.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), "at"));

		PPPhraseSpec phrase3 = nlgFactory.createPrepositionPhrase();

		if (RDFdata.containsKey("place")
				&& generateDiversionRoadsPhrase(RDFdata.get("place")
						.toString(), "around") != null)
			phrase3.addComplement(generateDiversionRoadsPhrase(
					RDFdata.get("place").toString(), "around"));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		//tweet.addComplement(phrase1);
		tweet=phrase1;
		tweet.addComplement(phrase2);
		tweet.addComplement(phrase3);
		
		return tweet;
	}
	
	/*
	 * due to <problem> on <location> <route> and <route> currently being
	 * diverted
	 */
	private SPhraseSpec generateDiversionTweetTemplate5(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject("due to " + RDFdata.get("factor"));

		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "on") != null)
			first_part.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), "on"));

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		first_part.addComplement(buses);

		tweet.setSubject(first_part);
		VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("is");
		tweet.setVerbPhrase(start_verb);

		tweet.setObject("diverted");
		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * <route><location> diversion in place on <date> for <timeInterval>
	 */
	private SPhraseSpec generateDiversionTweetTemplate6(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();

		// add bus phrase
		String bus_services_string = (String) RDFdata.get("service");
		CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);

		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject(buses);

		if (RDFdata.containsKey("primaryLocation") && generatePrimaryLocationPhrase(RDFdata.get("primaryLocation")
				.toString(), "") != null)
			first_part.addComplement(generatePrimaryLocationPhrase(
					RDFdata.get("primaryLocation").toString(), ""));

		first_part.addComplement("diversion in place");

		tweet.setSubject(first_part);

		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));

		// add the location info
		if (RDFdata.get("delayLength") != null) {
			NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
					"delayLength").toString());
			PPPhraseSpec duration_phrase = nlgFactory.createPrepositionPhrase();
			duration_phrase.addComplement(duration);
			duration_phrase.setPreposition("for");

			tweet.addComplement(duration_phrase);
		}

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	private SPhraseSpec shuffleOrder(Map<String, Object> RDFdata) {

		Random r = new Random();
		int i1 = r.nextInt(4);
		/*
		 * if(primary_location and secondary_location) if(roads) is being
		 * divering along locatin and then into roads if(primary_location and
		 * and roads secondary_location) route(s)primary_location
		 */
		return null;
	}

	/*
	 * <route> [<direction>] [and <route>] experiencing [<delay-size>] delays
	 * (of|of approx|of approximately|of up to)) <number> mins <direction>
	 * [<time-interval>] [due to <reason>]
	 */
	private SPhraseSpec generateDelayTweetTemplate1(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();
		CoordinatedPhraseElement buses = generateBusServicesWithDirections(RDFdata);

		if (buses != null)
			tweet.setSubject(buses);
		tweet.setVerb("is");

		tweet.setObject("experiencing delays");

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		// tweet.setSubject(RDFdata.get("bus-service").toString());

		// p.setVerb("effect"); //minimizing the characters by dropping out the
		// verb

		// add the location info
		if (RDFdata.containsKey("delayLength")
				&& (RDFdata.get("delayLength").toString()).length() > 0) {
			NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
					"delayLength").toString());
			PPPhraseSpec duration_phrase = nlgFactory.createPrepositionPhrase();
			duration_phrase.addComplement(duration);
			duration_phrase.setPreposition("of upto");

			tweet.addComplement(duration_phrase);
		}

		// add the problem phrase
		if (RDFdata.containsKey("problem")
				&& generateProblemReasonPhrase(RDFdata.get("problem")
						.toString()) != null)
			tweet.addComplement(generateProblemReasonPhrase(RDFdata.get(
					"problem").toString()));

		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * <route> [<direction>] running <number> mins late [<time-interval>]
	 */
	private SPhraseSpec generateDelayTweetTemplate2(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();
		CoordinatedPhraseElement buses = generateBusServicesWithDirections(RDFdata);

		if (buses != null)
			tweet.setSubject(buses);

		tweet.setVerb("is");
		tweet.setObject("running");

		Tense tense = (Tense) determineClauseTense(RDFdata);

		tweet.setFeature(Feature.TENSE, tense);

		// tweet.setSubject(RDFdata.get("bus-service").toString());

		// p.setVerb("effect"); //minimizing the characters by dropping out the
		// verb

		// add the location info
		if (RDFdata.containsKey("delayLength")) {
			NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
					"delayLength").toString() );
			PPPhraseSpec duration_phrase = nlgFactory.createPrepositionPhrase();
			duration_phrase.addComplement(duration);

			tweet.addComplement(duration);

		
		}

		NPPhraseSpec post_duration = nlgFactory.createNounPhrase("late");

		tweet.addComplement(post_duration);
		
		// add the date phrase
		if (createDatePhrase(RDFdata) != null)
			tweet.addComplement(createDatePhrase(RDFdata));

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * <reason> causing delays of <number> mins to <route>
	 */
	private SPhraseSpec generateDelayTweetTemplate3(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();

		if (RDFdata.containsKey("hasFactor")) {
			tweet.setSubject(RDFdata.get("hasFactor").toString());
			tweet.setVerb("is");
			tweet.setObject("causing delays");

			Tense tense = (Tense) determineClauseTense(RDFdata);

			tweet.setFeature(Feature.TENSE, tense);
		} else {
			tweet.setSubject("Delays");
//			tweet.setVerb("");
//			tweet.setObject("");

			//Tense tense = (Tense) determineClauseTense(RDFdata);

			//tweet.setFeature(Feature.TENSE, tense);
		}

		// add the location info
		if (RDFdata.containsKey("delayLength")){
		NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
				"delayLength").toString());
		PPPhraseSpec duration_phrase = nlgFactory.createPrepositionPhrase();
		duration_phrase.addComplement(duration);
		duration_phrase.setPreposition("of");

		tweet.addComplement(duration_phrase);
		}

		CoordinatedPhraseElement buses = generateBusServicesWithDirections(RDFdata);
		if (buses != null) {
			PPPhraseSpec buses_phrase = nlgFactory.createPrepositionPhrase();
			buses_phrase.addComplement(buses);
			buses_phrase.setPreposition("to");

			tweet.addComplement(buses_phrase);
		}
		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}

	/*
	 * delays (of | of up to) <number> mins (to | on) <route> [and [<number>
	 * mins on] <route>] <time-interval>
	 */
	private SPhraseSpec generateDelayTweetTemplate4(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();

		tweet.setSubject("delays");
		// tweet.setVerb("is");
		// tweet.setObject("causing delays");

		// add the location info
		if (RDFdata.containsKey("delayLength")){
		NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
				"delayLength").toString());
		PPPhraseSpec duration_phrase = nlgFactory.createPrepositionPhrase();
		duration_phrase.addComplement(duration);
		duration_phrase.setPreposition("of");

		tweet.addComplement(duration_phrase);
		}
		CoordinatedPhraseElement buses = generateBusServicesWithDirections(RDFdata);
		if (buses != null) {
			PPPhraseSpec buses_phrase = nlgFactory.createPrepositionPhrase();
			buses_phrase.addComplement(buses);
			buses_phrase.setPreposition("on");

			tweet.addComplement(buses_phrase);
		}
		// add the date phrase
		if (createDatePhrase(RDFdata) != null) {
			Map<String, String> dates = determineTodayTomorrowWeekend(RDFdata,
					"start");
			// tweet.setSubject(dates.get("hours"));
			// tweet.addComplement(dates.get(1));
			// tweet.setSubject("hours");
			tweet.addComplement(dates.get("phrase"));
		}

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
	}
	private SPhraseSpec generateDelayTweetTemplate44(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();

		SPhraseSpec phrase1 = nlgFactory.createClause();
        NPPhraseSpec noun = nlgFactory.createNounPhrase("delays");

		phrase1.setSubject(noun);
		//PPPhraseSpec phrase1 = nlgFactory.createPrepositionPhrase();
		//NPPhraseSpec delays = nlgFactory.createNounPhrase("delays");
		//phrase1.addComplement(delays);

		
		PPPhraseSpec phrase2 = nlgFactory.createPrepositionPhrase();
		NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get(
				"delayLength").toString());
		phrase2.addComplement(duration);
		phrase2.setPreposition("of");

		PPPhraseSpec phrase3 = nlgFactory.createPrepositionPhrase();
		CoordinatedPhraseElement buses = generateBusServicesWithDirections(RDFdata);
		if (buses != null) {
			phrase3.addComplement(buses);
			phrase3.setPreposition("on");
		}
		
		//tweet.addComplement(phrase1);
		tweet=phrase1;
		tweet.addComplement(phrase2);
		tweet.addComplement(phrase3);

		return tweet;
	}

	/*
	 * test
	 */
	private SPhraseSpec generateDelayTweetTemplate5(Map<String, Object> RDFdata) {
		/*
		 * create the first part
		 */

		SPhraseSpec tweet = nlgFactory.createClause();

		Map<String, String> dates = determineTodayTomorrowWeekend(RDFdata,
				"start");
		// tweet.setSubject(dates.get("hours"));
		// tweet.addComplement(dates.get(1));
		tweet.setSubject("Phrase: ");
		tweet.setObject(dates.get("phrase"));
		// tweet.setObject(dates.get("days"));

		return tweet;
	}

	private Document loadXML(ServletContext context){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		String fullPath = context.getRealPath("/WEB-INF/resources/templates.xml");
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(fullPath);

		} 
		catch(Exception e){
			e.printStackTrace();
		}
		
		return doc;
	}
	
	public SPhraseSpec testXPath(ServletContext context, Map<String,Object>RDFData){
		SPhraseSpec tweet = nlgFactory.createClause();;
		Document doc = loadXML(context);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		String templateType = RDFData.get(KEY_EVENT_TYPE).toString();
		switch (templateType){
		case KEY_EVENT_DELAY:
			templateType = TEMPLATE_EVENT_DELAY_TAG;
			break;
		case KEY_EVENT_DIVERSION:
			templateType = TEMPLATE_EVENT_DIVERSION_TAG;
			break;
		case KEY_EVENT_UPCOMING:
			templateType = KEY_EVENT_UPCOMING;
			break;
		}
		try{
		
		//XPathExpression expr = xpath.compile("/templates/template[@id='0']/phrase/tag/text()");
		
		//output = (String)expr.evaluate(doc, XPathConstants.STRING);
		
//		XPathExpression expr_event = xpath.compile("/templates/template[@type='diversion']");
		NodeList templates = (NodeList) xpath.evaluate("/templates/template[@type='"+templateType+"']",doc, XPathConstants.NODESET);

		//output = "nodes: "+ templates.getLength();
		
		ArrayList<String> outputArray = new ArrayList<String>();
		//phrases loop
		for(int i = 0; i < templates.getLength(); i++){
			


			Node template = templates.item(i);
			  NodeList phrases = (NodeList) xpath.evaluate("phrase", template, XPathConstants.NODESET);

		      String phraseType = "";
			  //iterate all the phrases
			  for(int j = 0; j < phrases.getLength(); j++){
	
					
				  SPhraseSpec part_phrase_sp=null;
				  PPPhraseSpec part_phrase_pp=null;
					
				  //get one phrase
				  Node phrase = phrases.item(j);
				  Element phrase_element = (Element) phrases.item(j);
				  phraseType = phrase_element.getAttribute("type").toString();

				  //get the tags in this one phrase
				  NodeList tags = (NodeList)xpath.evaluate("tag", phrase, XPathConstants.NODESET);
				  NodeList complements = (NodeList)xpath.evaluate("complement", phrase, XPathConstants.NODESET);
		    	  
			      String tagRequired = "";
			      String tagValue="";
			      String tagType="";
				  for(int k = 0; k < tags.getLength(); k++){ //right now, there is only one tag in each phrase
					  Element tag = (Element) tags.item(k);
				      tagValue = xpath.evaluate("tag", phrase);
				      tagRequired = tag.getAttribute("required");
				      //tagType = tag.getAttribute("type");
	
				  }
		    	  //System.out.println("phraseType: " + phraseType);

			      if(phraseType.equals("primary") || phraseType.equals("secondary")){
			    	  part_phrase_sp = nlgFactory.createClause();
			    	  part_phrase_sp.setSubject(extractPhraseTagSP(tagValue,RDFData));
				      //System.out.println("phraseType: " + part_phrase_sp.getSubject().toString());
			      }
			      else{
			    	  //part_phrase_pp = nlgFactory.createPrepositionPhrase();
			    	  part_phrase_pp = extractPhraseTagPP(tagValue,RDFData);
			      }
			      //System.out.println("phraseType: " + part_phrase_sp.getSubject().toString());

				 // System.out.println("complements : " + complements.getLength());
				//System.out.println("tagValue: " + tagValue);
				  String preposition_string="";
				  
				  //iterate the complements only if the tag is required
				  if(tagRequired.equals("yes"))
				  //if(part_phrase_sp!=null || part_phrase_pp!=null)
					  for(int l = 0; l < complements.getLength(); l++){
						  Node compliment_node = complements.item(l);

						  Element complement = (Element) complements.item(l);
					      
						 // System.out.println("node name "+compliment_node.getNodeName());

					     // String complementValue = xpath.evaluate("complement", phrase);
						  String complementValue =  compliment_node.getTextContent();
						 //System.out.println("complementValue: " + compliment_node.getTextContent());
						 //System.out.println("complementValue: " + complementValue);

					      //String RDFComplementValue = RDFData.get(complementValue).toString();
						  String complementType = complement.getAttribute("type");
						  //if complement is a tag
						  switch (complementType){
						  	  case "verb":
						  		VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase(complementValue);
						  		part_phrase_sp.setVerbPhrase(verb_phrase);
								break;
						  	  case "object"://
						  		part_phrase_sp.setObject(complementValue);
						  		  break;
						  	  case "preposition":
						  		preposition_string +=" "+ selectionPreposition(complementValue);
						  		//part_phrase_pp.setPreposition(RDFComplementValue);
						  		  break;
						  	  case "none":
						  		preposition_string +=" "+ selectionPreposition(complementValue);
						  		//part_phrase_pp.setPreposition(RDFComplementValue);
						  		  break;
							  case "tag": //special case for handling buses with directions
								  break;
							  default:
								  
								  break;
						  }
					  }
				  if(preposition_string.length()>0)
					  part_phrase_pp.setPreposition(preposition_string);
				  
			      if(phraseType.equals("primary")){
			    	  tweet=part_phrase_sp;
			    	  //System.out.println(part_phrase_sp.toString());
			      }
			      else{
			    	  if(part_phrase_sp!=null)
			    		  tweet.addComplement(part_phrase_sp);
			    	  if(part_phrase_pp!=null)
			    		  tweet.addComplement(part_phrase_pp);
			      }

			  }//phrases loop
			  
			}//templates loop
		
		}//try
		catch(Exception e){
			e.printStackTrace();
		}
		
		return tweet;
	}
	
	private String selectionPreposition(String prepositions_string){
		List<String> prepositions_list = new ArrayList<String>();

		if (prepositions_string!=null && prepositions_string.length()>0)
			prepositions_list.addAll(Arrays.asList(prepositions_string.split("\\|")));

		Random r = new Random();
		int random_index = r.nextInt(prepositions_list.size());

		return prepositions_list.get(random_index);
	}
	
	private CoordinatedPhraseElement extractPhraseTagSP(String tag, Map<String,Object>RDFData){
		CoordinatedPhraseElement element = null;
		switch (tag){
		case KEY_BUS_SERVICES:
			String bus_services_string = RDFData.get(KEY_BUS_SERVICES).toString();
			CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
			element = buses;
			break;
		case TEMPLATE_SERVICE_DIRECTION_TAG:
			CoordinatedPhraseElement buses_directions = generateBusServicesWithDirections(RDFData);
			element = buses_directions;
			break;
		}
		return element;
		
	}
	
	private PPPhraseSpec extractPhraseTagPP(String tag, Map<String,Object>RDFData){
		PPPhraseSpec element = null;
		switch (tag){
		case KEY_BUS_SERVICES:
			String bus_services_string = RDFData.get(KEY_BUS_SERVICES).toString();
			CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
			element = nlgFactory.createPrepositionPhrase(buses);
			break;

		case KEY_DIVERTED_ROADS_PLACES:
			String diverted_roads = (String) RDFData.get(KEY_DIVERTED_ROADS_PLACES);
			PPPhraseSpec diverted_roads_phrase = generateDiversionRoadsPhraseOnly(diverted_roads);
			element = diverted_roads_phrase;
			break;
		
		case KEY_PRIMARY_LOCATION:	
			String locations = (String) RDFData.get(KEY_PRIMARY_LOCATION);
			PPPhraseSpec primary_location_phrase = generatePrimaryLocationPhraseOnly(locations);
			element = primary_location_phrase;
			break;

		case KEY_PROBLEM_REASON:	
			String problem = (String) RDFData.get(KEY_PROBLEM_REASON);
			PPPhraseSpec problem_phrase = generateProblemReasonPhraseOnly(problem);
			element = problem_phrase;
			break;

		case KEY_DELAY_LENGTH:	
			String delay_length = (String) RDFData.get(KEY_DELAY_LENGTH);
			PPPhraseSpec delay_length_phrase = generateProblemReasonPhrase(delay_length);
			element = delay_length_phrase;
			break;
		case KEY_DURATION:	
			String duration = (String) RDFData.get(KEY_DURATION);
			PPPhraseSpec duration_phrase = generateProblemReasonPhrase(duration);
			element = duration_phrase;
			break;
		case "none":	
			element = nlgFactory.createPrepositionPhrase();
			break;
		}
		return element;
		
	}
	
}

/*
 * Sample tweets
 * 
 * Morning Aberdeen. All buses running as normal except #Service11 which is
 * running 6 mins late. Service11 running 5 mins late this afternoon. Traffic in
 * Dyce also causing delays to #Service17. All other services running as normal.
 * Afternoon, Aberdeen! Heavy traffic from city centre causing delays of 8 mins
 * to #Service18. Heavy traffic cause major delays this afternoon - 9 min delay
 * on #Service12, and 7 min delay on #Service3. Service12 is being diverted into
 * Byron Avenue and then right along Provost Fraser Drive. Traveling on
 * #Service12? Hilton Avenue diversion in place tomorrow until 9th March Fare
 * Changes from 8th March 2015 http://t.co/T1HKOHe36K Union St now open again
 * and all services back to normal. Hilton Avenue diversion has now finished on
 * #Service12. Service back to normal.
 */