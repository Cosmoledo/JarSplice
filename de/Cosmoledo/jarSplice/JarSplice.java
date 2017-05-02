package de.Cosmoledo.jarSplice;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import de.Cosmoledo.jarSplice.panel.*;

public class JarSplice extends JFrame {
	public static File lastDirectory;
	public static JarSplice jarSpliceFrame;
	public JarsPanel jarsPanel = new JarsPanel();
	public NativesPanel nativesPanel = new NativesPanel();
	public ClassPanel classPanel = new ClassPanel();
	
	public JarSplice() {
		super("JarSplice - The Fat Jar Creator - version 0.10 - Cosmoledo's Version");
		TabPane tabPane = new TabPane();
		tabPane.addTab("INTRODUCTION", new IntroductionPanel(), true);
		tabPane.addTab("1) ADD JARS", this.jarsPanel, true);
		tabPane.addTab("2) ADD NATIVES", this.nativesPanel, true);
		tabPane.addTab("3) MAIN CLASS", this.classPanel, true);
		tabPane.addTab("4) CREATE FAT JAR", new CreatePanel(), true);
		this.add(tabPane, "Center");
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ReadFromJar.deleteDirectory(ReadFromJar.TEMP_DIR);
				System.exit(0);
			}
		});
		this.setDefaultCloseOperation(3);
		this.setVisible(true);
		if(new File("jarsplice.config").exists())
			lastDirectory = new File(Methods.readExternal("jarsplice.config"));
		else
			Methods.write("jarsplice.config", new JFileChooser().getCurrentDirectory().getAbsolutePath());
	}
	
	public static void main(String[] args) {
		if(args != null && args.length > 0) {
			ArrayList<String> jars = new ArrayList<>();
			ArrayList<String> natives = new ArrayList<>();
			String output = "";
			String mainClass = "";
			String vmArgs = "";
			for(int i = 0; i < args.length - 1; i++)
				switch(args[i]) {
					case "-j":
						for(int j = i + 1; j < args.length; j++) {
							if(args[j].startsWith("-j") || args[j].startsWith("-n") || args[j].startsWith("-o") || args[j].startsWith("-m") || args[j].startsWith("-v"))
								break;
							jars.add(args[j]);
						}
						break;
					case "-n":
						for(int j = i + 1; j < args.length; j++) {
							if(args[j].startsWith("-j") || args[j].startsWith("-n") || args[j].startsWith("-o") || args[j].startsWith("-m") || args[j].startsWith("-v"))
								break;
							natives.add(args[j]);
						}
						break;
					case "-o":
						output = args[i + 1];
						break;
					case "-m":
						mainClass = args[i + 1];
						break;
					case "-v":
						for(int j = i + 1; j < args.length; j++) {
							if(args[j].startsWith("-j") || args[j].startsWith("-n") || args[j].startsWith("-o") || args[j].startsWith("-m") || args[j].startsWith("-v"))
								break;
							vmArgs += " " + args[j];
						}
						vmArgs = vmArgs.substring(1);
						break;
					default:
						break;
				}
			if(jars.isEmpty() || natives.isEmpty() || output.isEmpty() || mainClass.isEmpty()) {
				System.out.println("How to use the console version of JarSplice made by Cosmoledo (c):");
				System.out.println("\t-j\tDefine here the jars, which should be compressed");
				System.out.println("\t-n\tDefine here the natives, which should be compressed");
				System.out.println("\t-o\tDefine here the output path and name");
				System.out.println("\t-m\tDefine here the main class");
				System.out.println("\t-v\tDefine here vm arguments");
				System.out.println();
				System.out.println("Example:");
				System.out.println("\t-j a.jar b.jar -n x32.dll x32.so -o c.jar -m main.Main -v -Xms128m -Xmx512m");
				System.exit(0);
			}
			try {
				System.out.println("Jar files:\n" + makeBretty(toString(jars)));
				System.out.println("Native files:\n" + makeBretty(toString(natives)));
				System.out.println("Output:\n\t" + output);
				System.out.println("Main-Class:\n\t" + mainClass);
				System.out.println("VM-args:\n" + makeBretty(vmArgs.split(" ")));
				new Splicer().createFatJar(toString(jars), toString(natives), output, mainClass, vmArgs);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else
			jarSpliceFrame = new JarSplice();
	}
	
	private static String makeBretty(String[] in) {
		String out = "";
		for(int i = 0; i < in.length; i++) {
			out += "\t" + in[i];
			if(i < in.length - 1)
				out += "\n";
		}
		return out;
	}

	private static String[] toString(ArrayList<String> in) {
		String[] out = new String[in.size()];
		for(int i = 0; i < out.length; i++)
			out[i] = in.get(i);
		return out;
	}
}