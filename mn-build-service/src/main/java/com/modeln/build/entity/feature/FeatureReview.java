package com.modeln.build.entity.feature;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

import com.modeln.build.entity.auth.User;
import com.modeln.build.entity.feature.id.FeatureReviewId;
import com.modeln.build.enums.feature.ApprovalStatus;

/**
 * 
 * This class represents the feature_review table. 
 * 
 * CREATE TABLE feature_review (
 *     area_id          INT UNSIGNED   NOT NULL REFERENCES feature_area(area_id),
 *     build_id         INT UNSIGNED            REFERENCES build(build_id),
 *     user_id          INT UNSIGNED   NOT NULL REFERENCES login(user_id),
 *     review_date      DATETIME       NOT NULL,
 *     status           ENUM('approved','rejected') NOT NULL DEFAULT 'approved',
 *     comment          TEXT,
 *     PRIMARY KEY (area_id, build_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `feature_review` (
 *   `area_id` int(10) unsigned NOT NULL,
 *   `build_id` int(10) unsigned NOT NULL default '0',
 *   `user_id` int(10) unsigned NOT NULL,
 *   `status` enum('approved','rejected') NOT NULL default 'approved',
 *   `comment` text,
 *   `review_date` datetime NOT NULL,
 *   PRIMARY KEY  (`area_id`,`build_id`)
 * );
 * 
 * build_id is not a rel since some entry in the database don't correspond to any build entry. Ghost relationship
 * 
 * TODO
 * - Continue documentation.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "feature_review")
@Access(AccessType.FIELD)
@IdClass(FeatureReviewId.class)
@PrimaryKey(validation=IdValidation.ZERO)
public class FeatureReview {
	
	@Id
	@ManyToOne
	@JoinColumn(name="area_id")
	private FeatureArea area;
	
	@Id
	@Column(name="build_id")
	private Integer buildId;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="review_date")
	@Temporal(TemporalType.TIMESTAMP)
//	@NotNull
	private Date reviewDate;
	
	@Column(name="status")
//	@Enumerated(EnumType.STRING)
	@NotNull
	private String status;
	
	@Column(name="comment")
	@Lob
	private String comment;

	public FeatureArea getArea() {
		return area;
	}

	public void setArea(FeatureArea area) {
		this.area = area;
	}

	public Integer getBuildId() {
		return buildId;
	}

	public void setBuildId(Integer buildId) {
		this.buildId = buildId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public ApprovalStatus getStatus() {
		return ApprovalStatus.valueOf(status.toUpperCase());
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status.getName().toLowerCase();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
