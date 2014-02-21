package com.modeln.build.entity.customer;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.build.Build;

/**
 * 
 * CREATE TABLE IF NOT EXISTS `customer_env` (
 *   `env_id` int(10) unsigned NOT NULL auto_increment,
 *   `account_id` int(10) unsigned NOT NULL,
 *   `env_name` varchar(127) NOT NULL,
 *   `short_name` varchar(20) default NULL,
 *   `product_id` int(10) unsigned NOT NULL,
 *   `build_id` int(10) unsigned NOT NULL,
 *   KEY `account_idx` (`account_id`),
 *   KEY `env_id` (`env_id`)
 * );
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
@Table(name="customer_env", indexes = { @Index(name="customer_env_account_idx", columnList="account_id")})
@Access(AccessType.FIELD)
public class Environment {
	
	/** Auto-generated ID used to identify a customer deployment environment */
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="env_id")
    private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	@NotNull
	private Account account;
    
    /** Name of the environment */
	@Column(name="env_name")
	@NotNull
    private String name;

    /** Short name of the account */
	@Column(name="short_name")
    private String shortName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id")
	@NotNull
	private Product product;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="build_id")
	@NotNull
	private Build build;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

}
