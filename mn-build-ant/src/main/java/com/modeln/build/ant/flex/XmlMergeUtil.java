/*
 * Copyright 2000-2008 by Model N, Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.flex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides methods for merging XML documents. 
 *
 * @author Shawn Stafford
 */
public final class XmlMergeUtil {


    /** 
     * Merge the XML by appending all child elements of one document to
     * another document. 
     */
    public static final int APPEND = 0;

    /**
     * Merge the XML by adding all child elements of one document into 
     * a relatively similar position in another document.  This is somewhat
     * similar to shuffling two decks of cards by alternately combining the 
     * cards from each deck into a central pile.
     */
    public static final int COLLATE = 1;

    /**
     * Merge the XML by combining the contents of any elements that have
     * the same tag name.  All elements that do not have matching elements
     * will be appended to the merged document.
     */
    public static final int MERGE = 2;

    /**
     * Merge the XML by overwriting any matching node from the first
     * document with the corresponding node from the second document.
     * A matching node is any node with the same tag name.
     */
    public static final int OVERWRITE = 3;


    /**
     * Type of merge action to perform when matching nodes are found. 
     */
    private int nodeMode = OVERWRITE;

    /**
     * Type of merge action to perform when combining node content. 
     */
    private int contentMode = OVERWRITE;


    /**
     * Construct the merge utility.
     * 
     * @param  node     Action to perform when merging nodes
     * @param  content  Action to perform when merging node content
     */
    public XmlMergeUtil(int node, int content) {
        nodeMode = node;
        contentMode = content;
    }

    /**
     * Determine if the two nodes can be merged.  Nodes can be merged if all
     * of the following criteria can be met:
     *
     * - Both nodes are of the same type
     * - Both nodes have the same name
     *
     * @param  node1  Document node
     * @param  node2  Document node
     */
    private boolean isMergePossible(Node node1, Node node2) {
        boolean possible = true;

        // Fail if node types do not match
        if (node1.getNodeType() != node2.getNodeType()) {
            return false;
        }

        // Fail if node names do not match
        if (!node1.getNodeName().equals(node2.getNodeName())) {
            return false;
        }

        return possible;
    }


    /**
     * Merge the content of two nodes.
     */
    private String mergeContent(Node node1, Node node2) {
        String content = null;

        String content1 = node1.getNodeValue();
        String content2 = node2.getNodeValue();

        switch (contentMode) {
            case APPEND:
                content = content1 + content2;
                break;
            case COLLATE:
                System.out.println("COLLATE mode not implemented for node content.");
                break;
            case MERGE:
                System.out.println("MERGE mode not implemented for node content.");
                break;
            case OVERWRITE:
                content = content2;
                break;
            default:
                System.out.println("Unknown content mode: " + contentMode);
        }

        //System.out.println("Merged node content: node1=" + content1 + ", node2=" + content2 + ", merged=" + content);

        return content;
    }


    /**
     * Merges two sets of DOM nodes as the children of a combined node by
     * combining nodes of the same name in each list.  For example, if each list has
     * 3 nodes but only one node has the same name in both lists, the resulting node 
     * will have 5 children.
     *
     * <pre>
     * Node 1:  a b c
     * Node 2:  c d e
     * Result:  a b c d e
     * </pre>
     *
     * @param  parent  Parent node
     * @param  list1   List of child nodes
     * @param  list2   List of child nodes
     */
    private void mergeChildren(Node parent, NodeList list1, NodeList list2) {
        Node mergedNode = null;
        Node currentNode1 = null;
        Node currentNode2 = null;

        // Keep track of whether the items each list have been merged
        boolean[] matched1 = new boolean[list1.getLength()];
        boolean[] matched2 = new boolean[list2.getLength()];
        for (int idx = 0; idx < list1.getLength(); idx++) {
            matched1[idx] = false;
        }
        for (int idx = 0; idx < list2.getLength(); idx++) {
            matched2[idx] = false;
        }

        // Iterate through each child to determine if it should be merged in the list
        for (int idx1 = 0; idx1 < list1.getLength(); idx1++) {
            currentNode1 = list1.item(idx1);
            for (int idx2 = 0; idx2 < list2.getLength(); idx2++) {
                currentNode2 = list2.item(idx2);
                // Merge the two nodes if they match
                if (!matched1[idx1] && !matched2[idx2] && isMergePossible(currentNode1, currentNode2)) {
                    mergedNode = mergeNode(currentNode1, currentNode2);
                    //System.out.println("Adding merged node to parent: parent=" + parent.getNodeName() + ", child=" + mergedNode.getNodeName());
                    parent.appendChild(mergedNode);
                    matched1[idx1] = true;
                    matched2[idx2] = true; 
                }
            }

            // If no match was found for the node, add the unmodified version to the parent node
            if (!matched1[idx1]) {
                mergedNode = currentNode1.cloneNode(true);
                //System.out.println("Adding unmatched node1 to parent: parent=" + parent.getNodeName() + ", child=" + mergedNode.getNodeName());
                parent.appendChild(mergedNode);
            }
        }

        // Add all remaining elements in list 2 to the parent
        for (int idx = 0; idx < list2.getLength(); idx++) {
            if (!matched2[idx]) {
                mergedNode = list2.item(idx).cloneNode(true);
                //System.out.println("Adding unmatched node2 to parent: parent=" + parent.getNodeName() + ", child=" + mergedNode.getNodeName());
                parent.appendChild(mergedNode);
            }
        }

    }


    /**
     * Merges two DOM nodes into a combined node by combining the contents
     * of the two nodes together.  For example, if each node has 3 children,
     * the nodes will be combined into a single node that also contains 
     * 3 children.  The text and content of the nodes will be concatenated.
     *
     * <pre>
     * Node 1:  a b c
     * Node 2:  d e f
     * Result:  ad be fc
     * </pre>
     *
     * @param  node1  Document node
     * @param  node2  Document node
     * @return Merged node contents
     */
    private Node mergeNode(Node node1, Node node2) {
        Node merged = node1.cloneNode(false);
        //System.out.println("Merging " + node1.getNodeName() + " node with " + node2.getNodeName() + "...");

        // Merge the child nodes
        Node currentNode1 = null;
        Node currentNode2 = null;
        NodeList list1 = null;
        NodeList list2 = null;
        if (node1.hasChildNodes() && node2.hasChildNodes()) {
            mergeChildren(merged, node1.getChildNodes(), node2.getChildNodes());
        } else if (node1.hasChildNodes()) {
            list1 = node1.getChildNodes();

            // Keep all of the children of node1 unchanged
            for (int idx1 = 0; idx1 < list1.getLength(); idx1++) {
                currentNode1 = list1.item(idx1);
                //System.out.println("Adding node1 to parent: parent=" + merged.getNodeName() + ", child=" + node1.getNodeName());
                merged.appendChild(currentNode1.cloneNode(true));
            }
        } else if (node2.hasChildNodes()) {
            list2 = node2.getChildNodes();

            // Merge in all of the children of node2
            for (int idx2 = 0; idx2 < list2.getLength(); idx2++) {
                currentNode2 = list2.item(idx2);
                //System.out.println("Adding node1 to parent: parent=" + merged.getNodeName() + ", child=" + node2.getNodeName());
                merged.appendChild(currentNode2.cloneNode(true));
            }
        }

        // Merge the node value
        if (!merged.hasChildNodes()) {
            merged.setTextContent(mergeContent(node1, node2));
        }

        // Merge the node attributes


        return merged;
    }

    /**
     * Merges two sets of DOM nodes as the children of a combined node by
     * collating the nodes in each list.  For example, if each list has 
     * 3 nodes, the resulting node will have 6 children.
     *
     * <pre>
     * Node 1:  a b c
     * Node 2:  d e f
     * Result:  a d b e c f
     * </pre>
     *
     * @param  parent  Parent node
     * @param  list1   List of child nodes
     * @param  list2   List of child nodes
     */
    private void collateChildren(Node parent, NodeList list1, NodeList list2) {
        Node currentNode1 = null;
        Node currentNode2 = null;

        // Examine each child of node1 and compare with each child of node2
        int maxSize = list1.getLength();
        if (list2.getLength() > maxSize) {
            maxSize = list2.getLength();
        }
        for (int idx = 0; idx < maxSize; idx++) {
            // Obtain the two nodes for comparison
            if (list1.getLength() > idx) {
                currentNode1 = list1.item(idx);
            } else {
                currentNode1 = null;
            }
            if (list2.getLength() > idx) {
                currentNode2 = list2.item(idx);
            } else {
                currentNode2 = null;
            }

            if ((currentNode1 != null) && (currentNode2 != null) && isMergePossible(currentNode1, currentNode2)) {
                parent.appendChild(collateNode(currentNode1, currentNode2));
            } else {
                // Move both nodes to the parent
                if (currentNode1 != null) {
                    parent.appendChild(currentNode1.cloneNode(true));
                }
                if (currentNode2 != null) {
                    parent.appendChild(currentNode2.cloneNode(true));
                }
            }
        }

    }

    /**
     * Merges two DOM nodes into a combined node by collating the contents
     * of each node.  For example, if each node has 3 children, the children
     * will be combined into a single node containing 6 children.
     * 
     * <pre>
     * Node 1:  a b c
     * Node 2:  d e f
     * Result:  a d b e c f
     * </pre>
     *
     * @param  node1  Document node
     * @param  node2  Document node
     * @return Merged node contents
     */
    private Node collateNode(Node node1, Node node2) {
        Node merged = node1.cloneNode(false);

        //System.out.println("Collating " + node1.getNodeName() + " node with " + node2.getNodeName() + "...");

        // Merge the node value

        // Merge the node attributes 

        // Merge the child nodes 
        Node currentNode1 = null;
        Node currentNode2 = null;
        NodeList list1 = null;
        NodeList list2 = null;
        if (node1.hasChildNodes() && node2.hasChildNodes()) {
            collateChildren(merged, node1.getChildNodes(), node2.getChildNodes());
        } else if (node1.hasChildNodes()) {
            list1 = node1.getChildNodes();

            // Keep all of the children of node1 unchanged
            for (int idx1 = 0; idx1 < list1.getLength(); idx1++) {
                currentNode1 = list1.item(idx1);
                merged.appendChild(currentNode1.cloneNode(true));
            }
        } else if (node2.hasChildNodes()) {
            list2 = node2.getChildNodes();

            // Merge in all of the children of node2
            for (int idx2 = 0; idx2 < list2.getLength(); idx2++) {
                currentNode2 = list2.item(idx2);
                merged.appendChild(currentNode2.cloneNode(true));
            }
        }

        return merged;
    } 




    /**
     * Merge two configuration documents together.
     *
     * @param  doc1   Document instance
     * @param  doc2   Document instance
     */
    public Document merge(Document doc1, Document doc2) {
        Document result = null;
        if ((doc1 != null) && (doc2 != null)) {
            // Construct a new document to contain the merged nodes
            try {
                result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException pcex) {
                System.out.println("Failed to construct a new document.");
                pcex.printStackTrace();
            }

            // Merge the contents of the two documents
            if ((result != null) && isMergePossible((Node) doc1, (Node) doc2)) {
                Node currentNode = null;

                Node element1 = null;
                NodeList list1 = doc1.getChildNodes();
                for (int idx1 = 0; idx1 < list1.getLength(); idx1++) {
                    currentNode = list1.item(idx1);
                    //System.out.println("Attempting to add node from list1: " + currentNode.getNodeName());
                    currentNode = result.importNode(currentNode, true);
                    switch (currentNode.getNodeType()) {
                        case Node.ELEMENT_NODE:
                            element1 = currentNode;
                            break;
                        default:
                            //result.appendChild(currentNode);
                    }
                }

                Node element2 = null;
                NodeList list2 = doc2.getChildNodes();
                for (int idx2 = 0; idx2 < list2.getLength(); idx2++) {
                    currentNode = list2.item(idx2);
                    //System.out.println("Attempting to add node from list2: " + currentNode.getNodeName());
                    currentNode = result.importNode(currentNode, true);
                    switch (currentNode.getNodeType()) {
                        case Node.ELEMENT_NODE:
                            element2 = currentNode;
                            break;
                        default:
                            //result.appendChild(currentNode);
                    }
                }

                Node mergedElement = null;
                switch (nodeMode) {
                    case APPEND:
                        System.out.println("APPEND mode has not been implemented.");
                        break;
                    case COLLATE: 
                        mergedElement = collateNode(element1, element2);
                        //System.out.println("COLLATE mode has not been implemented.");
                        break;
                    case MERGE:
                        mergedElement = mergeNode(element1, element2);
                        break;
                    case OVERWRITE:
                        System.out.println("OVERWRITE mode has not been implemented.");
                        break;
                    default:
                        System.out.println("Unknown merge type: " + nodeMode);
                }

                // Add the combined root element to the document
                if (mergedElement != null) {
                    result.appendChild(mergedElement);
                }

            } else {
                System.out.println("Unable to merge XML documents.");
            }
        } else if (doc1 != null) {
            result = doc1;
        } else if (doc2 != null) {
            result = doc2;
        }

        return result;
    }

    /**
     * Parse the XML file and return a document object.
     *
     * @param  file   XML file to be parsed
     * @return Document object representing the contents of the XML file
     */
    public Document parse(File file) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    }

    /**
     * Write the XML document to stdout.
     *
     * @param doc   Document object to be printed
     */
    public void print(Document doc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source source = new DOMSource(doc);
            Result output = new StreamResult(System.out);
            transformer.transform(source, output);
        } catch (TransformerException tex) {
            tex.printStackTrace();
        }
    }


    /**
     * Write the XML document to a file. 
     *
     * @param doc  Document object to be saved
     * @param file File to write the data to
     */
    public void write(Document doc, File outfile) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(outfile);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source source = new DOMSource(doc);
            Result output = new StreamResult(writer);
            transformer.transform(source, output);

            System.out.println("Configuration file written to " + outfile);
        } catch (TransformerException tex) {
            tex.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }

    }


}

