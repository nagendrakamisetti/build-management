package com.modeln.build.ant.batam.typedef;

import java.util.ArrayList;
import java.util.List;

public class Reports {
	private List<PairType> reports = new ArrayList<PairType>();
	
	public void add(PairType report){
		reports.add(report);
	}

	public List<PairType> get() {
		return reports;
	}	
	
}
