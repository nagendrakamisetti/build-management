package com.modeln.build.ant.batam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.Pair;
import com.modeln.batam.connector.wrapper.TestInstance;
import com.modeln.build.ant.batam.typedef.Criteria;
import com.modeln.build.ant.batam.typedef.Criterias;

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
	
	private String override = "false";
	
	private List<Criterias> criterias = new ArrayList<Criterias>();
	
	public Criterias createCriterias() {                              
		Criterias criteria = new Criterias();
		criterias.add(criteria);
        return criteria;
    }
	
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getOverride() {
		return override;
	}

	public void setOverride(String override) {
		this.override = override;
	}

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
		test.setOverride(Boolean.parseBoolean(override));
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat == null? DEFAULT_DATE_FORMAT: dateFormat);
		try {
			if(startDate != null){
				test.setStartDate(formatter.parse(startDate));
			}
			if(endDate != null){
				test.setEndDate(formatter.parse(endDate));
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		test.setStatus(status);
		test.setLog(log);
		//Add criterias to test.
		List<Pair> testCriterias = new ArrayList<Pair>();
		if(!criterias.isEmpty()){
			for(int i = 0; i < criterias.get(0).getCriterias().size(); i++){
				Criteria criteria = criterias.get(0).getCriterias().get(i);
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
