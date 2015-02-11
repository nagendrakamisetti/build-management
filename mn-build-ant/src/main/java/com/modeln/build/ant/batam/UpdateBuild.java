package com.modeln.build.ant.batam;

import java.io.IOException;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.Build;

public class UpdateBuild extends AbstractBuildTask {
	
	@Override
	protected void operation(SimplePublisher connector, Object object) {
		try {
			connector.updateBuild((Build) object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
