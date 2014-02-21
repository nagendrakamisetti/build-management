package com.modeln.build.entity.test.ut;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.build.Build;

/**
 * CREATE TABLE unittest_suite (
 *     build_id         INT UNSIGNED    NOT NULL REFERENCES build(build_id),
 *     suite_id         INT UNSIGNED    NOT NULL auto_increment,
 *     suite_group_id   BIGINT(20) UNSIGNED    NOT NULL DEFAULT 0,
 *     suite_group_name VARCHAR(127),
 *     suite_name       VARCHAR(127)    NOT NULL,
 *     test_count       INT UNSIGNED    NOT NULL DEFAULT 0,
 *     suite_options    VARCHAR(255),
 *     start_date       DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date         DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     username         VARCHAR(127)    NOT NULL,
 *     hostname         VARCHAR(127)    NOT NULL,
 *     jdk_version      VARCHAR(127)    NOT NULL,
 *     jdk_vendor       VARCHAR(127)    NOT NULL,
 *     os_name          VARCHAR(127)    NOT NULL,
 *     os_arch          VARCHAR(127)    NOT NULL,
 *     os_version       VARCHAR(127)    NOT NULL,
 *     jdbc_url         VARCHAR(127),
 *     env_name         VARCHAR(127),
 *     max_threads      INT UNSIGNED    NOT NULL DEFAULT 1,
 *     failure_src      TEXT,
 *     INDEX build_idx(build_id),
 *     INDEX suite_idx(suite_name),
 *     INDEX host_idx(hostname, username),
 *     INDEX jdk_idx(jdk_vendor, jdk_version),
 *     INDEX os_idx(os_name, os_arch, os_version),
 *     INDEX env_idx(env_name),
 *     PRIMARY KEY (suite_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `unittest_suite` (
 *   `build_id` int(11) NOT NULL default '0',
 *   `suite_id` int(11) NOT NULL auto_increment,
 *   `suite_name` varchar(127) NOT NULL default '',
 *   `suite_group_id` bigint(20) unsigned NOT NULL default '0',
 *   `suite_group_name` varchar(127) default NULL,
 *   `suite_options` varchar(255) default NULL,
 *   `test_count` int(11) NOT NULL default '0',
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `username` varchar(127) NOT NULL default '',
 *   `hostname` varchar(127) NOT NULL default '',
 *   `jdk_version` varchar(127) NOT NULL default '',
 *   `jdk_vendor` varchar(127) NOT NULL default '',
 *   `os_name` varchar(127) NOT NULL default '',
 *   `os_arch` varchar(127) NOT NULL default '',
 *   `os_version` varchar(127) NOT NULL default '',
 *   `jdbc_url` varchar(127) default NULL,
 *   `env_name` varchar(127) default NULL,
 *   `max_threads` int(10) unsigned NOT NULL default '1',
 *   `failure_src` text,
 *   PRIMARY KEY  (`suite_id`),
 *   KEY `suite_idx` (`suite_name`),
 *   KEY `host_idx` (`hostname`),
 *   KEY `jdk_idx` (`jdk_vendor`,`jdk_version`),
 *   KEY `os_idx` (`os_name`,`os_arch`,`os_version`),
 *   KEY `build_id` (`build_id`),
 *   KEY `suite_group_id` (`suite_group_id`),
 *   KEY `suite_group_name` (`suite_group_name`),
 *   KEY `env_name` (`env_name`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */

@Entity
@Table(name = "unittest_suite", indexes = { @Index(name="unittest_suite_build_idx", columnList="build_id"),
		@Index(name="unittest_suite_suite_idx", columnList="suite_name"),
		@Index(name="unittest_suite_host_idx", columnList="hostname, username"),
		@Index(name="unittest_suite_jdk_idx", columnList="jdk_vendor, jdk_version"),
		@Index(name="unittest_suite_os_idx", columnList="os_name, os_arch, os_version"),
		@Index(name="unittest_suite_env_idx", columnList="env_name")})
@Access(AccessType.FIELD)
public class UnitTestSuite {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="suite_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="build_id")
//	@NotNull
	private Build build;
	
	@Column(name="suite_group_id")
	@NotNull
	private Long groupId;
	
	@Column(name="suite_group_name")
	private String groupName;
	
	@Column(name="suite_name")
	@NotNull
	private String name;
	
	@Column(name="test_count")
	@NotNull
	private Integer testCount;
	
	@Column(name="suite_options")
	private String suiteOptions;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
//	@NotNull
	private Date endDate;
	
	@Column(name="username")
	@NotNull
	private String username;
	
	@Column(name="hostname")
	@NotNull
	private String hostname;
	
	@Column(name="jdk_version")
	@NotNull
	private String jdkVersion;
	
	@Column(name="jdk_vendor")
	@NotNull
	private String jdkVendor;
	
	@Column(name="os_name")
	@NotNull
	private String osName;
	
	@Column(name="os_arch")
	@NotNull
	private String osArch;
	
	@Column(name="os_version")
	@NotNull
	private String osVersion;
	
	@Column(name="jdbc_url")
	private String jdbcUrl;
	
	@Column(name="env_name")
	private String envName;
	
	@Column(name="max_threads")
	@NotNull
	private Integer maxThreads;
	
	@Column(name="failure_src")
	@Lob
	private String failureSrc;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTestCount() {
		return testCount;
	}

	public void setTestCount(Integer testCount) {
		this.testCount = testCount;
	}

	public String getSuiteOptions() {
		return suiteOptions;
	}

	public void setSuiteOptions(String suiteOptions) {
		this.suiteOptions = suiteOptions;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getFailureSrc() {
		return failureSrc;
	}

	public void setFailureSrc(String failureSrc) {
		this.failureSrc = failureSrc;
	}
	 
	
}
