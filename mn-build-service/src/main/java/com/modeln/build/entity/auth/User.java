package com.modeln.build.entity.auth;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SecondaryTables;
import javax.persistence.SecondaryTable;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.validation.groups.LoginHistory;


/**
 * This entity represent a user with basic user informations. A user belong to a Group. This entity also keep track of user's connections into the system.
 * 
 * This class represents both the login and the login_history tables.
 * The login_history table has been mapped as a secondary table (@SecondaryTables) to the login table since both table id are the same. 
 * The login.user_id defined the login_history.user_id column value.
 * The user_id for the login_history table is not mapped with auto_increment.
 * The login_history.user_id column is directly related to the user_id column from the login table since login_history.user_id is a reference of login.user_id (one to one relationship)
 * 
 * SQL exported from production database foth both tables 
 *  
 * CREATE TABLE IF NOT EXISTS `login` (
 *   `user_id` int(10) unsigned NOT NULL auto_increment,
 *   `username` varchar(32) NOT NULL,
 *   `password` varchar(255) NOT NULL,
 *   `pass_type` enum('crypt','md5','pbkdf2') NOT NULL,
 *   `status` enum('active','inactive','deleted','abuse') NOT NULL,
 *   `email` varchar(255) default NULL,
 *   `pri_group` int(10) unsigned NOT NULL,
 *   `firstname` varchar(32) NOT NULL,
 *   `lastname` varchar(32) NOT NULL,
 *   `middlename` varchar(32) default NULL,
 *   `title` enum('Mr','Ms','Mrs') default NULL,
 *   `language` char(2) NOT NULL,
 *   `country` char(2) NOT NULL,
 *   PRIMARY KEY  (`user_id`)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `login_history` (
 *   `user_id` int(10) unsigned NOT NULL default '0',
 *   `date` date NOT NULL default '0000-00-00',
 *   `fail_date` date default NULL,
 *   `fail_count` tinyint(4) default NULL,
 *   PRIMARY KEY  (`user_id`)
 * );
 *
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - accountStatus is mapped to enum com.modeln.build.enums.auth.Status.
 * - passwordEncryption is mapped to enum com.modeln.build.enums.auth.Encryption.
 * - title is mapped to enum enum com.modeln.build.enums.auth.Title.
 * 
 * - login_history.date column is not annotated with annotation @NotNull since the database contains some null values (default 0000-00-00 values).
 * - login.email column contains null value, we can't annotate the @Email annotation. 
 * 
 * @PrimaryKey annotation with attribute IdValidation equals to IdValidation.NULL since the database contain id equals to 0
 * 
 * TODO
 * - Write migrations so we can do the mapping with emums instead of String fields.
 * - Either change the @Email annotation so we it can accept null values or write a migration so we make this column NOT NULL
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name="login")
@SecondaryTables({
	@SecondaryTable(name="login_history")
})
@PrimaryKey(validation=IdValidation.NULL)
@Access(AccessType.FIELD)
public class User {
	/** Unique user ID value, such as a UID */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    /** Unique name which identifies the user on the system for login */
    @Column(name="username")
    @NotNull
    private String username;

    /** Account password */
    @Column(name="password")
    @NotNull
    private String password;

    /** Primary user group */
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="pri_group", nullable=false)
    @NotNull
    private Group primaryGroup;

    /** Login status of the account */
    @Column(name="status")
//    @Enumerated(EnumType.STRING)
    @NotNull
    private String accountStatus = Status.ACTIVE.getName().toLowerCase();

    /** Type of password encryption used. */
    @Column(name="pass_type")
//    @Enumerated(EnumType.STRING)
    @NotNull
    private String passwordEncryption;

    @Column(name="firstname")
    @NotNull
    private String firstName;
    
    @Column(name="lastname")
    @NotNull
    private String lastName;
    
    @Column(name="middlename")
    private String middleName;

    /** Language and location for this user */
    @Transient
    private Locale locale;
    
    @Column(name="title")
//  @Enumerated(EnumType.STRING)
    private String title;
    
    @Column(name="language")
    @NotNull
    @Size(max = 2)
    private String language;
    
    @Column(name="country")
    @NotNull
    @Size(max = 2)
    private String country;

    /** Off-system user e-mail address */
    @Column(name="email")
//    @NotNull
//    @Email
    private String emailAddress;

    /** Date of the last successful login attempt */
    @Column(name="date", table = "login_history")
    @Temporal(TemporalType.TIMESTAMP)
//    @NotNull(groups=LoginHistory.class)
    @Past(groups=LoginHistory.class)
    private Date loginSuccess;

    /** Date of the last failed login attempt */
    @Column(name="fail_date", table = "login_history")
    @Temporal(TemporalType.TIMESTAMP)
    @Past(groups=LoginHistory.class)
    private Date loginFailure;

    /** Number of failed login attempts since the last successful login */
    @Column(name="fail_count", table = "login_history")
    @Min(value=0, groups=LoginHistory.class)
    private int loginFailureCount;

	public Integer getId() {
		return id;
	}

	public void setUid(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Group getPrimaryGroup() {
		return primaryGroup;
	}

	public void setPrimaryGroup(Group primaryGroup) {
		this.primaryGroup = primaryGroup;
	}

	public Status getAccountStatus() {
		return Status.valueOf(accountStatus.toUpperCase());
	}

	public void setAccountStatus(Status accountStatus) {
		this.accountStatus = accountStatus.getName().toLowerCase();
	}

	public Encryption getPasswordEncryption() {
		return Encryption.valueOf(passwordEncryption.toUpperCase());
	}

	public void setPasswordEncryption(Encryption passwordEncryption) {
		this.passwordEncryption = passwordEncryption.getName().toLowerCase();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Title getTitle() {
		return Title.valueOf(title.toUpperCase());
	}

	public void setTitle(Title title) {
		this.title = title.getName().toLowerCase();
	}

	public Locale getLocale() {
		//Synchronize value based on language and country values
		if(locale == null){
			if(language != null && country != null){
				setLocale(new Locale(language, country));
			}
		}
		
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Date getLoginSuccess() {
		return loginSuccess;
	}

	public void setLoginSuccess(Date loginSuccess) {
		this.loginSuccess = loginSuccess;
	}

	public Date getLoginFailure() {
		return loginFailure;
	}

	public void setLoginFailure(Date loginFailure) {
		this.loginFailure = loginFailure;
	}

	public int getLoginFailureCount() {
		return loginFailureCount;
	}

	public void setLoginFailureCount(int loginFailureCount) {
		this.loginFailureCount = loginFailureCount;
	}
    
}
