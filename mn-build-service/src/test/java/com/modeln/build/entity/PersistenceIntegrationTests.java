package com.modeln.build.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.modeln.build.entity.auth.GroupIntegrationTest;
import com.modeln.build.entity.auth.UserIntegrationTest;
import com.modeln.build.entity.build.BuildEventCriteriaIntegrationTest;
import com.modeln.build.entity.build.BuildEventIntegrationTest;
import com.modeln.build.entity.build.BuildMetricIntegrationTest;
import com.modeln.build.entity.build.BuildStatusNoteIntegrationTest;
import com.modeln.build.entity.build.BuildIntegrationTest;
import com.modeln.build.entity.customer.AccountIntegrationTest;
import com.modeln.build.entity.customer.EnvironmentIntegrationTest;
import com.modeln.build.entity.customer.ProductIntegrationTest;
import com.modeln.build.entity.deploy.DeployEventCriteriaIntegrationTest;
import com.modeln.build.entity.deploy.DeployEventIntegrationTest;
import com.modeln.build.entity.deploy.DeployMetricIntegrationTest;
import com.modeln.build.entity.feature.FeatureAreaMapIntegrationTest;
import com.modeln.build.entity.feature.FeatureAreaIntegrationTest;
import com.modeln.build.entity.feature.FeatureReviewIntegrationTest;
import com.modeln.build.entity.release.ReleaseSummaryIntegrationTest;
import com.modeln.build.entity.test.act.ActBlacklistIntegrationTest;
import com.modeln.build.entity.test.act.ActStoryMapIntegrationTest;
import com.modeln.build.entity.test.act.ActSuiteIntegrationTest;
import com.modeln.build.entity.test.act.ActIntegrationTest;
import com.modeln.build.entity.test.act.ActTestCaseMapIntegrationTest;
import com.modeln.build.entity.test.flex.FlexTestSuiteIntegrationTest;
import com.modeln.build.entity.test.flex.FlexTestIntegrationTest;
import com.modeln.build.entity.test.uit.UITestStepIntegrationTest;
import com.modeln.build.entity.test.uit.UITestSuiteIntegrationTest;
import com.modeln.build.entity.test.uit.UITestIntegrationTest;
import com.modeln.build.entity.test.ut.UnitTestBlacklistIntegrationTest;
import com.modeln.build.entity.test.ut.UnitTestSuiteIntegrationTest;
import com.modeln.build.entity.test.ut.UnitTestIntegrationTest;

@RunWith(Suite.class)
@SuiteClasses({GroupIntegrationTest.class,
	UserIntegrationTest.class,
	AccountIntegrationTest.class,
	ProductIntegrationTest.class,
	EnvironmentIntegrationTest.class,
	BuildIntegrationTest.class,
	BuildEventCriteriaIntegrationTest.class,
	BuildEventIntegrationTest.class,
	BuildMetricIntegrationTest.class,
	BuildStatusNoteIntegrationTest.class,
	DeployEventCriteriaIntegrationTest.class,
	DeployEventIntegrationTest.class,
	DeployMetricIntegrationTest.class,
	FeatureAreaIntegrationTest.class,
	FeatureAreaMapIntegrationTest.class,
	FeatureReviewIntegrationTest.class,
	ReleaseSummaryIntegrationTest.class,
	ActBlacklistIntegrationTest.class,
	ActStoryMapIntegrationTest.class,
	ActSuiteIntegrationTest.class,
	ActIntegrationTest.class,
	ActTestCaseMapIntegrationTest.class,
	FlexTestSuiteIntegrationTest.class,
	FlexTestIntegrationTest.class,
	UITestStepIntegrationTest.class,
	UITestSuiteIntegrationTest.class,
	UITestIntegrationTest.class,
	UnitTestBlacklistIntegrationTest.class,
	UnitTestSuiteIntegrationTest.class,
	UnitTestIntegrationTest.class})
public class PersistenceIntegrationTests {

}
