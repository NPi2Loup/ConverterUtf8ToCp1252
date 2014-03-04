package convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Convert all files under the rootpath from utf8 to Cp1252. 
 * Modified files are backuped in a directory "ubak".
 * Hard coded excludes : 
 * - directories : abak,ubak, .git
 * - files : *.zip, *.bak, *.jar
 * 
 * @author npiedeloup
 */
public class ConvertUtf8toCp1252 {
	private static final String FROM_CHARSET = "utf8";
	private static final String TO_CHARSET = "windows-1252";
	//private static final String FROM_CHARSET = "windows-1252";
	//private static final String TO_CHARSET = "utf8";

	// private static final String ROOT = "d:/Klee/@GitHub";

	public static void main(String[] args) throws Exception {
		if (!(args.length == 1)) {
			throw new IllegalArgumentException("Usage : java ConvertUtf8toCp1252 \"<<files RootPath>>\" ");
		}
		// ---------------------------------------------------------------------
		final String root = normalizePath(args[0]); 
		ConvertUtf8toCp1252 convertUtf8tocp1252 = new ConvertUtf8toCp1252(root);
		File dirIn = new File(root);
		convertUtf8tocp1252.convertDir(dirIn);
	}

	private static String normalizePath(String path) {
		return path.replaceAll("\\"+File.separator, "/");// the '\\' is for escaping File.separator any char it is.
	}

	private final String root;

	ConvertUtf8toCp1252(final String root) {
		this.root = root;
	}

	private void convertDir(File dirIn) throws IOException {
		if ("abak".equals(dirIn.getName()) || "ubak".equals(dirIn.getName()) || ".git".equals(dirIn.getName()) || dirIn.getName().endsWith(".zip") || dirIn.getName().endsWith(".bak") || dirIn.getName().endsWith(".jar"))
			return;
		for (File file : dirIn.listFiles()) {
			if (file.isDirectory()) {
				convertDir(file);
			} else {
				convertFile(file);
			}
		}
	}

	private void convertFile(File fileIn) throws IOException {
		Encoding encoding = Encoding.isEncoded(fileIn, FROM_CHARSET);
		if (encoding.isEncoded && !encoding.isAscii) {
			if (!fileIn.getName().endsWith(".java"))
				System.out.println(fileIn.getAbsolutePath() + " is " + FROM_CHARSET + ", converting to " + TO_CHARSET);
			String saveInPath = normalizePath(fileIn.getAbsolutePath()).replace(root, root + "/ubak"); 
			File saveIn = new File(saveInPath);
			if (saveInPath.equals(fileIn.getAbsolutePath())) {
				throw new IllegalArgumentException("Backup files errors : backup path is the same as source");
			}
			saveIn.getParentFile().mkdirs();
			if (fileIn.renameTo(saveIn)) {
				try (InputStream is = new FileInputStream(saveIn)) {
					try (Reader reader = new InputStreamReader(is, FROM_CHARSET)) {
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileIn), TO_CHARSET)) {
							copy(reader, writer);
						}
					}
				}
			} else {
				System.err.println("Can't rename to " + saveIn.getAbsolutePath());
			}
		} else {
			// System.out.println(fileIn.getAbsolutePath() +
			// " is not utf8 ("+(encoding.isAscii?"ascii":"")+")");
		}
	}

	public void copy(final Reader in, final Writer out) throws IOException {
		final int bufferSize = 10 * 1024;
		final char[] bytes = new char[bufferSize];
		int read = in.read(bytes);
		while (read != -1) {
			out.write(bytes, 0, read);
			read = in.read(bytes);
		}
	}

}
