package com.modeln.build.entity.test.act;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import com.modeln.build.entity.test.act.id.ActTestCaseMapId;

/**
 * CREATE TABLE act_testcase_map (
 *     test_id         INT UNSIGNED    NOT NULL REFERENCES act(test_id),
 *     testcase        VARCHAR(127)    NOT NULL,
 *     INDEX testcase_idx(testcase),
 *     PRIMARY KEY (test_id, testcase)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `act_testcase_map` (
 *   `test_id` int(10) unsigned NOT NULL,
 *   `testcase` varchar(127) NOT NULL,
 *   PRIMARY KEY  (`test_id`,`testcase`),
 *   KEY `testcase_idx` (`testcase`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "act_testcase_map", indexes = { @Index(name="act_testcase_map_testcase_idx", columnList="testcase")})
@Access(AccessType.FIELD)
@IdClass(ActTestCaseMapId.class)
public class ActTestCaseMap {
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="test_id")
	private Act act;
	
	@Id
	@Column(name="testcase")
	@NotNull
	private String testcase;
	
	public Act getAct() {
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	public String getTestcase() {
		return testcase;
	}

	public void setTestcase(String testcase) {
		this.testcase = testcase;
	}
}
