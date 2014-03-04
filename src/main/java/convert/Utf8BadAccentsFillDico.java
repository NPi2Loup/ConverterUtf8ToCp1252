package convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract a dictionnary of all word with bad encoding accents (show as �).
 * 
 * @author npiedeloup
 */
public final class Utf8BadAccentsFillDico {
	private static final String BAD_CHAR = "�";
	private static final Pattern BAD_CHAR_PATTERN = Pattern
			.compile("(?<=\\W)((?:\\w*" + BAD_CHAR + "\\w*)+)\\W");

	// private static final Map<String, Integer> DICO = new HashMap<String,
	// Integer>();
	private final SortedMap<String, Integer> dico = new TreeMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		if (!(args.length == 1)) {
			throw new IllegalArgumentException(
					"Usage : java Utf8BadAccentsFillDico \"<<files RootPath>>\" ");
		}
		// ---------------------------------------------------------------------
		final String root = args[0];
		Utf8BadAccentsFillDico utfAccentsFillDico = new Utf8BadAccentsFillDico();
		File dirIn = new File(root);
		utfAccentsFillDico.fillAccentDicoDir(dirIn);

		utfAccentsFillDico.readDico();
	}

	Utf8BadAccentsFillDico() { // rien

	}

	public void readDico() {
		StringBuilder sb = new StringBuilder();
		for (String key : dico.keySet()) {
			sb.append(key).append("=").append(key.replaceAll(BAD_CHAR, "é")); // é=�
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

	private void fillAccentDicoDir(File dirIn) throws IOException {
		for (File file : dirIn.listFiles()) {
			if (file.isDirectory()) {
				fillAccentDicoDir(file);
			} else {
				fillAccentDico(file);
			}
		}
	}

	private void fillAccentDico(File fileIn) throws IOException {
		try (InputStream is = new FileInputStream(fileIn)) {
			try (Reader reader = new InputStreamReader(is, "windows-1252")) {
				final int bufferSize = 10 * 1024;
				final char[] bytes = new char[bufferSize];
				int read = reader.read(bytes);
				while (read != -1) {
					String content = new String(bytes);
					Matcher matcher = BAD_CHAR_PATTERN.matcher(content);
					while (matcher.find()) {
						String found = matcher.group(1);
						Integer hit = dico.get(found);
						dico.put(found, hit == null ? 1 : hit + 1);
						// System.out.println("found in "+fileIn.getName());
					}
					read = reader.read(bytes);
				}
			}
		}
	}
}
