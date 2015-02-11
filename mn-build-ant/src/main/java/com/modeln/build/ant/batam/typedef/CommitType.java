package com.modeln.build.ant.batam.typedef;

public class CommitType {
	
	private String buildId;
	
	private String buildName;
	
	private String commitId;
	
	private String url;
	
	private String author;
	
	private String dateCommitted;

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getBuildName() {
		return buildName;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDateCommitted() {
		return dateCommitted;
	}

	public void setDateCommitted(String dateCommitted) {
		this.dateCommitted = dateCommitted;
	}

}
