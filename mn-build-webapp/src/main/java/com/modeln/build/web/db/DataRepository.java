/*
 * DataRepository.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.database;

/**
 * The DataRepository class represents generic information about a
 * data repository.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class DataRepository {

    /** Description of the repository */
    private String description;

    /**
     * Construct the data repository.
     */
    protected DataRepository() {
    }


    /**
     * Sets the description of the respository.
     *
     * @param   desc    Description of the repository
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /**
     * Returns the description of the respository.
     *
     * @return  Description of the repository
     */
    public String getDescription() {
        return description;
    }

}
