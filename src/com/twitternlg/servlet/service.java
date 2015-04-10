package com.twitternlg.servlet;

import com.google.gson.Gson;
import com.twitternlg.nlg.ATweet;
import com.twitternlg.nlg.TweetFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
		PrintWriter out = response.getWriter();
		
    	
    	String event = request.getParameter("event");
    	String bus_services = request.getParameter("bus_services");
    	String problem = request.getParameter("problem");
    	String location = request.getParameter("location");     
    	String diversion_road = request.getParameter("diversion_road"); 
    	//3)Services 1/2 / X40 from BOD are being diverted along East North St, Commerce St, Virginia St, Guild St and Bridge St.
    	String duration = request.getParameter("duration");
    	String date_start = request.getParameter("date_start");
    	String date_end = request.getParameter("date_end");
    	String service_status = request.getParameter("service_status");
    	
    	//out.println(t.generateTweetString(null));

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
	 
	   /* out.println(tweet.getEvent());
	    out.println(tweet.getBus_services());*/
	    
    	TweetFactory t = new TweetFactory();
		Map<String,Object> RDFdata = new HashMap<String,Object>();
		RDFdata.put(, value)
    	t.generateTweetString(RDFdata);

	}

	private Map<String,Object> prepareRDF(){
		return null;
	}
}
