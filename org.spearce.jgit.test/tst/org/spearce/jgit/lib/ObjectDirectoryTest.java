package org.spearce.jgit.lib;

import java.io.File;
import java.util.UUID;

import junit.framework.TestCase;

public class ObjectDirectoryTest extends TestCase {
	
	private File testDir;

	@Override
	protected void setUp() throws Exception {
		testDir = new File(new File(System.getProperty("java.io.tmpdir")), UUID.randomUUID().toString());
	}
	
	@Override
	protected void tearDown() throws Exception {
		if (testDir.exists()){
			deleteDir(testDir);
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
	
	public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }

	private void createTestDir(){
		testDir.mkdir();
	}
	
}
