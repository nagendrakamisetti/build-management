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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.patch.CommentVisibility;

/**
 * CREATE TABLE patch_comments (
 *     comment_id      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
 *     patch_id        INT UNSIGNED    NOT NULL REFERENCES patch_request(patch_id),
 *     save_date       DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login,
 *     status          ENUM('show','hide','admin') NOT NULL DEFAULT 'show',
 *     comment         TEXT,
 *     PRIMARY KEY (comment_id)
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
@Table(name = "patch_comments")
@Access(AccessType.FIELD)
public class PatchComments {
	@Id
	@GeneratedValue
	@Column(name="comment_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="patch_id", nullable=false)
	private PatchRequest patchRequest;
	
	@Column(name="save_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date saveDate;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	@NotNull
	private CommentVisibility status;
	
	@Column(name="comment")
	@Lob
	private String comment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PatchRequest getPatchRequest() {
		return patchRequest;
	}

	public void setPatchRequest(PatchRequest patchRequest) {
		this.patchRequest = patchRequest;
	}

	public Date getSaveDate() {
		return saveDate;
	}

	public void setSaveDate(Date saveDate) {
		this.saveDate = saveDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CommentVisibility getStatus() {
		return status;
	}

	public void setStatus(CommentVisibility status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
