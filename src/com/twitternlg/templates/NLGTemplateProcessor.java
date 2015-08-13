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

import com.twitternlg.database.DatabaseManager;

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
import static com.twitternlg.nlg.Constants.KEY_EVENT_REAL_TIME;
import static com.twitternlg.nlg.Constants.KEY_EVENT_REAL_TIME5;
import static com.twitternlg.nlg.Constants.KEY_EVENT_QUESTIONNAIRE;
import static com.twitternlg.nlg.Constants.KEY_EVENT_ALL_OK;
import static com.twitternlg.nlg.Constants.KEY_PROBLEM_REASON;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES_DIRECTIONS;
import static com.twitternlg.nlg.Constants.KEY_PRIMARY_LOCATION;
import static com.twitternlg.nlg.Constants.KEY_START_TIME;
import static com.twitternlg.nlg.Constants.KEY_END_TIME;
import static com.twitternlg.nlg.Constants.KEY_DELAY_LENGTH;
import static com.twitternlg.nlg.Constants.KEY_START_END_DATETIME;
import static com.twitternlg.nlg.Constants.KEY_DURATION;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICE_TIME;
import static com.twitternlg.nlg.Constants.KEY_DIVERTED_ROADS_PLACES;
import static com.twitternlg.nlg.Constants.KEYWORD_DIVERSION;
import static com.twitternlg.nlg.Constants.KEYWORD_DELAY;
import static com.twitternlg.nlg.Constants.KEY_GREETING;
import static com.twitternlg.nlg.Constants.KEY_TIMEOFDAY;
import static com.twitternlg.nlg.Constants.KEY_REPORTED_AT;
import static com.twitternlg.nlg.Constants.KEY_TODAYS_TIMEOFDAY;
import static com.twitternlg.nlg.Constants.TEMPLATE_EVENT_DIVERSION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_EVENT_DELAY_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_EVENT_ALL_OK;
import static com.twitternlg.nlg.Constants.TEMPLATE_PROBLEM_REASON_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_SERVICE_DIRECTION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_PRIMARY_LOCATION_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_START_TIME_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_END_TIME_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_DIVERTED_ROADS_PLACES_TAG;
import static com.twitternlg.nlg.Constants.TEMPLATE_DELAY_LENGTH_TAG;
import static com.twitternlg.nlg.Constants.NLG_MESSAGE_TIME_THRESHOLD;
import static com.twitternlg.nlg.Constants.KEY_CERTAINTY;
import static com.twitternlg.nlg.Constants.KEY_EVENT_GENERAL_DISRUPTION;
public class NLGTemplateProcessor {
	// core variables
	private Lexicon lexicon = null;
	private NLGFactory nlgFactory = null;
	private Realiser realiser = null;
	private ServletContext server_context = null;
	
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

	public ArrayList<String> generateTweetss(Map<String, Object> RDFdata, ServletContext context, String is_ranking) {
		ArrayList<String> tweets = new ArrayList<String>();

		//realiser.realiseSentence(testXPath(context,RDFdata));
		if(RDFdata.get(KEY_EVENT_TYPE).equals(KEY_EVENT_DIVERSION))
			tweets = generateTweetsFromCode(RDFdata);

		else{
			List<Map<String, Object>> output = processXMLTemplates(context,RDFdata,is_ranking);
			for (Map<String, Object> obj : output) {
				tweets.add(obj.get("paragraph").toString());				
			}

//			tweets = processXMLTemplates(context,RDFdata);
		}
		//String tweet = 	selectFinalTweet(tweets,RDFdata);
		tweets = selectFinalTweet(tweets,RDFdata);
		//do random stuff
		//check for duplicate
		return tweets;

	}

	public List<Map<String,Object>> generateTweetsWithRanking(Map<String, Object> RDFdata, ServletContext context,String is_ranking) {
		List<Map<String, Object>> output = processXMLTemplates(context,RDFdata,is_ranking);
		//ArrayList<String> tweets = new ArrayList<String>();

		//realiser.realiseSentence(testXPath(context,RDFdata));
		//if(RDFdata.get(KEY_EVENT_TYPE).equals(KEY_EVENT_DIVERSION))
		//tweets = generateTweetsFromCode(RDFdata);
		//ArrayList<String> tweets = processXMLTemplates(context,RDFdata);
		//String tweet = 	selectFinalTweet(tweets,RDFdata);
		//tweets = selectFinalTweet(tweets,RDFdata);
		//do random stuff
		//check for duplicate
		return output;

	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> selectFinalTweet(ArrayList<String> tweets,Map<String, Object> RDFdata){

		ArrayList<String>drop_tweets = null;
		ArrayList<String>re_tweets = new ArrayList<String>();


		//System.out.println(RDFdata.get("dropTweets").getClass());

		//remove drop tweets from final array
		if(RDFdata.containsKey("dropTweets")){
			drop_tweets=(ArrayList<String>)RDFdata.get("dropTweets");

			for(String d_tweet: drop_tweets){
				if(tweets.contains(d_tweet)){
					tweets.remove(d_tweet);
					System.out.println("removing: " + tweets.size());

				}
			}
		}

		if(tweets.size()>0){
			Random r = new Random();
			int index = r.nextInt(tweets.size());

			System.out.println("selected tweet: "+tweets.get(index));
			re_tweets.add(tweets.get(index));
		}else
			re_tweets.add("null");
			//re_tweets.add("No tweet generated");

		return re_tweets;
	}

	/*
	 * ranking algorithm
	 */
	private Map<String,Object> doRanking(ArrayList<String>messages){

/*		for(String message: messages){
			output.put("message", message);
			int characterCount = message.length();
			output.put("characters", characterCount);
		}
		return output;*/
		
		return null;
	}

	/*
	 * for diversion, still using old code
	 */
	public ArrayList<String> generateTweetsFromCode(Map<String, Object> RDFdata) {
		ArrayList<String> tweets = new ArrayList<String>();

		switch (RDFdata.get(KEY_EVENT_TYPE).toString()) {
		case KEY_EVENT_DIVERSION:
			tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate1(RDFdata)));
			tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate2(RDFdata)));
			tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate3(RDFdata)));
			tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate4(RDFdata)));
			if (RDFdata.get(KEY_PROBLEM_REASON) != null)
				tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate5(RDFdata)));

			tweets.add(realiser.realiseSentence(generateDiversionTweetTemplate6(RDFdata)));
			break;
		default:
			break;
		}

		return tweets;

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
			//System.out.println("future1");
			return Tense.FUTURE;
		}
		else if (start_diff < 0 && end_diff>0) {
			//System.out.println("future1");
			return Tense.PRESENT;
		}
		else if (start_diff <=0 && end_diff>0) {
			// System.out.println("past0");
			return Tense.PAST;
		}
		else if (start_diff <=0) {
			//System.out.println("past1");
			return Tense.PAST;
		} else if (end_diff <=0){
			//System.out.println("past2");
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

			if (hour >= 6 && hour <= 12) {
				if (phrase == "today")
					phrase = "this morning";
				else
					phrase = phrase + " " + "morning";
			} else if (hour >= 12 && hour <= 15) {
				if (phrase == "today")
					phrase = "this afternoon";
				else
					phrase = phrase + " " + "afternoon";
			} else if (hour >= 18 && hour <= 20) {
				if (phrase == "today")
					phrase = "this evening";
				else
					phrase = phrase + " " + "evening";
			}

			else if (hour >= 22) {
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

	/*
	 * Determines past,present or future tense based on given date/time in the
	 * annotation
	 */
	private Map<String, String> calculateTimeOfTheDay(String timestamp_string) {
		Date timestamp = new Date();
		Map<String, String> results = new HashMap<String, String>();


		if(timestamp_string==null || timestamp_string.length()==0)
			timestamp = new Date();
		else{
			//create date from string
		}

		int hour = timestamp.getHours();

		String phrase = "";

		if (hour >= 3 && hour <= 12)
			phrase = "morning";
		else if (hour >= 12 && hour <= 18)
			phrase = "afternoon";
		else if (hour >= 18 && hour <= 20)
			phrase = "evening";
		else if (hour >= 23)
			phrase = "tonight";

		results.put("day",dayFromInt(timestamp.getDay()));
		results.put("phrase", phrase);

		//System.out.println("calculateTimeOfTheDay "+ phrase);
		return results;
	}

	private String dayFromInt(int day){
		String day_str ="";
		switch (day){
		case 0:
			day_str = "Sunday";
			break;
		case 1:
			day_str = "Monday";
			break;
		case 2:
			day_str = "Tuesday";
			break;
		case 3:
			day_str = "Wednesday";
			break;
		case 4:
			day_str = "Thursday";
			break;
		case 5:
			day_str = "Friday";
			break;
		case 6:
			day_str = "Saturday";
			break;		
		}

		return day_str;
	}
	/*
	 * calculates time of the day sring
	 */
	private NPPhraseSpec generateTimeOfDay(String timestamp_string){

		NPPhraseSpec timeOfTheDay_obj=null;
		String timeOfTheDay_string ="";

		timeOfTheDay_string = calculateTimeOfTheDay(timestamp_string).get("phrase");

		timeOfTheDay_obj = nlgFactory.createNounPhrase(timeOfTheDay_string);

		return timeOfTheDay_obj;
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
		//System.out.println(start_end);
		if (RDFdata.containsKey(start_end + "sAtDateTime") && RDFdata.get(start_end + "sAtDateTime") != null){
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

		if (date_str.length() > 0 && formatter_pattern.length() > 0 && RDFdata.containsKey(start_end + "sAtDateTime"))
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

	/*
	 * Generate greeting phrase
	 */
	private CoordinatedPhraseElement generateGreetingPhrase(Map<String, Object> RDFdata) {
		String greeting_string = "";
		String todays_timeOfDay_string = "";

		/*if(RDFdata.containsKey(KEY_GREETING))
				greeting_string=RDFdata.get(KEY_GREETING).toString();*/

		//get morning,afternoon,evening

		//		if(RDFdata.containsKey(KEY_TODAYS_TIMEOFDAY)){
		//			todays_timeOfDay_string=RDFdata.get(KEY_TODAYS_TIMEOFDAY).toString();
		NPPhraseSpec timeOfDay = generateTimeOfDay(null);

		todays_timeOfDay_string=" "+realiser.realise(timeOfDay)+",";
		//	}

		CoordinatedPhraseElement greeting = nlgFactory.createCoordinatedPhrase();

		ArrayList<String> greetings_arr = new ArrayList<String>();
		try{
			Document doc = loadXML(server_context,"greetings.xml");
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
	
			NodeList greetings_nodes = (NodeList) xpath.evaluate("/greetings/greeting",doc, XPathConstants.NODESET);

			for(int a = 0; a < greetings_nodes.getLength(); a++){
				Element greeting_element = (Element) greetings_nodes.item(a);
				String greeting_node_value = greeting_element.getTextContent();
				greetings_arr.add(greeting_node_value);
			}
		}
		catch(Exception e){
			System.out.println("greetings crashed");
			e.printStackTrace();
		}
		
		//greetings_arr.add("Good"+todays_timeOfDay_string);

		if(calculateTimeOfTheDay(null).get("day").equals("Friday"))
			greetings_arr.add("Happy Friday!");

		//pickup a random greeting message
		int index = getRandomIndex(greetings_arr.size());
		greeting_string=greetings_arr.get(index);

		//generate the NLG phrase
		NPPhraseSpec greeting_obj = nlgFactory.createNounPhrase(greeting_string);
		greeting.addCoordinate(greeting_obj);

		return greeting;
	}

	/*
	 * generates bus service directions phrase
	 */
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

	private PPPhraseSpec generateDelayLengthPhrase(String delay_length_string) {
		PPPhraseSpec delay_length_phrase = null;
		delay_length_phrase = nlgFactory.createPrepositionPhrase();

		if (delay_length_string.length() > 0) {
			NPPhraseSpec delay = nlgFactory.createNounPhrase(delay_length_string);
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


	private SPhraseSpec checkRecipient(Map<String, Object> RDFData, SPhraseSpec tweet){
		boolean isRecipient=false;
		if(RDFData.containsKey("recipient"))
			if(DatabaseManager.isWithinTimeThreshold(RDFData.get("recipient").toString(), NLG_MESSAGE_TIME_THRESHOLD)){
				isRecipient=true;
				NPPhraseSpec conjuction = nlgFactory.createNounPhrase("and");
				tweet.addFrontModifier(conjuction);
			}
			
		return tweet;
	}

	
	private SPhraseSpec generateDiversionTweet(Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();
		tweet = checkRecipient(RDFdata,tweet);
		
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
		tweet = checkRecipient(RDFdata,tweet);

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


		// PPPhraseSpec secondary_location_phrase = null; //todo
		//if(((String)RDFdata.get("place")).length()>0){ //secondary
		//location phrase goes at the end of message
		//tweet.addComplement(generateDiversionSecondaryLocationPhrase
		//(RDFdata.get("place").toString(),"into"));

		/*if(generateDiversionSecondaryLocationPhrase(RDFdata.get(
		  "secondary-location").toString(),"at")!=null)
		  secondary_location_phrase =
		  generateDiversionSecondaryLocationPhrase(RDFdata
		  .get("secondary-location").toString(),"at"); 
		 */
		//  }


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
		tweet = checkRecipient(RDFdata,tweet);

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
		//System.out.println(tweet);
		return tweet;
	}

	/*
	 * 3 <route> diversion starts <timeInterval> for <timeInterval> only -
	 * <location>
	 */
	private SPhraseSpec generateDiversionTweetTemplate3(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();
		tweet = checkRecipient(RDFdata,tweet);

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
		tweet = checkRecipient(RDFdata,tweet);

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
		tweet = checkRecipient(RDFdata,tweet);

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
		tweet = checkRecipient(RDFdata,tweet);

		// first part is "Service 12 diversion"
		SPhraseSpec first_part = nlgFactory.createClause();

		first_part.setSubject("due to " + RDFdata.get("hasFactor"));

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
	 * 
	 * <route><location> diversion in place on <date> for <timeInterval>
	 */
	private SPhraseSpec generateDiversionTweetTemplate6(
			Map<String, Object> RDFdata) {

		SPhraseSpec tweet = nlgFactory.createClause();
		tweet = checkRecipient(RDFdata,tweet);

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

	private int getRandomIndex(int count) {

		Random r = new Random();
		int index = r.nextInt(count);

		return index;
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

	private Document loadXML(ServletContext context,String filename){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		//String folderLocation = "/WEB-INF/resources/";
		//String folderLocation = "http://sj.abdn.ac.uk/NLG/";

		//String fullPath = context.getRealPath(folderLocation+filename);
		//String fullPath = context.getRealPath("/WEB-INF/resources/"+filename);
		String fullPath = "http://sj.abdn.ac.uk/NLG/"+filename;

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

	public List<Map<String, Object>> processXMLTemplates(ServletContext context, Map<String,Object>RDFData, String ranking){

		SPhraseSpec tweet = null;
		this.server_context = context;
		boolean isRecipient = false;
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		ArrayList<String> outputArray = new ArrayList<String>();


		String templateEvent = null;
		String paragraphType = "";

		if(RDFData.containsKey(KEY_EVENT_TYPE))
			paragraphType=RDFData.get(KEY_EVENT_TYPE).toString();

		String filename = "templates-para.xml";


		switch (paragraphType){
		case KEY_EVENT_DELAY:
			paragraphType = TEMPLATE_EVENT_DELAY_TAG;
			break;
		case KEY_EVENT_DIVERSION:
			paragraphType = TEMPLATE_EVENT_DIVERSION_TAG;
			break;
		case KEY_EVENT_REAL_TIME:
			paragraphType = KEY_EVENT_REAL_TIME;
			break;
		case KEY_EVENT_REAL_TIME5:
			paragraphType = KEY_EVENT_REAL_TIME5;
			break;
		case KEY_EVENT_ALL_OK:
			paragraphType = TEMPLATE_EVENT_ALL_OK;
			break;
		case KEY_EVENT_QUESTIONNAIRE:
			paragraphType = KEY_EVENT_QUESTIONNAIRE;
			break;
		case KEY_EVENT_GENERAL_DISRUPTION:
			paragraphType = KEY_EVENT_GENERAL_DISRUPTION;
			break;
		default:
			paragraphType = "paragraph";
			break;
		}

		//check timelog condition for Realtime and Realtime5

		
		if(RDFData.containsKey("recipient")){
			if(DatabaseManager.isWithinTimeThreshold(RDFData.get("recipient").toString(), NLG_MESSAGE_TIME_THRESHOLD)){
				isRecipient=true;
				if (paragraphType.equals(KEY_EVENT_REAL_TIME) || paragraphType.equals(KEY_EVENT_REAL_TIME5))
					filename = "templates-para-connecting.xml";
			}
		}

		Document doc = loadXML(context,filename);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		
		try{

			//XPathExpression expr = xpath.compile("/templates/template[@id='0']/phrase/tag/text()");

			//output = (String)expr.evaluate(doc, XPathConstants.STRING);

			//		XPathExpression expr_event = xpath.compile("/templates/template[@type='diversion']");
			//NodeList templates = (NodeList) xpath.evaluate("/templates/template[@type='"+templateType+"']",doc, XPathConstants.NODESET);

			NodeList paragraphs = (NodeList) xpath.evaluate("/templates/paragraph[@type='"+paragraphType+"']",doc, XPathConstants.NODESET);

			//paragraphs
			for(int a = 0; a < paragraphs.getLength(); a++){

				Node paragprah = paragraphs.item(a);
				NodeList templates = (NodeList) xpath.evaluate("template", paragprah, XPathConstants.NODESET);
				String paragraph_string = "";
				int tags_missed=0;
				boolean all_true=true;

				//templates loop
				for(int i = 0; i < templates.getLength(); i++){

					tweet = nlgFactory.createClause();

					if (isRecipient==true && (paragraphType.equals(TEMPLATE_EVENT_DELAY_TAG) || paragraphType.equals(TEMPLATE_EVENT_DIVERSION_TAG) || paragraphType.equals(KEY_EVENT_GENERAL_DISRUPTION))){
						NPPhraseSpec conjuction = nlgFactory.createNounPhrase("and");
						tweet.addPostModifier(conjuction);
						//System.out.println("yaaaaaaaaaaa");
					}
					
					Node template = templates.item(i);
					NodeList phrases = (NodeList) xpath.evaluate("phrase", template, XPathConstants.NODESET);

					String phraseType = "";

					//phrases loop
					for(int j = 0; j < phrases.getLength(); j++){


						SPhraseSpec part_phrase_sp=null;
						PPPhraseSpec part_phrase_pp=null;
						all_true = true;
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
						String tagInclude="";

						for(int k = 0; k < tags.getLength(); k++){ //right now, there is only one tag in each phrase
							Element tag = (Element) tags.item(k);
							tagValue = xpath.evaluate("tag", phrase);
							tagRequired = tag.getAttribute("required");
							tagInclude = tag.getAttribute("include");
							//tagType = tag.getAttribute("type");

						}
						//System.out.println("phraseType: " + phraseType);
						
						if(phraseType.equals("primary") || phraseType.equals("secondary")){
							part_phrase_sp = nlgFactory.createClause();
							part_phrase_sp.setSubject(extractPhraseTagSP(tagValue,RDFData));
							//System.out.println("setting subject: " + tagValue);
						}
						else{
							//part_phrase_pp = nlgFactory.createPrepositionPhrase();
							part_phrase_pp = extractPhraseTagPP(tagValue,RDFData);
							//System.out.println("setting subject: " + realiser.realise(part_phrase_pp));
						}
						//System.out.println("phraseType: " + part_phrase_sp.getSubject().toString());

						// System.out.println("complements : " + complements.getLength());
						//System.out.println("tagValue: " + tagValue);
						String preposition_string="";

						//iterate the complements only if the tag is required
							for(int l = 0; l < complements.getLength(); l++){
								Node compliment_node = complements.item(l);

								Element complement = (Element) complements.item(l);

								// System.out.println("node name "+compliment_node.getNodeName());

								// String complementValue = xpath.evaluate("complement", phrase);
								String complementValue =  compliment_node.getTextContent();
								//System.out.println("complementValue: " + compliment_node.getTextContent());
								//
								//String RDFComplementValue = RDFData.get(complementValue).toString();
								String complementType = complement.getAttribute("type");
								
								//check complement 
								switch (complementType){
								case "subject"://
									part_phrase_sp.setSubject(complementValue);
									break;
								case "verb":
									VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase(complementValue);
									Tense tense = (Tense) determineClauseTense(RDFData);
									verb_phrase.setFeature(Feature.TENSE, tense);
									String subject_string = realiser.realiseSentence(part_phrase_sp);
									part_phrase_sp.setVerbPhrase(verb_phrase);	

									//temporary work
									if(tagInclude.equals("no")){
										String phrase_string = realiser.realiseSentence(part_phrase_sp);
										phrase_string = phrase_string.replaceAll(subject_string, "");
										phrase_string=phrase_string.replace(".", "");
										part_phrase_sp.setSubject("");
										part_phrase_sp.setVerb(phrase_string);
									}
									break;
								case "object"://
									part_phrase_sp.setObject(complementValue);
									break;
								case "preposition":
									preposition_string +=" "+ selectionPreposition(complementValue);
									//part_phrase_pp.setPreposition(RDFComplementValue);
									// System.out.println("preposition: " + preposition_string);
									break;
								case "certainty":
									preposition_string +=" "+ selectionCertaintyTerm(complementValue,RDFData);
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
							}//complements loop

						if(preposition_string.length()>0 && part_phrase_pp!=null){
							part_phrase_pp.setPreposition(preposition_string);
							//System.out.println("part_phrase_pp "+ realiser.realise(part_phrase_pp));

						}

						if(preposition_string.length()>0 && part_phrase_sp!=null){
							part_phrase_sp.addPostModifier(preposition_string);
							//System.out.println("part_phrase_pp "+ realiser.realise(part_phrase_pp));

						}
						if(phraseType.equals("primary")){
							if(!isRecipient && tagValue.equals(KEY_GREETING))
								tweet=part_phrase_sp;	
							//System.out.println(part_phrase_sp.toString());
						}
						else{
							if(part_phrase_sp!=null)
								tweet.addPostModifier(part_phrase_sp);
							// tweet.addComplement(part_phrase_sp);
							else if(part_phrase_pp!=null)
								tweet.addPostModifier(part_phrase_pp);
							else if(tagRequired.equals("yes")){
								//System.out.println("falssse");
								all_true=false;
								tags_missed++;
							}
							// tweet.addComplement(part_phrase_pp);
						}

					}//phrases loop
					if(all_true==true){
						//paragraph_string = paragraph_string + realiser.realiseSentence(tweet);
						//outputArray.add(paragraph_string);
					}

				}
				//templates loop


				try{
					
					tweet = addReportedAtTimestamp(RDFData, tweet);
					
					paragraph_string = paragraph_string + realiser.realiseSentence(tweet);
					//paragraph_string.trim();
					
					//System.out.println("paragraph_stringL:" + paragraph_string);
					Map<String,Object> obj = new HashMap<String,Object>();
					obj.put("paragraph", paragraph_string);
					obj.put("tags_missed", tags_missed);
					obj.put("characters", paragraph_string.length());
					
					if(ranking.equals("yes"))
						output.add(obj);
					else if(ranking.equals("no") && all_true) //if ranking information is not requested
						output.add(obj);
							
				}

				catch(Exception e){
					e.printStackTrace();
				}
				tags_missed=0; //reset tags missed counter


			}//paragraphs loop

		}//try
		catch(Exception e){
			e.printStackTrace();
		}

		doc=null;
		return output;
	}

	/*
	 * random preposition selector
	 */
	private String selectionPreposition(String prepositions_string){
		List<String> prepositions_list = new ArrayList<String>();

		if (prepositions_string!=null && prepositions_string.length()>0)
			prepositions_list.addAll(Arrays.asList(prepositions_string.split("\\|")));

		Random r = new Random();
		int random_index = r.nextInt(prepositions_list.size());

		return prepositions_list.get(random_index);
	}

	/*
	 * select certainty term based on certainty value
	 */
	private String selectionCertaintyTerm(String terms_string,Map<String,Object>RDFData){
		
		float certainty_value =  Float.parseFloat(RDFData.get("certainty").toString());
		
		List<String> terms_list = new ArrayList<String>();
		int index=0;


		
		if (terms_string!=null && terms_string.length()>0)
			terms_list.addAll(Arrays.asList(terms_string.split("\\|")));

		//System.out.println("selectionCertaintyTerm array"+terms_list);

		
		if(certainty_value>=0 && certainty_value <=0.3)
			index=0;
		if(certainty_value>=0.31 && certainty_value <=0.5)
			index=1;
		if(certainty_value>=0.51 && certainty_value <=0.7)
			index=2;
		if(certainty_value>=0.71)
			index=3;
		
		//System.out.println("selectionCertaintyTerm"+terms_list.get(index));
		//return "is likely";
		return terms_list.get(index);
	}
	
	private String createTweetTimestamp(Map<String,Object>RDFData){
		String timestamp_string="";
		
	//	if(RDFData.containsKey("timestamp"))
			
		
		return timestamp_string;
	}
	/*
	 * extractdata for SPPhrase
	 */
	private CoordinatedPhraseElement extractPhraseTagSP(String tag, Map<String,Object>RDFData){
		CoordinatedPhraseElement element = null;
		if(tag.equals("none") || tag.equals(KEY_GREETING) ||tag.equals(KEY_TODAYS_TIMEOFDAY) || RDFData.containsKey(tag)){
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
			case KEY_GREETING:
				//String greeting_message  = RDFData.get(KEY_GREETING).toString();
				CoordinatedPhraseElement greeting = generateGreetingPhrase(RDFData);
				element = greeting;
				//System.out.println("KEY_GREETING "+ realiser.realise(element));
				break;
			case KEY_PROBLEM_REASON:	
				String problem = (String) RDFData.get(KEY_PROBLEM_REASON);
				PPPhraseSpec problem_phrase = generateProblemReasonPhraseOnly(problem);
				CoordinatedPhraseElement problem_cordinate = nlgFactory.createCoordinatedPhrase();
				problem_cordinate.addCoordinate(problem_phrase);
				element = problem_cordinate;
				break;
			case "none":	
				element = nlgFactory.createCoordinatedPhrase(); //blank clause, subject set from complement
				break;
			}
		}
		return element;

	}

	/*
	 * extractdata for PPPhraseSpec
	 */
	private PPPhraseSpec extractPhraseTagPP(String tag, Map<String,Object>RDFData){
		PPPhraseSpec element = null;
		if(tag.equals("none") || tag.equals(KEY_GREETING) ||tag.equals(KEY_TODAYS_TIMEOFDAY) ||
				RDFData.containsKey(tag) || tag.equals(KEY_START_END_DATETIME)){
			switch (tag){
			case KEY_BUS_SERVICES:
				String bus_services_string = RDFData.get(KEY_BUS_SERVICES).toString();
				CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
				element = nlgFactory.createPrepositionPhrase();
				element.addComplement(buses);
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
				element = nlgFactory.createPrepositionPhrase();
				if(RDFData.containsKey(KEY_DELAY_LENGTH)){
					String delay_length = (String) RDFData.get(KEY_DELAY_LENGTH);
					PPPhraseSpec delay_length_phrase = generateDelayLengthPhrase(delay_length);
					element = delay_length_phrase;
				}
				break;
			case KEY_START_END_DATETIME:	
				SPhraseSpec start_end_dateime_phrase = createDatePhrase(RDFData);
				element = nlgFactory.createPrepositionPhrase();
				element.addComplement(start_end_dateime_phrase);;
				break;
			case KEY_DURATION:	
				String duration = (String) RDFData.get(KEY_DURATION);
				CoordinatedPhraseElement durations = generateBusServicesPhrase(duration);
				element = nlgFactory.createPrepositionPhrase();
				element.addComplement(durations);
				break;
			case KEY_BUS_SERVICE_TIME:	
				String service_time = (String) RDFData.get(KEY_BUS_SERVICE_TIME);
				CoordinatedPhraseElement service_time_phrase = generateBusServicesPhrase(service_time);
				element = nlgFactory.createPrepositionPhrase();
				element.addComplement(service_time_phrase);
				break;
			case KEY_TODAYS_TIMEOFDAY:	
				String timeOfDay = "this "+calculateTimeOfTheDay("").get("phrase");
				//System.out.println("KEY_TODAYS_TIMEOFDAY "+ timeOfDay);
				PPPhraseSpec timeOfDay_phrase = generateProblemReasonPhrase(timeOfDay);
				element = timeOfDay_phrase;
				break;
			case KEY_CERTAINTY:	
				element = nlgFactory.createPrepositionPhrase();
				break;	
				/*case KEY_TIMEOFDAY:	
				String timeOfDay = (String) RDFData.get(KEY_TIMEOFDAY);
				PPPhraseSpec timeOfDay_phrase = generateProblemReasonPhrase(timeOfDay);
				element = timeOfDay_phrase;
				break;*/
			case "none":	
				element = nlgFactory.createPrepositionPhrase();
				break;
			}
		}

		return element;

	}

	private SPhraseSpec addReportedAtTimestamp( Map<String,Object>RDFData, SPhraseSpec tweet){
		String event_type = RDFData.get(KEY_EVENT_TYPE).toString();
		
		if(RDFData.containsKey(KEY_REPORTED_AT) && 
				!(event_type.equals(KEY_EVENT_REAL_TIME) || event_type.equals(KEY_EVENT_REAL_TIME5) || event_type.equals(KEY_EVENT_ALL_OK) || event_type.equals(KEY_EVENT_QUESTIONNAIRE)) 
			){
			try {
				String formatter_pattern = "yyyy-MM-dd'T'HH:mm:ss";
				DateFormat format = new SimpleDateFormat(formatter_pattern);
				Date date = format.parse(RDFData.get(KEY_REPORTED_AT).toString());
				
				SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
				
				String date_str = outputFormat.format(date);
				NPPhraseSpec date_phrase = nlgFactory.createNounPhrase(date_str);
				tweet.addPostModifier("reported at");
				tweet.addPostModifier(date_phrase);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
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