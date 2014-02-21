package com.modeln.build.entity.auth;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.PermissionGroup;
import com.modeln.build.validation.constraints.EnumSet;

/**
 * This entity define a user group. A group can have sub groups.
 * 
 * This class represent the login_group table.
 * This entity doesn't generate values for its primary key automatically. The Id must be entered manually!
 * 
 * CREATE TABLE IF NOT EXISTS `login_group` (
 *   `group_id` int(10) unsigned NOT NULL default '0',
 *   `parent_id` int(10) unsigned default NULL,
 *   `group_name` varchar(50) NOT NULL default '',
 *   `group_desc` varchar(255) NOT NULL default '',
 *   `group_type` enum('admin','user') NOT NULL default 'admin',
 *   `perm_self` set('edit') default NULL,
 *   `perm_group` set('edit','add','delete') default NULL,
 *   `perm_user` set('edit','add','delete') default NULL,
 *   `perm_listing` set('edit','add','delete') default NULL,
 *   PRIMARY KEY  (`group_id`),
 *   KEY `parent_idx` (`parent_id`)
 * );
 * 
 * ENUM column have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - type is mapped to enum com.modeln.build.common.account.enums.GroupType.
 * 
 * SET columns have been converted to String on the java side but are accessed through getter and setter using Set<Enum.class> type.
 * permSelf, permGroup and permUser map to Set<com.modeln.build.enums.auth.Permissions>.
 * Every SET columns have a custom validation @EnumSet in order to make sure we only store valid values.
 * 
 * @PrimaryKey annotation with attribute IdValidation equals to IdValidation.NULL since the database contain id equals to 0.
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Write migrations so we can do the mapping with emums instead of String fields.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "login_group", indexes = { @Index(name="login_group_parent_idx", columnList="parent_id") })
@Access(AccessType.FIELD)
@PrimaryKey(validation=IdValidation.NULL)
public class Group {
	
	@Id 
    @Column(name="group_id")
	private Integer  id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
    private Group  parent;
    
    @Column(name="group_name")
    @NotNull
    private String  name;
    
    @Column(name="group_desc")
    @NotNull
    private String  desc;
    
//    @Enumerated(EnumType.STRING)
    @Column(name="group_type")
    @NotNull
    private String type;
    
    @Column(name="perm_self")
    @EnumSet(value=Permission.class, excludes={"ADD", "DELETED"})
    private String permSelf;
    
    @Column(name="perm_group")
    @EnumSet(value=Permission.class)
    private String permGroup;
    
    @Column(name="perm_user")
    @EnumSet(value=Permission.class)
    private String permUser;
    
    @Column(name="perm_listing")
    @EnumSet(value=Permission.class)
    private String permListing;
    
 	@Transient
    private boolean[][] permissions = {
        {false, false, false},      // Self
        {false, false, false},      // Group
        {false, false, false}       // User
    };

	public Integer getId() {
		return id;
	}

	public void setGid(Integer id) {
		this.id = id;
	}

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Role getType() {
		return Role.valueOf(type.toUpperCase());
	}

	public void setType(Role type) {
		this.type = type.getName().toLowerCase();
	}

	public Set<Permission> getPermSelf() {
		Set<Permission> result = new HashSet<Permission>();
		if(permSelf == null){
			return result;
		}
		String[] permSelfSplit = permSelf.split(",");
		for(String currentPermSelf : permSelfSplit){
			Permission currentPerm = Permission.valueOf(currentPermSelf.trim().toUpperCase());
			result.add(currentPerm);
		}
		return result;
	}

	public void setPermSelf(Set<Permission> permSelf) {
		String result = "";
		if(permSelf == null){
			this.permSelf = null;
		}
		int i = 0;
		for(Iterator<Permission> it = permSelf.iterator(); it.hasNext(); i++){
			if(i != 0){
				result += ",";
			}
			result += ((Permission)it.next()).getName().toLowerCase();
		}
		this.permSelf = result;
	}

	public Set<Permission> getPermGroup() {
		Set<Permission> result = new HashSet<Permission>();
		if(permGroup == null){
			return result;
		}
		String[] permGroupSplit = permGroup.split(",");
		for(String currentPermGroup : permGroupSplit){
			Permission currentPerm = Permission.valueOf(currentPermGroup.trim().toUpperCase());
			result.add(currentPerm);
		}
		return result;
	}

	public void setPermGroup(Set<Permission> permGroup) {
		String result = "";
		if(permGroup == null){
			this.permGroup = null;
		}
		int i = 0;
		for(Iterator<Permission> it = permGroup.iterator(); it.hasNext(); i++){
			if(i != 0){
				result += ",";
			}
			result += ((Permission)it.next()).getName().toLowerCase();
		}
		this.permGroup = result;
	}

	public Set<Permission> getPermUser() {
		Set<Permission> result = new HashSet<Permission>();
		if(permUser == null){
			return result;
		}
		String[] permUserSplit = permUser.split(",");
		for(String currentPermUser : permUserSplit){
			Permission currentPerm = Permission.valueOf(currentPermUser.trim().toUpperCase());
			result.add(currentPerm);
		}
		return result;
	}

	public void setPermUser(Set<Permission> permUser) {
		String result = "";
		if(permUser == null){
			this.permUser = null;
		}
		int i = 0;
		for(Iterator<Permission> it = permUser.iterator(); it.hasNext(); i++){
			if(i != 0){
				result += ",";
			}
			result += ((Permission)it.next()).getName().toLowerCase();
		}
		this.permUser = result;
	}
	
	public Set<Permission> getPermListing() {
		Set<Permission> result = new HashSet<Permission>();
		if(permListing == null){
			return result;
		}
		String[] permListingSplit = permListing.split(",");
		for(String currentPermListing : permListingSplit){
			Permission currentPerm = Permission.valueOf(currentPermListing.trim().toUpperCase());
			result.add(currentPerm);
		}
		return result;
	}

	public void setPermListing(Set<Permission> permListing) {
		String result = "";
		if(permListing == null){
			this.permListing = null;
		}
		int i = 0;
		for(Iterator<Permission> it = permListing.iterator(); it.hasNext(); i++){
			if(i != 0){
				result += ",";
			}
			result += ((Permission)it.next()).getName().toLowerCase();
		}
		this.permListing = result;
	}

	public boolean[][] getPermissions() {
		if(permSelf != null){
			Set<Permission> permSet = convertStringToPermissionSet(permSelf);
			for(Permission perm : permSet){
				permissions[PermissionGroup.SELF.getValue()][perm.getValue()] = true;
			}
		}
		if(permGroup != null){
			Set<Permission> permSet = convertStringToPermissionSet(permGroup);
			for(Permission perm : permSet){
				permissions[PermissionGroup.GROUP.getValue()][perm.getValue()] = true;
			}
		}
		if(permUser != null){
			Set<Permission> permSelfSet = convertStringToPermissionSet(permUser);
			for(Permission perm : permSelfSet){
				permissions[PermissionGroup.USER.getValue()][perm.getValue()] = true;
			}
		}
		return permissions;
	}
	
	private Set<Permission> convertStringToPermissionSet(@EnumSet(value=Permission.class) String enumSet){
		String values = enumSet.trim();
		String[] enumValues = values.split(",");
		Set<Permission> result = new HashSet<Permission>();
		for(String value : enumValues){
			result.add(Permission.valueOf(Permission.class, value));
		}
		
		return result;
	}

	public void setPermissions(boolean[][] permissions) {
		this.permissions = permissions;
	}
 	
 	
}
