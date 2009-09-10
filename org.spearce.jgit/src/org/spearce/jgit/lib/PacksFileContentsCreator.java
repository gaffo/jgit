package org.spearce.jgit.lib;

import java.util.List;

public class PacksFileContentsCreator {

	private List<PackFile> packs;

	public PacksFileContentsCreator(List<PackFile> packs) {
		this.packs = packs;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for (PackFile packFile : packs) {
			builder.append("P ").append(packFile.getPackFile().getName()).append('\r');
		}
		return builder.toString();
	}

}
