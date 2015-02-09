/*
 * JndiRepository.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.db;

/**
 * The JndiRepository class contains information required to connect to a 
 * JNDI resource.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class JndiRepository extends DataRepository {

    /** Name used to lookup the JNDI resource */
    private String name;

    /**
     * Construct the JNDI repository.
     */
    protected JndiRepository() {
    }

    /**
     * Construct the JNDI repository
     *
     * @param   name    Name used to lookup the repository
     */
    public JndiRepository(String name) {
        this.name = name;
        setDescription("JNDI repository: " + name);
    }

    /**
     * Sets the name used to lookup the JNDI resource. 
     *
     * @param   name    Name used to lookup the repository
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name used to lookup the JNDI resource. 
     *
     * @return  Name used to perform the JNDI lookup 
     */
    public String getName() {
        return name;
    }


}
