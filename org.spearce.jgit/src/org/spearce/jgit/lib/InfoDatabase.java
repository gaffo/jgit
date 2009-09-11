package org.spearce.jgit.lib;

import java.io.IOException;
import java.util.Collection;

public abstract class InfoDatabase {

	/**
	 * Create the info database
	 */
	public void create() {
	}

	/**
	 * Updates the info cache typically done by update-server-info command.
	 * This writes THIS repository's refs out to the info/refs file.
	 * @param collection the collections of refs to update the info cache with
	 * @throws IOException for any type of failure on the local or remote 
	 * 					   data store
	 */
	public abstract void updateInfoCache(Collection<Ref> collection) throws IOException;

}
