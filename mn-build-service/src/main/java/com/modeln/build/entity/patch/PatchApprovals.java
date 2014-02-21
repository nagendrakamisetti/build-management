package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.patch.PatchStatus;
import com.modeln.build.enums.patch.Status;

/**
 * CREATE TABLE patch_approvals (
 *     patch_id        INT UNSIGNED    NOT NULL REFERENCES patch_request(patch_id),
 *     user_id         INT UNSIGNED    NOT NULL REFERENCES login,
 *     status          ENUM('approved','rejected') NOT NULL DEFAULT 'approved',
 *     patch_status    ENUM('approval','complete') NOT NULL DEFAULT 'approval',
 *     comment         TEXT,
 *     PRIMARY KEY (patch_id, user_id, patch_status)
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
@Table(name = "patch_approvals")
@Access(AccessType.FIELD)
@IdClass(PatchApprovals.PatchApprovalsId.class)
public class PatchApprovals {

	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="patch_id")
	private PatchRequest patchRequest;
	
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	@NotNull
	private User user;
	
	@Column(name="status")
	@NotNull
	private Status status;
	
	@Id
	@Column(name="patch_status")
	@NotNull
	private PatchStatus patchStatus;
	
	@Column(name="comment")
	@Lob
	private String comment;
	
	public PatchRequest getPatchRequest() {
		return patchRequest;
	}

	public void setPatchRequest(PatchRequest patchRequest) {
		this.patchRequest = patchRequest;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public PatchStatus getPatchStatus() {
		return patchStatus;
	}

	public void setPatchStatus(PatchStatus patchStatus) {
		this.patchStatus = patchStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public class PatchApprovalsId {
		private Integer patchRequest;
		
		private Integer user;
		
		private PatchStatus patchStatus;

		public Integer getPatchRequest() {
			return patchRequest;
		}

		public void setPatchRequest(Integer patchRequest) {
			this.patchRequest = patchRequest;
		}

		public Integer getUser() {
			return user;
		}

		public void setUser(Integer user) {
			this.user = user;
		}

		public PatchStatus getPatchStatus() {
			return patchStatus;
		}

		public void setPatchStatus(PatchStatus patchStatus) {
			this.patchStatus = patchStatus;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((patchRequest == null) ? 0 : patchRequest.hashCode());
			result = prime * result
					+ ((patchStatus == null) ? 0 : patchStatus.hashCode());
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
			PatchApprovalsId other = (PatchApprovalsId) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (patchRequest == null) {
				if (other.patchRequest != null)
					return false;
			} else if (!patchRequest.equals(other.patchRequest))
				return false;
			if (patchStatus != other.patchStatus)
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

		private PatchApprovals getOuterType() {
			return PatchApprovals.this;
		}
		
		
	}

}
