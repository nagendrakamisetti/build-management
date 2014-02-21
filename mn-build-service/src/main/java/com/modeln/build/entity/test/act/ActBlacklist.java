package com.modeln.build.entity.test.act;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.test.act.id.ActBlacklistId;

/**
 * CREATE TABLE act_blacklist (
 *     filename          VARCHAR(127)    NOT NULL,
 *     timeout           INT UNSIGNED    NOT NULL,
 *     start_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date          DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     version_ctrl_root VARCHAR(255),
 *     hostname          VARCHAR(127),
 *     message           TEXT,
 *     PRIMARY KEY (filename, version_ctrl_root)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `act_blacklist` (
 *   `filename` varchar(127) NOT NULL,
 *   `timeout` int(10) unsigned NOT NULL,
 *   `start_date` datetime NOT NULL,
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `version_ctrl_root` varchar(255) NOT NULL default '',
 *   `hostname` varchar(127) default NULL,
 *   `message` text,
 *   PRIMARY KEY  (`filename`,`version_ctrl_root`)
 * );
 *
 * @author gzussa
 *
 */
@Entity
@Table(name = "act_blacklist")
@Access(AccessType.FIELD)
@IdClass(ActBlacklistId.class)
public class ActBlacklist {
	
	@Id
	@Column(name="filename")
	@NotNull
	private String filename;
	
	@Id
	@Column(name="version_ctrl_root")
	private String versionControlRoot;
	
	@Column(name="timeout")
	@NotNull
	private Integer timeout;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date endDate;
	
	@Column(name="hostname")
	private String hostname;
	
	@Column(name="message")
	@Lob
	private String message;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getVersionControlRoot() {
		return versionControlRoot;
	}

	public void setVersionControlRoot(String versionControlRoot) {
		this.versionControlRoot = versionControlRoot;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
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

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
