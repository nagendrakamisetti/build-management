package com.modeln.build.entity.test.flex;

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
 * CREATE TABLE flextest (
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
 *     INDEX status_idx(status),
 *     INDEX group_idx(test_group_name),
 *     PRIMARY KEY (test_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `flextest` (
 *   `suite_id` int(10) unsigned NOT NULL,
 *   `test_id` int(10) unsigned NOT NULL auto_increment,
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `class` varchar(127) NOT NULL,
 *   `method` varchar(127) NOT NULL,
 *   `message` text,
 *   `status` enum('PASS','FAIL','ERROR','SKIP','KILL','PENDING','RUNNING','BLACKLIST') NOT NULL default 'PASS',
 *   PRIMARY KEY  (`test_id`),
 *   KEY `test_idx` (`class`,`method`),
 *   KEY `suite_idx` (`suite_id`),
 *   KEY `status_idx` (`status`)
 * );
 * 
 * test_group_name column don't exist!
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 *
 * @author gzussa
 *
 */
@Entity
@Table(name = "flextest", indexes = { @Index(name="flextest_test_idx", columnList="class, method"),
		@Index(name="flextest_suite_idx", columnList="suite_id"),
		/*@Index(name="flextest_group_idx", columnList="test_group_name"),*/
		@Index(name="flextest_status_idx", columnList="status")})
@Access(AccessType.FIELD)
public class FlexTest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="test_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="suite_id")
	@NotNull
	private FlexTestSuite suite;
	
//	@Column(name="test_group_name")
//	private String groupName;
	
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

	public FlexTestSuite getSuite() {
		return suite;
	}

	public void setSuite(FlexTestSuite suite) {
		this.suite = suite;
	}

//	public String getGroupName() {
//		return groupName;
//	}
//
//	public void setGroupName(String groupName) {
//		this.groupName = groupName;
//	}

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
