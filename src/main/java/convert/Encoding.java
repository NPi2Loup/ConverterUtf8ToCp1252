package convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class Encoding {
	final boolean isEncoded;
	final boolean isAscii;

	public Encoding(final boolean isEncoded, final boolean isAscii) {
		this.isEncoded = isEncoded;
		this.isAscii = isAscii;
	}

	public static Encoding isEncoded(final File in, final String fromCharset) throws IOException {
		try (InputStream is = new FileInputStream(in)) {
			boolean isAlreadyEncoded = true;
			boolean isAscii = true;
			final int bufferSize = 10 * 1024;
			final byte[] bytes = new byte[bufferSize];
			int read = is.read(bytes);
			while (read != -1) {
				final byte[] outputFromCharset = new String(bytes, fromCharset).getBytes(fromCharset);
				final byte[] outputAscii = new String(bytes, "ASCII").getBytes("ASCII");
				// System.out.println(in.getAbsolutePath() +
				// " ASCII : "+Arrays.equals(bytes, outputAscii));
				if (!Arrays.equals(bytes, outputFromCharset)) {
					System.out.println(in.getAbsolutePath() + " with " + fromCharset + " differ");
					isAlreadyEncoded = false;
					break;
				}
				isAscii = isAscii && Arrays.equals(outputFromCharset, outputAscii);
				read = is.read(bytes);
			}
			//				if (isAscii) {
			//					System.out.println(in.getAbsolutePath() + " ascii");
			//				}
			return new Encoding(isAlreadyEncoded, isAscii);
		}
	}

}
