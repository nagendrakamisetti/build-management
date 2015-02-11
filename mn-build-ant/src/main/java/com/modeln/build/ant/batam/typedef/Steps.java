package com.modeln.build.ant.batam.typedef;

import java.util.ArrayList;
import java.util.List;

public class Steps {
	private List<StepType> steps = new ArrayList<StepType>();
	
	public void add(StepType step){
		steps.add(step);
	}

	public List<StepType> get() {
		return steps;
	}
	
	
	
}
