package com.modeln.build.entity.test.act;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.modeln.build.enums.test.Status;

/**
 * CREATE TABLE act (
 *     suite_id        INT UNSIGNED    NOT NULL REFERENCES act_suite(suite_id),
 *     test_id         INT UNSIGNED    NOT NULL auto_increment,
 *     test_group_name VARCHAR(127),
 *     start_date      DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     author          VARCHAR(127)    NOT NULL,
 *     filename        VARCHAR(127)    NOT NULL,
 *     summary         TEXT,
 *     message         TEXT,
 *     status          ENUM("PASS", "FAIL", "ERROR", "SKIP", "KILL", "PENDING", "RUNNING", "BLACKLIST") NOT NULL,
 *     INDEX author_idx(author),
 *     INDEX file_idx(filename),
 *     INDEX suite_idx(suite_id),
 *     INDEX status_idx(status),
 *     INDEX group_idx(test_group_name),
 *     PRIMARY KEY (test_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `act` (
 *   `suite_id` int(10) unsigned NOT NULL,
 *   `test_id` int(10) unsigned NOT NULL auto_increment,
 *   `test_group_name` varchar(127) default NULL,
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `author` varchar(127) NOT NULL,
 *   `filename` varchar(127) NOT NULL,
 *   `summary` text,
 *   `message` text,
 *   `status` enum('PASS','FAIL','ERROR','SKIP','KILL','PENDING','RUNNING','BLACKLIST') NOT NULL default 'PASS',
 *   PRIMARY KEY  (`test_id`),
 *   KEY `author_idx` (`author`),
 *   KEY `file_idx` (`filename`),
 *   KEY `suite_idx` (`suite_id`),
 *   KEY `status_idx` (`status`)
 * );
 *
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "act", indexes = { @Index(name="act_author_idx", columnList="author"),
		@Index(name="act_file_idx", columnList="filename"),
		@Index(name="act_suite_idx", columnList="suite_id"),
		@Index(name="act_group_idx", columnList="test_group_name"),
		@Index(name="act_status_idx", columnList="status")})
@Access(AccessType.FIELD)
public class Act {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="test_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="suite_id")
	@NotNull
	private ActSuite suite;
	
	@Column(name="test_group_name")
	private String groupName;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date endDate;

	@Column(name="author")
	@NotNull
	private String author;
	
	@Column(name="filename")
	@NotNull
	private String filename;
	
	@Column(name="summary")
	@Lob
	private String summary;
	
	@Column(name="message")
	@Lob
	private String message;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Status status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ActSuite getSuite() {
		return suite;
	}

	public void setSuite(ActSuite suite) {
		this.suite = suite;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
