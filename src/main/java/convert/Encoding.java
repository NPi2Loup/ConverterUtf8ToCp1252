package convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
 
class Encoding {
		final boolean isUtf8;
		final boolean isAscii;
		
		public Encoding(final boolean isUtf8, final boolean isAscii) {
			this.isUtf8 = isUtf8;
			this.isAscii = isAscii;
		}
		public static Encoding isUtf8(final File in) throws IOException {
			try (InputStream is = new FileInputStream(in)) {
				boolean isUtf8 = true;
				boolean isAscii = true;
				final int bufferSize = 10 * 1024;
				final byte[] bytes = new byte[bufferSize];
				int read = is.read(bytes);
				while (read != -1) {
					byte[] outputUtf8 = new String(bytes, "utf8").getBytes("utf8");
				byte[] outputAscii = new String(bytes, "ASCII")
						.getBytes("ASCII");
				// System.out.println(in.getAbsolutePath() +
				// " ASCII : "+Arrays.equals(bytes, outputAscii));
					if (!Arrays.equals(bytes, outputUtf8)) {					
						isUtf8 = false;
						break;
					}
					isAscii = isAscii && Arrays.equals(outputUtf8, outputAscii);
					read = is.read(bytes);
				}
//				if (isAscii) {
//					System.out.println(in.getAbsolutePath() + " ascii");
//				}
				return new Encoding(isUtf8, isAscii);
			}
		}
		
	}