package de.Cosmoledo.jarSplice.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import de.Cosmoledo.jarSplice.JarSplice;
import de.Cosmoledo.jarSplice.Methods;
import de.Cosmoledo.jarSplice.ReadFromJar;
import de.Cosmoledo.jarSplice.Splicer;

public class CreatePanel extends JPanel implements ActionListener {
	private Splicer splicer = new Splicer();
	private JFileChooser fileChooser;
	private JButton createButton;
	
	public CreatePanel() {
		this.fileChooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = this.getSelectedFile();
				if((f.exists()) && (this.getDialogType() == 1)) {
					int result = JOptionPane.showConfirmDialog(this, "The file already exists. Do you want to overwrite it?", "Confirm Replace", 0);
					switch(result) {
						case 0:
							super.approveSelection();
							return;
						case 1:
							return;
						case 2:
							return;
					}
				}
				super.approveSelection();
			}
		};
		this.fileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(file.isDirectory())
					return true;
				String filename = file.getName();
				return filename.endsWith(".jar");
			}
			
			@Override
			public String getDescription() {
				return "*.jar";
			}
		};
		this.fileChooser.setFileFilter(filter);
		TitledBorder border = BorderFactory.createTitledBorder("Create Fat Jar");
		border.setTitleJustification(2);
		this.setBorder(border);
		JPanel buttonPanel = new JPanel();
		this.createButton = new JButton("Create Fat Jar");
		this.createButton.addActionListener(this);
		buttonPanel.add(this.createButton);
		this.add(buttonPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.createButton) {
			this.fileChooser.setCurrentDirectory(JarSplice.lastDirectory);
			if(this.fileChooser.showSaveDialog(this) == 0) {
				if(JarSplice.lastDirectory != this.fileChooser.getCurrentDirectory()) {
					Methods.write("jarsplice.config", this.fileChooser.getCurrentDirectory().getAbsolutePath());
					JarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
				}
				String[] jars = JarSplice.jarSpliceFrame.jarsPanel.getSelectedFiles();
				String[] natives = JarSplice.jarSpliceFrame.nativesPanel.getSelectedFiles();
				String output = this.getOutputFile(this.fileChooser.getSelectedFile());
				String mainClass = JarSplice.jarSpliceFrame.classPanel.vmTextField.getText();
				if(mainClass.length() == 0)
					try {
						ReadFromJar.extractJar(new File(jars[0]), ReadFromJar.TEMP_DIR.getAbsolutePath());
						String[] lines = Methods.readExternal(ReadFromJar.TEMP_DIR.getAbsolutePath() + "/manifest.mf").split("\n");
						for(int i = 0; i < lines.length; i++)
							if(lines[i].toLowerCase().contains("main-class")) {
								mainClass = lines[i].split(" ")[1];
								JarSplice.jarSpliceFrame.classPanel.classTextField.setText(mainClass);
								break;
							}
					} catch(IOException e1) {
						e1.printStackTrace();
					}
				String vmArgs = JarSplice.jarSpliceFrame.classPanel.vmTextField.getText();
				try {
					this.splicer.createFatJar(jars, natives, output, mainClass, vmArgs);
					JOptionPane.showMessageDialog(this, "Fat Jar Successfully Created.", "Success", -1);
				} catch(Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Jar creation failed due to the following exception:\n" + ex.getMessage(), "Failed", 0);
				}
			}
		}
	}
	
	private String getOutputFile(File file) {
		String outputFile = file.getAbsolutePath();
		if(!outputFile.endsWith(".jar"))
			outputFile = outputFile + ".jar";
		return outputFile;
	}
}