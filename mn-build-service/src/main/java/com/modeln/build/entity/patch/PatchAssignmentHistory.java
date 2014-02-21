package com.modeln.build.entity.patch;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;

/**
 * CREATE TABLE patch_assignment_history (
 *     patch_id        INT UNSIGNED    NOT NULL REFERENCES patch_request(patch_id),
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login,
 *     change_date     DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     old_user        INT UNSIGNED    NOT NULL REFERENCES login,
 *     new_user        INT UNSIGNED    NOT NULL REFERENCES login,
 *     INDEX patch_idx (patch_id)
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
@Table(name = "patch_assignment_history", indexes={@Index(name="patch_idx", columnList="patch_id")})
@Access(AccessType.FIELD)
public class PatchAssignmentHistory {
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="patch_id")
	private PatchRequest patch;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Column(name="user_id")
	private User user;
	
	@Column(name="change_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date changeDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Column(name="old_user")
	private User oldUser;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Column(name="new_user")
	private User newUser;

	public PatchRequest getPatch() {
		return patch;
	}

	public void setPatch(PatchRequest patch) {
		this.patch = patch;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public User getOldUser() {
		return oldUser;
	}

	public void setOldUser(User oldUser) {
		this.oldUser = oldUser;
	}

	public User getNewUser() {
		return newUser;
	}

	public void setNewUser(User newUser) {
		this.newUser = newUser;
	}

}
