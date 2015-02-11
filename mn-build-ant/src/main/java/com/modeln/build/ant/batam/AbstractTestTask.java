package com.modeln.build.ant.batam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.Pair;
import com.modeln.batam.connector.wrapper.TestInstance;
import com.modeln.build.ant.batam.typedef.Criterias;
import com.modeln.build.ant.batam.typedef.PairType;

public abstract class AbstractTestTask extends AbstractBatamTask {
	
	private String reportId; 
	
	private String reportName; 
	
	private String name; 
	
	private String description; 
	
	private String startDate; 
	
	private String endDate;
	
	private String dateFormat;
	
	private String status; 
	
	private String log; 
	
	private List<Criterias> criterias;
	
	@Override
	public void execute(){
		//Check params.
		checkUnaryList(criterias);
		
		//Build test object.
		
		TestInstance test = new TestInstance();
		test.setReportId(reportId);
		test.setReportName(reportName);
		test.setName(name);
		test.setDescription(description);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat == null? DEFAULT_DATE_FORMAT: dateFormat);
		try {
			test.setStartDate(formatter.parse(startDate));
			test.setEndDate(formatter.parse(endDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		test.setStatus(status);
		test.setLog(log);
		//Add criterias to test.
		List<Pair> testCriterias = new ArrayList<Pair>();
		if(!criterias.isEmpty()){
			for(int i = 0; i < criterias.get(0).get().size(); i++){
				PairType criteria = criterias.get(0).get().get(i);
				testCriterias.add(new Pair(criteria.getName(), criteria.getValue()));
			}
		}
		test.setCriterias(testCriterias);
		
		SimplePublisher publisher = SimplePublisher.getInstance();

		try {
			publisher.beginConnection(getHost(), getUsername(), getPassword(), getPort(), getVhost(), getQueue(), getMode());
			
			operation(publisher, test);
			
			publisher.endConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
