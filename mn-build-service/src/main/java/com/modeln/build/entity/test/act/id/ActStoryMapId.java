package com.modeln.build.entity.test.act.id;

/**
 * ActStoryMap composite id
 * 
 * @author gzussa
 *
 */
public class ActStoryMapId {
	private Integer act;
	
	private String story;

	public ActStoryMapId(Integer act, String story) {
		super();
		this.act = act;
		this.story = story;
	}

	public Integer getAct() {
		return act;
	}

	public void setAct(Integer act) {
		this.act = act;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((act == null) ? 0 : act.hashCode());
		result = prime * result + ((story == null) ? 0 : story.hashCode());
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
		ActStoryMapId other = (ActStoryMapId) obj;
		if (act == null) {
			if (other.act != null)
				return false;
		} else if (!act.equals(other.act))
			return false;
		if (story == null) {
			if (other.story != null)
				return false;
		} else if (!story.equals(other.story))
			return false;
		return true;
	}
	
}
