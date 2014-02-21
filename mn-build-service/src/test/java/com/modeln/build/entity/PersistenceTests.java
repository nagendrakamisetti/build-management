package com.modeln.build.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.modeln.build.entity.auth.GroupTest;
import com.modeln.build.entity.auth.UserTest;
import com.modeln.build.entity.build.BuildEventCriteriaTest;
import com.modeln.build.entity.build.BuildEventTest;
import com.modeln.build.entity.build.BuildMetricTest;
import com.modeln.build.entity.build.BuildStatusNoteTest;
import com.modeln.build.entity.build.BuildTest;
import com.modeln.build.entity.customer.AccountTest;
import com.modeln.build.entity.customer.EnvironmentTest;
import com.modeln.build.entity.customer.ProductTest;
import com.modeln.build.entity.deploy.DeployEventCriteriaTest;
import com.modeln.build.entity.deploy.DeployEventTest;
import com.modeln.build.entity.deploy.DeployMetricTest;
import com.modeln.build.entity.feature.FeatureAreaMapTest;
import com.modeln.build.entity.feature.FeatureAreaTest;
import com.modeln.build.entity.feature.FeatureReviewTest;
import com.modeln.build.entity.release.ReleaseSummaryTest;
import com.modeln.build.entity.test.act.ActBlacklistTest;
import com.modeln.build.entity.test.act.ActStoryMapTest;
import com.modeln.build.entity.test.act.ActSuiteTest;
import com.modeln.build.entity.test.act.ActTest;
import com.modeln.build.entity.test.act.ActTestCaseMapTest;
import com.modeln.build.entity.test.flex.FlexTestSuiteTest;
import com.modeln.build.entity.test.flex.FlexTestTest;
import com.modeln.build.entity.test.uit.UITestStepTest;
import com.modeln.build.entity.test.uit.UITestSuiteTest;
import com.modeln.build.entity.test.uit.UITestTest;
import com.modeln.build.entity.test.ut.UnitTestBlacklistTest;
import com.modeln.build.entity.test.ut.UnitTestSuiteTest;
import com.modeln.build.entity.test.ut.UnitTestTest;

@RunWith(Suite.class)
@SuiteClasses({GroupTest.class,
	UserTest.class,
	AccountTest.class,
	ProductTest.class,
	EnvironmentTest.class,
	BuildTest.class,
	BuildEventCriteriaTest.class,
	BuildEventTest.class,
	BuildMetricTest.class,
	BuildStatusNoteTest.class,
	DeployEventCriteriaTest.class,
	DeployEventTest.class,
	DeployMetricTest.class,
	FeatureAreaTest.class,
	FeatureAreaMapTest.class,
	FeatureReviewTest.class,
	ReleaseSummaryTest.class,
	ActBlacklistTest.class,
	ActStoryMapTest.class,
	ActSuiteTest.class,
	ActTest.class,
	ActTestCaseMapTest.class,
	FlexTestSuiteTest.class,
	FlexTestTest.class,
	UITestStepTest.class,
	UITestSuiteTest.class,
	UITestTest.class,
	UnitTestBlacklistTest.class,
	UnitTestSuiteTest.class,
	UnitTestTest.class})
public class PersistenceTests {

}
