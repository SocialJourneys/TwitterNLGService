package com.twitternlg.servlet;

import com.google.gson.Gson;
import com.twitternlg.database.DatabaseManager;
import com.twitternlg.nlg.ATweet;
import com.twitternlg.nlg.TweetFactory;
import com.twitternlg.templates.NLGTemplateProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simplenlg.phrasespec.NPPhraseSpec;
import static com.twitternlg.nlg.Constants.KEY_EVENT_TYPE;
import static com.twitternlg.nlg.Constants.KEY_PROBLEM_REASON;
import static com.twitternlg.nlg.Constants.KEY_BUS_SERVICES;
import static com.twitternlg.nlg.Constants.KEY_PRIMARY_LOCATION;
import static com.twitternlg.nlg.Constants.KEY_START_TIME;
import static com.twitternlg.nlg.Constants.KEY_END_TIME;
import static com.twitternlg.nlg.Constants.KEY_DELAY_LENGTH;
import static com.twitternlg.nlg.Constants.KEY_DIVERTED_ROADS_PLACES;

/**
 * Servlet implementation class NLGService
 */
@WebServlet("/")
public class service extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public service() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
		PrintWriter out = response.getWriter();
		
		//BufferedReader reader = request.getReader();
		Gson gson = new Gson();

	    /*ATweet tweet = gson.fromJson(reader, ATweet.class);

	    response.setContentType("application/json");
	    
	    TweetFactory t = new TweetFactory();
    	Map<String,Object> RDFdata = prepareInput(tweet);
    	
    	ArrayList <String>tweets = t.generateTweets(RDFdata);
    	if(tweets.size()>0)
    		out.println(tweets.get(0));
	    /*for(String objTweet: tweets){
    	out.println(objTweet);

    	}*/	
    	
	 // Get the printwriter object from response to write the required json object to the output stream      
	 
/*	    out.println(tweet.getEvent());
	    out.println(tweet.getDiversion_road());
	  
	    tweet.print(out);*/
	   
	    
	    
	    //Map<String, String> obj = gson.fromJson(reader, HashMap.class);

		//convert request body into string json
		StringBuilder buffer = new StringBuilder();
	    Scanner scanner = new Scanner(request.getInputStream());
	    while (scanner.hasNextLine()) {
	    	buffer.append(scanner.nextLine());
	    }
	    scanner.close();
	    String body = buffer.toString();
	    
	    //convert json string into hashmap
	    Map<String, Object> RDFdata = gson.fromJson(body, HashMap.class);
	    
	
	    // TweetFactory t = new TweetFactory();
	    NLGTemplateProcessor t = new NLGTemplateProcessor();
	    String ranking = "no";
	    if(RDFdata.containsKey("ranking"))
	    	ranking = RDFdata.get("ranking").toString();
	    
	    switch (ranking){
	    case "yes":
			List<Map<String, Object>> output = t.generateTweetsWithRanking(RDFdata,request.getServletContext(), "yes");
			/*for (Map<String, Object> obj : output) {
				String json = new Gson().toJson(foo );

				out.println("\n");
			}*/
			String json = new Gson().toJson(output);
			out.println(json);
	    	break;
	    case "no":{
	    	ArrayList <String>tweets = t.generateTweetss(RDFdata,request.getServletContext(), "no");
	    	
	    	//if(tweets.size()>0)
	    		//out.println(tweets.get(0));
		    for(String objTweet: tweets){
		    	out.println(objTweet);
		    	//out.println("\n");
	    	}
		    break;
	    }
	    	
	    default:
	    	ArrayList <String>tweets = t.generateTweetss(RDFdata,request.getServletContext(), "no");
	    	
	    	//if(tweets.size()>0)
	    		//out.println(tweets.get(0));
		    for(String objTweet: tweets){
		    	out.println(objTweet);
		    	//out.println("\n");
	    	}
	    	break;
	    }
	    

    	
	
	}

	//todo: remove this datatype conversion
	//method not used anymore!
	private Map<String,Object> prepareInput(ATweet tweet){
		Map<String,Object> RDFdata = new HashMap<String,Object>();
		
		RDFdata.put(KEY_EVENT_TYPE,tweet.getType());
		RDFdata.put(KEY_BUS_SERVICES,tweet.getService());
		RDFdata.put(KEY_PROBLEM_REASON,tweet.getHasFactor());
		RDFdata.put(KEY_PRIMARY_LOCATION,tweet.getPrimaryLocation());
		RDFdata.put(KEY_DIVERTED_ROADS_PLACES,tweet.getPlace());
		RDFdata.put("duration",tweet.getDuration());
		RDFdata.put(KEY_START_TIME,tweet.getStartsAtDateTime());
			
		RDFdata.put("start-day",tweet.getStart_day());
		RDFdata.put("start-time",tweet.getStart_time());
		RDFdata.put("start-date",tweet.getStart_date());
		RDFdata.put("start-month",tweet.getStart_month());
		RDFdata.put("start-year",tweet.getStart_year());
		RDFdata.put("end-day",tweet.getEnd_day());
		RDFdata.put("end-time",tweet.getEnd_time());
		RDFdata.put("end-date",tweet.getEnd_date());
		RDFdata.put("end-month",tweet.getEnd_month());
		RDFdata.put("end-year",tweet.getEnd_year());
		RDFdata.put(KEY_END_TIME,tweet.getEndsAtDateTime());
		
		RDFdata.put("service-status",tweet.getService_status());
		
		RDFdata.put("bus-services-directions",tweet.getBus_services_directions());
		RDFdata.put(KEY_DELAY_LENGTH,tweet.getDelayLength());

		/*RDFdata.put("event",tweet.getEvent());
		RDFdata.put("bus-services",tweet.getBus_services());
		RDFdata.put("problem",tweet.getProblem());
		RDFdata.put("primary-location",tweet.getLocation());
		RDFdata.put("diversion-roads",tweet.getDiversion_road());
		RDFdata.put("duration",tweet.getDuration());
		RDFdata.put("start-day",tweet.getStart_day());
		RDFdata.put("start-time",tweet.getStart_time());
		RDFdata.put("start-date",tweet.getStart_date());
		RDFdata.put("start-month",tweet.getStart_month());
		RDFdata.put("start-year",tweet.getStart_year());
		RDFdata.put("end-day",tweet.getEnd_day());
		RDFdata.put("end-time",tweet.getEnd_time());
		RDFdata.put("end-date",tweet.getEnd_date());
		RDFdata.put("end-month",tweet.getEnd_month());
		RDFdata.put("end-year",tweet.getEnd_year());
		RDFdata.put("service-status",tweet.getService_status());
		
		RDFdata.put("bus-services-directions",tweet.getBus_services_directions());
		RDFdata.put("delay-size",tweet.getDelay_size());*/
		
		return RDFdata;
	}
}
