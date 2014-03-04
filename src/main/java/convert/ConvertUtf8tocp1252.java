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
public class ConvertUtf8tocp1252 {

	// private static final String ROOT = "d:/Klee/@GitHub";

	public static void main(String[] args) throws Exception {
		if (!(args.length == 1)) {
			throw new IllegalArgumentException(
					"Usage : java Utf8AccentsCorrecter \"<<files RootPath>>\" ");
		}
		// ---------------------------------------------------------------------
		final String root = args[0];
		ConvertUtf8tocp1252 convertUtf8tocp1252 = new ConvertUtf8tocp1252(root);
		File dirIn = new File(root);
		convertUtf8tocp1252.convertDir(dirIn);
	}

	private final String root;

	ConvertUtf8tocp1252(final String root) {
		this.root = root;
	}

	private void convertDir(File dirIn) throws IOException {
		if ("abak".equals(dirIn.getName()) || "ubak".equals(dirIn.getName())
				|| ".git".equals(dirIn.getName())
				|| dirIn.getName().endsWith(".zip")
				|| dirIn.getName().endsWith(".bak")
				|| dirIn.getName().endsWith(".jar"))
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
		Encoding encoding = Encoding.isUtf8(fileIn);
		if (encoding.isUtf8 && !encoding.isAscii) {
			if (!fileIn.getName().endsWith(".java"))
				System.out.println(fileIn.getAbsolutePath()
						+ " is utf8, converting to cp1252");
			String saveInPath = fileIn.getAbsolutePath()
					.replaceAll("\\" + File.separator, "/")
					.replace(root, root + "/ubak");
			File saveIn = new File(saveInPath);
			saveIn.getParentFile().mkdirs();
			if (fileIn.renameTo(saveIn)) {
				try (InputStream is = new FileInputStream(saveIn)) {
					try (Reader reader = new InputStreamReader(is, "utf8")) {
						try (Writer writer = new OutputStreamWriter(
								new FileOutputStream(fileIn), "windows-1252"/* "windows-1252" */)) {
							copy(reader, writer);
						}
					}
				}
			} else {
				System.err.println("Can't rename to "
						+ saveIn.getAbsolutePath());
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
