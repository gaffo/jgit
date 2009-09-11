package org.spearce.jgit.lib;

import java.io.File;
import java.util.List;

public class UpdateDirectoryInfoCache {

	private List<PackFile> packsList;
	private File infoDirectory;

	public UpdateDirectoryInfoCache(List<PackFile> packsList,
			File infoDirectory) {
		this.packsList = packsList;
		this.infoDirectory = infoDirectory;
	}

	public void execute() {
//		File objectFile = objectDatabase.
//		String packsContents = new PacksFileContentsCreator(this.objectDatabase.listLocalPacks()).toString();
	}

}
