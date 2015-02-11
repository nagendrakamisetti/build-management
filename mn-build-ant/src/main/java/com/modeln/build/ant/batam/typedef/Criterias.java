package com.modeln.build.ant.batam.typedef;

import java.util.ArrayList;
import java.util.List;

public class Criterias {
	private List<PairType> criterias = new ArrayList<PairType>();
	
	public void add(PairType criteria){
		criterias.add(criteria);
	}

	public List<PairType> get() {
		return criterias;
	}

}
