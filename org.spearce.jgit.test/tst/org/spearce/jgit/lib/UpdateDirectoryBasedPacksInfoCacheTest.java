package org.spearce.jgit.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.spearce.jgit.util.JGitTestUtil;

public class UpdateDirectoryBasedPacksInfoCacheTest extends TestCase {
	private static final String PACK_NAME = "pack-34be9032ac282b11fa9babdc2b2a93ca996c9c2f";
	private static final File TEST_PACK = JGitTestUtil.getTestResourceFile(PACK_NAME + ".pack");
	private static final File TEST_IDX = JGitTestUtil.getTestResourceFile(PACK_NAME + ".idx");
	
	public void testCreatesTheFileAndPutsTheContentsIn() throws Exception {
		List<PackFile> packs = new ArrayList<PackFile>();
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		
		File packsFile = File.createTempFile(UpdateDirectoryBasedPacksInfoCacheTest.class.getSimpleName(), "tstdata");
		packsFile.deleteOnExit();
		
		String expectedContents = new PacksFileContentsCreator(packs).toString();
		
		new UpdateDirectoryBasedPacksInfoCache(packs, packsFile).execute();
		
		assertEquals(expectedContents, JGitTestUtil.readFileAsString(packsFile));
	}

}
