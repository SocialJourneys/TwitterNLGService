package com.twitternlg.servlet;

import com.twitternlg.nlg.ATweet;
import com.twitternlg.nlg.TweetFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;


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
		
    	TweetFactory t = new TweetFactory();
    	
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
		 
	    // 1. get received JSON data from request
	    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
	    String json = "";
	    if(br != null){
	        json = br.readLine();
	    }

	    // 2. initiate jackson mapper
	    ObjectMapper mapper = new ObjectMapper();

	    // 3. Convert received JSON to Article
	    ATweet tweet = mapper.readValue(json, ATweet.class);

	    // 4. Set response type to JSON
	    response.setContentType("application/json");            

		PrintWriter out = response.getWriter();
    	
		out.println(tweet.getEvent());

	    // 6. Send List<Article> as JSON to client
	   // mapper.writeValue(response.getOutputStream(), articles);
	}

}
