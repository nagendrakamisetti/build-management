<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchFix"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchOwner"%>
<%@ page import="com.modeln.build.common.data.product.CMnBaseFixDependency"%>
<%@ page import="com.modeln.build.jenkins.Job"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formPatchUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnDependencyAdd");
    URL formDeleteUrl = new URL(appUrl + "/patch/CMnDependencyDelete");
    URL formImageUrl = new URL(imgUrl);

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);

    String pageTitle = "Update Dependencies";
    

    //
    // Construct the request form and populate it with data
    //
    CMnPatchRequestForm patchForm = new CMnPatchRequestForm(formPatchUrl, formImageUrl);
    patchForm.setPostEnabled(false);
    patchForm.setAdminMode(admin);
    patchForm.setInputMode(false);
    if (patch != null) {
        patchForm.setEnvironments(patch.getCustomer().getEnvironments());
        patchForm.setValues(patch);
    }

    CMnFixDependencyForm dependencyForm = new CMnFixDependencyForm(formSubmitUrl, formImageUrl);
    dependencyForm.setDeleteUrl(formDeleteUrl);
    dependencyForm.setAdminMode(admin);
    dependencyForm.setInputMode(false);
    dependencyForm.setValues(request);

    int svgWidth = 500;
    int svgHeight = 500;

%>
<html>
<head>
  <title><%= pageTitle %></title>
  <%@ include file="../stylesheet.html" %>
  <%@ include file="../javascript.html" %>
  <script src="<%= d3Url %>"></script>
  <style>
    .link {
        fill: none;
        stroke: #666;
        stroke-width: 1.5px;
    }

    #MERGE {
        fill: black;
    }

    #COMPILE {
        fill: black;
    }

    #FUNCTIONAL {
        fill: green;
    }

    .link.TEST {
        stroke-dasharray: 0,2 1;
    }


    circle {
        fill: lightgray;
        stroke: #333;
        stroke-width: 1.5px;
    }

    text {
        font: 10px sans-serif;
        pointer-events: none;
        text-shadow: 0 1px 0 #fff, 1px 0 0 #fff, 0 -1px 0 #fff, -1px 0 0 #fff;
    }

  </style>
</head>

<body>
  <table border="0" width="100%">
    <tr>
      <td>
<%@ include file="../header.jsp" %>
      </td>
    </tr>

    <tr>
      <td align="left">
      <h3><%= pageTitle %></h3><br>

      <!-- =============================================================== -->
      <!-- Patch information and status                                    -->
      <!-- =============================================================== -->
      <table border="0" cellspacing="0" cellpadding="5" width="45%">
        <tr>
          <td>
<%= patchForm.getTitledBorder("Patch Information", patchForm.getPatchSummaryTable()) %>
          </td>
        </tr>
        <tr>
          <td>
<%
        try {
            out.println(dependencyForm.getTitledBorder("Fix Dependencies", dependencyForm.toString()));
        } catch (Exception ex) {
            out.println("JSP Exception: " + ex);
            out.println("<pre>\n");
            StackTraceElement[] lines = ex.getStackTrace();
            for (int idx = 0; idx < lines.length; idx++) {
                out.println(lines[idx] + "\n");
            }
            out.println("</pre>\n");
        }
%>
          </td>
        </tr>

        <tr>
          <td>
            <svg width="<%= svgWidth %>" height="<%= svgHeight %>"/>
          </td>
        </tr>
      </table>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

  <script language="JavaScript" type="text/javascript">

    var linktypes = [
<%  // Convert the Java array to javascript
    String[] types = CMnBaseFixDependency.getTypeList();
    if (types != null) {
        for (int typeIdx = 0; typeIdx < types.length; typeIdx++) {
            out.print("\"" + types[typeIdx] + "\"");
            if (typeIdx + 1 < types.length) {
                out.print(", ");
            }
        }
    }
%>
    ];

    var dataset = [
<%  // Generate the JavaScript data for displaying the graph
    boolean firstdep = true;
    if ((patch != null) && (patch.getFixes() != null)) {
        Enumeration fixlist = patch.getFixes().elements();
        while (fixlist.hasMoreElements()) {
            CMnPatchFix fix = (CMnPatchFix) fixlist.nextElement();
            Vector<CMnBaseFixDependency> dependencies = fix.getDependencies();
            // Display the list of existing dependencies
            if (dependencies != null) {
                Enumeration list = dependencies.elements();
                while (list.hasMoreElements()) {
                    CMnBaseFixDependency dep = (CMnBaseFixDependency) list.nextElement();
                    if (firstdep) {
                        firstdep = false;
                    } else {
                        out.println(",");
                    }
                    out.print("{ ");
                    out.print("source: \"" + fix.getBugId() + "\", ");
                    out.print("target: \"" + dep.getBugId() + "\", ");
                    out.print("type: \""   + dep.getType().toString() + "\"");
                    out.print(" }");
                }
            }
        }
    }
%>
    ];

    // Hashtable containing the list of node names
    var nodes = {};
    dataset.forEach(function(item) {
        // Create each undefined node from the source name
        if (!nodes[item.source]) {
            nodes[item.source] = {name: item.source, checked: false};
        }

        if (!nodes[item.target]) {
            nodes[item.target] = {name: item.target, checked: false};
        }
    });

    // Array of objects representing relationships in the graph
    var linkset = new Array();
    dataset.forEach(function(item) {
        // Add a link to the list of links in the tree 
        linkset.push({
            source: nodes[item.source],   // pointer to the source node 
            target: nodes[item.target],   // pointer to the target none
            type: item.type               // type of relationship between source and target
        }); 
    }); 


    /* ==========================================================
     * D3 Visualization Functions
     * ==========================================================
     */

        var width  = <%= svgWidth %>;
        var height = <%= svgHeight %>;

        var force = d3.layout.force()
            .nodes(d3.values(nodes))
            .links(linkset)
            .size([width, height])
            .linkDistance(60)
            .charge(-300)
            .on("tick", tick)
            .start();

        var svg = d3.select("body").selectAll("svg")
            .attr("width", width)
            .attr("height", height);

        // Per-type markers, as they don't inherit styles.
        svg.append("defs").selectAll("marker")
            .data(linktypes)
            .enter()
            .append("marker")
            .attr("id", function(d) { return d; })
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 15)
            .attr("refY", -1.5)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .append("path")
           .attr("d", "M0,-5L10,0L0,5");

       var path = svg.append("g").selectAll("path")
           .data(force.links())
           .enter()
           .append("path")
           .attr("class", function(d) { return "link " + d.type; })
           .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

       var circle = svg.append("g").selectAll("circle")
           .data(force.nodes())
           .enter()
           .append("circle")
           .attr("id", function(d) { return "nodeid" + d.name; })
           .attr("r", 6)
           .on("click", showDependencies)
           .on("dblclick", clearDependencies)
           .call(force.drag);

        var text = svg.append("g").selectAll("text")
            .data(force.nodes())
            .enter()
            .append("text")
            .attr("x", 8)
            .attr("y", ".31em")
            .text(function(d) { return d.name; });

    // Use elliptical arc path segments to doubly-encode directionality.
    function tick() {
        path.attr("d", linkArc);
        circle.attr("transform", transform);
        text.attr("transform", transform);
    }

    function linkArc(d) {
        var dx = d.target.x - d.source.x,
            dy = d.target.y - d.source.y,
            dr = Math.sqrt(dx * dx + dy * dy);
        return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
    }

    function transform(d) {
        return "translate(" + d.x + "," + d.y + ")";
    }

    function clearDependencies() {
        d3.selectAll("circle").style("fill", "lightgray");

        // Reset the inspection status of each node so that the
        // graph traversal algorithm can check them again
        var keys = Object.keys(nodes);
        keys.forEach(function(key) {
            nodes[key].checked = false;
        });
    }

    // Visually highlight any nodes pointing to the specified node
    function showDependencies(d) {
        //alert(d.name);
        clearDependencies();

        // Recursively mark all of the dependent fixes as selected
        selectDependencies(d);

        // Mark the clicked circle
        d3.select(this).style("fill", "red");

    }


    /* ==========================================================
     * Data Manipulation Functions
     * ==========================================================
     */

    // Recursive function which highlights nodes which point to this one 
    function selectDependencies(node) {
        if (!node.checked) {
            // Mark the node as checked to prevent an endless loop
            node.checked = true;

            // Determine if the node is the target of any links
            linkset.forEach(function(link) {
                if (link.target == node) {
                    // Follow the link to the next node
                    selectDependencies(link.source);
                } else {
                    // Highlight the current node as being pointed to
                    d3.select("#nodeid" + node.name).style("fill", "green");
                }
            });
        }
    }

  </script>


</body>
</html>
