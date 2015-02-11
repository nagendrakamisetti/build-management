package com.modeln.build.ant.batam;

import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.Task;

import com.modeln.batam.connector.SimplePublisher;

public abstract class AbstractBatamTask extends Task {
	protected static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private String host;
	
	private String username; 
	
	private String password;
	
	private Integer port;
	
	private String vhost;
	
	private String queue; 
	
	private String mode;
	
	private SimplePublisher connector = SimplePublisher.getInstance();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public SimplePublisher getConnector() {
		return connector;
	}

	public void setConnector(SimplePublisher connector) {
		this.connector = connector;
	}
	
	protected void checkUnaryList(List<?> list){
		if(list != null && !list.isEmpty()){
			if(list.size() != 1){
				throw new RuntimeException("task has too many nested elements.");
			}
		}
	}
	
	public void beginConnection(){
		try {
			connector.beginConnection(host, username, password, port, vhost, queue, mode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void endConnection(){
		try {
			connector.endConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected abstract void operation(SimplePublisher connector, Object object);
	
}
