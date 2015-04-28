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

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class GenericTemplate {
	
	private Map<String,Object> RDFdata = null;
	private Lexicon lexicon = null;
    private NLGFactory nlgFactory = null;
    private Realiser realiser = null;
    
	public GenericTemplate(Map<String,Object> data, Lexicon lex, NLGFactory nlg, Realiser real) {
	
		//initialize core variables
		this.lexicon = lex;
		this.nlgFactory = nlg;
		this.realiser = real;
		this.RDFdata = data;
	}
	
	public SPhraseSpec createDateTimeIntervalPhrase(Map<String,Object>RDFdata){
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
	
	public SPhraseSpec createDatePhrase(Map<String,Object>RDFdata){
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
	public Object determineClauseTense(Map<String,Object>RDFdata){
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
	
	/*
	 * Determines past,present or future tense based on given date/time in the annotation
	 */
	public Map<String,String> determineTodayTomorrowWeekend(Map<String,Object>RDFdata,String start_end_date){
		Date current_date = new Date();
		Date start_date = new Date();
		
		ArrayList<Object> arr_start = createDateFormat(RDFdata,start_end_date);
		
		Map<String,String> results = new HashMap<String,String>();
		
		if(arr_start.size()>0)
			start_date = (Date)arr_start.get(0);

		//check if today
			//check if morning,evening,night
		//check if tomorrow
			//check if morning,evening,night
		//check if weekend
			//check if morning,evening,night
		
		long seconds_difference = start_date.getTime()-current_date.getTime();
		int days_difference = start_date.getDate()-current_date.getDate();
		//days_difference = days_difference/(1000*60*60*24);
		long minutes_difference = seconds_difference/(60*1000);
		int hour = start_date.getHours();

		String phrase = "";
		
		if(days_difference==1 || days_difference==0){
			if(days_difference==1)
				phrase = "tomorrow";
			else if(days_difference==0)
				phrase = "today";
			
			if(hour>=6&&hour<=11){
				if(phrase=="today")
					phrase = "this morning";
				else
					phrase = phrase + " "+"morning";
			}
			else if(hour>=12&&hour<=15){
				if(phrase=="today")
					phrase = "this afternoon";
				else
					phrase = phrase + " "+"afternoon";
			}
			else if(hour>=17&&hour<=19){
				if(phrase=="today")
					phrase = "this evening";
				else
					phrase = phrase + " "+"evening";
			}
			
			else if(hour>=21){
				if(phrase=="today")
					phrase = "tonight";
				else
					phrase = phrase + " "+"night";
			}

		}
		else{
			if(days_difference>0 && days_difference<=8){
				if(start_date.getDay()==6 || start_date.getDay()==0)
					phrase = "this weekend";
				else
					phrase = createDateString(RDFdata,start_end_date);

			}
			else
				phrase = createDateString(RDFdata,start_end_date);
		}


		results.put("minutes", String.valueOf(minutes_difference));
		results.put("days", String.valueOf(days_difference));
		results.put("hours", String.valueOf(hour));
		results.put("phrase", phrase);

		return results;
	}

	
	public String createDateString(Map<String,Object>RDFdata, String start_end){
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
	public ArrayList<Object> createDateFormat(Map<String,Object>RDFdata, String start_end){
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
	public CoordinatedPhraseElement generateBusServicesPhrase(String buses_string){
	    List<String> buses_list = new ArrayList<String>(Arrays.asList(buses_string.split(",")));

	    CoordinatedPhraseElement buses = nlgFactory.createCoordinatedPhrase();
	    for(String bus: buses_list){
	    	NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
	    	buses.addCoordinate(bus_obj);
	    }

		return buses;
	}
	
	
	public CoordinatedPhraseElement generateBusServicesWithDirections(Map<String,Object>RDFdata){
		String buses_string = (String)RDFdata.get("bus-services");
		CoordinatedPhraseElement  buses_with_directions = null;
	  
		if(buses_string.length()>0){
	    	
			String buses_directions_string = (String)RDFdata.get("bus-services-directions");
		    List<String> buses_list = new ArrayList<String>(Arrays.asList(buses_string.split(",")));
		    List<String> buses_directions_list = new ArrayList<String>();
		    
		    if(buses_directions_string.length()>0)
		    	buses_directions_list = Arrays.asList(buses_directions_string.split(","));
	
		    buses_with_directions = nlgFactory.createCoordinatedPhrase();
		    for(int i=0; i<buses_list.size(); i++){
		    	String bus = buses_list.get(i);
		    	if(buses_directions_list.size()>i)
		    		bus=bus+" "+buses_directions_list.get(i);
		    	
		    	NPPhraseSpec bus_obj = nlgFactory.createNounPhrase(bus);
		    	buses_with_directions.addCoordinate(bus_obj);
		    }

	}
		return buses_with_directions;
	}
	/*
	 * Generate diversion secondary locations phrase
	 */
	
	public PPPhraseSpec generateDiversionSecondaryLocationPhrase(String secondary_location_string, String preposition){
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
	
	public PPPhraseSpec generatePrimaryLocationPhrase(String primary_location_string, String preposition){
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
	
	public PPPhraseSpec generateDurationPhrase(String duration_string, String preposition){
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
	
	public PPPhraseSpec generateDiversionRoadsPhrase(String diversion_roads_string, String preposition){
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
	
	public SPhraseSpec generateProblemReasonPhrase(String problem_reason_string){
	    SPhraseSpec problem_phrase = null;
	    
		if(problem_reason_string.length()>0){
		    problem_phrase = nlgFactory.createClause();
		    problem_phrase.setObject(problem_reason_string);
		    problem_phrase.setFeature(Feature.COMPLEMENTISER, "due to"); //todo
		 }
		
	    return problem_phrase;
	}
	
}
