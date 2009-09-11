package org.spearce.jgit.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class UpdateDirectoryBasedPacksInfoCache {

	private List<PackFile> packsList;
	private File infoPacksFile;

	public UpdateDirectoryBasedPacksInfoCache(List<PackFile> packsList,
									File infoPacksFile) {
		this.packsList = packsList;
		this.infoPacksFile = infoPacksFile;
	}

	public void execute() throws IOException {
		String packsContents = new PacksFileContentsCreator(packsList).toString();
		FileOutputStream fos = new FileOutputStream(infoPacksFile);
		fos.write(packsContents.getBytes());
		fos.close();
	}

}
