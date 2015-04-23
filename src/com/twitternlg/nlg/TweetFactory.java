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

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class TweetFactory {
	//core variables
	private Lexicon lexicon = null;
    private NLGFactory nlgFactory = null;
    private Realiser realiser = null;
    
	public TweetFactory() {
	
	//initialize core variables
	lexicon = Lexicon.getDefaultLexicon();
	nlgFactory = new NLGFactory(lexicon);
	realiser = new Realiser(lexicon);
	    
   // Map <String,Object> RDFdata = createRDFData();
    
  //  SPhraseSpec tweet = generateTweet((Map <String,Object>)RDFdata.get("diversion_tweet_1"));
   //SPhraseSpec tweet = generateTweet( (Map <String,Object>)RDFdata.get("delay_tweet"));
    
   // String output = realiser.realiseSentence(tweet);
  //  System.out.println(output);
    
    //HTTPManager.getInstance().sendPost();

    } 
	
	public String generateTweetString(Map <String,Object> RDFdata){
	    //Map <String,Object> RDFdata = createRDFData();
//	    SPhraseSpec tweet = generateTweet((Map <String,Object>)RDFdata.get("diversion_tweet_1"));
	    
		
	    SPhraseSpec tweet = generateTweet(RDFdata);     
	    String output = realiser.realiseSentence(tweet);
	    //System.out.println(output);

		return output;
	}
	
	
	/*
	 * Decide which tweet to generate depending on type of event
	 */
	private SPhraseSpec generateTweet(Map<String,Object> RDFdata){
			SPhraseSpec tweet=null;
			
		    switch(RDFdata.get("event").toString()){
	    	case "diversion":
	    		tweet = generateDiversionTweetTemplate1(RDFdata);
	    		break;
	    	case "delay":
	    		tweet = generateDelayTweet(RDFdata);
	    		break;
	    	case "accident":
	    	//	tweet = generateDelayTweet(RDFdata);
	    		break;
	    	case "greeting":
	    	//	tweet = generateDelayTweet(RDFdata);
	    		break;
	    	default:
	    		break;
		    }
		    
		    return tweet;
			
		}

	public ArrayList<String> generateTweets(Map<String,Object> RDFdata){
		ArrayList<String> tweets = new ArrayList<String>();
		
		switch(RDFdata.get("event").toString()){
    	case "diversion":
    		tweets.add("<route> is being diverted (into | along) <location> and then (right along | into) < road ><br>" + 
    	realiser.realiseSentence(generateDiversionTweetTemplate1(RDFdata)));
    		
    		tweets.add("<route> <primary_location> diversion starts <timeInterval> until <date><br>" + 
    		    	realiser.realiseSentence(generateDiversionTweetTemplate2(RDFdata)));
 		   
    		tweets.add("<route> diversion starts <timeInterval> for <timeInterval> only - <location><br>" + 
    		    	realiser.realiseSentence(generateDiversionTweetTemplate3(RDFdata)));
    		
    		tweets.add("<route> <route> <route> from <primary_location> are being diverted along < road >,< road >,< road >,< road > and < road ><br>" + 
    		    	realiser.realiseSentence(generateDiversionTweetTemplate4(RDFdata)));
    		

		
    		break;
    	case "delay":
    		//tweet = generateDelayTweet(RDFdata);
    		break;
    	case "accident":
    	//	tweet = generateDelayTweet(RDFdata);
    		break;
    	case "greeting":
    	//	tweet = generateDelayTweet(RDFdata);
    		break;
    	default:
    		break;
	    }
	    
	    return tweets;
		
	}
	
	private SPhraseSpec generateDiversionTweet2(Map<String,Object>RDFdata){
		
	    /*
	     * Assumption: Every tweet will have two parts - info about the bus and place, info about the problem and date
	     *
	     *create the first part
	     */
		
	    SPhraseSpec tweet = nlgFactory.createClause();
	    //add bus info
	    ArrayList<String>bus_services = (ArrayList<String>)RDFdata.get("bus-services");
	    CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
	    for(String bus: bus_services){
	    	NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
	    	buses.addCoordinate(bus_obj);
	    }

	    tweet.setSubject(buses);

	    tweet.setObject(RDFdata.get("event").toString());
	//  p.setVerb("effect"); //minimizing the characters by dropping out the verb
	    
	    //add the location info
	    NPPhraseSpec place = nlgFactory.createNounPhrase(RDFdata.get("primary-location").toString());
	    PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
	    pp.addComplement(place);
	    pp.setPreposition("at");

	    tweet.addComplement(pp);

	    /*
	     *
	     *create the second part - problem and date
	     */
	    
	    //add the problem info
	    //NPPhraseSpec q = nlgFactory.createNounPhrase(RDFdata.get("reason"));
	    SPhraseSpec q = nlgFactory.createClause();
	    q.setObject(RDFdata.get("reason").toString());
	    q.setFeature(Feature.COMPLEMENTISER, "due to");
	    q.setFeature(Feature.TENSE, Tense.PRESENT);
	    
	    tweet.addComplement(q);
	   
	    //add the start end date info

	    SPhraseSpec date = createDatePhrase(RDFdata);
	    
	    tweet.addComplement(date);
	    
	    String output = realiser.realiseSentence(tweet);
	    //System.out.println(output);
	    
	    return tweet;
	}

	private SPhraseSpec createDatePhrase(Map<String,Object>RDFdata){
		Tense tense = (Tense)determineClauseTense(RDFdata);
		
		SPhraseSpec date_phrase = null;

		
		String start_date = createDateString(RDFdata,"start");
		String end_date = createDateString(RDFdata,"end");
		
		if(start_date.length()>0 || end_date.length()>0){
			
			date_phrase = nlgFactory.createClause();
			
			String full_date = start_date;
			
			if(start_date.length()>0 && end_date.length()>0)
				full_date = full_date+" to "+end_date;
			else if (start_date.length()<=0 && end_date.length()>0)
				full_date = end_date;
			
			date_phrase.setObject(full_date);
			
			switch(tense){
				case PAST:{
					date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
					break;
				}
				case PRESENT:{
					if(end_date.length()>0)
						date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
					else if(start_date.length()>0)
						date_phrase.setFeature(Feature.COMPLEMENTISER, "since");
					break;
				}
				case FUTURE:{
					if(start_date.length()>0)
						date_phrase.setFeature(Feature.COMPLEMENTISER, "from");
					else
						date_phrase.setFeature(Feature.COMPLEMENTISER, "until");
	
					break;
				}
				default:
				break;
	
			}
		}

		//date_phrase.setObject(tense.toString());
		
		return date_phrase;
	}
	
	/*
	 * Determines past,present or future tense based on given date/time in the annotation
	 */
	private Object determineClauseTense(Map<String,Object>RDFdata){
		Date current_date = new Date();
		Date start_date = new Date();
		Date end_date = new Date();
		
		ArrayList<Object> arr_start = createDateFormat(RDFdata,"start");
		ArrayList<Object> arr_end = createDateFormat(RDFdata,"end");
		
		if(arr_start.size()>0)
			start_date = (Date)arr_start.get(0);
		if(arr_end.size()>0)
			end_date = (Date)arr_end.get(0);

		long start_diff = start_date.getTime()-current_date.getTime();
		long end_diff = end_date.getTime()-current_date.getTime();
		
		if (start_diff>0){
			//System.out.println("future");
			return Tense.FUTURE;
		}
		else if (end_diff<0){
			//System.out.println("past");
			return Tense.PAST;
		}
		else{
			//System.out.println("present");
			return Tense.PRESENT;
		}
		
		//if end date is past
		//return Tense.PAST
	}
	
	private String createDateString(Map<String,Object>RDFdata, String start_end){
		ArrayList<Object> arr = createDateFormat(RDFdata,start_end);
		DateFormat format = null;
		String date = "";
		if(arr.size()>0){
		    format = new SimpleDateFormat((String)arr.get(1));
		    date = format.format((Date)arr.get(0));
		}

		return date;
	}
	
	/*
	 * creates date formatter for the input date : e.g mm, dd, yyyy or just dd, yyyy
	 */
	private ArrayList<Object> createDateFormat(Map<String,Object>RDFdata, String start_end){
	    String formatter_pattern="";
	    String date_str = "";
	    if(RDFdata.get(start_end+"-day")!=null){
	    	formatter_pattern=formatter_pattern+"E"+" ";
	    	date_str=date_str+RDFdata.get(start_end+"-day")+" ";
	    }
	    if(RDFdata.get(start_end+"-time")!=null){
	    	formatter_pattern=formatter_pattern+"HH:mm"+" ";
	    	date_str=date_str+RDFdata.get(start_end+"-time")+" ";
	    }
	    if(RDFdata.get(start_end+"-date")!=null){
	    	formatter_pattern=formatter_pattern+"d"+" ";
	    	date_str=date_str+RDFdata.get(start_end+"-date")+" ";
	    }
	    if(RDFdata.get(start_end+"-month")!=null){
	    	formatter_pattern=formatter_pattern+"MMMM"+" ";
	    	date_str=date_str+RDFdata.get(start_end+"-month")+" ";
	    }
	    if(RDFdata.get(start_end+"-year")!=null){
	    	formatter_pattern=formatter_pattern+"yyyy"+" ";
	    	date_str=date_str+RDFdata.get(start_end+"-year")+" ";
	    }
	    
	    date_str = date_str.replaceAll("\\s+$", "");
	    formatter_pattern = formatter_pattern.replaceAll("\\s+$", "");
		

		ArrayList<Object> arr = new ArrayList<Object>();

		if(date_str.length()>0 && formatter_pattern.length()>0)
			try {
				DateFormat format = new SimpleDateFormat(formatter_pattern);
				Date date=format.parse(date_str);
				arr.add(date);
				arr.add(formatter_pattern);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return arr;
	}

	/*
	 * Generate bus services phrase
	 */
	private CoordinatedPhraseElement generateBusServicesPhrase(String buses_string){
	    List<String> buses_list = new ArrayList<String>(Arrays.asList(buses_string.split(",")));

	    CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
	    for(String bus: buses_list){
	    	NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
	    	buses.addCoordinate(bus_obj);
	    }

		return buses;
	}
	
	/*
	 * Generate diversion secondary locations phrase
	 */
	
	private PPPhraseSpec generateDiversionSecondaryLocationPhrase(String secondary_location_string, String preposition){
		PPPhraseSpec secondary_location_phrase = null;

		if(secondary_location_string.length()>0){
			NPPhraseSpec place = nlgFactory.createNounPhrase(secondary_location_string);
		    secondary_location_phrase = nlgFactory.createPrepositionPhrase();
		    secondary_location_phrase.addComplement(place);
		    secondary_location_phrase.setPreposition(preposition); //todo
	    }	    
	    
		return secondary_location_phrase;
	}
	
	/*
	 * Generate locations phrase
	 */
	
	private PPPhraseSpec generatePrimaryLocationPhrase(String primary_location_string, String preposition){
		PPPhraseSpec primary_location_phrase = null;

		if(primary_location_string.length()>0){
			NPPhraseSpec place = nlgFactory.createNounPhrase(primary_location_string);
		    primary_location_phrase = nlgFactory.createPrepositionPhrase();
		    primary_location_phrase.addComplement(place);
		    primary_location_phrase.setPreposition(preposition); //todo
	    }	    
	    
		return primary_location_phrase;
	}
	
	/*
	 * Generate duration phrase
	 */
	
	private PPPhraseSpec generateDurationPhrase(String duration_string, String preposition){
		PPPhraseSpec duration_phrase = null;

		if(duration_string.length()>0){
			NPPhraseSpec duration = nlgFactory.createNounPhrase(duration_string);
			duration_phrase = nlgFactory.createPrepositionPhrase();
			duration_phrase.addComplement(duration);
			//duration_phrase.addComplement("only");
			duration_phrase.setPreposition(preposition); //todo
	    }	    
	    
		return duration_phrase;
	}
	
	private PPPhraseSpec generateDiversionRoadsPhrase(String diversion_roads_string, String preposition){
		PPPhraseSpec diversion_roads_phrase = null;

	    List<String> roads_list = new ArrayList<String>(Arrays.asList(diversion_roads_string.split(",")));

	    //if there are roads
	    if(roads_list.size()>0){
	    	
		    CoordinatedPhraseElement roads = nlgFactory.createCoordinatedPhrase();
		    for(String road: roads_list){
		    	NPPhraseSpec road_obj = nlgFactory.createNounPhrase(road);
		    	roads.addCoordinate(road_obj);
		    }
		    
	
		    diversion_roads_phrase = nlgFactory.createPrepositionPhrase();
		    diversion_roads_phrase.addComplement(roads);
		    diversion_roads_phrase.setPreposition(preposition);
	    }
	   /* if(diversion_roads_string.length()>0){
			NPPhraseSpec roads = nlgFactory.createNounPhrase(diversion_roads_string);
		    diversion_roads_phrase = nlgFactory.createPrepositionPhrase();
		    diversion_roads_phrase.addComplement(roads);
		    diversion_roads_phrase.setPreposition(preposition);
	    }*/
	    return diversion_roads_phrase;
	}
	
	/*
	 * Generate problem reason phrase
	 */
	
	private SPhraseSpec generateProblemReasonPhrase(String problem_reason_string){
	    SPhraseSpec problem_phrase = null;
	    
		if(problem_reason_string.length()>0){
		    problem_phrase = nlgFactory.createClause();
		    problem_phrase.setObject(problem_reason_string);
		    problem_phrase.setFeature(Feature.COMPLEMENTISER, "due to"); //todo
		 }
		
	    return problem_phrase;
	}
	
		private SPhraseSpec generateDiversionTweet(Map<String,Object>RDFdata){
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		   
		    //add bus phrase
		    String bus_services_string = (String)RDFdata.get("bus-services");
		    CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);

		    tweet.setSubject(buses);

		    //add the reason phrase - diversion,delay etc
		    tweet.setObject(RDFdata.get("event").toString());
		    
		    //add the location phrase

		    if(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"at")!=null)
		    	tweet.addComplement(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"at"));
		    
		    //add the problem phrase
		    if(generateProblemReasonPhrase(RDFdata.get("problem").toString())!=null)
		    	 tweet.addComplement(generateProblemReasonPhrase(RDFdata.get("problem").toString()));		    
		   
		    //add the date phrase
		    if(createDatePhrase(RDFdata)!=null)		    
		    	tweet.addComplement(createDatePhrase(RDFdata));
		    
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;
		}
		
		/*
  Template # 
			
			<route> <primary_location> diversion - 2nd - <Date>
			<route> <primary_location> diversion from <time-interval>
			<route> <primary_location> diversion this wkend at <secondary_location> - <Date>
			<route> <primary_location< diversion is in place until <Date>
			<route> <primary_location< diversion in place <timeInterval> until <Date>
			<route> diversion in place on <date> for <timeInterval>

		 */
		
		/*
		 * <route> is being diverted (into | along) <location> and then (right along | into) < road >
		 */
		private SPhraseSpec generateDiversionTweetTemplate1(Map<String,Object>RDFdata){
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		    
		    //add bus phrase
		    String bus_services_string = (String)RDFdata.get("bus-services");
		    CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);

		    tweet.setSubject(buses);
		    
		    VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase("is");
		    tweet.setVerbPhrase(verb_phrase);
		    tweet.setObject("diverted");
		    
		    Tense tense = (Tense)determineClauseTense(RDFdata);
		   
		    tweet.setFeature(Feature.TENSE, tense);
		   
		    if(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"into")!=null)
		    	 tweet.addComplement(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"into"));		    
		   		    
		    if(generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"and then into")!=null)
		    	 tweet.addComplement(generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"and then into"));		    

		    
		    /*PPPhraseSpec secondary_location_phrase = null;
		    if(((String)RDFdata.get("primary-location")).length()>0){ //secondary location phrase goes at the end of message
		    	tweet.addComplement(generateDiversionSecondaryLocationPhrase(RDFdata.get("primary-location").toString(),"into"));

		    	if(generateDiversionSecondaryLocationPhrase(RDFdata.get("secondary-location").toString(),"at")!=null)
			    	secondary_location_phrase = generateDiversionSecondaryLocationPhrase(RDFdata.get("secondary-location").toString(),"at");
		    }*/
		    
		    	
		    	  
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;
		}
		
		/*
		   2<route> <primary_location> diversion starts <timeInterval> until <date>
		 */
		private SPhraseSpec generateDiversionTweetTemplate2(Map<String,Object>RDFdata){
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		    
		    //add bus phrase
		    String bus_services_string = (String)RDFdata.get("bus-services");
		    CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		    if(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"")!=null)
		    	 buses.addComplement(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),""));		    
		   	
		    //first part is "Service 12 diversion"
		    SPhraseSpec first_part = nlgFactory.createClause();

		    first_part.setSubject(buses);
		    first_part.setObject("diversion");
		    
		    tweet.setSubject(first_part);

		    VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("start");
		    tweet.setVerbPhrase(start_verb);

		    Tense tense = (Tense)determineClauseTense(RDFdata);
		   
		    tweet.setFeature(Feature.TENSE, tense);
		       
		    /*PPPhraseSpec secondary_location_phrase = null;
		    if(((String)RDFdata.get("primary-location")).length()>0){ //secondary location phrase goes at the end of message
		    	tweet.addComplement(generateDiversionSecondaryLocationPhrase(RDFdata.get("primary-location").toString(),"into"));

		    	if(generateDiversionSecondaryLocationPhrase(RDFdata.get("secondary-location").toString(),"at")!=null)
			    	secondary_location_phrase = generateDiversionSecondaryLocationPhrase(RDFdata.get("secondary-location").toString(),"at");
		    }*/
		    
		    //add the date phrase
		    if(createDatePhrase(RDFdata)!=null)		    
		    	tweet.addComplement(createDatePhrase(RDFdata));
		 
		    	  
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;
		}

			/*
		   3		<route> diversion starts <timeInterval> for <timeInterval> only - <location>
		 */
		private SPhraseSpec generateDiversionTweetTemplate3(Map<String,Object>RDFdata){
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		    
		    //add bus phrase
		    String bus_services_string = (String)RDFdata.get("bus-services");
		    CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		    //first part is "Service 12 diversion"
		    SPhraseSpec first_part = nlgFactory.createClause();

		    first_part.setSubject(buses);
		    first_part.setObject("diversion");
		    
		    tweet.setSubject(first_part);

		    VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("start");
		    tweet.setVerbPhrase(start_verb);

		    Tense tense = (Tense)determineClauseTense(RDFdata);
		   
		    tweet.setFeature(Feature.TENSE, tense);
		    
		    //add the date phrase
		    if(createDatePhrase(RDFdata)!=null)		    
		    	tweet.addComplement(createDatePhrase(RDFdata));
		    
		    if(generateDurationPhrase(RDFdata.get("duration").toString(),"for")!=null)
		    	 tweet.addComplement(generateDurationPhrase(RDFdata.get("duration").toString(),"for"));		    
		   	 
		    
		    if(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"-")!=null)
		    	 tweet.addComplement(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"-"));		    
		   	
		    	  
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;
		}
		
		/*
		 <route> <route> <route> from <primary_location> are being diverted along < road >,< road >,< road >,< road > and < road >
		 */
		private SPhraseSpec generateDiversionTweetTemplate4(Map<String,Object>RDFdata){
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		    
		    //add bus phrase
		    String bus_services_string = (String)RDFdata.get("bus-services");
		    CoordinatedPhraseElement buses = generateBusServicesPhrase(bus_services_string);
		    //first part is "Service 12 diversion"
		    SPhraseSpec first_part = nlgFactory.createClause();

		    first_part.setSubject(buses);
		    if(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"from")!=null)
		    	first_part.addComplement(generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"from"));		    
		   	
		    
		    tweet.setSubject(first_part);
		    VPPhraseSpec start_verb = nlgFactory.createVerbPhrase("is");
		    tweet.setVerbPhrase(start_verb);
		    
		    tweet.setObject("diverted");
		    
		    Tense tense = (Tense)determineClauseTense(RDFdata);
		   
		    tweet.setFeature(Feature.TENSE, tense);
		    
		    if(generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"along")!=null)
		    	 tweet.addComplement(generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"along"));		    

		    	  
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;
		} 

		   
		private SPhraseSpec shuffleOrder(Map<String,Object>RDFdata){
			
			Random r = new Random();
			int i1 = r.nextInt(4);
			/*if(primary_location and secondary_location)
					if(roads)
				is being divering along locatin and then into roads
			if(primary_location and and roads secondary_location)
				route(s)primary_location*/
			return null;
		}
		
		private SPhraseSpec generateDelayTweet(Map<String,Object>RDFdata){
		    /*
		     * Assumption: Every tweet will have two parts - info about the bus and place, info about the problem and date
		     *
		     *create the first part
		     */
			
		    SPhraseSpec tweet = nlgFactory.createClause();
		    
		    VPPhraseSpec problem = nlgFactory.createVerbPhrase(RDFdata.get("problem").toString());
	     
		    tweet.setSubject(problem);
		    
		    //add bus info
		    ArrayList<String>bus_services = (ArrayList<String>)RDFdata.get("bus-services");
		    //System.out.println(bus_services);
		    CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
		    for(String bus: bus_services){
		    	NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
		    	buses.addCoordinate(bus_obj);
		    }

		    //add the start end date info
		    SPhraseSpec bus_phrase = nlgFactory.createClause();
		    bus_phrase.setObject(buses);
		    bus_phrase.setFeature(Feature.COMPLEMENTISER, "on");

		    
		    tweet.setObject(bus_phrase);

		    //tweet.setSubject(RDFdata.get("bus-service").toString());
		    
		//  p.setVerb("effect"); //minimizing the characters by dropping out the verb
		    
		    //add the location info
		    NPPhraseSpec duration = nlgFactory.createNounPhrase(RDFdata.get("duration").toString());
		    PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
		    pp.addComplement(duration);
		    pp.setPreposition("for");

		    tweet.addComplement(pp);

		    /*
		     *
		     *create the second part - problem and date
		     */
		    
		    //add the start end date info
		    SPhraseSpec date = nlgFactory.createClause();
		    date.setObject(RDFdata.get("time-of-day").toString());
		    date.setFeature(Feature.COMPLEMENTISER, "this");

		    tweet.addComplement(date);
		    
		    //String output = realiser.realiseSentence(tweet);
		    //System.out.println(output);
		    
		    return tweet;			
		}
		
		
		//types of tweets: Disruption, Diversion, Delay, Accident, Traffic jam, general update.

		/*
		 * Generate dummy RDF with tags/annotation 
	     * 
	     * Tweets: 
	     * 1) Tillydrone Av diversion on Service 19 has been extended until the 5th of April due to roadworks in preparation for the third Don crossing.
	     * 2) Due to an incident on Bremner Terrace, #Service11 and #Service12 are currently being diverted.
	     * 3) Delays of 5 mins to #Service11 and #Service1 this morning. All other services running as normal.
	     * 4) Very poor road conditions this morning and we expect delays as traffic builds up towards 8am. All services are running.
	     * 5) Due to an incident, Union St is closed between Market St and Bridge St. All buses are being diverted along Schoolhill and Union Terrace.
		 * */
		
		private Map<String,Object> createRDFData(){
			
			//main RDFData variable
			Map<String,Object> RDFdata = new HashMap<String,Object>();

			//create a greeting tweet data
			Map<String,Object> greeting_data = new HashMap<String,Object>();
			greeting_data.put("event", " ");
			greeting_data.put("time-of-day", "Good morning");
			//greeting_data.put("time-of-day", "Good afternoon");
			greeting_data.put("location", "Aberdeen");

			RDFdata.put("greeting_tweet", greeting_data);
			
			/*
			 * create a diversion tweet data
			 * mentioning a place, end date and effected bus service
			 */
			Map<String,Object> diversion_data = new HashMap<String,Object>();
			diversion_data.put("event", "diversion");
			diversion_data.put("primary-location", "Tillydrone Av");

			ArrayList<String> bus_services = new ArrayList<String>();
			bus_services.add("Service 19");
			//bus_services.add("Service 18");
			//bus_services.add("Service 17");

			diversion_data.put("bus-services", bus_services);
			
			diversion_data.put("problem", "diversion");
			diversion_data.put("reason", "roadworks");
			diversion_data.put("reason-compliment", "preparation for the third Don crossing");
			//RDFdata.put("start-date", ""); //we don't have start date in the source tweet
			diversion_data.put("end-date", "5th April");
			
			RDFdata.put("diversion_tweet", diversion_data);
			
			
			/*
			 * create a 2nd diversion tweet data
			 * with multiple buses, locations and start, end time
			 * 
			 * Samples:
			 * 1)Due to an incident, Union St is closed between Market St and Bridge St. All buses are being diverted along Schoolhill and Union Terrace.
			 * 2)Service12 is being diverted into Byron Avenue and then right along Provost Fraser Drive.
			 * 3)Services 1/2 / X40 from BOD are being diverted along East North St, Commerce St, Virginia St, Guild St and Bridge St.
			 */
			Map<String,Object> diversion_data_1 = new HashMap<String,Object>();
			diversion_data_1.put("event", "diversion");
			diversion_data_1.put("primary-location", "Tillydrone Av");
			//diversion_data_1.put("secondary-location", "Tillydrone Av");

			bus_services = new ArrayList<String>();
			bus_services.add("Service 1");
			bus_services.add("Service 2");
			//bus_services.add("Service 17");

			diversion_data_1.put("bus-services", bus_services);
			
			diversion_data_1.put("problem", "diversion");
			diversion_data_1.put("reason", "roadworks");
			diversion_data_1.put("reason-compliment", "preparation for the third Don crossing");
			
			diversion_data_1.put("start-day","sunday");
			//diversion_data_1.put("start-time","00:00");
			diversion_data_1.put("start-date","5");
			diversion_data_1.put("start-month","April");
			diversion_data_1.put("start-year","2015");

			diversion_data_1.put("end-time","18:30");
			diversion_data_1.put("end-date","25");
			diversion_data_1.put("end-month","April");
			diversion_data_1.put("end-year","2015");
			
			RDFdata.put("diversion_tweet_1", diversion_data_1);
			

			
			/*
			 * create a delay tweet data
			 * 
			 * Samples:
			 * 1)There are delays on Service 11 due to a bus stuck at Kingswells roundabout to Cults.
			 * 2)Delays of upto 40 minutes on service 11 due to a fault with the traffic lights on Springfield Road.
			 * 3)Service 3 experiencing delays due to icy conditions in the Foresterhill area.
			 */

			Map<String,Object> delay_data = new HashMap<String,Object>();
			
			delay_data.put("event", "delay"); //delay event has time in minutes, a reason, one or more bus services, time of the day (morning, evening, afternoon)
			
			bus_services = new ArrayList<String>();
			bus_services.add("Service 11");
			//bus_services.add("Service 1");
			//bus_services.add("Service 2");

			delay_data.put("bus-services", bus_services);
			delay_data.put("problem", "delay");
			delay_data.put("time-of-day", "morning");
			delay_data.put("duration", "15 mins");
			
			RDFdata.put("delay_tweet", delay_data);
			
			return RDFdata;
		}
		
}

/*
 * Sample tweets
 *
 * Morning Aberdeen. All buses running as normal except #Service11 which is running 6 mins late.
 * Service11 running 5 mins late this afternoon. Traffic in Dyce also causing delays to #Service17. All other services running as normal.
 * Afternoon, Aberdeen! Heavy traffic from city centre causing delays of 8 mins to #Service18.
 * Heavy traffic cause major delays this afternoon - 9 min delay on #Service12, and 7 min delay on #Service3.
 * Service12 is being diverted into Byron Avenue and then right along Provost Fraser Drive.
 * Traveling on #Service12? Hilton Avenue diversion in place tomorrow until 9th March 
 * Fare Changes from 8th March 2015 http://t.co/T1HKOHe36K
 * Union St now open again and all services back to normal.
 * Hilton Avenue diversion has now finished on #Service12. Service back to normal.
 */