package org.home.model;

import java.util.ArrayList;

public class ResultDocument {
	private String version;
	private String date;
	private String title;
	private ArrayList<String> entity;
	private String document;
	private String documentFirstCapture;
	private String documentLastCapture;
	private String documentNumOfCaptures;

	public ResultDocument() {
		super();
	}

	public ResultDocument(String version, String date, String title, ArrayList<String> entity, String document,
			String documentFirstCapture, String documentLastCapture, String documentNumOfCaptures) {
		super();
		this.version = version;
		this.date = date;
		this.title = title;
		this.entity = entity;
		this.document = document;
		this.documentFirstCapture = documentFirstCapture;
		this.documentLastCapture = documentLastCapture;
		this.documentNumOfCaptures = documentNumOfCaptures;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getEntity() {
		return entity;
	}

	public void setEntity(ArrayList<String> entity) {
		this.entity = entity;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getDocumentFirstCapture() {
		return documentFirstCapture;
	}

	public void setDocumentFirstCapture(String documentFirstCapture) {
		this.documentFirstCapture = documentFirstCapture;
	}

	public String getDocumentLastCapture() {
		return documentLastCapture;
	}

	public void setDocumentLastCapture(String documentLastCapture) {
		this.documentLastCapture = documentLastCapture;
	}

	public String getDocumentNumOfCaptures() {
		return documentNumOfCaptures;
	}

	public void setDocumentNumOfCaptures(String documentNumOfCaptures) {
		this.documentNumOfCaptures = documentNumOfCaptures;
	}

	@Override
	public String toString() {
		return "ResultDocument [version=" + version + ", date=" + date + ", title=" + title + ", entity=" + entity
				+ ", document=" + document + ", documentFirstCapture=" + documentFirstCapture + ", documentLastCapture="
				+ documentLastCapture + ", documentNumOfCaptures=" + documentNumOfCaptures + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((documentFirstCapture == null) ? 0 : documentFirstCapture.hashCode());
		result = prime * result + ((documentLastCapture == null) ? 0 : documentLastCapture.hashCode());
		result = prime * result + ((documentNumOfCaptures == null) ? 0 : documentNumOfCaptures.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultDocument other = (ResultDocument) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (documentFirstCapture == null) {
			if (other.documentFirstCapture != null)
				return false;
		} else if (!documentFirstCapture.equals(other.documentFirstCapture))
			return false;
		if (documentLastCapture == null) {
			if (other.documentLastCapture != null)
				return false;
		} else if (!documentLastCapture.equals(other.documentLastCapture))
			return false;
		if (documentNumOfCaptures == null) {
			if (other.documentNumOfCaptures != null)
				return false;
		} else if (!documentNumOfCaptures.equals(other.documentNumOfCaptures))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
