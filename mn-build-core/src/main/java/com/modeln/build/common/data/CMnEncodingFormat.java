package com.modeln.build.common.data;

/**
 * The encoding format identifies the process used to encrypt or encode data.
 * This information should be stored along with the encrypted data (such as
 * user passwords) so the system is able to interpret the encrypted data 
 * correctly.
 *
 * @hibernate.class table="encoding_type"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnEncodingFormat {

    /** Auto-generated ID used to identify the password type */
    private Integer id;
    
    /** Type of encoding or encryption used. */
    private String format;

    /** Encryption provider. */
    private String provider;
    
    /**
     * Set the ID used to look-up the password type.
     *
     * @param  id   Unique ID of the password format
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the password type.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the password type
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Set the encryption or encoding format used to create the password.
     *
     * @param  format  Encryption or encoding format
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * Return the encryption or encoding format used to create the password.
     *
     * @hibernate.property
     *
     * @return Encryption or encoding format of the password string
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the provider or implementation for the encryption format.
     *
     * @param  provider  Encryption or encoding format
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    /**
     * Return the implementation provider
     *
     * @hibernate.property
     *
     * @return Encryption format provider
     */
    public String getProvider() {
        return provider;
    }
    
}

