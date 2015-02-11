package com.modeln.build.ant.batam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.TestReport;

public abstract class AbstractReportTask extends AbstractBatamTask {
	private String id;
	
	private String buildId; 
	
	private String buildName;
	
	private String name; 
	
	private String description;
	
	private String startDate;
	
	private String endDate;
	
	private String dateFormat;
	
	private String status;
	
	private List<String> logs;
	
	public void add(String log){
		logs.add(log);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public List<String> getLogs() {
		return logs;
	}

	public void setLogs(List<String> logs) {
		this.logs = logs;
	}

	@Override
	public void execute(){
		//Check params.
		checkUnaryList(logs);
		
		//Build report object.
		TestReport report = new TestReport();
		report.setBuildId(buildId);
		report.setBuildName(buildName);
		report.setDescription(description);
		report.setId(id);
		report.setName(name);
		report.setStatus(status);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat == null? DEFAULT_DATE_FORMAT: dateFormat);
		try {
			report.setStartDate(formatter.parse(startDate));
			report.setEndDate(formatter.parse(endDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Add logs to report.
		List<String> reportLogs = new ArrayList<String>();
		if(!logs.isEmpty()){
			for(int i = 0; i < logs.size(); i++){
				String log = logs.get(i);
				reportLogs.add(log);
			}
		}
		report.setLogs(reportLogs);
		
		SimplePublisher publisher = SimplePublisher.getInstance();

		try {
			publisher.beginConnection(getHost(), getUsername(), getPassword(), getPort(), getVhost(), getQueue(), getMode());
			
			operation(publisher, report);
			
			publisher.endConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
