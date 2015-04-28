package com.twitternlg.nlg;

import java.util.Map;

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

public class DiversionTemplate {
	private Map<String,Object> RDFdata = null;

	private Lexicon lexicon = null;
    private NLGFactory nlgFactory = null;
    private Realiser realiser = null;
    GenericTemplate genericTemplate = null;
	public DiversionTemplate(Map<String,Object> data) {
	
		//initialize core variables
		lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);
		this.RDFdata=data;
		genericTemplate = new GenericTemplate(data,lexicon, nlgFactory,realiser);
	}
	/*
	 * <route> is being diverted (into | along) <location> and then (right along | into) < road >
	 */
	private SPhraseSpec generateDiversionTweetTemplate1(Map<String,Object>RDFdata){
		
	    SPhraseSpec tweet = nlgFactory.createClause();
	    
	    //add bus phrase
	    String bus_services_string = (String)RDFdata.get("bus-services");
	    CoordinatedPhraseElement buses = genericTemplate.generateBusServicesPhrase(bus_services_string);

	    tweet.setSubject(buses);
	    
	    VPPhraseSpec verb_phrase = nlgFactory.createVerbPhrase("is");
	    tweet.setVerbPhrase(verb_phrase);
	    tweet.setObject("diverted");
	    
	    Tense tense = (Tense)genericTemplate.determineClauseTense(RDFdata);
	   
	    tweet.setFeature(Feature.TENSE, tense);
	   
	    if(genericTemplate.generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"into")!=null)
	    	 tweet.addComplement(genericTemplate.generatePrimaryLocationPhrase(RDFdata.get("primary-location").toString(),"into"));		    
	   		    
	    if(genericTemplate.generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"and then into")!=null)
	    	 tweet.addComplement(genericTemplate.generateDiversionRoadsPhrase(RDFdata.get("diversion-roads").toString(),"and then into"));		    

	    
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
	
}
