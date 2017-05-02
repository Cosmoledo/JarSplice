package de.Cosmoledo.jarSplice;

import java.io.*;

public class Methods {
	public static String readExternal(String FileName) {
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(FileName));
			String line;
			while((line = br.readLine()) != null) {
				text.append(line);
				if(br.ready())
					text.append('\n');
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return text + "";
	}
	
	public static void write(String FileName, String Point) {
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(FileName));
			b.write(Point);
			b.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}