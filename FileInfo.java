import java.io.File;
import java.util.Vector;

public class FileInfo {

	private final File file;
	private final String matchName;

	public FileInfo(File aFile) {
		this.file = aFile;
		this.matchName = this.GetMatchName(aFile);
	}

	public static String GetMatchName(File aFile) {
		String name = aFile.getName();
		int index = name.lastIndexOf('.');
		if (index > 0 && index <= name.length() - 2) {
			return name.substring(0, index);
		}
		return "";
	}

	/*
	 * search for files with same matchname in the same directory as the itself.
	 * this is usually used for finding a xmp file to a nef file
	 */
	public Vector<File> GetSiblings() {
		Vector<File> ret = new Vector<File>();
		System.out.println(this.file.getParentFile().getPath());
		File[] files = new File(this.file.getParentFile().getPath()).listFiles();
		for (File aFile : files) {
			if (this.IsMatch(aFile)) {
				ret.add(aFile);
			}
		}
		return ret;
	}

	public Boolean IsMatch(FileInfo other) {
		return this.matchName.equals(other.matchName);
	}

	public Boolean IsMatch(File other) {
		return this.matchName.equals(this.GetMatchName(other));
	}

	// TODO: what about extensions? dont we care?
	public static Vector<FileInfo> GetFileInfoItems(String absPath) {
		Vector<FileInfo> ret = new Vector<FileInfo>();
		File f = new File(absPath);
		File[] files = f.listFiles();
		for (File aFile : files) {
			ret.add(new FileInfo(aFile));
		}
		return ret;
	}

	public String GetString() {
		return this.file.getName() + " >> " + this.matchName + ", #siblings:" + this.GetSiblings().size();
	}
}