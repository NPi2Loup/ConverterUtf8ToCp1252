package convert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class FileUtil {
	private static final Set<String> EXCLUDE_FILE_NAME = new HashSet<>(Arrays.asList(new String[] //
			{ "abak", "ubak", ".git" }//
			));
	private static final Set<String> EXCLUDE_FILE_SUFFIX = new HashSet<>(Arrays.asList(new String[] //
			{ ".bak", ".jar", ".class" }//
			));

	private FileUtil() {
		//rien
	}

	public static boolean isExclude(final String name) {
		if (EXCLUDE_FILE_NAME.contains(name)) {
			return true;
		}
		for (final String suffix : EXCLUDE_FILE_SUFFIX) {
			if (name.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}
}
