package specsWindow;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelData.TextureData;

public class SpecsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private SpecsWindow specsWindow;
	private SpecsListener listener;
	
	private GroupLayout layout;
	
	private JButton selectRoot;
	private JTextField rootDirectory;
	
	private JScrollPane pathTableScrollPane;
	private JTable texturePathTable;
	private JButton addButton;
	private JButton browseButton;
	private JButton deleteButton;
	
	private JScrollPane indexTableScrollPane;
	private JTable textureIndexTable;
	private JButton okButton;
	private JButton cancelButton;
	
	private TextureData textureData;
	private Map<String, Integer> textureIndices;
		
	public SpecsPanel(SpecsWindow specsWindow) {
		this.specsWindow = specsWindow;
		textureIndices = new HashMap<>();
		initializeComponents();
		initializeLayout();
	}
	
	private void initializeComponents() {
		listener = new SpecsListener(this);
		
		selectRoot = new JButton("Select Root Directory");
		selectRoot.setActionCommand("Select Root");
		selectRoot.addActionListener(listener);
		
		rootDirectory = new JTextField("");
		
		DefaultTableModel pathModel = new DefaultTableModel();
		texturePathTable = new JTable(pathModel);
		pathModel.addColumn("#");
		pathModel.addColumn("Texture Path");
		pathTableScrollPane = new JScrollPane(texturePathTable);
		
		DefaultTableModel indexModel = new DefaultTableModel();
		textureIndexTable = new JTable(indexModel);
		indexModel.addColumn("Object Name");
		indexModel.addColumn("Texture Index");
		indexTableScrollPane = new JScrollPane(textureIndexTable);
		
		addButton = new JButton("Add Texture");
		addButton.setActionCommand("Add Texture");
		addButton.addActionListener(listener);
		
		browseButton = new JButton("Browse");
		browseButton.setActionCommand("Browse");
		browseButton.addActionListener(listener);
		
		deleteButton = new JButton("Delete Texture");
		deleteButton.setActionCommand("Delete Texture");
		deleteButton.addActionListener(listener);
		
		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(listener);
		
		cancelButton = new JButton("Cancel");	
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(listener);
	}
	
	private void initializeLayout() {
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.linkSize(browseButton, okButton, cancelButton);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(selectRoot)
								.addComponent(rootDirectory))
						.addComponent(pathTableScrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(addButton)
								.addComponent(browseButton)
								.addComponent(deleteButton))
						.addComponent(indexTableScrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(okButton)
								.addComponent(cancelButton))));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup()
										.addComponent(selectRoot)
										.addComponent(rootDirectory))
								.addComponent(pathTableScrollPane)
								.addGroup(layout.createParallelGroup()
										.addComponent(addButton)
										.addComponent(browseButton)
										.addComponent(deleteButton))
								.addComponent(indexTableScrollPane)
								.addGroup(layout.createParallelGroup()
										.addComponent(okButton)
										.addComponent(cancelButton)))));
	}
	
	public String getRootDirectory() {
		return rootDirectory.getText();
	}
	
	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory.setText(rootDirectory.getPath());
	}
	
	public TextureData getTextureData() {
		return textureData;
	}
	
	//TODO: Do what needs to be done for the texture indices as well.
	public void setActiveData(TextureData textureData) {
		this.textureData = textureData;
		clearTexturePaths();
		
		for(String path : this.textureData.getTexturePaths()) {
			expandPathTable();
			addTexturePath(path, texturePathTable.getRowCount() - 1);
		}
	}
	
	public String[] getTexturePaths() {
		String[] paths = new String[texturePathTable.getRowCount()];
		
		for(int i = 0; i < texturePathTable.getRowCount(); i++) {
			paths[i] = (String) texturePathTable.getValueAt(i, 1);
		}
		
		return paths;
	}
	
	public void addTexturePath(File file) {
		addTexturePath(file.getPath(), texturePathTable.getSelectedRow());
	}
	
	public void addTexturePath(File file, int whichRow) {
		addTexturePath(file.getPath(), whichRow);
	}
	
	public void addTexturePath(String path) {
		addTexturePath(path, texturePathTable.getSelectedRow());
	}
	
	public void addTexturePath(String path, int whichRow) {		
		if(!rootDirectory.getText().isEmpty()) {
			path = path.replace(rootDirectory.getText(), "");
			path = path.replace("\\", "/");
		}
		
		texturePathTable.setValueAt(path, whichRow, 1);
	}
	
	public void populateIndexTable(Map<String, Integer> textureIndices) {
		this.textureIndices = textureIndices; 
		Set<String> objects = textureIndices.keySet();
		expandTable((DefaultTableModel) textureIndexTable.getModel(), objects.size());
		updateIndexTable(textureIndices);
	}
	
	public void expandPathTable() {
		expandTable((DefaultTableModel) texturePathTable.getModel(), texturePathTable.getRowCount() + 1);
		updatePathTable();
	}
	
	private void expandTable(DefaultTableModel model, int numRows) {
		for(int i = model.getRowCount(); i < numRows; i++) {
			model.addRow(new Object[]{"", ""});
		}
	}
	
	private void updatePathTable() {
		for(int i = 0; i < texturePathTable.getRowCount(); i++) {
			texturePathTable.setValueAt(i, i, 0);
		}
	}
	
	private void updateIndexTable(Map<String, Integer> textureIndices) {
		Set<String> objects = textureIndices.keySet();
		int iterations = 0;
		for(String objectName : objects) {
			textureIndexTable.setValueAt(objectName, iterations, 0);
			textureIndexTable.setValueAt(textureIndices.get(objectName), iterations, 1);
			iterations++;
		}
	}
	
	public void deleteTexturePath() {
		int deleteIndex = texturePathTable.getSelectedRow();
				
		if(!inPathTableBounds(deleteIndex)) return;
		
		DefaultTableModel model = (DefaultTableModel) texturePathTable.getModel();
		model.removeRow(deleteIndex);
		
		updatePathTable();
	}
	
	public void clearTexturePaths() {
		while(texturePathTable.getRowCount() > 0) {
			DefaultTableModel model = (DefaultTableModel) texturePathTable.getModel();
			model.removeRow(0);
		}
		
		updatePathTable();
	}
	
	private boolean inPathTableBounds(int index) {
		return index >= 0 && index < texturePathTable.getRowCount();
	}
	
	public boolean indexInputValid() {
		for(int i = 0; i < textureIndexTable.getRowCount(); i++) {
			try {
				Integer.parseInt(textureIndexTable.getValueAt(i, 1).toString());
			} catch(NumberFormatException e) {
				return false;
			}
		}
		
		return true;
	}
	
	public void promptForValidTextureIndex() {
		JOptionPane.showMessageDialog(null, "Enter only integers into the text fields.");
	}
	
	public void updateTextureData() {
		this.textureData.setTexturePaths(getTexturePaths());
	}
	
	public void updateSavedTextureIndices() {
		if(textureIndexTable.getRowCount() == 0) return;
		
		for(int i = 0; i < textureIndexTable.getRowCount(); i++) {
			textureIndices.put(textureIndexTable.getValueAt(i, 0).toString(),
					Integer.parseInt(textureIndexTable.getValueAt(i, 1).toString()));
		}
		
		this.textureData.setAtlas((int) Math.sqrt(Collections.max(textureIndices.values())) + 1);
	}
	
	public void closeWindow() {
		specsWindow.setVisible(false);
	}
}
