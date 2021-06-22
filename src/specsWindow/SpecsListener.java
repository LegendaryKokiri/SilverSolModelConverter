package specsWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

public class SpecsListener implements ActionListener {

	private SpecsPanel panel;
	
	private JFileChooser fileChooser;
	
	public SpecsListener(SpecsPanel panel) {
		this.panel = panel;
		
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals("Select Root")) {
			fileChooser.setDialogTitle("Choose the resources directory for your project.");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int textureLoadApproved = fileChooser.showOpenDialog(panel);
			
			if(textureLoadApproved == JFileChooser.APPROVE_OPTION) {
				panel.setRootDirectory(fileChooser.getSelectedFile());
			}
		}
		
		if(action.getActionCommand().equals("Add Texture")) {
			panel.expandPathTable();
		}
		
		if(action.getActionCommand().equals("Browse")) {
			fileChooser.setDialogTitle("Choose the texture associated with this model.");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int textureLoadApproved = fileChooser.showOpenDialog(panel);
			
			if(textureLoadApproved == JFileChooser.APPROVE_OPTION) {
				panel.addTexturePath(fileChooser.getSelectedFile());
			}
		}

		if(action.getActionCommand().equals("Delete Texture")) {
			panel.deleteTexturePath();
		}
		
		if(action.getActionCommand().equals("OK")) {
			if(panel.indexInputValid()) {
				panel.updateTextureData();
				panel.updateSavedTextureIndices();
				panel.closeWindow();
			} else {
				panel.promptForValidTextureIndex();
			}
		}
		
		if(action.getActionCommand().equals("Cancel")) {
			panel.closeWindow();
		}
	}
}
