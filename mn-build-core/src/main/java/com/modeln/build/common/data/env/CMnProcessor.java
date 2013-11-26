package com.modeln.build.common.data.env;


/**
 * The processor information represensts a processor architecture
 * that forms the foundation for operating systems and software. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProcessor {

    /** Identifies the CPU as an unknown architecture */
    public static final int ARCH_UNKNOWN_TYPE = 0;

    /** Identifies the CPU as a 32-bit x86 compatible architecture */
    public static final int ARCH_X86_TYPE = 1;

    /** Identifies the CPU as a 64-bit x86 compatible architecture */
    public static final int ARCH_AMD64_TYPE = 2;

    /** Identifies the CPU as a Sun SPARC architecture */
    public static final int ARCH_SPARC_TYPE = 3;



    /** Identifies the CPU as an unknown architecture */
    public static final String ARCH_UNKNOWN_STR = "other";

    /** Identifies the CPU as a 32-bit x86 compatible architecture */
    public static final String ARCH_X86_STR = "x86";

    /** Identifies the CPU as a 64-bit x86 compatible architecture */
    public static final String ARCH_AMD64_STR = "amd64";

    /** Identifies the CPU as a Sun SPARC architecture */
    public static final String ARCH_SPARC_STR = "sparc";




    /** Auto-generated ID used to identify a CPU */
    private Integer id;

    /** Type of instruction set used by the CPU */
    private int arch;

    /** Comments associated with this CPU */
    private String comments;


    /**
     * Set the ID used to look-up the CPU information.
     *
     * @param   id   Unique account ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the CPU ID used to look-up the CPU information.
     *
     * @return ID for the CPU
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the CPU architecture comments.
     *
     * @param  text    CPU architecture comments
     */
    public void setComments(String text) {
        comments = text;
    }

    /**
     * Return the CPU architecture comments.
     *
     * @return CPU architecture comments
     */
    public String getComments() {
        return comments;
    }


    /**
     * Set the CPU architecture.
     *
     * @param  type    CPU architecture type
     */
    public void setArchitecture(int type) {
        arch = type;
    }


    /**
     * Set the CPU architecture. 
     *
     * @param  type    CPU architecture type 
     */
    public void setArchitecture(String type) {
        arch = getArchitecture(type);
    }

    /**
     * Return the CPU architecture. 
     *
     * @return CPU architecture type 
     */
    public int getArchitecture() {
        return arch;
    }


    /**
     * Convert the string to the CPU architecture type.
     *
     * @param  type   CPU architecture type
     * @return CPU architecture type
     */
    public static int getArchitecture(String type) {
        int typeValue = ARCH_UNKNOWN_TYPE;
        if (type.equals(ARCH_X86_STR)) {
            typeValue = ARCH_X86_TYPE;
        } else if (type.equals(ARCH_AMD64_STR)) {
            typeValue = ARCH_AMD64_TYPE;
        } else if (type.equals(ARCH_SPARC_STR)) {
            typeValue = ARCH_SPARC_TYPE;
        }
        return typeValue;
    }


    /**
     * Convert the CPU architecture type to a string. 
     *
     * @param  type   CPU architecture type
     * @return CPU architecture type
     */
    public static String getArchitecture(int type) {
        switch (type) {
            case ARCH_X86_TYPE:     return ARCH_X86_STR;
            case ARCH_AMD64_TYPE:   return ARCH_AMD64_STR;
            case ARCH_SPARC_TYPE:   return ARCH_SPARC_STR;
            default: return ARCH_UNKNOWN_STR;
        }
    }


}

