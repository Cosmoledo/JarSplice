package de.Cosmoledo.jarSplice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReadFromJar {
	public static final File TEMP_DIR = createTempDir();

	private static File createTempDir() {
		int TEMP_DIR_ATTEMPTS = 50;
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";
		for(int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if(tempDir.mkdir())
				return tempDir;
		}
		throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1)
			+ ')');
	}

	public static boolean deleteDirectory(File directory) {
		if(directory.exists()) {
			File[] files = directory.listFiles();
			if(null != files)
				for(int i = 0; i < files.length; i++)
					if(files[i].isDirectory())
						deleteDirectory(files[i]);
					else
						files[i].delete();
		}
		return (directory.delete());
	}

	public static void extractJar(File input, String output) throws IOException {
		JarFile jarfile = new JarFile(input);
		Enumeration<JarEntry> enu = jarfile.entries();
		while(enu.hasMoreElements()) {
			JarEntry je = enu.nextElement();
			String name = je.getName().toLowerCase();
			if(!name.contains("manifest.mf"))
				continue;
			name = name.replace("meta-inf", "");
			if(name.length() == 0)
				continue;
			File fl = new File(output, name);
			InputStream is = jarfile.getInputStream(je);
			FileOutputStream fo = new FileOutputStream(fl);
			while(is.available() > 0)
				fo.write(is.read());
			fo.close();
			is.close();
		}
		jarfile.close();
	}
}
