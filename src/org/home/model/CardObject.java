package org.home.model;

import java.util.ArrayList;

public class CardObject {

	private static String title;
	private static String description;
	private static ArrayList<String> relatedTerms;
	private static String imgUrl;

	public CardObject() {
		super();
	}

	public static String getTitle() {
		return title;
	}

	public static void setTitle(String title) {
		CardObject.title = title;
	}

	public static String getDescription() {
		return description;
	}

	public static void setDescription(String description) {
		CardObject.description = description;
	}

	public static ArrayList<String> getRelatedTerms() {
		return relatedTerms;
	}

	public static void setRelatedTerms(ArrayList<String> relatedTerms) {
		CardObject.relatedTerms = relatedTerms;
	}

	public static String getImgUrl() {
		return imgUrl;
	}

	public static void setImgUrl(String imgUrl) {
		CardObject.imgUrl = imgUrl;
	}

}
