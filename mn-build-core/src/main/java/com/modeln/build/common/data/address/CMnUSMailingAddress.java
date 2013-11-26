package com.modeln.build.common.data.address;

/**
 * The USMailingAddress object contains postal information for
 * a mailing location.  This may be used for billing or address
 * book purposes.
 *
 * @hibernate.class table="mailing_addr_us"
 *
 * @version            1.0
 * @author             Shawn Stafford (sstafford@modeln.com)
 *
 */
public class CMnUSMailingAddress extends CMnMailingAddress {

    /** Unique ID of the address */
    private Integer id;
    
    /** Primary street address */
    private String addr1 = "";

    /** Additional address information such as apartment or suite number */
    private String addr2 = "";

    /** City */
    private String city = "";

    /** 2-Character state abbreviation */
    private String state = "";

    /** 5-digit zip code */
    private String zip = "";

    /** 4-digit zip code extension */
    private String zip_ext = "";

    /**
     * Set the ID used to look-up the address.
     *
     * @param  id   Unique ID of the address
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the address.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the address
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the street address.
     * 
     * @param   street    Street and address number
     */
    public void setStreet(String street) {
        addr1 = street;
    }

    /**
     * Return the street address.
     *
     * @hibernate.property
     * 
     * @return  street address string
     */
    public String getStreet() {
        return addr1;
    }

    /**
     * Set the additional street address information such as an
     * apartment or suite number.
     * 
     * @param   ext    Additional suite or apartment information
     */
    public void setStreetExt(String ext) {
        addr2 = ext;
    }

    /**
     * Returns the additional street address information such as an
     * apartment or suite number.
     *
     * @hibernate.property
     * 
     * @return Additional suite or apartment information
     */
    public String getStreetExt() {
        return addr2;
    }

    /**
     * Set the city
     * 
     * @param   city    City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Return the city
     *
     * @hibernate.property
     * 
     * @return  city
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the 2-character state abbreviation
     * 
     * @param   state   2-character state abbreviation
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Return the 2-character state abbreviation
     *
     * @hibernate.property
     * 
     * @return 2-character state abbreviation
     */
    public String getState() {
        return state;
    }

    /**
     * Set the 5-digit zip code.
     *
     * @param   zip     5-digit zip code
     */
    public void setZipCode(String zip) {
        this.zip = zip;
    }

    /**
     * Returns the 5-digit zip code
     *
     * @hibernate.property
     *
     * @return  5-digit zip code
     */
    public String getZipCode() {
        return zip;
    }

    /**
     * Set the 4-digit zip code extension.
     *
     * @param   zip     4-digit zip code
     */
    public void setZipCodeExt(String ext) {
        zip_ext = ext;
    }

    /**
     * Returns the 4-digit zip code extension
     *
     * @hibernate.property
     *
     * @return  4-digit zip code extension
     */
    public String getZipCodeExt() {
        return zip_ext;
    }

}


