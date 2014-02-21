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

import com.modeln.build.enums.customer.BranchType;

/**
 * 
 * CREATE TABLE IF NOT EXISTS `customer_account` (
 *   `account_id` int(10) unsigned NOT NULL auto_increment,
 *   `account_name` varchar(127) NOT NULL,
 *   `short_name` varchar(20) NOT NULL,
 *   `branch_type` enum('product','customer') NOT NULL default 'product',
 *   PRIMARY KEY  (`account_id`),
 *   KEY `name_idx` (`account_name`)
 * );
 *
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - branchType is mapped to enum com.modeln.build.enums.customer.BranchType.
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO 
 *  - Finish documentation
 *  
 * @author gzussa
 *
 */
@Entity
@Table(name="customer_account", indexes = {@Index(name="customer_account_name_idx", columnList="account_name")})
@Access(AccessType.FIELD)
public class Account {
	
	/** Auto-generated ID used to identify an account */
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="account_id")
    private Integer id;
    
    /** Name of the account */
	@Column(name="account_name")
	@NotNull
    private String name;

    /** Short name of the account */
	@Column(name="short_name")
	@NotNull
    private String shortName;
	
	@Column(name="branch_type")
//	@Enumerated(EnumType.STRING)
	@NotNull
	private String branchType = BranchType.PRODUCT.getName().toLowerCase();
	
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public BranchType getBranchType() {
		return BranchType.valueOf(branchType.toUpperCase());
	}

	public void setBranchType(BranchType branchType) {
		this.branchType = branchType.getName().toLowerCase();
	}
    
}
