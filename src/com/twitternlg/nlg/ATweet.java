/*
 * Annotated Tweet class
 */

package com.twitternlg.nlg;

import java.io.PrintWriter;

public class ATweet {
	private String event;
	private String bus_services;
	private String problem;
	private String location;
	private String diversion_road;
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
	
	public void print(PrintWriter out){
		out.println("event: "+ event);
		out.println("bus_services: "+ bus_services);
		out.println("problem: "+ problem);
		out.println("location: "+ location);
		out.println("diversion_road: "+ diversion_road);
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
	}

	public String getEvent(){
		return this.event;
	}
	public String getBus_services(){
		return this.bus_services;
	}
	public String getProblem(){
		return this.problem;
	}
	public String getLocation(){
		return this.location;
	}
	public String getDiversion_road(){
		return this.diversion_road;
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
}

