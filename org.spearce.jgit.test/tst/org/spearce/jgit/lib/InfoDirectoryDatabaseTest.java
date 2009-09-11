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
