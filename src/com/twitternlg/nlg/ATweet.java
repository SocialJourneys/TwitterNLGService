/*
 * Annotated Tweet class
 */

package com.twitternlg.nlg;

import java.io.PrintWriter;


public class ATweet {
	private String type;
	private String service;
	private String hasFactor;
	private String primaryLocation;
	private String place;
	private String duration;
	private String start_day;
	private String start_time;
	private String start_date;
	private String start_month;
	private String start_year;
	private String end_day;
	private String end_time;
	private String end_date;
	private String end_month;
	private String end_year;
	private String service_status;
	private String startsAtDateTime;
	private String endsAtDateTime;
	
	private String bus_services_directions;
	private String delayLength;

	//private String diversion_secondary_locations;
	
	public void print(PrintWriter out){
		out.println("event: "+ type);
		out.println("bus_services: "+ service);
		out.println("problem: "+ hasFactor);
		out.println("primaryLocation: "+ primaryLocation);
		out.println("diversion_roads: "+ place);
		out.println("duration: "+ duration);
		out.println("start_day: "+ start_day);
		out.println("start_time: "+ start_time);
		out.println("start_date: "+ start_date);
		out.println("start_month: "+ start_month);
		out.println("start_year: "+ start_year);
		out.println("end_day: "+ end_day);
		out.println("end_time: "+ end_time);
		out.println("end_date: "+ end_date);
		out.println("end_month: "+ end_month);
		out.println("end_year: "+ end_year);
		out.println("service_status: "+ service_status);
		out.println("bus_services_directions: "+ bus_services_directions);
		out.println("delay_size: "+ delayLength);

	}

	public String getType(){
		return this.type;
	}
	public String getDuration(){
		return this.duration;
	}
	public String getStart_day(){
		return this.start_day;
	}
	public String getStart_time(){
		return this.start_time;
	}
	public String getStart_date(){
		return this.start_date;
	}
	public String getStart_month(){
		return this.start_month;
	}
	public String getStart_year(){
		return this.start_year;
	}

	public String getEnd_day(){
		return this.end_day;
	}
	public String getEnd_time(){
		return this.end_time;
	}
	public String getEnd_date(){
		return this.end_date;
	}
	public String getEnd_month(){
		return this.end_month;
	}
	public String getEnd_year(){
		return this.end_year;
	}
	public String getService_status(){
		return this.service_status;
	}
	
	public String getBus_services_directions(){
		return this.bus_services_directions;
	}
	
	public String getStartsAtDateTime() {
		return startsAtDateTime;
	}

	public String getEndsAtDateTime() {
		return endsAtDateTime;
	}

	public String getService() {
		return service;
	}

	public String getHasFactor() {
		return hasFactor;
	}

	public String getPrimaryLocation() {
		return primaryLocation;
	}

	public String getPlace() {
		return place;
	}

	public String getDelayLength() {
		return delayLength;
	}
}

