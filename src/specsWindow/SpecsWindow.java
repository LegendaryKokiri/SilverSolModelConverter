package specsWindow;

import java.util.Map;

import javax.swing.JFrame;

import modelData.TextureData;

public class SpecsWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private SpecsPanel panel;

	public SpecsWindow() {
		panel = new SpecsPanel(this);
		
		setVisible(false);
		setContentPane(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
	
	public void populateIndexTable(Map<String, Integer> textureIndices) {
		if(textureIndices == null) return;
		panel.populateIndexTable(textureIndices);
	}
	
	public void setActiveData(TextureData textureData) {
		panel.setActiveData(textureData);
	}
}
