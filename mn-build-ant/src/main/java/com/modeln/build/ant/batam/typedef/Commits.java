package com.modeln.build.ant.batam.typedef;

import java.util.ArrayList;
import java.util.List;

public class Commits {
	private List<CommitType> commits = new ArrayList<CommitType>();
	
	public void add(CommitType commit){
		commits.add(commit);
	}

	public List<CommitType> get() {
		return commits;
	}
	
	
}
