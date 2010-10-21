import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.source.tree.AssertTree;

public class FileInfoTests {
	private String masterPath;
	private String slavePath;
	private String targetPath;

	@Before
	public void setUp() {
		// create some files and dirs
		try {
			// dirs
			this.masterPath = "testMaster";
			File f = new File(this.masterPath);
			assertTrue(f.mkdir());
			this.slavePath = "testSlave";
			f = new File(this.slavePath);
			assertTrue(f.mkdir());
			this.targetPath = "testTarget";
			f = new File(this.targetPath);
			assertTrue(f.mkdir());
			// files
			f = new File(this.masterPath + File.separator + "a.a");
			assertTrue(f.createNewFile());
			f = new File(this.masterPath + File.separator + "b.a");
			assertTrue(f.createNewFile());
			f = new File(this.masterPath + File.separator + "c.a");
			assertTrue(f.createNewFile());
			f = new File(this.masterPath + File.separator + "a.b");
			assertTrue(f.createNewFile());
			f = new File(this.masterPath + File.separator + "b.b");
			assertTrue(f.createNewFile());

			f = new File(this.slavePath + File.separator + "a.a");
			assertTrue(f.createNewFile());
			f = new File(this.slavePath + File.separator + "b.a");
			assertTrue(f.createNewFile());
			f = new File(this.slavePath + File.separator + "b.b");
			assertTrue(f.createNewFile());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		File f = new File(this.masterPath);
		File[] files = f.listFiles();
		for (File file : files) {
			assertTrue(file.delete());
		}
		assertTrue(f.delete());
		f = new File(this.slavePath);
		files = f.listFiles();
		for (File file : files) {
			assertTrue(file.delete());
		}
		assertTrue(f.delete());
		f = new File(this.targetPath);
		files = f.listFiles();
		for (File file : files) {
			assertTrue(file.delete());
		}
		assertTrue(f.delete());
	}

	@Test
	public void testFilesExist() {
		assertTrue(new File(this.masterPath).listFiles().length > 0);
		assertTrue(new File(this.slavePath).listFiles().length > 0);
		assertTrue(new File(this.targetPath).listFiles().length == 0);
	}

	@Test
	public void testIsMatchFileSameExtension() {
		File f1 = new File(this.masterPath + File.separator + "a.a");
		File f2 = new File(this.slavePath + File.separator + "a.a");
		FileInfo fi = new FileInfo(f1);
		assertTrue(fi.IsMatch(f2));
		assertTrue(fi.IsMatch(new FileInfo(f2)));
	}
	
	@Test
	public void testIsMatchFileDifferentExtension() {
		File f1 = new File(this.masterPath + File.separator + "a.b");
		File f2 = new File(this.slavePath + File.separator + "a.a");
		FileInfo fi = new FileInfo(f1);
		assertTrue(fi.IsMatch(f2));
		assertTrue(fi.IsMatch(new FileInfo(f2)));
	}

	@Test
	public void testGetFileInfoItems() {
		Vector<FileInfo> fis =FileInfo.GetFileInfoItems(this.masterPath);
		assertTrue(fis.size()==5);
		fis =FileInfo.GetFileInfoItems(this.slavePath);
		assertTrue(fis.size()==3);
	}
	@Test
	public void testGetSiblings() {

	}

	@Test
	public void testGetMatchName() {
		File f = new File("meintest.xml");
		Assert.assertEquals("meintest", FileInfo.GetMatchName(f));
		f = new File("meintest.blabla.xml");
		Assert.assertEquals("meintest.blabla", FileInfo.GetMatchName(f));
		f = new File("mein  test.xml");
		Assert.assertEquals("mein  test", FileInfo.GetMatchName(f));
	}

}
