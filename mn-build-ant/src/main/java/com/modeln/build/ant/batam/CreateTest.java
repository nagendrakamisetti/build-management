package com.modeln.build.ant.batam;

import java.io.IOException;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.TestInstance;

public class CreateTest extends AbstractTestTask {

	@Override
	protected void operation(SimplePublisher connector, Object object) {
		try {
			connector.createTest((TestInstance) object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
