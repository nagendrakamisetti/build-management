package com.modeln.build.ant.batam.typedef;

import java.util.ArrayList;
import java.util.List;

public class Infos {
	private List<PairType> infos = new ArrayList<PairType>();
	
	public void add(PairType info){
		infos.add(info);
	}

	public List<PairType> get() {
		return infos;
	}	
	
}
