package com.modeln.build.entity.test.act.id;

/**
 * ActBlacklist composite id
 * 
 * @author gzussa
 *
 */
public class ActBlacklistId {
	
	private String filename;

	private String versionControlRoot;
	
	public ActBlacklistId(String filename, String versionControlRoot) {
		super();
		this.filename = filename;
		this.versionControlRoot = versionControlRoot;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getVersionControlRoot() {
		return versionControlRoot;
	}

	public void setVersionControlRoot(String versionControlRoot) {
		this.versionControlRoot = versionControlRoot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime
				* result
				+ ((versionControlRoot == null) ? 0 : versionControlRoot
						.hashCode());
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
		ActBlacklistId other = (ActBlacklistId) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (versionControlRoot == null) {
			if (other.versionControlRoot != null)
				return false;
		} else if (!versionControlRoot.equals(other.versionControlRoot))
			return false;
		return true;
	}
}
