package com.modeln.build.ant.report;

import java.util.Vector;

public class ReportTreeNode {
	
	private String name;
	
	private Vector<ReportParseEvent> events;
	
	private Vector<ReportTreeNode> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<ReportParseEvent> getEvents() {
		return events;
	}

	public void setEvents(Vector<ReportParseEvent> events) {
		this.events = events;
	}

	public Vector<ReportTreeNode> getChildren() {
		return children;
	}

	public void setChildren(Vector<ReportTreeNode> children) {
		this.children = children;
	}
}
