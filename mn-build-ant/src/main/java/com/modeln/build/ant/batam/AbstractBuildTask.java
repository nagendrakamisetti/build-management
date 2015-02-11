package com.modeln.build.ant.batam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.Build;
import com.modeln.batam.connector.wrapper.Commit;
import com.modeln.batam.connector.wrapper.Pair;
import com.modeln.batam.connector.wrapper.Step;
import com.modeln.build.ant.batam.typedef.CommitType;
import com.modeln.build.ant.batam.typedef.Commits;
import com.modeln.build.ant.batam.typedef.Criterias;
import com.modeln.build.ant.batam.typedef.Infos;
import com.modeln.build.ant.batam.typedef.PairType;
import com.modeln.build.ant.batam.typedef.Reports;
import com.modeln.build.ant.batam.typedef.StepType;
import com.modeln.build.ant.batam.typedef.Steps;

public abstract class AbstractBuildTask extends AbstractBatamTask {

	private String id;
	
	private String name;
	
	private String startDate; 
	
	private String endDate;
	
	private String dateFormat;
	
	private String status;
	
	private String description;
	
	private List<Criterias> criterias = new ArrayList<Criterias>();
	
	private List<Infos> infos = new ArrayList<Infos>();
	
	private List<Reports> reports = new ArrayList<Reports>();
	
	private List<Steps> steps = new ArrayList<Steps>(); 
	
	private List<Commits> commits = new ArrayList<Commits>();
	
	public void add(Criterias criteria){
		criterias.add(criteria);
	}
	
	public void add(Infos info){
		infos.add(info);
	}
	
	public void add(Reports report){
		reports.add(report);
	}
	
	public void add(Steps step){
		steps.add(step);
	}
	
	public void add(Commits commit){
		commits.add(commit);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void execute(){
		//Check params.
		checkUnaryList(criterias);
		checkUnaryList(infos);
		checkUnaryList(reports);
		checkUnaryList(steps);
		checkUnaryList(commits);
		
		//Build build object.
		Build build = new Build();
		build.setId(id);
		build.setName(name);
		build.setDescription(description);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat == null? DEFAULT_DATE_FORMAT: dateFormat);
		try {
			build.setStartDate(formatter.parse(startDate));
			build.setEndDate(formatter.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		build.setStatus(status);
		//Add criterias to build.
		List<Pair> buildCriterias = new ArrayList<Pair>();
		if(!criterias.isEmpty()){
			for(int i = 0; i < criterias.get(0).get().size(); i++){
				PairType criteria = criterias.get(0).get().get(i);
				buildCriterias.add(new Pair(criteria.getName(), criteria.getValue()));
			}
		}
		build.setCriterias(buildCriterias);
		//Add infos to build.
		List<Pair> buildInfos = new ArrayList<Pair>();
		if(!infos.isEmpty()){
			for(int i = 0; i < infos.get(0).get().size(); i++){
				PairType info = infos.get(0).get().get(i);
				buildInfos.add(new Pair(info.getName(), info.getValue()));
			}
		}
		build.setInfos(buildInfos);
		//Add reports to build.
		List<Pair> buildReports = new ArrayList<Pair>();
		if(!reports.isEmpty()){
			for(int i = 0; i < reports.get(0).get().size(); i++){
				PairType info = reports.get(0).get().get(i);
				buildReports.add(new Pair(info.getName(), info.getValue()));
			}
		}
		build.setReports(buildReports);
		//Add steps to build.
		List<Step> buildSteps = new ArrayList<Step>();
		if(!steps.isEmpty()){
			for(int i = 0; i < steps.get(0).get().size(); i++){
				StepType step = steps.get(0).get().get(i);
				try {
					buildSteps.add(new Step(step.getName(), formatter.parse(step.getStartDate()), formatter.parse(step.getEndDate())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		build.setSteps(buildSteps);
		//Add commits to build.
		List<Commit> buildCommits = new ArrayList<Commit>();
		if(!commits.isEmpty()){
			for(int i = 0; i < commits.get(0).get().size(); i++){
				CommitType commit = commits.get(0).get().get(i);
				try {
					buildCommits.add(new Commit(commit.getBuildId(), commit.getBuildName(), commit.getCommitId(), commit.getUrl(), commit.getAuthor(), formatter.parse(commit.getDateCommitted())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		build.setCommits(buildCommits);
		
		SimplePublisher publisher = SimplePublisher.getInstance();

		try {
			publisher.beginConnection(getHost(), getUsername(), getPassword(), getPort(), getVhost(), getQueue(), getMode());
			
			operation(publisher, build);
			
			publisher.endConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
