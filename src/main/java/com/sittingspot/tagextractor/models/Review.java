package com.sittingspot.tagextractor.models;

public class Review{

    private String sittingSpotId;

    private String corpus;

    public Review(String sittingSpotId, String corpus){      
        this.sittingSpotId=sittingSpotId;
        this.corpus=corpus;
    }

    public String corpus(){
        return this.corpus;
    }

    public String sittingSpotId(){
        return this.sittingSpotId;
    }

    public void print(){
        System.out.println("sittingSpotId:"+this.sittingSpotId);
        System.out.println("corpus:"+this.corpus);
    }
}