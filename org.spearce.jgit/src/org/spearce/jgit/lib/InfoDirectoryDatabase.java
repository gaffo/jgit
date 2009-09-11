package org.spearce.jgit.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class InfoDirectoryDatabase extends InfoDatabase {

	private File info;

	public InfoDirectoryDatabase(final File directory) {
		info = directory;
	}
	
	@Override
	public void create() {
		info.mkdirs();
	}

	@Override
	public void updateInfoCache(Collection<Ref> refs) throws IOException {
		new RefWriter(refs) {
			@Override
			protected void writeFile(String file, byte[] content) throws IOException {
				FileOutputStream fos = new FileOutputStream(new File(info, "refs"));
				fos.write(content);
				fos.close();
			}
		}.writeInfoRefs();
	}

}
