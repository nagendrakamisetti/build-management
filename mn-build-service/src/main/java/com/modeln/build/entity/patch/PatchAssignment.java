package com.modeln.build.entity.patch;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.patch.Priority;

/**
 * CREATE TABLE patch_assignment (
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login,
 *     patch_id        INT UNSIGNED    NOT NULL REFERENCES patch_request(patch_id),
 *     start_date      DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     deadline        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00', 
 *     priority        ENUM('low','medium','high') NOT NULL DEFAULT 'low',
 *     comments        TEXT,
 *     INDEX (user_id, patch_id)
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
@Table(name = "patch_assignment")
@Access(AccessType.FIELD)
@IdClass(PatchAssignment.PatchAssignmentId.class)
public class PatchAssignment {
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="patch_id")
	private PatchRequest patchRequest;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date endDate;
	
	@Column(name="deadline")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date deadline;
	
	@Column(name="priority")
	@Enumerated(EnumType.STRING)
	@NotNull
	private Priority priority;
	
	@Column(name="comments")
	@Lob
	private String comments;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PatchRequest getPatchRequest() {
		return patchRequest;
	}

	public void setPatchRequest(PatchRequest patchRequest) {
		this.patchRequest = patchRequest;
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

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public class PatchAssignmentId {
		
		private Integer user;
		
		private Integer patchRequest;

		public Integer getUser() {
			return user;
		}

		public void setUser(Integer user) {
			this.user = user;
		}

		public Integer getPatchRequest() {
			return patchRequest;
		}

		public void setPatchRequest(Integer patchRequest) {
			this.patchRequest = patchRequest;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((patchRequest == null) ? 0 : patchRequest.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PatchAssignmentId other = (PatchAssignmentId) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (patchRequest == null) {
				if (other.patchRequest != null)
					return false;
			} else if (!patchRequest.equals(other.patchRequest))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

		private PatchAssignment getOuterType() {
			return PatchAssignment.this;
		}
		
	}

}
