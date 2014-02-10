/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.database;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnTable;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.text.SimpleDateFormat;


/**
 * The account table interface defines all of the table and column names
 * used to represent customer accounts. 
 *
 * @author  Shawn Stafford
 */
public class CMnCustomerTable extends CMnTable {


    /** Name of the table used for the customer account information */
    public static final String ACCOUNT_TABLE = "customer_account";

    /** Name of the column that identifies the customer by ID */
    public static final String ACCOUNT_ID = "account_id";

    /** Name of the column that identifies the customer by name */
    public static final String ACCOUNT_NAME = "account_name";

    /** Name of the column that identifies the customer by a short name */
    public static final String ACCOUNT_SHORT_NAME = "short_name";

    /** Name of the column that identifies the customer branch type */
    public static final String ACCOUNT_BRANCH_TYPE = "branch_type";



    /** Name of the table used for the customer environment information */
    public static final String ENV_TABLE = "customer_env";

    /** Name of the column that identifies the customer environment by ID */
    public static final String ENV_ID = "env_id";

    /** Name of the column that identifies the customer environment by name */
    public static final String ENV_NAME = "env_name";

    /** Name of the column that identifies the build ID deployed to that environment */
    public static final String ENV_BUILD_ID = "build_id";

    /** Name of the column that identifies the environment by a short name */
    public static final String ENV_SHORT_NAME = "short_name";



    /** Name of the table used to describe the product deployed to an environment */
    public static final String PRODUCT_TABLE = "release_product";

    /** Name of the column that identifies the product by ID */
    public static final String PRODUCT_ID = "product_id";

    /** Name of the column that identifies the product name */
    public static final String PRODUCT_NAME = "name";

    /** Name of the column that identifies the product description */
    public static final String PRODUCT_DESC = "description";



    /** Singleton instance of the table class */
    private static CMnCustomerTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnCustomerTable getInstance() {
        if (instance == null) {
            instance = new CMnCustomerTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnCustomerTable.txt";
            try {
                instance.setDebugOutput(new PrintStream(logfile));
                instance.debugEnable(true);
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }

        }
        return instance;
    }



    /**
     * Retrieve a list of all customers from the database.
     *
     * @param   conn    Database connection
     *
     * @return  List of customer account objects
     */
    public synchronized Vector<CMnAccount> getAllCustomers(Connection conn)
        throws SQLException
    {
        Vector list = new Vector<CMnAccount>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + ACCOUNT_TABLE + " ORDER BY " + ACCOUNT_NAME + " ASC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getAllCustomers", sql.toString());
            if (rs != null) {
                CMnAccount account = null;
                while (rs.next()) {
                    account = parseCustomerData(rs);

                    // Load environment information for that customer
                    account.setEnvironments(getEnvironments(conn, account.getId().toString()));

                    list.add(account);
                }
            } else {
                getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve customer information from the database.
     *
     * @param   conn    Database connection
     * @param   custId  Primary key used to locate the customer info
     *
     * @return  Customer information
     */
    public synchronized CMnAccount getCustomer(
            Connection conn,
            String custId)
        throws SQLException
    {
        CMnAccount customer = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + ACCOUNT_TABLE +
            " WHERE " + ACCOUNT_ID + "=" + custId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getCustomer", sql.toString());
            if (rs != null) {
                rs.first();
                customer = parseCustomerData(rs);

                // Load environment information for that customer
                customer.setEnvironments(getEnvironments(conn, custId));
            } else {
                getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return customer;
    }


    /**
     * Retrieve customer information from the database using
     * the customer short name.
     *
     * @param   conn    Database connection
     * @param   name    Customer short name 
     *
     * @return  Customer information
     */
    public synchronized CMnAccount getCustomerByName(
            Connection conn,
            String name)
        throws SQLException
    {
        CMnAccount customer = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + ACCOUNT_TABLE +
            " WHERE " + ACCOUNT_SHORT_NAME + " = '" + name + "'"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getCustomerByName", sql.toString());
            if (rs != null) {
                rs.first();
                customer = parseCustomerData(rs);

                // Load environment information for that customer
                customer.setEnvironments(getEnvironments(conn, customer.getId().toString()));
            } else {
                getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return customer;
    }



    /**
     * Retrieve environment information from the database.
     *
     * @param   conn    Database connection
     * @param   envId   Primary key used to locate the environment info
     *
     * @return  Environment information
     */
    public synchronized CMnEnvironment getEnvironment(
            Connection conn,
            String envId)
        throws SQLException
    {
        CMnEnvironment env = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + ENV_TABLE +
            " WHERE " + ENV_ID + "=" + envId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getEnvironment", sql.toString());
            if (rs != null) {
                rs.first();
                env = parseEnvironmentData(rs);
            } else {
                getInstance().debugWrite("Unable to obtain the environment data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain environment data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return env;
    }

    /**
     * Add environment information to the database.
     *
     * @param   conn    Database connection
     * @param   acctId  Customer account ID
     * @param   env     Environment data 
     *
     * @return  Autogenerated ID that identifies the environment 
     */
    public synchronized String addEnvironment(
            Connection conn,
            String acctId,
            CMnEnvironment env)
        throws SQLException
    {
        String envId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + ENV_TABLE + " ");
        sql.append("(" + ACCOUNT_ID);
        if (env.getProduct() != null) sql.append(", " + PRODUCT_ID); 
        if (env.getBuild() != null) sql.append(", " + ENV_BUILD_ID);
        if (env.getShortName() != null) sql.append(", " + ENV_SHORT_NAME);
        sql.append(", " + ENV_NAME + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + acctId + "\"");
        if (env.getProduct() != null) sql.append(", \"" + env.getProduct().getId() + "\"");
        if (env.getBuild() != null)   sql.append(", \"" + env.getBuild().getId() + "\"");
        if (env.getShortName() != null) sql.append(", \"" + env.getShortName() + "\"");
        sql.append(", \"" + env.getName() + "\")");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            envId = executeInsert(st, "addEnvironment", sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to add environment: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }


        return envId;
    }

    /**
     * Add customer information to the database.
     *
     * @param   conn    Database connection
     * @param   cust    Customer data
     *
     * @return  Autogenerated ID that identifies the customer
     */
    public synchronized String addCustomer(
            Connection conn,
            CMnAccount cust)
        throws SQLException
    {
        String custId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + ACCOUNT_TABLE + " ");
        sql.append("(" + ACCOUNT_NAME);
        if (cust.getBranchType() != null) {
            sql.append(", " + ACCOUNT_BRANCH_TYPE);
        }
        sql.append(", " + ACCOUNT_SHORT_NAME + ")");

        sql.append(" VALUES ");
        sql.append("(\"" + cust.getName() + "\"");
        if (cust.getBranchType() != null) {
            sql.append(", \"" + cust.getBranchType() + "\"");
        }
        sql.append(", \"" + cust.getShortName() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            custId = executeInsert(st, "addCustomer", sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to add customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }


        return custId;
    }


    /**
     * Update customer information to the database.
     *
     * @param   conn    Database connection
     * @param   cust    Customer data
     *
     * @return TRUE if the row was updated
     */
    public synchronized boolean updateCustomer(
            Connection conn,
            CMnAccount cust)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();

        sql.append("UPDATE " + ACCOUNT_TABLE + " ");
        sql.append("SET " + ACCOUNT_NAME + "=\"" + cust.getName() + "\" ");
        if (cust.getBranchType() != null) {
            sql.append(", " + ACCOUNT_BRANCH_TYPE + "=\"" + cust.getBranchType() + "\" ");
        }
        sql.append(", " + ACCOUNT_SHORT_NAME + "=\"" + cust.getShortName() + "\" ");
        sql.append("WHERE " + ACCOUNT_ID + "=\"" + cust.getId() + "\"");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateCustomer", sql.toString());
            result = true;
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to update customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }



    /**
     * Update environment information to the database.
     *
     * @param   conn    Database connection
     * @param   env     Environment data
     *
     * @return TRUE if the row was updated
     */
    public synchronized boolean updateEnvironment(
            Connection conn,
            CMnEnvironment env)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();

        sql.append("UPDATE " + ENV_TABLE + " ");
        sql.append("SET " + ENV_NAME + "=\"" + env.getName() + "\" ");
        if (env.getProduct() != null) sql.append(", " + PRODUCT_ID + "=\"" + env.getProduct().getId() + "\" ");
        if (env.getBuild() != null)   sql.append(", " + ENV_BUILD_ID + "=\"" + env.getBuild().getId() + "\" ");
        if (env.getShortName() != null) sql.append(", " + ENV_SHORT_NAME + "=\"" + env.getShortName() + "\" ");
        sql.append("WHERE " + ENV_ID + "=\"" + env.getId() + "\"");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateEnvironment", sql.toString());
            result = true;
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to update environment data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }


    /**
     * Retrieve product information from the database.
     *
     * @param   conn    Database connection
     * @param   prodId  Primary key used to locate the product info
     *
     * @return  Product information
     */
    public synchronized CMnProduct getProduct(
            Connection conn,
            String prodId)
        throws SQLException
    {
        CMnProduct product = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + PRODUCT_TABLE +
            " WHERE " + PRODUCT_ID + "=" + prodId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getProduct", sql.toString());
            if (rs != null) {
                rs.first();
                product = parseProductData(rs);
            } else {
                getInstance().debugWrite("Unable to obtain the product data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain product data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return product;
    }


    /**
     * Retrieve a list of all products from the database.
     *
     * @param   conn    Database connection
     *
     * @return  List of product data objects
     */
    public synchronized Vector<CMnProduct> getAllProducts(Connection conn)
        throws SQLException
    {
        Vector list = new Vector<CMnProduct>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + PRODUCT_TABLE + " ORDER BY " + PRODUCT_NAME + " ASC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getAllProducts", sql.toString());
            if (rs != null) {
                CMnProduct product = null;
                while (rs.next()) {
                    product = parseProductData(rs);
                    list.add(product);
                }
            } else {
                getInstance().debugWrite("Unable to obtain the product data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain product data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve customer environment information from the database.
     *
     * @param   conn    Database connection
     * @param   custId  Primary key used to locate the customer environment info
     *
     * @return  List of customer environments 
     */
    public synchronized Set<CMnEnvironment> getEnvironments(
            Connection conn,
            String custId)
        throws SQLException
    {
        Set envlist = new HashSet<CMnEnvironment>();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + ENV_TABLE + ", " + PRODUCT_TABLE + 
            " WHERE " + PRODUCT_TABLE + "." + PRODUCT_ID + " = " + ENV_TABLE + "." + PRODUCT_ID +
            " AND " + ACCOUNT_ID + "=" + custId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getEnvironments", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    CMnEnvironment envData = parseEnvironmentData(rs);
                    envData.setProduct(parseProductData(rs));

                    // Get information about the currently deployed build
                    String buildId = rs.getString(ENV_BUILD_ID);
                    if (buildId != null) {
                        CMnBuildTable buildTable = CMnBuildTable.getInstance();
                        CMnDbBuildData build = buildTable.getBuild(conn, buildId);
                        envData.setBuild(build);
                    }

                    envlist.add(envData);
                }
            } else {
                getInstance().debugWrite("Unable to obtain the customer environment data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain environment data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return envlist;
    }



    /**
     * Parse the result set to obtain customer information.
     *
     * @param   rs    Result set containing customer data
     *
     * @return  Customer information
     */
    public static CMnAccount parseCustomerData(ResultSet rs)
        throws SQLException
    {
        CMnAccount data = new CMnAccount();

        int id = rs.getInt(ACCOUNT_TABLE + "." + ACCOUNT_ID);
        data.setId(id);

        String name = rs.getString(ACCOUNT_TABLE + "." + ACCOUNT_NAME);
        data.setName(name);

        String shortName = rs.getString(ACCOUNT_TABLE + "." + ACCOUNT_SHORT_NAME);
        data.setShortName(shortName);

        String branchType = rs.getString(ACCOUNT_TABLE + "." + ACCOUNT_BRANCH_TYPE);
        data.setBranchType(branchType);

        return data;
    }


    /**
     * Parse the result set to obtain customer environment information.
     *
     * @param   rs    Result set containing customer environment data
     *
     * @return  Customer environment information
     */
    public static CMnEnvironment parseEnvironmentData(ResultSet rs)
        throws SQLException
    {
        CMnEnvironment data = new CMnEnvironment();

        int id = rs.getInt(ENV_TABLE + "." + ENV_ID);
        data.setId(new Integer(id));

        String name = rs.getString(ENV_TABLE + "." + ENV_NAME);
        data.setName(name);

        String shortname = rs.getString(ENV_TABLE + "." + ENV_SHORT_NAME);
        data.setShortName(shortname);

        return data;
    }


    /**
     * Parse the result set to obtain product information.
     *
     * @param   rs   Result set containing product data
     *
     * @return  Product information
     */
    public static CMnProduct parseProductData(ResultSet rs) 
        throws SQLException
    {
        CMnProduct product = new CMnProduct();
        int prodId = rs.getInt(PRODUCT_TABLE + "." + PRODUCT_ID);
        product.setId(prodId);

        String prodName = rs.getString(PRODUCT_TABLE + "." + PRODUCT_NAME);
        product.setName(prodName);

        String prodDesc = rs.getString(PRODUCT_TABLE + "." + PRODUCT_DESC);
        product.setDescription(prodDesc);

        return product;
    }


}

