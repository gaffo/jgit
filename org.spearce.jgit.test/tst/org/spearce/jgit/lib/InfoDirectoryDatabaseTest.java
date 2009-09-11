package org.spearce.jgit.lib;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

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
	
	public void testUpdateInfoCache() throws Exception {
		Collection<Ref> refs = new ArrayList<Ref>();
		refs.add(new Ref(Ref.Storage.LOOSE, "refs/heads/master", ObjectId.fromString("32aae7aef7a412d62192f710f2130302997ec883")));
		refs.add(new Ref(Ref.Storage.LOOSE, "refs/heads/development", ObjectId.fromString("184063c9b594f8968d61a686b2f6052779551613")));

		File expectedFile = new File(testDir, "refs");
		assertFalse(expectedFile.exists());
		
		
		final StringWriter expectedString = new StringWriter();
		new RefWriter(refs) {
			@Override
			protected void writeFile(String file, byte[] content) throws IOException {
				expectedString.write(new String(content));
			}
		}.writeInfoRefs();
		
		InfoDirectoryDatabase out = new InfoDirectoryDatabase(testDir);
		out.create();
		out.updateInfoCache(refs);
		assertTrue(expectedFile.exists());
		
		String actual = JGitTestUtil.readFileAsString(expectedFile);
		assertEquals(expectedString.toString(), actual);
	}
}
