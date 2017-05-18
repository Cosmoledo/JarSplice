package de.Cosmoledo.jarSplice.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import de.Cosmoledo.jarSplice.JarSplice;
import de.Cosmoledo.jarSplice.Methods;

public class NativesPanel extends JPanel implements ActionListener {
	private DefaultListModel listModel = new DefaultListModel();
	private JButton addButton, removeButton;
	private JFileChooser fileChooser;
	private File[] selectedFiles;
	private JList list;

	public NativesPanel() {
		this.fileChooser = new JFileChooser();
		this.fileChooser.setMultiSelectionEnabled(true);
		this.fileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(file.isDirectory())
					return true;
				String filename = file.getName();
				return (filename.endsWith(".dll")) || (filename.endsWith(".so")) || (filename.endsWith(".jnilib")) || (filename.endsWith(".dylib"));
			}

			@Override
			public String getDescription() {
				return "*.dll, *.so, *.jnilib, *.dylib";
			}
		};
		this.fileChooser.setFileFilter(filter);
		this.setLayout(new BorderLayout(5, 5));
		this.list = new JList(this.listModel);
		this.add(this.list, "Center");
		TitledBorder border = BorderFactory.createTitledBorder("Add Natives");
		border.setTitleJustification(2);
		this.setBorder(border);
		JPanel buttonPanel = new JPanel();
		this.addButton = new JButton("Add Native(s)");
		this.addButton.addActionListener(this);
		buttonPanel.add(this.addButton);
		this.removeButton = new JButton("Remove Native(s)");
		this.removeButton.addActionListener(this);
		buttonPanel.add(this.removeButton);
		this.add(buttonPanel, "Last");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.addButton) {
			this.fileChooser.setCurrentDirectory(JarSplice.lastDirectory);
			if(this.fileChooser.showDialog(this, "Add") == 0) {
				if(JarSplice.lastDirectory != this.fileChooser.getCurrentDirectory()) {
					Methods.write("jarsplice.config", this.fileChooser.getCurrentDirectory().getAbsolutePath());
					JarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
				}
				this.selectedFiles = this.fileChooser.getSelectedFiles();
				for(int i = 0; i < this.selectedFiles.length; i++) {
					this.listModel.removeElement(this.selectedFiles[i].getAbsolutePath());
					this.listModel.addElement(this.selectedFiles[i].getAbsolutePath());
				}
			}
		} else if(e.getSource() == this.removeButton) {
			List selectedItems = this.list.getSelectedValuesList();
			for(int i = 0; i < selectedItems.size(); i++)
				this.listModel.removeElement(selectedItems.get(i));
		}
	}

	public String[] getSelectedFiles() {
		if(this.selectedFiles == null)
			return new String[0];
		String[] files = new String[this.listModel.getSize()];
		for(int i = 0; i < files.length; i++)
			files[i] = ((String) this.listModel.get(i));
		return files;
	}
}