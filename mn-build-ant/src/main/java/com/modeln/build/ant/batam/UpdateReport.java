package com.modeln.build.ant.batam;

import java.io.IOException;

import com.modeln.batam.connector.SimplePublisher;
import com.modeln.batam.connector.wrapper.TestReport;

public class UpdateReport extends AbstractReportTask {
	@Override
	protected void operation(SimplePublisher connector, Object object) {
		try {
			connector.updateReport((TestReport) object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
