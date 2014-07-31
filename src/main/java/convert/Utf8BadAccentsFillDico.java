package convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
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
	private static final Pattern BAD_CHAR_PATTERN = Pattern.compile("(?<=\\W)((?:\\w*" + BAD_CHAR + "\\w*)+)\\W");

	// private static final Map<String, Integer> DICO = new HashMap<String,
	// Integer>();
	private final SortedMap<String, String> dicoConvert = new TreeMap<>(); //contains trad
	private final Map<String, Integer> dicoCount = new HashMap<>(); //contains count

	public static void main(final String[] args) throws Exception {
		if (!(args.length == 2 || args.length == 3)) {
			throw new IllegalArgumentException("Usage : java Utf8BadAccentsFillDico \"<<files RootPath>>\" \"<<optional:previousDico.properties>>\" \"<<outpoutDicoPath>>\"");
		}
		// ---------------------------------------------------------------------
		final String root = args[0];
		final String previousDicoBundle = args.length == 3 ? args[1] : null; //"previousDico.properties"
		if (previousDicoBundle != null && previousDicoBundle.endsWith(".properties")) {
			throw new IllegalArgumentException("Don't write the .properties extension to your previous dico");
		}
		final String outputDicoPath = args.length == 3 ? args[2] : args[1];
		if (!outputDicoPath.endsWith(".properties")) {
			throw new IllegalArgumentException("Output dico must have the .properties extension");
		}
		final Utf8BadAccentsFillDico utfAccentsFillDico = new Utf8BadAccentsFillDico(previousDicoBundle);
		final File dirIn = new File(root);
		utfAccentsFillDico.fillAccentDicoDir(dirIn);
		final File outputDico = new File(outputDicoPath);
		utfAccentsFillDico.writeDico(outputDico);
	}

	Utf8BadAccentsFillDico(final String previousDicoBundle) {
		//		dicoConvert = new TreeMap<>(new Comparator<String>() {
		//			@Override
		//			public int compare(final String o1, final String o2) {
		//				final Integer count1 = dicoCount.get(o1);
		//				final Integer count2 = dicoCount.get(o2);
		//				int compare = (count1 != null ? count1 : Integer.valueOf(0)).compareTo((count2 != null ? count2 : 0));
		//				if (compare == 0) {
		//					compare = o1.compareTo(o2);
		//				}
		//				return compare;
		//			}
		//		});
		if (previousDicoBundle != null) {
			final ResourceBundle rb = ResourceBundle.getBundle(previousDicoBundle, new ResourceBundleControl("windows-1252"));//Charge le fichier properties en UTF8

			for (final Enumeration<String> e = rb.getKeys(); e.hasMoreElements();) {
				final String key = e.nextElement();
				dicoConvert.put(key, rb.getString(key));
			}
			System.out.println("#Load " + dicoConvert.size() + " words from previous " + previousDicoBundle);
		}
	}

	public void writeDico(final File outputDico) throws IOException {
		System.out.println("#" + dicoConvert.size() + " words were discovered");
		try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDico), "windows-1252"))) {
			final StringBuilder sb = new StringBuilder();
			for (final String key : dicoConvert.keySet()) {
				//final int found = dicoCount.containsKey(key) ? dicoCount.get(key) : 0;			
				sb.append(key).append("=").append(dicoConvert.get(key));
				sb.append("\n");
			}
			writer.write(sb.toString());
		}
	}

	private void fillAccentDicoDir(final File dirIn) throws IOException {
		if (FileUtil.isExclude(dirIn.getName())) {
			return;
		}
		for (final File file : dirIn.listFiles()) {
			if (file.isDirectory()) {
				fillAccentDicoDir(file);
			} else {
				fillAccentDico(file);
			}
		}
	}

	private void fillAccentDico(final File fileIn) throws IOException {
		if (FileUtil.isExclude(fileIn.getName())) {
			return;
		}
		try (InputStream is = new FileInputStream(fileIn)) {
			try (Reader reader = new InputStreamReader(is, "windows-1252")) {
				final int bufferSize = 10 * 1024;
				final char[] bytes = new char[bufferSize];
				int read = reader.read(bytes);
				while (read != -1) {
					final String content = new String(bytes);
					final Matcher matcher = BAD_CHAR_PATTERN.matcher(content);
					while (matcher.find()) {
						final String found = matcher.group(1);
						final Integer hit = dicoCount.get(found);
						dicoCount.put(found, hit == null ? 1 : hit + 1);
						if (!dicoConvert.containsKey(found)) {
							dicoConvert.put(found, found.replaceAll(BAD_CHAR, "é")); // é=�
						}
						// System.out.println("found in "+fileIn.getName());
					}
					read = reader.read(bytes);
				}
			}
		}
	}
}
