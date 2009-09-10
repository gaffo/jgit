package org.spearce.jgit.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spearce.jgit.util.JGitTestUtil;

import junit.framework.TestCase;

public class PacksFileContentsCreatorTest extends TestCase {
	private static final String PACK_NAME = "pack-34be9032ac282b11fa9babdc2b2a93ca996c9c2f";
	private static final File TEST_PACK = JGitTestUtil.getTestResourceFile(PACK_NAME + ".pack");
	private static final File TEST_IDX = JGitTestUtil.getTestResourceFile(PACK_NAME + ".idx");

	public void testGettingPacksContentsSinglePack() throws Exception {
		List<PackFile> packs = new ArrayList<PackFile>();
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		
		assertEquals("P " + TEST_PACK.getName() + '\r', new PacksFileContentsCreator(packs).toString());
	}
	
	public void testGettingPacksContentsMultiplePacks() throws Exception {
		List<PackFile> packs = new ArrayList<PackFile>();
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		
		StringBuilder expected = new StringBuilder();
		expected.append("P ").append(TEST_PACK.getName()).append("\r");
		expected.append("P ").append(TEST_PACK.getName()).append("\r");
		expected.append("P ").append(TEST_PACK.getName()).append("\r");
		
		assertEquals(expected.toString(), new PacksFileContentsCreator(packs).toString());
	}
	
}
