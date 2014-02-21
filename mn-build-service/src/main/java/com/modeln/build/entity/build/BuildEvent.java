package com.modeln.build.entity.build;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;
import javax.validation.constraints.NotNull;

import com.modeln.build.enums.build.EventLevel;


 /** 
  * This entity represents build event. Basically it collects build executions logs from the build system.
  * These logs are parsed and linked to event criteria that help understanding the state of the build execution tasks.
  * 
  * It is important to note that this table is frequently cleaned by CRON jobs. 
  * Also, the parsing step that links event entries with event criteria is done outside of the system by ant scripts.
  * 
  * The buildEvent entity only collect information from steps that are executed in serial (across one or multiple servers). 
  * For parallel execution, events are logged in the deploy_event table since the application need to be deployed on multiple servers.
  * 
  * Depending on your build executions, you can choose which step should be done in serial or in parallel.
  * A build execution is usually composed of multiple steps: build source code, data population in the database, javadoc generation, test execution and so on.
  * Those actions can occur on one or multiple server and in serial or in parallel.
  * For example, the source code checkout from the central source code repository and the source code compilation and packaging is usually required by all subsequent actions and can be done only by one server.
  * All other actions are optional, but they all depend on the compiled/packaging steps to be available (which is why we use the build_version as a unique identifier of a build execution) 
  * Here's an example:
  * 1 - server1:  checkout, compile and package source code and push the packaged app to some central ftp.
  * 2 - server1:  generate javadoc and push doc to ftp server.
  * 3 - server2:  populate database schemas and create a database dump file and push files to ftp server.
  * 4 - server3:  Deploy the application build, create database instance and import dump file to 2 application server instances.
  * 5 - Then we run test, reporting tools in parallel on both application server instances.
  * So in that example, step 1 to 4 (usual before deployment steps) are done in serial on multiple servers. These steps can also be done on one server. 
  * Step 5 (Step coming after the deployment phase are usually done in parallel for performance reason even if they can be done on one server as well) are not logged into this build_event table but in the deploy_event table.
  * 
  * This class represents the build_event table.
  * 
  * CREATE TABLE IF NOT EXISTS `build_event` (
  *   `event_id` int(10) unsigned NOT NULL auto_increment,
  *   `event_level` enum('debug','verbose','information','warning','error') NOT NULL default 'debug',
  *   `criteria_id` int(10) unsigned default '0',
  *   `build_version` varchar(127) default NULL,
  *   `event_stack` text,
  *   `event_date` datetime NOT NULL default '0000-00-00 00:00:00',
  *   `event_message` text,
  *   PRIMARY KEY  (`event_id`),
  *   KEY `criteria_idx` (`criteria_id`),
  *   KEY `event_level` (`event_level`),
  *   KEY `build_idx` (`build_version`),
  *   KEY `event_date` (`event_date`)
  * );
  * 
  * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
  * - level is mapped to enum com.modeln.build.enums.build.EventLevel.
  * 
  * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
  * 
  * This build_version is of String type in the Java class since build.build_version can't be used as FK since values are not unique. Moreover build_version is not the build table primary key.
  * 
  * TODO
  * - Write migrations so we can do the mapping with emums instead of String fields.
  * - Enhance the app so that the import, parsing and cleaning steps are done by the service (batch processing) rather than external script. The database should only be directly used by this service.
  * - Migrate/enhance the database/system so that the build_id get used instead of the build_version column.
  * 
  * @author gzussa
  * 
  **/

@Entity
@Table(name="build_event", indexes={@Index(name="build_event_build_idx", columnList="build_version"),
		@Index(name="build_event_criteria_idx", columnList="criteria_id"),
		@Index(name="build_event_date_idx", columnList="event_date")})
@Access(AccessType.FIELD)
public class BuildEvent {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="event_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="criteria_id")
	@NotNull
	private BuildEventCriteria criteria;
	
	@Column(name="build_version")
	@NotNull
	private String buildVersion;
	
	@Column(name="event_stack")
	@Lob
	private String stack;
	
	@Column(name="event_level")
	@NotNull
	private String level;
	
	@Column(name="event_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date date;
	
	@Column(name="event_message")
	@Lob
	private String message;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BuildEventCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(BuildEventCriteria criteria) {
		this.criteria = criteria;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public EventLevel getLevel() {
		return EventLevel.valueOf(level.toUpperCase());
	}

	public void setLevel(EventLevel level) {
		this.level = level.getName().toLowerCase();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
