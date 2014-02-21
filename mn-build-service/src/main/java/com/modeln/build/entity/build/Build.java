package com.modeln.build.entity.build;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.build.Status;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;
import com.modeln.build.validation.constraints.EnumSet;
import com.modeln.build.validation.groups.BuildStatus;

/**
 * This entity represents a build and its status.
 *  
 * A build is uniquely identified by the build_version column (even if the build_version column is not an primary key column).
 * A build entry corresponds to a specific source code version that has been picked to be used for continuous integration processing.
 * It's important to understand that a build version can be deployed on multiple server in order to execute multiple continuous integration tasks. 
 * It can be one or many continuous integration tasks (running test, running code coverage tools, etc...) executed on one or many app servers on one or many servers machines.
 * This ability to split the continuous integration processing across multiple servers, systems and environments increase performances and the number of continuous build integration cycles in a given period of time. 
 * 
 * This class represents both the build and the build_status tables.
 * The build_status table has been mapped as a secondary table (@SecondaryTables) to the build table since both table id are the same. 
 * The build.build_id defined the build_status.build_id column value.
 * The build_id for the build_status table is not mapped with auto_increment.
 * The build_status.build_id column is directly related to the build_id column from the build table since build_status.build_id is a reference of build.build_id (one to one relationship)
 * 
 * 
 * CREATE TABLE IF NOT EXISTS `build` (
 *   `build_id` int(11) NOT NULL auto_increment,
 *   `build_version` varchar(127) NOT NULL default '',
 *   `download_uri` varchar(255) default NULL,
 *   `username` varchar(127) NOT NULL default '',
 *   `hostname` varchar(127) NOT NULL default '',
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `version_ctrl_type` enum('perforce','git') NOT NULL default 'perforce',
 *   `version_ctrl_id` varchar(127) default NULL,
 *   `version_ctrl_root` varchar(255) default NULL,
 *   `comments` text,
 *   `jdk_version` varchar(127) NOT NULL default '',
 *   `jdk_vendor` varchar(127) NOT NULL default '',
 *   `os_name` varchar(127) NOT NULL default '',
 *   `os_arch` varchar(127) NOT NULL default '',
 *   `os_version` varchar(127) NOT NULL default '',
 *   `build_status` varchar(127) NOT NULL default '',
 *   `key_algorithm` varchar(127) default NULL,
 *   `ver_public_key` text,
 *   `ver_private_key` text,
 *   `job_url` varchar(255) default NULL,
 *   PRIMARY KEY  (`build_id`),
 *   KEY `version_idx` (`build_version`),
 *   KEY `host_idx` (`hostname`),
 *   KEY `jdk_idx` (`jdk_vendor`,`jdk_version`),
 *   KEY `os_idx` (`os_name`,`os_arch`,`os_version`),
 *   KEY `build_status` (`build_status`)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `build_status` (
 *   `build_id` int(10) unsigned NOT NULL default '0',
 *   `user_id` int(10) unsigned NOT NULL default '0',
 *   `status` set('passing','verified','stable','tested','released') NOT NULL default 'passing',
 *   `support` enum('active','inactive','extended') NOT NULL,
 *   `comments` text,
 *   PRIMARY KEY  (`build_id`),
 *   KEY `user_idx` (`user_id`),
 *   KEY `status_idx` (`status`)
 * );
 * 
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - buildStatus is mapped to enum com.modeln.build.enums.build.Status.
 * - buidSupport is mapped to enum com.modeln.build.enums.build.SupportStatus.
 * - versionControlType is mapped to enum com.modeln.build.enums.build.SourceVersionControlSystem.
 * 
 * end_date not null in entity since some values are null (0000-00-00 00:00:00 in database).
 * user_id is null default 0 which correspond to nothing in database.
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Write migrations so we can do the mapping with emums instead of String fields.
 * - Write migrations to get rid of the user_id column which is not used.
 * - Enforce unique contraint on the build_version column.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "build", indexes = { 
		@Index(name="build_version_idx", columnList="build_version"),
		@Index(name="build_job_idx", columnList="job_url"),
		@Index(name="build_build_status_idx", columnList="build_status"),
		@Index(name="build_host_idx", columnList="hostname, username"),
		@Index(name="build_jdk_idx", columnList="jdk_vendor, jdk_version"),
		@Index(name="build_os_idx", columnList="os_name, os_arch, os_version")})
@SecondaryTables({
	@SecondaryTable(name="build_status", 
			indexes = { @Index(name="build_status_user_idx", columnList="user_id"),
						@Index(name="build_status_status_idx", columnList="status")})
})
@Access(AccessType.FIELD)
public class Build {
	/** Unique key value used to identify a build */
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="build_id")
    private Integer id;
	
	/** Version string which identifies the build */
	@Column(name="build_version")
	@NotNull
    private String version;
	
	/** Status of the build */
	@Column(name="build_status")
	@NotNull
    private String status;
	
	/** URI which stores the build */
	@Column(name="download_uri")
    private String downloadUri;
	
	/** URL of the Jenkins job used to invoke the build */
	@Column(name="job_url")
    private String jobUrl;

	/** Username of the build account on the host */
	@Column(name="username")
	@NotNull
    private String username;

    /** Name of the build host computer */
	@Column(name="hostname")
    @NotNull
    private String hostname;
    
    /** Record the starting time of the test suite */
	@Column(name="start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startTime;

    /** Record the completion time of the test suite */
	@Column(name="end_date")
    @Temporal(TemporalType.TIMESTAMP)
//    @NotNull
    private Date endTime;

	/** Provides a version control ID which can be used to obtain the build source from version control */
	@Column(name="version_ctrl_id")
    private String versionControlId;
    
    /** Specifies the type of version control system used */
	@Column(name="version_ctrl_type")
//	@Enumerated(EnumType.STRING)
	@NotNull
    private String versionControlType;

    /** Provides a version control path which can be used to obtain the build source from version control */
	@Column(name="version_ctrl_root")
    private String versionControlRoot;
    
    /** User generated comments about the build */
	@Column(name="comments")
	@Lob
    private String comments;

    /** Version of the JDK used for the build */
	@Column(name="jdk_version")
	@NotNull
    private String jdkVersion;

    /** Distributor of the JDK implementation */
	@Column(name="jdk_vendor")
	@NotNull
    private String jdkVendor;

    /** Name of the build operating system */
	@Column(name="os_name")
	@NotNull
    private String osName;

    /** CPU architecture of the build operating system */
	@Column(name="os_arch")
	@NotNull
    private String osArch;

    /** Version of the build operating system */
	@Column(name="os_version")
	@NotNull
    private String osVersion;
	
	@Column(name="key_algorithm")
	private String keyAlgorithm;
	
	@Column(name="ver_public_key")
	@Lob
	private String verPublicKey;
	
	@Column(name="ver_private_key")
	@Lob
	private String verPrivateKey;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", table="build_status")
//	@NotNull
	private User user;
	
	@Column(name="status", table="build_status")
//	@Enumerated
	@NotNull(groups=BuildStatus.class)
	@EnumSet(value=Status.class, groups=BuildStatus.class)
	private String buildStatus = Status.PASSING.getName().toLowerCase();
	
	@Column(name="support", table="build_status")
//	@Enumerated
	@NotNull(groups=BuildStatus.class)
	private String supportStatus;
	
	@Column(name="comments", table="build_status")
	@Lob
    private String buildComments;
		
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDownloadUri() {
		return downloadUri;
	}

	public void setDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
	}

	public String getJobUrl() {
		return jobUrl;
	}

	public void setJobUrl(String jobUrl) {
		this.jobUrl = jobUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getVersionControlId() {
		return versionControlId;
	}

	public void setVersionControlId(String versionControlId) {
		this.versionControlId = versionControlId;
	}

	public SourceVersionControlSystem getVersionControlType() {
		return SourceVersionControlSystem.valueOf(versionControlType.toUpperCase());
	}

	public void setVersionControlType(SourceVersionControlSystem sourceVersionControlSystem) {
		this.versionControlType = sourceVersionControlSystem.getName().toLowerCase();
	}

	public String getVersionControlRoot() {
		return versionControlRoot;
	}

	public void setVersionControlRoot(String versionControlRoot) {
		this.versionControlRoot = versionControlRoot;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getJdkVersion() {
		return jdkVersion;
	}

	public void setJdkVersion(String jdkVersion) {
		this.jdkVersion = jdkVersion;
	}

	public String getJdkVendor() {
		return jdkVendor;
	}

	public void setJdkVendor(String jdkVendor) {
		this.jdkVendor = jdkVendor;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsArch() {
		return osArch;
	}

	public void setOsArch(String osArch) {
		this.osArch = osArch;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Status> getBuildStatus() {
		Set<Status> result = new HashSet<Status>();
		if(buildStatus == null){
			return result;
		}
		String[] buildStatusSplit = buildStatus.split(",");
		for(String currentEnum : buildStatusSplit){
			Status currentPerm = Status.valueOf(currentEnum.trim().toUpperCase());
			result.add(currentPerm);
		}
		return result;
	}

	public void setBuildStatus(Set<Status> buildStatus) {
		String result = "";
		if(buildStatus == null){
			this.buildStatus = null;
		}
		int i = 0;
		for(Iterator<Status> it = buildStatus.iterator(); it.hasNext(); i++){
			if(i != 0){
				result += ",";
			}
			result += ((Status)it.next()).getName().toLowerCase();
		}
		this.buildStatus = result;
	}

	public SupportStatus getSupportStatus() {
		return SupportStatus.valueOf(supportStatus.toUpperCase());
	}

	public void setSupportStatus(SupportStatus supportStatus) {
		this.supportStatus = supportStatus.getName().toLowerCase();
	}

	public String getBuildComments() {
		return buildComments;
	}

	public void setBuildComments(String buildComments) {
		this.buildComments = buildComments;
	}

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getVerPublicKey() {
		return verPublicKey;
	}

	public void setVerPublicKey(String verPublicKey) {
		this.verPublicKey = verPublicKey;
	}

	public String getVerPrivateKey() {
		return verPrivateKey;
	}

	public void setVerPrivateKey(String verPrivateKey) {
		this.verPrivateKey = verPrivateKey;
	}

//	public List<BuildNote> getNotes() {
//		return notes;
//	}
//
//	public void setNotes(List<BuildNote> notes) {
//		this.notes = notes;
//	}
//
//	public List<BuildEvent> getEvents() {
//		return events;
//	}
//
//	public void setEvents(List<BuildEvent> events) {
//		this.events = events;
//	}
	
	

}
