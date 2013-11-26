
/*
 * Build Report JavaScript library
 *
 * The build reports use HTML visibility settings to hide and show errors and
 * messages from the build reports.  The functions specified in this file 
 * perform the visibility actions required for this.
 * 
 * @author Shawn Stafford
 */

/**
 * Toggle the visibility of the element using the stylesheet 
 * visibility attribute.
 *
 * @param   elementId     Unique id of an HTML element
 */
function toggleElement(elementId) {
    if (isVisible(elementId)) {
        hide(elementId);
    } else {
		show(elementId);
    }
}


/**
 * Display the HTML element to the user
 *
 * @param   elementId     Unique id of an HTML element
 */
function show(elementId) {
    getStyleReference(elementId).display="";
}

/**
 * Hide the HTML element from the user
 *
 * @param   elementId     Unique id of an HTML element
 */
function hide(elementId) {
    getStyleReference(elementId).display="none";
}


/**
 * Return a browser-independent reference to the HTML element.
 *
 * @param    elementId     Unique id of an HTML element
 * @return   A document reference to the element
 */
function getElementReference(elementId) {
    var value=false;

    if(document.layers) {
        value=document.layers[elementId];
    } else {
        if(document.all) {
            value = document.all[elementId];
        } else if(document.getElementById) {
            value = document.getElementById(elementId);
        }
    }

    return value;
}

/**
 * Return a browser-independent reference to the HTML element.
 *
 * @param    elementId     Unique id of an HTML element
 * @return   A document reference to the element
 */
function getStyleReference(elementId) {
    var value=false;

    if(getElementReference(elementId)) {
        value=getElementReference(elementId);
        if(!document.layers) {
            value=value.style;
        }
    }

    return value;
}

/**
 * Return the stylesheet visibility value of the element.
 *
 * @param    elementId     Unique id of an HTML element
 * @return   The visibility value of the element
 */
function isVisible(elementId) {
    var vis = getStyleReference(elementId).display;

    return !(vis == "none");
}
