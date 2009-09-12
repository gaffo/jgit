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
		
		assertEquals("P " + TEST_PACK.getName() + "\n\n", new PacksFileContentsCreator(packs).toString());
	}
	
	public void testGettingPacksContentsMultiplePacks() throws Exception {
		List<PackFile> packs = new ArrayList<PackFile>();
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		packs.add(new PackFile(TEST_IDX, TEST_PACK));
		
		StringBuilder expected = new StringBuilder();
		expected.append("P ").append(TEST_PACK.getName()).append('\n');
		expected.append("P ").append(TEST_PACK.getName()).append('\n');
		expected.append("P ").append(TEST_PACK.getName()).append('\n');
		expected.append('\n');
		
		assertEquals(expected.toString(), new PacksFileContentsCreator(packs).toString());
	}
	
}
