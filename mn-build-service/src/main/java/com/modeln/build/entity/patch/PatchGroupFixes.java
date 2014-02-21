package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * CREATE TABLE patch_group_fixes (
 *     group_id        INT UNSIGNED    NOT NULL REFERENCES patch_group(group_id),
 *     bug_id          INT UNSIGNED    NOT NULL,
 *     version_ctrl_root VARCHAR(255),
 *     exclusions      VARCHAR(255),
 *     notes           TEXT,
 *     PRIMARY KEY (group_id, bug_id)
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
@Table(name = "patch_group_fixes")
@Access(AccessType.FIELD)
@IdClass(PatchGroupFixes.PatchGroupFixesId.class)
public class PatchGroupFixes {
	@Id
	@ManyToOne
	@JoinColumn(name="group_id", nullable=false)
	private PatchGroup group;
	
	@Id
	@Column(name="bug_id")
	@NotNull
	private Integer bugId;
	
	@Column(name="version_ctrl_root")
	private String versionControlRoot;
	
	@Column(name="exclusions")
	private String exclusions;
	
	@Column(name="notes")
	@Lob
	private String notes;
	
	public PatchGroup getGroup() {
		return group;
	}

	public void setGroup(PatchGroup group) {
		this.group = group;
	}

	public Integer getBugId() {
		return bugId;
	}

	public void setBugId(Integer bugId) {
		this.bugId = bugId;
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

	public class PatchGroupFixesId {
		private Integer group;
		
		private Integer bugId;

		public Integer getGroup() {
			return group;
		}

		public void setGroup(Integer group) {
			this.group = group;
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
			result = prime * result + ((group == null) ? 0 : group.hashCode());
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
			PatchGroupFixesId other = (PatchGroupFixesId) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (bugId == null) {
				if (other.bugId != null)
					return false;
			} else if (!bugId.equals(other.bugId))
				return false;
			if (group == null) {
				if (other.group != null)
					return false;
			} else if (!group.equals(other.group))
				return false;
			return true;
		}

		private PatchGroupFixes getOuterType() {
			return PatchGroupFixes.this;
		}
		
	}

}
