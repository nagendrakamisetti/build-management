package com.modeln.build;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.modeln.build.entity.PersistenceIntegrationTests;
import com.modeln.build.entity.PersistenceTests;

@RunWith(Suite.class)
@SuiteClasses({
	PersistenceTests.class,
	PersistenceIntegrationTests.class})
public class AllTests {

}
