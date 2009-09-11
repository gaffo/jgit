package org.spearce.jgit.lib;

import java.io.File;

import org.spearce.jgit.util.JGitTestUtil;

import junit.framework.TestCase;

public class InfoDirectoryDatabaseTest extends TestCase {

	private File testDir;

	@Override
	protected void setUp() throws Exception {
		testDir = JGitTestUtil.generateTempDirectoryFileObject();
	}

	@Override
	protected void tearDown() throws Exception {
		if (testDir.exists()){
			JGitTestUtil.deleteDir(testDir);
		}
	}
	
	public void testCreateCreatesDirectory() throws Exception {
		assertFalse(testDir.exists());
		new InfoDirectoryDatabase(testDir).create();
		assertTrue(testDir.exists());
	}
}
