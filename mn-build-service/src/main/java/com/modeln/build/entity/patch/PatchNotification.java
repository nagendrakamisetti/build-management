package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.entity.customer.Account;
import com.modeln.build.enums.patch.NotificationStatus;
import com.modeln.build.validation.constraints.EnumSet;

/**
 * CREATE TABLE patch_notification (
 *     notification_id INT UNSIGNED    NOT NULL AUTO_INCREMENT,
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login,
 *     account_id      INT UNSIGNED    REFERENCES customer_account(account_id),
 *     build_version   VARCHAR(127),
 *     status          SET('saved','approval','rejected','pending','canceled','running','failed','complete','release') NOT NULL DEFAULT 'approval',
 *     PRIMARY KEY (notification_id)
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
@Table(name="patch_notification")
@Access(AccessType.FIELD)
public class PatchNotification {
	
	@Id
	@GeneratedValue
	@Column(name="notification_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@Column(name="build_version")
	private String buildVersion;
	
	@Column(name="notification_id")
	@EnumSet(value=NotificationStatus.class)
	@NotNull
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
