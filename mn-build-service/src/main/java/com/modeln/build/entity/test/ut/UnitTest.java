package com.modeln.build.entity.test.ut;

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
 * CREATE TABLE unittest (
 *     suite_id        INT UNSIGNED    NOT NULL REFERENCES unittest_suite(suite_id),
 *     test_id         INT UNSIGNED    NOT NULL auto_increment,
 *     test_group_name VARCHAR(127),
 *     start_date      DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     class           VARCHAR(127)    NOT NULL,
 *     method          VARCHAR(127)    NOT NULL,
 *     message         TEXT,
 *     status          ENUM("PASS", "FAIL", "ERROR", "SKIP", "KILL", "PENDING", "RUNNING", "BLACKLIST") NOT NULL,
 *     INDEX test_idx(class, method),
 *     INDEX suite_idx(suite_id),
 *     INDEX group_idx(test_group_name),
 *     INDEX status_idx(status),
 *     PRIMARY KEY (test_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `unittest` (
 *   `suite_id` int(11) NOT NULL default '0',
 *   `test_id` int(11) NOT NULL auto_increment,
 *   `test_group_name` varchar(217) default NULL,
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `class` varchar(127) NOT NULL default '',
 *   `method` varchar(127) NOT NULL default '',
 *   `message` text,
 *   `status` enum('PASS','FAIL','ERROR','SKIP','KILL','PENDING','RUNNING','BLACKLIST') NOT NULL default 'PASS',
 *   PRIMARY KEY  (`test_id`),
 *   KEY `status_idx` (`status`),
 *   KEY `suite_id` (`suite_id`),
 *   KEY `test_idx` (`class`,`method`),
 *   KEY `test_group_name` (`test_group_name`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */

@Entity
@Table(name = "unittest", indexes = { @Index(name="unittest_test_idx", columnList="class, method"),
		@Index(name="unittest_suite_idx", columnList="suite_id"),
		@Index(name="unittest_group_idx", columnList="test_group_name"),
		@Index(name="unittest_status_idx", columnList="status")})
@Access(AccessType.FIELD)
public class UnitTest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="test_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="suite_id")
	@NotNull
	private UnitTestSuite suite;
	
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

	@Column(name="class")
	@NotNull
	private String testClass;
	
	@Column(name="method")
	@NotNull
	private String testMethod;
	
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

	public UnitTestSuite getSuite() {
		return suite;
	}

	public void setSuite(UnitTestSuite suite) {
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

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
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
