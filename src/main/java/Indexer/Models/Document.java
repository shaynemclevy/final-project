package Indexer.Models;

import java.util.List;

//The processed document
public class Document {
	//Article NEWID
	Integer id;
	//Tokenized body of the article
	List<String> tokens;
	
	public Document(Integer id, List<String> tokens) {
		super();
		this.id = id;
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

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
}