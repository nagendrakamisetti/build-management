package com.modeln.build.ant.test;

import com.modeln.build.ant.report.ReportParseCriteria;

import junit.framework.TestCase;

import org.apache.tools.ant.Target;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.taskdefs.Echo;

/**
 * Test methods for verifying the reporting methods.
 *
 * @author Shawn Stafford (sstafford@modeln.com)
 */
public class ReportTest extends TestCase {

    public Echo getEchoTask() {
        Project project = new Project();
        project.setName("testProject");

        Target target = new Target();
        target.setName("testTarget");
        target.setProject(project);

        Echo.EchoLevel level = new Echo.EchoLevel();
        level.setValue("error");
        Echo echoTask = new Echo();
        echoTask.setOwningTarget(target);
        echoTask.setLevel(level);
        echoTask.setTaskName("echo");
        echoTask.setTaskType("echo");
        echoTask.setMessage("This is a sample message.");

        return echoTask;
    }

    public void testNumberCriteria() throws Exception {
        Echo echoTask = getEchoTask();
        BuildEvent event = new BuildEvent(echoTask);
        event.setMessage("Population completed with 2 errors.", Project.MSG_INFO);

        ReportParseCriteria criteria = new ReportParseCriteria();

        // Verify that the regular expression parsing can find the match
        criteria.setText("with [1-9] errors");
        assertEquals(true, criteria.matches(event));

        // Verify that the regular expression does not produce false matches
        event.setMessage("Population completed with no errors.", Project.MSG_INFO);
        assertEquals(false, criteria.matches(event));
    }

    public void testJavadocCriteria() throws Exception {
        Echo echoTask = getEchoTask();
        BuildEvent event = new BuildEvent(echoTask);
        event.setMessage("/path/SomeFile.java:28: warning - Tag @link: reference not found: com.xyz.SomeClass", Project.MSG_INFO);

        ReportParseCriteria criteria = new ReportParseCriteria();

        // Verify that the regular expression parsing can find the match
        criteria.setText(": warning -");
        assertEquals(true, criteria.matches(event));

        // Verify that the regular expression does not produce false matches
        event.setMessage("This is a false error: some error", Project.MSG_INFO);
        assertEquals(false, criteria.matches(event));
    }

}
