package com.modeln.build.entity.patch;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.entity.build.Build;
import com.modeln.build.entity.customer.Account;
import com.modeln.build.entity.customer.Environment;
import com.modeln.build.enums.patch.PatchRequestStatus;

/**
 * CREATE TABLE patch_request (
 *     patch_id        INT UNSIGNED    NOT NULL auto_increment,
 *     patch_name      VARCHAR(127)    NOT NULL,
 *     request_date    DATETIME        NOT NULL,
 *     account_id      INT UNSIGNED    NOT NULL REFERENCES customer_account(account_id),
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login(user_id),
 *     env_id          INT UNSIGNED    NOT NULL REFERENCES customer_env(env_id),
 *     build_id        INT UNSIGNED    NOT NULL REFERENCES build(build_id),
 *     patch_build     INT UNSIGNED,
 *     previous_patch  INT UNSIGNED,
 *     internal_only   ENUM('true', 'false') NOT NULL DEFAULT 'false',
 *     status          ENUM('saved','approval','rejected','pending','canceled','running','branching','branched','building','built','failed','complete','release') NOT NULL DEFAULT 'saved',
 *     patch_options   VARCHAR(255),
 *     notification    TEXT,
 *     justification   TEXT,
 *     PRIMARY KEY (patch_id),
 *     INDEX status_idx(status),
 *     INDEX customer_idx(account_id),
 *     INDEX user_idx(user_id)
 * );
 * 
 * TODO
 * - Finish documentation
 * - Fix implementation and test!
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name="patch_request", indexes={@Index(name="status_idx", columnList="status"),
		@Index(name="customer_idx", columnList="account_id"),
		@Index(name="user_idx", columnList="user_id")})
@Access(AccessType.FIELD)
public class PatchRequest {
	@Id
	@GeneratedValue
	@Column(name="patch_id")
	private Integer id;
	
	@Column(name="patch_name")
	@NotNull
	private String patchName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date requestDate;
	
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="env_id", nullable=false)
	private Environment environment;
	
	@ManyToOne
	@JoinColumn(name="build_id", nullable=false)
	private Build build;
	
	@Column(name="patch_build")
	private Integer patchBuild;
	
	@Column(name="previous_patch")
	private Integer previousPatch;
	
	@Column(name="internal_only")
	@NotNull
	private Boolean internalOnly;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	@NotNull
	private PatchRequestStatus status;
	
	@Column(name="patch_options")
	private String patchOptions;
	
	@Column(name="notification")
	@Lob
	private String notification;
	
	@Column(name="justification")
	@Lob
	private String justification;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPatchName() {
		return patchName;
	}

	public void setPatchName(String patchName) {
		this.patchName = patchName;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Integer getPatchBuild() {
		return patchBuild;
	}

	public void setPatchBuild(Integer patchBuild) {
		this.patchBuild = patchBuild;
	}

	public Integer getPreviousPatch() {
		return previousPatch;
	}

	public void setPreviousPatch(Integer previousPatch) {
		this.previousPatch = previousPatch;
	}

	public Boolean getInternalOnly() {
		return internalOnly;
	}

	public void setInternalOnly(Boolean internalOnly) {
		this.internalOnly = internalOnly;
	}

	public PatchRequestStatus getStatus() {
		return status;
	}

	public void setStatus(PatchRequestStatus status) {
		this.status = status;
	}

	public String getPatchOptions() {
		return patchOptions;
	}

	public void setPatchOptions(String patchOptions) {
		this.patchOptions = patchOptions;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}
	
}
