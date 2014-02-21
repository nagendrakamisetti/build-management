package com.modeln.build.entity.test.uit;

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

import com.modeln.build.enums.test.Status;

/**
 * CREATE TABLE uit_step (
 *     test_id         INT UNSIGNED    NOT NULL REFERENCES uit(test_id),
 *     step_id         INT UNSIGNED    NOT NULL auto_increment,
 *     step_name       VARCHAR(127)    NOT NULL,
 *     start_date      DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     message         TEXT,
 *     status          ENUM("PASS", "FAIL", "ERROR", "SKIP", "KILL", "PENDING", "RUNNING", "BLACKLIST") NOT NULL,
 *     INDEX test_idx(test_id),
 *     INDEX status_idx(status),
 *     PRIMARY KEY (step_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `uit_step` (
 *   `test_id` int(10) unsigned NOT NULL,
 *   `step_id` int(10) unsigned NOT NULL auto_increment,
 *   `step_name` varchar(127) NOT NULL,
 *   `start_date` datetime NOT NULL,
 *   `end_date` datetime NOT NULL,
 *   `message` text,
 *   `status` enum('PASS','FAIL','ERROR','SKIP') NOT NULL,
 *   PRIMARY KEY  (`step_id`),
 *   KEY `test_idx` (`test_id`),
 *   KEY `status_idx` (`status`)
 * );
 * 
 * enum as string [status]
 * startDate and EndDate null in db
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "uit_step", indexes = { @Index(name="uit_step_test_idx", columnList="test_id"),
		@Index(name="uit_step_status_idx", columnList="status")})
@Access(AccessType.FIELD)
public class UITestStep {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="step_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="test_id")
//	@NotNull
	private UITest test;
	
	@Column(name="step_name")
	@NotNull
	private String stepName;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
//	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
//	@NotNull
	private Date endDate;
	
	@Column(name="message")
	@Lob
	private String message;
	
//	@Enumerated(EnumType.STRING)
//	@NotNull
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UITest getTest() {
		return test;
	}

	public void setTest(UITest test) {
		this.test = test;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Status getStatus() {
		return Status.valueOf(status.toUpperCase());
	}

	public void setStatus(Status status) {
		this.status = status.getName().toLowerCase();
	}

}
