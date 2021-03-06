package com.twitternlg.nlg;

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
import static com.twitternlg.nlg.Constants.KEY_PROBLEM_REASON;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES_DIRECTIONS;
import static com.twitternlg.nlg.Constants.KEY_PRIMARY_LOCATION;
import static com.twitternlg.nlg.Constants.KEY_START_TIME;
import static com.twitternlg.nlg.Constants.KEY_END_TIME;
import static com.twitternlg.nlg.Constants.KEY_DELAY_LENGTH;
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


public class TweetFactory {
	// core variables
	private Lexicon lexicon = null;
	private NLGFactory nlgFactory = null;
	private Realiser realiser = null;

	public TweetFactory() {

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

	public ArrayList<String> generateTweets(Map<String, Object> RDFdata) {
		ArrayList<String> tweets = new ArrayList<String>();

		switch (RDFdata.get(KEY_EVENT_TYPE).toString()) {
		case KEY_EVENT_DIVERSION:
			tweets.add(/*
						 * "<strong>T1:</strong> < route > is being diverted (into | along) < location > and then (right along | into) < road >	<br/> <strong>Message:</strong> "
						 * +
						 */realiser
					.realiseSentence(generateDiversionTweetTemplate1(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T2:</strong> < route > <primary_location> diversion starts < timeInterval > until < date ><br/> <strong>Message:</strong> "
						 * +
						 */realiser
					.realiseSentence(generateDiversionTweetTemplate2(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T3:</strong> < route > diversion starts < timeInterval > for < timeInterval > only - < location ><br/> <strong>Message:</strong> "
						 * +
						 */realiser
					.realiseSentence(generateDiversionTweetTemplate3(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T4:</strong> < route > <route> <route> from < primary_location > are being diverted along < road >,< road >,< road >,< road > and < road ><br/> <strong>Message:</strong> "
						 * +
						 */realiser
					.realiseSentence(generateDiversionTweetTemplate4(RDFdata))
			/* + "<br/><br/>" */);
			if (RDFdata.get("problem") != null)
				tweets.add(/*
							 * "<strong>T5:</strong> due to an incident on < primary_location > < route > and < route > currently being diverted<br/> <strong>Message:</strong> "
							 * +
							 */realiser
						.realiseSentence(generateDiversionTweetTemplate5(RDFdata))
				/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T6:</strong> < route > < primary_location > diversion in place on < date > for < timeInterval ><br/> <strong>Message:</strong> "
						 * +
						 */realiser
					.realiseSentence(generateDiversionTweetTemplate6(RDFdata))
			/* + "<br/><br/>" */);
			break;
		case KEY_EVENT_DELAY:
			tweets.add(/*
						 * "<strong>T1:</strong> < route > [< direction>] [and < route >] experiencing [< delay-size >] delays (of|of approx|of approximately|of up to)) < number > mins < direction > [< time-interval >] [due to < reason >]<br/>"
						 * + "<strong>Message:</strong> "+
						 */
			realiser.realiseSentence(generateDelayTweetTemplate1(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T2:</strong> < route > [< direction >] running < number > mins late [< time-interval >]<br/>"
						 * + "<strong>Message:</strong> "+
						 */
			realiser.realiseSentence(generateDelayTweetTemplate2(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T3:</strong> < reason > causing delays of < number > mins to < route ><br/>"
						 * + "<strong>Message:</strong> "+
						 */
			realiser.realiseSentence(generateDelayTweetTemplate3(RDFdata))
			/* + "<br/><br/>" */);
			tweets.add(/*
						 * "<strong>T4:</strong>  delays (of | of up to) < number > mins (to | on) < route > [and [< number > mins on] < route >] < time-interval ><br/>"
						 * + "<strong>Message:</strong> "+
						 */
			realiser.realiseSentence(generateDelayTweetTemplate4(RDFdata))
			/* + "<br/><br/>" */);

			tweets.add(/*
						 * "<strong>T5:</strong>  date test<br/>" +
						 * "<strong>Message:</strong> " +
						 */
			realiser.realiseSentence(generateDelayTweetTemplate5(RDFdata))
			/* + "<br/><br/>" */);

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
					bus = bus + " " + buses_directions_list.get(i);

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

	/*
	 * Generate problem reason phrase
	 */

	private SPhraseSpec generateProblemReasonPhrase(String problem_reason_string) {
		SPhraseSpec problem_phrase = null;

		if (problem_reason_string.length() > 0) {
			problem_phrase = nlgFactory.createClause();
			problem_phrase.setObject(problem_reason_string);
			problem_phrase.setFeature(Feature.COMPLEMENTISER, "due to"); // todo
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

	// types of tweets: Disruption, Diversion, Delay, Accident, Traffic jam,
	// general update.

	/*
	 * Generate dummy RDF with tags/annotation
	 * 
	 * Tweets: 1) Tillydrone Av diversion on Service 19 has been extended until
	 * the 5th of April due to roadworks in preparation for the third Don
	 * crossing. 2) Due to an incident on Bremner Terrace, #Service11 and
	 * #Service12 are currently being diverted. 3) Delays of 5 mins to
	 * #Service11 and #Service1 this morning. All other services running as
	 * normal. 4) Very poor road conditions this morning and we expect delays as
	 * traffic builds up towards 8am. All services are running. 5) Due to an
	 * incident, Union St is closed between Market St and Bridge St. All buses
	 * are being diverted along Schoolhill and Union Terrace.
	 */

	private Map<String, Object> createRDFData() {

		// main RDFData variable
		Map<String, Object> RDFdata = new HashMap<String, Object>();

		// create a greeting tweet data
		Map<String, Object> greeting_data = new HashMap<String, Object>();
		greeting_data.put(KEY_EVENT_TYPE, " ");
		greeting_data.put("time-of-day", "Good morning");
		// greeting_data.put("time-of-day", "Good afternoon");
		greeting_data.put("location", "Aberdeen");

		RDFdata.put("greeting_tweet", greeting_data);

		/*
		 * create a diversion tweet data mentioning a place, end date and
		 * effected bus service
		 */
		Map<String, Object> diversion_data = new HashMap<String, Object>();
		diversion_data.put(KEY_EVENT_TYPE, "diversion");
		diversion_data.put("place", "Tillydrone Av");

		ArrayList<String> bus_services = new ArrayList<String>();
		bus_services.add("Service 19");
		// bus_services.add("Service 18");
		// bus_services.add("Service 17");

		diversion_data.put("service", bus_services);

		diversion_data.put("problem", "diversion");
		diversion_data.put("reason", "roadworks");
		diversion_data.put("reason-compliment",
				"preparation for the third Don crossing");
		// RDFdata.put("start-date", ""); //we don't have start date in the
		// source tweet
		diversion_data.put("end-date", "5th April");

		RDFdata.put("diversion_tweet", diversion_data);

		/*
		 * create a 2nd diversion tweet data with multiple buses, locations and
		 * start, end time
		 * 
		 * Samples: 1)Due to an incident, Union St is closed between Market St
		 * and Bridge St. All buses are being diverted along Schoolhill and
		 * Union Terrace. 2)Service12 is being diverted into Byron Avenue and
		 * then right along Provost Fraser Drive. 3)Services 1/2 / X40 from BOD
		 * are being diverted along East North St, Commerce St, Virginia St,
		 * Guild St and Bridge St.
		 */
		Map<String, Object> diversion_data_1 = new HashMap<String, Object>();
		diversion_data_1.put(KEY_EVENT_TYPE, "diversion");
		diversion_data_1.put("place", "Tillydrone Av");
		// diversion_data_1.put("secondary-location", "Tillydrone Av");

		bus_services = new ArrayList<String>();
		bus_services.add("Service 1");
		bus_services.add("Service 2");
		// bus_services.add("Service 17");

		diversion_data_1.put("service", bus_services);

		diversion_data_1.put("problem", "diversion");
		diversion_data_1.put("reason", "roadworks");
		diversion_data_1.put("reason-compliment",
				"preparation for the third Don crossing");

		diversion_data_1.put("start-day", "sunday");
		// diversion_data_1.put("start-time","00:00");
		diversion_data_1.put("start-date", "5");
		diversion_data_1.put("start-month", "April");
		diversion_data_1.put("start-year", "2015");

		diversion_data_1.put("end-time", "18:30");
		diversion_data_1.put("end-date", "25");
		diversion_data_1.put("end-month", "April");
		diversion_data_1.put("end-year", "2015");

		RDFdata.put("diversion_tweet_1", diversion_data_1);

		/*
		 * create a delay tweet data
		 * 
		 * Samples: 1)There are delays on Service 11 due to a bus stuck at
		 * Kingswells roundabout to Cults. 2)Delays of upto 40 minutes on
		 * service 11 due to a fault with the traffic lights on Springfield
		 * Road. 3)Service 3 experiencing delays due to icy conditions in the
		 * Foresterhill area.
		 */

		Map<String, Object> delay_data = new HashMap<String, Object>();

		delay_data.put(KEY_EVENT_TYPE, "delay"); // delay event has time in minutes, a
											// reason, one or more bus services,
											// time of the day (morning,
											// evening, afternoon)

		bus_services = new ArrayList<String>();
		bus_services.add("Service 11");
		// bus_services.add("Service 1");
		// bus_services.add("Service 2");

		delay_data.put("service", bus_services);
		delay_data.put("problem", "delay");
		delay_data.put("time-of-day", "morning");
		delay_data.put("delayLength", "15 mins");

		RDFdata.put("delay_tweet", delay_data);

		return RDFdata;
	}
	
	private SPhraseSpec generateDiversionTweetTemplate1aa(
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

		// String output = realiser.realiseSentence(tweet);
		// System.out.println(output);

		return tweet;
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