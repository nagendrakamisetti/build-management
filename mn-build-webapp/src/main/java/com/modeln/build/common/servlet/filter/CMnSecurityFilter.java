package com.modeln.build.common.servlet.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The authentication filter is used to determine if the user has been
 * authorized to use a protected resource.  If the user has already
 * logged in to an authentication system, the user login object will
 * be obtained from a cookie and verified.  If the authentication 
 * cookie is valid, the request will be forwarded to the appropriate
 * resource.  If the authentication cookie is invalid or the user has
 * not logged in, the response will be forwarded to a login page.
 * <p>
 * The filter is configured by specifying init-param values within the
 * filter definition of the web.xml file.  The following configuration
 * parameters may be used:
 * <dl>
 *   <dt><b>login_page</b></dt>
 *   <dd>
 *     The login page is the URI to which the user should be redirected
 *     if authentication fails.
 *   </dd>
 * </dl>
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnSecurityFilter implements Filter {

	/** The Log4J logger is used to write debugging and log messages */
	private Log logger = LogFactory.getLog(this.getClass());

    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException 
    {
        String contextPath = ((HttpServletRequest)req).getContextPath();
        String requestUri = ((HttpServletRequest)req).getRequestURI();
        logger.debug("Processing security filter: " +
            "contextPath = " + contextPath +
            "requestUri = " + requestUri);
		
        String username = ((HttpServletRequest)req).getRemoteUser();
        if (username != null) {
            // Load the user account information if it does not already
            // exist in the session
		}
	}

    public void destroy() {} 

}

