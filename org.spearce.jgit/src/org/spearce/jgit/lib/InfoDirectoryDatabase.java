package org.spearce.jgit.lib;

import java.io.File;

public class InfoDirectoryDatabase extends InfoDatabase {

	private File info;

	public InfoDirectoryDatabase(final File directory) {
		info = directory;
	}
	
	@Override
	public void create() {
		info.mkdirs();
	}

}
