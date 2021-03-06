package Indexer.Models;

import java.util.List;

import Sentiment.SentimentDictionary;

//The processed document
public class Document {
	//Article NEWID
	Integer id;
	String title;
	//Tokenized body of the article
	List<String> tokens;
	
	public Document(Integer id, String title, List<String> tokens) {
		super();
		this.id = id;
		this.title = title;
		this.tokens = tokens;
	}

	public Document() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
	
	public int analyzeSentiment(){	
		int sentimentValue = 0;
		for(String token : tokens){
			SentimentDictionary dict = SentimentDictionary.getInstance();
			sentimentValue += dict.getValue(token);
		}
		return sentimentValue;
	}
}
