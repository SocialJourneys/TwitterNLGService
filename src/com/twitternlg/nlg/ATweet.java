package com.twitternlg.nlg;

public class ATweet {
	private String event;
	private String bus_services;
	private String problem;
	private String location;
	private String diversion_road;
	private String duration;
	private String date_start;
	private String date_end;
	private String service_status;
	
	public void print(){
		System.out.println("event: "+ event);
		System.out.println("bus_services: "+ bus_services);
		System.out.println("problem: "+ problem);
		System.out.println("location: "+ location);
		System.out.println("diversion_road: "+ diversion_road);
		System.out.println("duration: "+ duration);
		System.out.println("date_start: "+ date_start);
		System.out.println("date_end: "+ date_end);
		System.out.println("service_status: "+ service_status);
	}

	public String getEvent(){
		return this.event;
	}
}

