package com.twitternlg.servlet;

import com.google.gson.Gson;
import com.twitternlg.nlg.ATweet;
import com.twitternlg.nlg.TweetFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simplenlg.phrasespec.NPPhraseSpec;


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
		
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();

	    ATweet tweet = gson.fromJson(reader, ATweet.class);

	    response.setContentType("application/json");
	 // Get the printwriter object from response to write the required json object to the output stream      
	 
/*	    out.println(tweet.getEvent());
	    out.println(tweet.getDiversion_road());
	  
	    tweet.print(out);*/
    	TweetFactory t = new TweetFactory();
    	Map<String,Object> RDFdata = prepareInput(tweet);
    	/*String message = t.generateTweetString(RDFdata);
    	
    	out.println(message);*/
    	
    	ArrayList <String>tweets = t.generateTweets(RDFdata);
    	out.println(tweets.get(0));
/*	    for(String objTweet: tweets){
	    	out.println(objTweet);

	    }	*/
	}

	//todo: remove this datatype conversion
	private Map<String,Object> prepareInput(ATweet tweet){
		Map<String,Object> RDFdata = new HashMap<String,Object>();
		
		RDFdata.put("event",tweet.getEvent());
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
		RDFdata.put("delay-size",tweet.getDelay_size());

		return RDFdata;
	}
}
