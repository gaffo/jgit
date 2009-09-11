/*
 * Copyright (C) 2009, Mike Gaffney.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Git Development Community nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.spearce.jgit.lib;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.spearce.jgit.util.JGitTestUtil;

public class ObjectDirectoryTest extends TestCase {
	private static final String PACK_NAME = "pack-34be9032ac282b11fa9babdc2b2a93ca996c9c2f";
	private static final File TEST_PACK = JGitTestUtil.getTestResourceFile(PACK_NAME + ".pack");
	private static final File TEST_IDX = JGitTestUtil.getTestResourceFile(PACK_NAME + ".idx");
	
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

	public void testCanGetDirectory() throws Exception {
		ObjectDirectory od = new ObjectDirectory(testDir);
		assertEquals(testDir, od.getDirectory());
	}
	
	public void testExistsWithExistingDirectory() throws Exception {
		createTestDir();
		ObjectDirectory od = new ObjectDirectory(testDir);
		assertTrue(od.exists());
	}
	
	public void testExistsWithNonExistantDirectory() throws Exception {
		assertFalse(new ObjectDirectory(new File("/some/nonexistant/file")).exists());
	}
	
	public void testCreateMakesCorrectDirectories() throws Exception {
		assertFalse(testDir.exists());
		new ObjectDirectory(testDir).create();
		assertTrue(testDir.exists());
		
		File infoDir = new File(testDir, "info");
		assertTrue(infoDir.exists());
		assertTrue(infoDir.isDirectory());
		
		File packDir = new File(testDir, "pack");
		assertTrue(packDir.exists());
		assertTrue(packDir.isDirectory());
	}
	
	public void testGettingObjectFile() throws Exception {
		ObjectDirectory od = new ObjectDirectory(testDir);
		assertEquals(new File(testDir, "02/829ae153935095e4223f30cfc98c835de71bee"), 
					 od.fileFor(ObjectId.fromString("02829ae153935095e4223f30cfc98c835de71bee")));
		assertEquals(new File(testDir, "b0/52a1272310d8df34de72f60204dee7e28a43d0"), 
				 od.fileFor(ObjectId.fromString("b052a1272310d8df34de72f60204dee7e28a43d0")));
	}
	
	public void testListLocalPacksNotCreated() throws Exception {
		assertEquals(0, new ObjectDirectory(testDir).listLocalPacks().size());
	}
	
	public void testListLocalPacksWhenThereIsAPack() throws Exception {
		createSamplePacksDir();

		ObjectDirectory od = new ObjectDirectory(testDir);
		List<PackFile> localPacks = od.listLocalPacks();
		assertEquals(1, localPacks.size());
		assertEquals(TEST_PACK.getName(), localPacks.get(0).getPackFile().getName());
	}
	
	public void testUpdateInfoCacheCreatesPacksAndRefsFile() throws Exception {
		createSamplePacksDir();

		ObjectDirectory od = new ObjectDirectory(testDir);
		od.create();
		od.updateInfoCache();
		
		String expectedContents = new PacksFileContentsCreator(od.listLocalPacks()).toString();
		File packsFile = new File(od.getDirectory(), Constants.CACHED_PACKS_FILE);

		assertTrue(packsFile.exists());
		assertEquals(expectedContents, JGitTestUtil.readFileAsString(packsFile));
	}
	
	private void createTestDir(){
		testDir.mkdir();
	}

	private void createSamplePacksDir() throws IOException {
		createTestDir();
		File packsDir = new File(testDir, "pack");
		packsDir.mkdirs();
		
		JGitTestUtil.copyFile(TEST_PACK, new File(packsDir, TEST_PACK.getName()));
		JGitTestUtil.copyFile(TEST_IDX, new File(packsDir, TEST_IDX.getName()));
	}
	
}
