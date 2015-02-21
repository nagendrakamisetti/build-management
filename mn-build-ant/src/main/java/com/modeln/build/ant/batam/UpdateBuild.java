package com.modeln.build.ant.batam;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

import com.modeln.batam.connector.wrapper.BuildEntry;

public class UpdateBuild extends AbstractBuildTask {
	
	@Override
	protected void operation(Object object) {
		try {
			connector.updateBuild((BuildEntry) object);
		} catch (IOException e) {
			throw new BuildException("Task failed", e);
		}
	}
	
}
