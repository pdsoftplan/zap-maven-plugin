package br.com.softplan.security.zap.api.model;

public enum SeleniumDriver {

	HTMLUNIT("HtmlUnit"), 
	FIREFOX("Firefox"), 
	PHANTOMJS("PhantomJS");
	
	private String name;
	
	private SeleniumDriver(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
