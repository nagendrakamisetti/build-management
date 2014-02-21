package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * CREATE TABLE patch_fixes (
 *     patch_id        INT UNSIGNED    NOT NULL REFERENCES patch_request(patch_id),
 *     bug_id          INT UNSIGNED    NOT NULL,
 *     origin          INT UNSIGNED    NULL,
 *     version_ctrl_root VARCHAR(255),
 *     exclusions      VARCHAR(255),
 *     notes           TEXT,
 *     PRIMARY KEY (patch_id, bug_id),
 *     INDEX origin_idx(origin)
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
@Table(name = "patch_fixes", indexes={@Index(name="origin_idx", columnList="origin")})
@Access(AccessType.FIELD)
@IdClass(PatchFixes.PatchFixesId.class)
public class PatchFixes {

	@Id
	@ManyToOne
	@JoinColumn(name="patch_id", nullable=false)
	private PatchRequest patchRequest;
	
	@Id
	@Column(name="bug_id")
	@NotNull
	private Integer bugId;
	
	@Column(name="origin")
	private Integer origin;
	
	@Column(name="version_ctrl_root")
	private String versionControlRoot;
	
	@Column(name="exclusions")
	private String exclusions;
	
	@Column(name="notes")
	@Lob
	private String notes;
	
	public PatchRequest getPatchRequest() {
		return patchRequest;
	}

	public void setPatchRequest(PatchRequest patchRequest) {
		this.patchRequest = patchRequest;
	}

	public Integer getBugId() {
		return bugId;
	}

	public void setBugId(Integer bugId) {
		this.bugId = bugId;
	}

	public Integer getOrigin() {
		return origin;
	}

	public void setOrigin(Integer origin) {
		this.origin = origin;
	}
	
	public String getVersionControlRoot() {
		return versionControlRoot;
	}

	public void setVersionControlRoot(String versionControlRoot) {
		this.versionControlRoot = versionControlRoot;
	}

	public String getExclusions() {
		return exclusions;
	}

	public void setExclusions(String exclusions) {
		this.exclusions = exclusions;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public class PatchFixesId {
		private Integer patchRequest;
		
		private Integer bugId;

		public Integer getPatchRequest() {
			return patchRequest;
		}

		public void setPatchRequest(Integer patchRequest) {
			this.patchRequest = patchRequest;
		}

		public Integer getBugId() {
			return bugId;
		}

		public void setBugId(Integer bugId) {
			this.bugId = bugId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((bugId == null) ? 0 : bugId.hashCode());
			result = prime * result
					+ ((patchRequest == null) ? 0 : patchRequest.hashCode());
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
			PatchFixesId other = (PatchFixesId) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (bugId == null) {
				if (other.bugId != null)
					return false;
			} else if (!bugId.equals(other.bugId))
				return false;
			if (patchRequest == null) {
				if (other.patchRequest != null)
					return false;
			} else if (!patchRequest.equals(other.patchRequest))
				return false;
			return true;
		}

		private PatchFixes getOuterType() {
			return PatchFixes.this;
		}
		
		
	}

}
