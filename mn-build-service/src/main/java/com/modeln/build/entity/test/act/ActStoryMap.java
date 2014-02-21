package com.modeln.build.entity.test.act;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import com.modeln.build.entity.test.act.id.ActStoryMapId;

/**
 * CREATE TABLE act_story_map (
 *     test_id         INT UNSIGNED    NOT NULL REFERENCES act(test_id),
 *     story           VARCHAR(127)    NOT NULL,
 *     INDEX story_idx(story),
 *     PRIMARY KEY (test_id, story)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `act_story_map` (
 *   `test_id` int(10) unsigned NOT NULL,
 *   `story` varchar(127) NOT NULL,
 *   PRIMARY KEY  (`test_id`,`story`),
 *   KEY `story_idx` (`story`)
 * );
 *
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "act_story_map", indexes = { @Index(name="act_story_map_story_idx", columnList="story")})
@Access(AccessType.FIELD)
@IdClass(ActStoryMapId.class)
public class ActStoryMap {
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="test_id")
	private Act act;
	
	@Id
	@Column(name="story")
	@NotNull
	private String story;
	
	public Act getAct() {
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

}
