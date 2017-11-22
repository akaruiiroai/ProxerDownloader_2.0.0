package me.akaruiiroai.proxer.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

public class DirectorySelector implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		JFileChooser chooser;
	    chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File(Frame.textField_1.getText()));
	    chooser.setDialogTitle("Select where to save the Anime");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
      
	    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	System.out.println(chooser.getSelectedFile());
	    	Frame.textField_1.setText(chooser.getSelectedFile().getAbsolutePath());
	    }
	    
		
	}
}
