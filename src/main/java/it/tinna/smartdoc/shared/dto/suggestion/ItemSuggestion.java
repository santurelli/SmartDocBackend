package it.tinna.smartdoc.shared.dto.suggestion;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ItemSuggestion implements Serializable, Comparable<ItemSuggestion> {
	
	private String htmlString;
	private String plainString;
	
	public ItemSuggestion() {}
	
	public ItemSuggestion(String s) {
		this.plainString = s;
		this.htmlString = s;
	}
	
	@Override
	public int compareTo(ItemSuggestion o) {
		return this.getPlainString().compareTo(o.getPlainString());
	}

	public boolean equals(Object obj) {
		if (obj instanceof ItemSuggestion) {
			return ((ItemSuggestion)obj).getPlainString().toLowerCase().equals(this.getPlainString().toLowerCase());
		}
		return false;
	}

	public String getHtmlString() {
		return htmlString;
	}

	public String getPlainString() {
		return plainString;
	}

	public void setDisplayString(String s) {
		this.htmlString = s;
	}

	public void setHtmlString(String htmlString) {
		this.htmlString = htmlString;
	}

	public void setPlainString(String plainString) {
		this.plainString = plainString;
	}

	public void setReplacementString(String s) {
		this.plainString = s;
	}

}

