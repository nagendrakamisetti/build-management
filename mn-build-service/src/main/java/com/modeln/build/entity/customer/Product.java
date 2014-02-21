package com.modeln.build.entity.customer;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

/**
 * 
 * CREATE TABLE IF NOT EXISTS `release_product` (
 *   `product_id` int(10) unsigned NOT NULL auto_increment,
 *   `name` varchar(127) NOT NULL,
 *   `description` varchar(255) NOT NULL,
 *   PRIMARY KEY  (`product_id`),
 *   KEY `name_idx` (`name`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @PrimaryKey annotation with attribute IdValidation equals to IdValidation.NULL since the database contain id equals to 0
 *
 * TODO 
 *  - Finish documentation
 *  
 * @author gzussa
 *
 */
@Entity
@Table(name="release_product", indexes = {@Index(name="release_product_name_idx", columnList="name")})
@PrimaryKey(validation=IdValidation.NULL)
@Access(AccessType.FIELD)
public class Product {
	/** Auto-generated ID used to identify the product */
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="product_id")
	private Integer id;
    
    /** Product title */
	@Column(name="name")
	@NotNull
    private String name;

    /** Brief product description */
	@Column(name="description")
	@NotNull
    private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
