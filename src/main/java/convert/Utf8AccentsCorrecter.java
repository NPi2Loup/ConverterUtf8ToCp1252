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

	public static void main(String[] args) throws Exception {
		final String root = args[0];
		final String dicoBundle = args[1]; //"convert.AccentsDico"
		Utf8AccentsCorrecter utf8AccentsCorrecter = new Utf8AccentsCorrecter(
				root, dicoBundle);
		File dirIn = new File(root);
		utf8AccentsCorrecter.convertDir(dirIn);
	}

	private final String root;
	private final SortedMap<String, String> dico;

	Utf8AccentsCorrecter(final String root, final String dicoBundle) {
		this.root = root;
		dico = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int compare = Integer.valueOf(o2.length()).compareTo(
						Integer.valueOf(o1.length()));
				if (compare == 0) {
					compare = o1.compareTo(o2);
				}
				return compare;
			}
		});
		final ResourceBundle rb = ResourceBundle.getBundle(dicoBundle,
				new UTF8Control());//Charge le fichier properties en UTF8
		
		for (Enumeration<String> e = rb.getKeys(); e.hasMoreElements();) {
			String key = e.nextElement();
			dico.put(key, rb.getString(key));
		}
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
				Encoding encoding = Encoding.isUtf8(file);
				if (encoding.isUtf8 && !encoding.isAscii) {
					convertFile(file);
				}
			}
		}
	}

	private void convertFile(File fileIn) throws IOException {
		String saveInPath = fileIn.getAbsolutePath()
				.replaceAll("\\" + File.separator, "/")
				.replace(root, root + "/abak");
		File saveIn = new File(saveInPath);
		saveIn.getParentFile().mkdirs();
		if (fileIn.renameTo(saveIn)) {
			try (InputStream is = new FileInputStream(saveIn)) {
				try (Reader reader = new InputStreamReader(is, "utf8")) {
					try (Writer writer = new OutputStreamWriter(
							new FileOutputStream(fileIn), "utf8")) {
						copy(reader, writer);
					}
				}
			}
		} else {
			System.err.println("Can't rename to " + saveIn.getAbsolutePath());
		}
	}

	private void copy(final Reader in, final Writer out)
			throws IOException {
		final int bufferSize = 10 * 1024;
		final char[] bytes = new char[bufferSize];
		int read = in.read(bytes);
		while (read != -1) {
			out.write(convert(bytes), 0, read);
			read = in.read(bytes);
		}
	}

	private char[] convert(char[] bytes) {
		String buffer = new String(bytes);
		for (Map.Entry<String, String> entry : dico.entrySet()) {
			if (buffer.contains(entry.getKey())) {
				// System.out.println("replace "+entry.getKey());
				buffer = buffer.replaceAll("(\\W)" + entry.getKey() + "(\\W)",
						"$1" + entry.getValue() + "$2");
			}
		}
		return buffer.toCharArray();
	}

}
