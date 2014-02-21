package com.modeln.build.entity.build;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.enums.build.Status;

/**
 * This entity represents build notes. It allows users to add comments regarding a build status.
 * 
 * This class corresponds to the build_status_notes
 * 
 * CREATE TABLE IF NOT EXISTS `build_status_notes` (
 *   `note_id` int(10) unsigned NOT NULL auto_increment,
 *   `build_id` int(10) unsigned NOT NULL default '0',
 *   `STATUS` enum('passing','verified','stable','tested','released') NOT NULL default 'passing',
 *   `comments` text NOT NULL,
 *   `attachment` blob,
 *   PRIMARY KEY  (`note_id`),
 *   KEY `status_idx` (`STATUS`),
 *   KEY `build_idx` (`build_id`)
 * );
 * 
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - status is mapped to enum com.modeln.build.enums.build.Status.
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Write migrations so we can do the mapping with emums instead of String fields.
 * - Remove the status enum and create a more flexible schema where user will be able to create their own status!
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name="build_status_notes", indexes={@Index(name="build_status_notes_status_idx", columnList="status"), 
		@Index(name="build_status_notes_build_idx", columnList="build_id")})
@Access(AccessType.FIELD)
public class BuildStatusNote {
	
	/** Identifies the note in the data table. */
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="note_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="build_id")
	@NotNull
	private Build build;
	
	/** Status that the note corresponds to */
	@Column(name="status")
//	@Enumerated(EnumType.ORDINAL)
	@NotNull
    private String status;

    /** Comments associated with the status */
	@Column(name="comments")
	@Lob
	@NotNull
    private String comments;

    /** Binary attachment associated with the status */
	@Column(name="attachment")
	@Lob
	@Basic(fetch=FetchType.LAZY)
    private String attachment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Status getStatus() {
		return Status.valueOf(status.toUpperCase());
	}

	public void setStatus(Status status) {
		this.status = status.getName().toLowerCase();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
