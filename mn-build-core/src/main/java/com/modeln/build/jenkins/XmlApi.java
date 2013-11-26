package com.modeln.build.jenkins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




/**
 * This API class provides HTTP-based integration with Jenkins. 
 *
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class XmlApi {

    /** Enables or disables output to the debug print stream */
    private boolean debugEnabled = false;

    /** Output stream used to write debug messages to. */
    private PrintStream debug = null;


    /** Jenkins hostname */
    private URL baseUrl;


    /**
     * Construct an instance for communicating with a specific
     * Jenkins instance.
     *
     * @param    url    Jenkins URL
     */
    public XmlApi(URL url) {
        baseUrl = url;

        // Enable debbuging to a file
        String logfile = "/var/tmp/XmlApi.txt";
        try {
            setDebugOutput(new PrintStream(logfile));
            debugEnable(true);
        } catch (FileNotFoundException nfex) {
            System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
        }

    }


    /**
     * Set the output stream for debugging output.
     *
     * @param  stream   Debugging output stream
     */
    public synchronized void setDebugOutput(PrintStream stream) {
        debug = stream;
    }

    /**
     * Enable SQL debugging by passing in a reference to an output stream.  Any
     * debugging messages will be written to this stream when debugging is enabled.
     *
     * @param  enable   TRUE to enable debug output
     */
    public synchronized void debugEnable(boolean enable) {
        debugEnabled = enable;
    }

    /**
     * Determines if debugging output is available.
     *
     * @return Returns TRUE if debugging is enabled, FALSE otherwise.
     */
    public boolean debugActive() {
        return (debugEnabled && (debug != null));
    }

    /**
     * Writes a debugging message to the debugging output stream if debugging is enabled.
     *
     * @param  str   Debugging output string
     */
    public synchronized void debugWrite(String str) {
        if (debugActive()) {
            debug.println(str);
            debug.flush();
        }
    }


    /**
     * Construct the URL for creating a new job using the base URL and job name.
     *
     * @param   name  Job name
     * @return  Job URL
     */
    public URL getJobCreationUrl(String name) throws MalformedURLException {
        return new URL(baseUrl.toString() + "/createItem?name=" + name);
    }

    /**
     * Construct the URL for querying an existing job using the base URL and job name.
     *
     * @param   name  Job name
     * @return  Job URL
     */
    public URL getJobQueryUrl(String name) throws MalformedURLException {
        return new URL(baseUrl.toString() + "/job/" + name + "/api/xml?depth=1");
    }

    /**
     * Construct the URL for executing an existing job using the base URL and job name.
     *
     * @param   name  Job name
     * @return  Job URL
     */
    public URL getJobRunUrl(String name) throws MalformedURLException {
        return new URL(baseUrl.toString() + "/job/" + name + "/build");
    }

    /**
     * Construct the URL for deleting an existing job using the base URL and job name.
     *
     * @param   name   Job name
     * @return  Job URL
     */
    public URL getJobDeleteUrl(String name) throws MalformedURLException {
        return new URL(baseUrl.toString() + "/job/" + name + "/doDelete");
    }

    /**
     * Create a new job using the specified XML content.
     *
     * @param   name  Job name
     * @param   doc   Job config.xml content
     * @return  HTTP response code 
     */
    public int createJob(String name, Document doc) throws MalformedURLException,IOException {
        int response = send(getJobCreationUrl(name), doc);

        return response;
    }

    /**
     * Initiate the execution of the job.
     *
     * @param   name   Job name
     * @return  HTTP response code
     */
    public int runJob(String name) throws MalformedURLException,IOException {
        int response = post(getJobRunUrl(name));

        return response;
    }

    /**
     * Delete the job.
     *
     * @param  name   Job name
     * @return HTTP response code
     */
    public int deleteJob(String name) throws MalformedURLException,IOException {
        int response = doPost(getJobDeleteUrl(name));

        return response;
    }

    /**
     * Return information about the specified job.
     */
    public Job getJob(String name) {
        Job job = null;
        Document jobdoc = null;

        // Fetch the data from Jenkins
        try {
            jobdoc = get(getJobQueryUrl(name));
            if (jobdoc != null) {
                job = parseJob(jobdoc.getDocumentElement());
            }
        } catch (MalformedURLException mfex) {
            // Invalid URL
        } catch (IOException ioex) {
            // Unable to parse output
        }

        return job;
    }

    /**
     * Parse the XML document and construct the job data object.
     *
     * @param   root   XML document
     * @return  Job data
     */
    private Job parseJob(Element root) {
        Job job = null;

        // Parse the XML
        if (root != null) {
            job = new Job();

            // Parse the job data
            NodeList jobNodes = root.getChildNodes();
            for (int idx = 0; idx < jobNodes.getLength(); idx++) {
                Node node = jobNodes.item(idx);
                if ((node != null) && (node.getNodeName() != null) && (node.getTextContent() != null)) {
                    String nodeName  = node.getNodeName();
                    String nodeValue = node.getTextContent();
                    if (nodeName == "name") {
                        job.setName(nodeValue);
                    } else if (nodeName == "url") {
                        if (nodeValue != null) {
                            try {
                                job.setURL(new URL(nodeValue.trim()));
                            } catch (MalformedURLException mfex) {
                                // Invalid URL
                            }
                        }
                    } else if (nodeName == "buildable") {
                        job.setBuildable(Boolean.parseBoolean(nodeValue));
                    } else if (nodeName == "build") {
                        job.addBuild(parseBuild(node));
                    } else {
                        debugWrite("parseJob: Unhandled node: " + nodeName);
                    }
                } else if (node == null) {
                    debugWrite("parseJob: Current node is null");
                } else {
                    debugWrite("parseJob: Unable to parse current node: name=" + node.getNodeName() + ", value=" + node.getTextContent());
                }
            }

        }

        return job; 
    }

    /**
     * Parse the build data from XML.
     *
     * @param   node   Build XML node
     * @return  Build data
     */
    private Build parseBuild(Node node) {
        Build build = new Build();

        NodeList children = node.getChildNodes();
        for (int idx = 0; idx < children.getLength(); idx++) {
            Node child = children.item(idx);
            if (child != null) {
                String name = child.getNodeName();
                String value = child.getTextContent();
                debugWrite("parseBuild: parsing tag: name=" + name + ", value=" + value);
                if (name.equalsIgnoreCase("fullDisplayName")) {
                    build.setDisplayName(value);
                } else if (name.equalsIgnoreCase("action")) {
                } else if (name.equalsIgnoreCase("building")) {
                    build.setBuilding(Boolean.parseBoolean(value));
                } else if (name.equalsIgnoreCase("duration")) {
                } else if (name.equalsIgnoreCase("id")) {
                } else if (name.equalsIgnoreCase("keepLog")) {
                    build.setKeepLog(Boolean.parseBoolean(value));
                } else if (name.equalsIgnoreCase("number")) {
                } else if (name.equalsIgnoreCase("result")) {
                } else if (name.equalsIgnoreCase("timestamp")) {
                    long timestamp = Long.parseLong(value);
                    build.setDate(new Date(timestamp));
                } else if (name.equalsIgnoreCase("url")) {
                    try {
                        build.setURL(new URL(value));
                    } catch (MalformedURLException mfex) {
                    }
                } else if (name.equalsIgnoreCase("builtOn")) {
                } else if (name.equalsIgnoreCase("changeSet")) {
                } else {
                    debugWrite("parseBuild: Unable to parse current node: name=" + name + ", value=" + value);
                }
            } else {
                debugWrite("parseBuild: Current node is null");
            }
        }

        return build;
    }


    /**
     * Submit an XML API request to the Jenkins instance and retrieve
     * the XML response.
     *
     * @param  url   Jenkins XML API url request
     * @return XML result 
     */
    private Document get(URL url) throws MalformedURLException,IOException {
        Document doc = null; 

        URLConnection conn = url.openConnection();
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.connect();

        // Read the XML reponse from the Jenkins server
        try {
            // Read the response from the server
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    doc = db.parse(conn.getInputStream());        
                }
            }
        } catch (ParserConfigurationException pcex) {
            // Unable to create the document parser 
        } catch (SAXException sex) {
            // Error parsing the XML document
        }

        return doc;
    }



    /**
     * Submit an XML API request to the Jenkins instance.
     *
     * @param   doc    XML API request
     * @return  HTTP response code 
     */
    private int send(URL url, Document doc) throws MalformedURLException,IOException {
        int response = 0;

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", "ISO-8859-1");
        conn.setRequestProperty("Content-Type", "text/xml");

        // Send the XML to the Jenkins server
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(conn.getOutputStream());
            transformer.transform(source, result);

            // Read the response from the server
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                response = httpConn.getResponseCode();
            }

        } catch (TransformerConfigurationException tcex) {
            // Unable to create the XML printer
        } catch (TransformerException tex) {
            // Unable to output the XML
        }

        return response;
    }

    /**
     * Submit an XML API request to the Jenkins instance.
     *
     * @param  url   Jenkins XML API url request
     * @return HTTP response code
     */
    private int post(URL url) throws MalformedURLException,IOException {
        int response = 0;

        URLConnection conn = url.openConnection();
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.connect();

        // Read the response from the server
        if (conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            response = httpConn.getResponseCode();
        }

        return response;
    }

    /**
     * Submit an XML API request to the Jenkins instance.
     *
     * @param  url   Jenkins XML API url request
     * @return HTTP response code
     */
    private int doPost(URL url) throws MalformedURLException,IOException {
        int response = 0;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.connect();

        // Read the response from the server
        response = conn.getResponseCode();

        return response;
    }


}

