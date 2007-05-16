/*
 *  Copyright (C) 2006  Robin Rosenberg <robin.rosenberg@dewire.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License, version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 */
package org.spearce.egit.core.internal.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.spearce.egit.core.GitProvider;
import org.spearce.egit.core.op.ConnectProviderOperation;
import org.spearce.egit.core.test.GitTestCase;
import org.spearce.jgit.lib.Commit;
import org.spearce.jgit.lib.FileTreeEntry;
import org.spearce.jgit.lib.ObjectId;
import org.spearce.jgit.lib.ObjectWriter;
import org.spearce.jgit.lib.PersonIdent;
import org.spearce.jgit.lib.RefLock;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.lib.Tree;

public class T0002_history extends GitTestCase {

	protected static final PersonIdent jauthor;

	protected static final PersonIdent jcommitter;

	static {
		jauthor = new PersonIdent("J. Author", "jauthor@example.com");
		jcommitter = new PersonIdent("J. Committer", "jcommitter@example.com");
	}

	private File workDir;
	private File gitDir;
	private Repository thisGit;
	private Tree tree;
	private ObjectWriter objectWriter;

	protected void setUp() throws Exception {
		super.setUp();
		project.createSourceFolder();
		gitDir = new File(project.getProject().getWorkspace().getRoot()
				.getRawLocation().toFile(), ".git");
		workDir = gitDir.getParentFile();
		thisGit = new Repository(gitDir);
		thisGit.create();
		objectWriter = new ObjectWriter(thisGit);

		tree = new Tree(thisGit);
		Tree projectTree = tree.addTree("Project-1");
		File project1_a_txt = createFile("Project-1/A.txt","A.txt - first version\n");
		addFile(projectTree,project1_a_txt);
		projectTree.setId(objectWriter.writeTree(projectTree));
		tree.setId(objectWriter.writeTree(tree));
		Commit commit = new Commit(thisGit);
		commit.setAuthor(new PersonIdent(jauthor, new Date(0L)));
		commit.setCommitter(new PersonIdent(jcommitter, new Date(0L)));
		commit.setMessage("Foo\n\nMessage");
		commit.setTree(tree);
		ObjectId commitId = objectWriter.writeCommit(commit);
		RefLock lck = thisGit.lockRef("refs/heads/master");
		assertNotNull("obtained lock", lck);
		lck.write(commitId);
		assertTrue("committed lock", lck.commit());

		ConnectProviderOperation operation = new ConnectProviderOperation(
				project.getProject(), null);
		operation.run(null);
	}

	private void addFile(Tree t,File f) throws IOException {
		ObjectId id = objectWriter.writeBlob(f);
		t.addEntry(new FileTreeEntry(t,id,f.getName().getBytes("UTF-8"),false));
	}

	private File createFile(String name, String content) throws IOException {
		File f = new File(workDir, name);
		FileWriter fileWriter = new FileWriter(f);
		fileWriter.write(content);
		fileWriter.close();
		return f;
	}

	public void testShallowHistory() {
		GitProvider provider = (GitProvider)RepositoryProvider.getProvider(project.project);
		assertNotNull(provider);
		IFileHistoryProvider fileHistoryProvider = provider.getFileHistoryProvider();
		IFileHistory fileHistory = fileHistoryProvider.getFileHistoryFor(project.getProject().getWorkspace().getRoot().findMember("Project-1/A.txt"), IFileHistoryProvider.SINGLE_LINE_OF_DESCENT, new NullProgressMonitor());
		IFileRevision[] fileRevisions = fileHistory.getFileRevisions();
		assertEquals(1, fileRevisions.length);
		assertEquals("e2eadee5e6de7315df91cf03a75a8b2194a69af2", fileRevisions[0].getContentIdentifier());
	}
}