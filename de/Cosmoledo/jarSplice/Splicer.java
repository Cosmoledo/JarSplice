package de.Cosmoledo.jarSplice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Splicer {
	private ArrayList<String> dirs = new ArrayList();
	private int bufferSize;
	private byte[] buffer = new byte[4096];

	private void addFilesFromJars(String[] jars, JarOutputStream out) throws Exception {
		for(int i = 0; i < jars.length; i++) {
			ZipFile jarFile = new ZipFile(jars[i]);
			Enumeration entities = jarFile.entries();
			while(entities.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entities.nextElement();
				if(entry.isDirectory()) {
					if(!this.dirs.contains(entry.getName()))
						this.dirs.add(entry.getName());
				} else if(!entry.getName().toLowerCase().startsWith("meta-inf"))
					if(!entry.getName().toLowerCase().contains("JarSpliceLauncher")) {
						InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
						out.putNextEntry(new ZipEntry(entry.getName()));
						while((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
							out.write(this.buffer, 0, this.bufferSize);
						in.close();
						out.closeEntry();
					}
			}
			jarFile.close();
		}
	}

	private void addJarSpliceLauncher(JarOutputStream out) throws Exception {
		InputStream in = JarSplice.class.getResourceAsStream("JarSpliceLauncher.class");
		out.putNextEntry(new ZipEntry("de/Cosmoledo/jarSplice/JarSpliceLauncher.class"));
		while((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
			out.write(this.buffer, 0, this.bufferSize);
		in.close();
		out.closeEntry();
	}

	private void addNativesToJar(String[] natives, JarOutputStream out) throws Exception {
		for(int i = 0; i < natives.length; i++) {
			InputStream in = new FileInputStream(natives[i]);
			out.putNextEntry(new ZipEntry(this.getFileName(natives[i])));
			while((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
				out.write(this.buffer, 0, this.bufferSize);
			in.close();
			out.closeEntry();
		}
	}

	public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs) throws Exception {
		this.dirs.clear();
		Manifest manifest = this.getManifest(mainClass, vmArgs);
		FileOutputStream fos = new FileOutputStream(output);
		JarOutputStream jos = new JarOutputStream(fos, manifest);
		try {
			this.addFilesFromJars(jars, jos);
			this.addNativesToJar(natives, jos);
			this.addJarSpliceLauncher(jos);
		} finally {
			jos.close();
			fos.close();
		}
	}

	private String getFileName(String ref) {
		ref = ref.replace('\\', '/');
		return ref.substring(ref.lastIndexOf('/') + 1);
	}

	private Manifest getManifest(String mainClass, String vmArgs) {
		Manifest manifest = new Manifest();
		Attributes attribute = manifest.getMainAttributes();
		attribute.putValue("Manifest-Version", "1.0");
		attribute.putValue("Main-Class", "de.Cosmoledo.jarSplice.JarSpliceLauncher");
		attribute.putValue("Launcher-Main-Class", mainClass);
		attribute.putValue("Launcher-VM-Args", vmArgs);
		return manifest;
	}
}