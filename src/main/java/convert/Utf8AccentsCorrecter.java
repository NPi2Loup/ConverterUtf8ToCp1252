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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Read a .properties of all words to replace.
 * And replace them in all files under the rootpath.
 * Modified files are backuped in a directory "abak".
 * Hard coded excludes : 
 * - directories : abak,ubak, .git
 * - files : *.zip, *.bak, *.jar
 * @author npiedeloup
 */
public final class Utf8AccentsCorrecter {
	private int convert = 0;
	private static long totalConvert = 0;
	private static long filesConvert = 0;

	public static void main(final String[] args) throws Exception {
		if (!(args.length == 2)) {
			throw new IllegalArgumentException("Usage : java Utf8AccentsCorrecter \"<<files RootPath>>\" \"<<dico.properties path>>\"");
		}
		// ---------------------------------------------------------------------
		final String root = normalizePath(args[0]);
		final String dicoBundle = args[1]; //"convert.AccentsDico"
		final Utf8AccentsCorrecter utf8AccentsCorrecter = new Utf8AccentsCorrecter(root, dicoBundle);
		final File dirIn = new File(root);
		utf8AccentsCorrecter.convertDir(dirIn);
		System.out.println("Done " + totalConvert + " replacements into " + filesConvert + " files");

	}

	private final String root;
	private final SortedMap<String, String> dico;

	Utf8AccentsCorrecter(final String root, final String dicoBundle) {
		this.root = root;
		dico = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(final String o1, final String o2) {
				int compare = Integer.valueOf(o2.length()).compareTo(Integer.valueOf(o1.length()));
				if (compare == 0) {
					compare = o1.compareTo(o2);
				}
				return compare;
			}
		});
		final ResourceBundle rb = ResourceBundle.getBundle(dicoBundle, new ResourceBundleControl("UTF8"));//Charge le fichier properties en UTF8

		for (final Enumeration<String> e = rb.getKeys(); e.hasMoreElements();) {
			final String key = e.nextElement();
			dico.put(key, rb.getString(key));
		}
	}

	private void convertDir(final File dirIn) throws IOException {
		if (FileUtil.isExclude(dirIn.getName())) {
			return;
		}

		for (final File file : dirIn.listFiles()) {
			if (file.isDirectory()) {
				convertDir(file);
			} else {
				if (FileUtil.isExclude(file.getName())) {
					return;
				}
				final Encoding encoding = Encoding.isEncoded(file, "utf8");
				if (encoding.isEncoded && !encoding.isAscii) {
					convertFile(file);
				}
			}
		}
	}

	private void convertFile(final File fileIn) throws IOException {
		final String saveInPath = normalizePath(fileIn.getAbsolutePath()).replace(root, root + "/abak");
		final File tempIn = File.createTempFile(getClass().getSimpleName(), ".utf8");
		try {
			final File saveIn = new File(saveInPath);
			if (!saveIn.exists()) {

				convert = 0;
				try (InputStream is = new FileInputStream(fileIn)) {
					try (Reader reader = new InputStreamReader(is, "utf8")) {
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempIn), "utf8")) {
							copy(reader, writer);
						}
					}
				}
				if (convert > 0) {//si modifs on sauve
					saveIn.getParentFile().mkdirs();
					fileIn.renameTo(saveIn);
					tempIn.renameTo(fileIn);

					totalConvert += convert;
					filesConvert++;
					System.out.println("Do " + convert + " replacements into " + fileIn.getAbsolutePath());
				}

			} else {
				System.err.println("Can't rename to " + saveIn.getAbsolutePath());
			}
		} finally {
			tempIn.delete();
		}
	}

	private static String normalizePath(final String path) {
		return path.replaceAll("\\" + File.separator, "/");
	}

	private void copy(final Reader in, final Writer out) throws IOException {
		final int bufferSize = 10 * 1024;
		final char[] bytes = new char[bufferSize];
		int read = in.read(bytes);
		while (read != -1) {
			//			if (Arrays.binarySearch(bytes, 0, read, (char) 0) != -1) {
			//				System.err.println(" char 0 found at read");
			//			}
			final char[] convertedChar = convert(bytes, read);
			out.write(convertedChar, 0, convertedChar.length);
			read = in.read(bytes);
		}
	}

	private char[] convert(final char[] bytes, final int len) {
		String buffer = new String(bytes, 0, len);
		//String buffer = new String(bytes);
		for (final Map.Entry<String, String> entry : dico.entrySet()) {
			if (buffer.contains(entry.getKey())) {
				// System.out.println("replace "+entry.getKey());
				buffer = buffer.replaceAll("(\\W)" + entry.getKey() + "(\\W)", "$1" + entry.getValue() + "$2");
				convert++;
			}
		}
		//		if (Arrays.binarySearch(buffer.toCharArray(), 0, buffer.length(), (char) 0) != -1) {
		//			System.err.println(" char 0 found after convert");
		//		}
		return buffer.toCharArray();
	}
}
