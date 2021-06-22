package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import modelData.TextureData;
import specsWindow.SpecsWindow;
import toolbox.M;

public class WindowPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private SpecsWindow specsWindow;
	
	private WindowListener windowListener;

	private GroupLayout layout;
	
	private JLabel welcome;
	private JButton loadModels;
	private JButton loadAnimations;
	private JButton prepareTextureIndices;
	private JButton convertFiles;
	
	private JScrollPane tableScrollPane;
	private JTable modelFileTable;
	private List<File> modelFiles;
	private List<File> animationFiles;
	private Map<File, Map<String, Integer>> objectTextureIndices;
	
	private JCheckBox indices, objectIndices, textureAtlasIndices, positions, textureCoordinates, normals;
	
	private JButton moveModelFileUp, moveModelFileDown;
	private JButton moveAnimationFileUp, moveAnimationFileDown;
	private JButton deleteModelFile, deleteAnimationFile;
	
	public WindowPanel() {
		specsWindow = new SpecsWindow();
		
		windowListener = new WindowListener(this);
		initializeComponents();
		initializeLayout();
		
		modelFiles = new ArrayList<>();
		animationFiles = new ArrayList<>();
		objectTextureIndices = new HashMap<>();
	}
	
	private void initializeComponents() {
		welcome = new JLabel("Welcome. Please select files to convert.");
		
		loadModels = new JButton("Load Models");
		loadModels.setActionCommand("Load Models");
		loadModels.addActionListener(windowListener);
		
		loadAnimations = new JButton("Load Animations");
		loadAnimations.setActionCommand("Load Animations");
		loadAnimations.addActionListener(windowListener);
		
		prepareTextureIndices = new JButton("Prepare Texture Data");
		prepareTextureIndices.setActionCommand("Prepare Texture Data");
		prepareTextureIndices.addActionListener(windowListener);
		
		convertFiles = new JButton("Convert Files");
		convertFiles.setActionCommand("Convert Files");
		convertFiles.addActionListener(windowListener);
		
		DefaultTableModel model = new DefaultTableModel();
		modelFileTable = new JTable(model);
		model.addColumn("Model File");
		model.addColumn("Animation File");
		tableScrollPane = new JScrollPane(modelFileTable);
				
		indices = new JCheckBox("Export Indices");
			indices.setSelected(true);
		objectIndices = new JCheckBox("Export Object Indices");
			objectIndices.setSelected(true);
		textureAtlasIndices = new JCheckBox("Export Texture Atlas Indices");
			textureAtlasIndices.setSelected(true);
		positions = new JCheckBox("Export Vertex Positions");
			positions.setSelected(true);
		textureCoordinates = new JCheckBox("Export Texture Coordinates");
			textureCoordinates.setSelected(true);
		normals = new JCheckBox("Export Normals");
			normals.setSelected(true);
		
		moveModelFileUp = new JButton("Move Model File Up");
		moveModelFileUp.setActionCommand("Model File Up");
		moveModelFileUp.addActionListener(windowListener);
		
		moveModelFileDown = new JButton("Move Model File Down");
		moveModelFileDown.setActionCommand("Model File Down");
		moveModelFileDown.addActionListener(windowListener);
		
		moveAnimationFileUp = new JButton("Move Animation File Up");
		moveAnimationFileUp.setActionCommand("Animation File Up");
		moveAnimationFileUp.addActionListener(windowListener);
		
		moveAnimationFileDown = new JButton("Move Animation File Down");
		moveAnimationFileDown.setActionCommand("Animation File Down");
		moveAnimationFileDown.addActionListener(windowListener);
		
		deleteModelFile = new JButton("Delete Model File");
		deleteModelFile.setActionCommand("Delete Model File");
		deleteModelFile.addActionListener(windowListener);
		
		deleteAnimationFile = new JButton("Delete Animation File");
		deleteAnimationFile.setActionCommand("Delete Animation File");
		deleteAnimationFile.addActionListener(windowListener);
	}
	
	private void initializeLayout() {
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.linkSize(welcome, loadModels, loadAnimations, prepareTextureIndices, convertFiles);
		layout.linkSize(moveModelFileUp, moveModelFileDown);
		layout.linkSize(moveAnimationFileUp, moveAnimationFileDown);
		layout.linkSize(deleteModelFile, deleteAnimationFile);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(welcome)
						.addComponent(loadModels)
						.addComponent(loadAnimations)
						.addComponent(prepareTextureIndices)
						.addComponent(convertFiles))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(tableScrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(moveModelFileUp)
								.addComponent(moveAnimationFileUp)
								.addComponent(deleteModelFile))
						.addGroup(layout.createSequentialGroup()
								.addComponent(moveModelFileDown)
								.addComponent(moveAnimationFileDown)
								.addComponent(deleteAnimationFile)))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(indices)
						.addComponent(objectIndices)
						.addComponent(textureAtlasIndices)
						.addComponent(positions)
						.addComponent(textureCoordinates)
						.addComponent(normals))
				);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(welcome)
								.addComponent(loadModels)
								.addComponent(loadAnimations)
								.addComponent(prepareTextureIndices)
								.addComponent(convertFiles))
						.addGroup(layout.createSequentialGroup()
								.addComponent(tableScrollPane)
								.addGroup(layout.createParallelGroup()
										.addComponent(moveModelFileUp)
										.addComponent(moveAnimationFileUp)
										.addComponent(deleteModelFile))
								.addGroup(layout.createParallelGroup()
										.addComponent(moveModelFileDown)
										.addComponent(moveAnimationFileDown)
										.addComponent(deleteAnimationFile)))
						.addGroup(layout.createSequentialGroup()
								.addComponent(indices)
								.addComponent(objectIndices)
								.addComponent(textureAtlasIndices)
								.addComponent(positions)
								.addComponent(textureCoordinates)
								.addComponent(normals))));
	}
	
	public List<File> getModelFiles() {
		return modelFiles;
	}

	public void setModelFiles(File[] modelFiles) {
		this.modelFiles.clear();
		for(File modelFile : modelFiles) {
			this.modelFiles.add(this.modelFiles.size(), modelFile);
		}
		
		updateTable();
	}
	
	public void populateModelFiles(File[] modelFiles, int startIndex) {
		int trueStartIndex = (startIndex < 0) ? this.modelFiles.size() : startIndex;
		
		//Append the new elements onto the existing ArrayList. Make sure the animation ArrayList is equal in size.
		for(int i = trueStartIndex; i < trueStartIndex + modelFiles.length; i++) {
			this.modelFiles.add(i, modelFiles[i - trueStartIndex]);
		}
		
		while(this.animationFiles.size() < this.modelFiles.size()) {
			this.animationFiles.add(null);
		}
		
		updateTable();
	}

	public List<File> getAnimationFiles() {
		return animationFiles;
	}
	
	public void setAnimationFiles(File[] animationFiles) {
		this.animationFiles.clear();
		for(File animationFile : animationFiles) {
			this.animationFiles.add(this.animationFiles.size(), animationFile);
		}
		
		updateTable();
	}
	
	public void populateAnimationFiles(File[] animationFiles, int startIndex) {
		int trueStartIndex = (startIndex < 0) ? 0 : startIndex;
		
		//Replace existing elements with the new elements. Do not expand the ArrayList.
		for(int i = trueStartIndex; i < trueStartIndex + animationFiles.length; i++) {
			if(i >= this.animationFiles.size()) break;
			
			this.animationFiles.add(i, animationFiles[i - trueStartIndex]);
			this.animationFiles.remove(i + 1);
		}
		
		updateTable();
	}
	
	public void putObjectTextureIndexMap(int fileIndex, Map<String, Integer> objectTextureIndexMap) {
		if(!inModelListBounds(fileIndex)) return;
		putObjectTextureIndexMap(modelFiles.get(fileIndex), objectTextureIndexMap);
	}
	
	public void putObjectTextureIndexMap(File modelFile, Map<String, Integer> objectTextureIndexMap) {
		objectTextureIndices.put(modelFile, objectTextureIndexMap);
	}

	private void updateTable() {
		expandTable(modelFiles.size());
		
		for(int i = 0; i < modelFileTable.getRowCount(); i++) {
			File modelFile = (modelFiles.size() > i) ? modelFiles.get(i) : null;
			File animationFile = (animationFiles.size() > i) ? animationFiles.get(i) : null;
			modelFileTable.setValueAt((modelFile != null) ? modelFile.getName() : "", i, 0);
			modelFileTable.setValueAt((animationFile != null) ? animationFile.getName() : "", i, 1);
		}
	}
	
	private void expandTable(int numRows) {
		DefaultTableModel model = (DefaultTableModel) modelFileTable.getModel();
		for(int i = model.getRowCount(); i < numRows; i++) {
			model.addRow(new Object[]{"", ""});
		}
	}
	
	public void swapModelFilePositions(int fileIndex1, int fileIndex2) {
		if(!inModelListBounds(fileIndex1) || !inModelListBounds(fileIndex2)) return;
		int lowerIndex = M.min(fileIndex1, fileIndex2);
		int upperIndex = M.max(fileIndex1, fileIndex2);
		
		modelFiles.add(lowerIndex, modelFiles.get(upperIndex));
		modelFiles.add(upperIndex + 1, modelFiles.get(lowerIndex + 1));
		modelFiles.remove(upperIndex + 2);
		modelFiles.remove(lowerIndex + 1);
		updateTable();
	}
	
	public void swapAnimationFilePositions(int fileIndex1, int fileIndex2) {
		if(!inModelListBounds(fileIndex1) || !inModelListBounds(fileIndex2)) return;
		int lowerIndex = M.min(fileIndex1, fileIndex2);
		int upperIndex = M.max(fileIndex1, fileIndex2);
		
		animationFiles.add(lowerIndex, animationFiles.get(upperIndex));
		animationFiles.add(upperIndex + 1, animationFiles.get(lowerIndex + 1));
		animationFiles.remove(upperIndex + 2);
		animationFiles.remove(lowerIndex + 1);
		updateTable();
	}
	
	private boolean inModelListBounds(int index) {
		return index >= 0 && index < modelFiles.size();
	}
	
	private boolean inAnimationListBounds(int index) {
		return index >= 0 && index < modelFiles.size();
	}
	
	public File getModelFile(int index) {
		if(!inModelListBounds(index)) return null;
		return modelFiles.get(index);
	}
	
	public void moveTableSelectionUp() {
		int rowToSelect = modelFileTable.getSelectedRow() - 1;
		if(rowToSelect < 0) return;
		modelFileTable.setRowSelectionInterval(rowToSelect, rowToSelect);
	}
	
	public void moveTableSelectionDown() {
		int rowToSelect = modelFileTable.getSelectedRow() + 1;
		if(rowToSelect >= modelFileTable.getModel().getRowCount()) return;
		modelFileTable.setRowSelectionInterval(rowToSelect, rowToSelect);
	}
	
	public void deleteModelFile() {
		int deleteIndex = modelFileTable.getSelectedRow();
				
		if(!inModelListBounds(deleteIndex) || !inAnimationListBounds(deleteIndex)) return;
				
		modelFiles.remove(deleteIndex);
		animationFiles.remove(deleteIndex);
		
		deleteAnimationFile();
	}
	
	public void deleteAnimationFile() {
		int deleteIndex = modelFileTable.getSelectedRow();
		
		if(!inAnimationListBounds(deleteIndex)) return;
		animationFiles.add(deleteIndex, null);
		animationFiles.remove(deleteIndex + 1);
		
		updateTable();
	}
	
	public int getSelectedTableRow() {
		return modelFileTable.getSelectedRow();
	}
	
	public boolean objectIndicesChecked() {
		return objectIndices.isSelected();
	}
	
	public boolean textureIndicesChecked() {
		return textureAtlasIndices.isSelected();
	}
	
	public boolean positionsChecked() {
		return positions.isSelected();
	}
	
	public boolean textureCoordinatesChecked() {
		return textureCoordinates.isSelected();
	}
	
	public boolean normalsChecked() {
		return normals.isSelected();
	}
	
	public boolean indicesChecked() {
		return indices.isSelected();
	}
	
	public void promptForTextureData(TextureData textureData) {
		int whichModel = modelFileTable.getSelectedRow();
		if(!inModelListBounds(whichModel)) return;
		
		specsWindow.setActiveData(textureData);
		specsWindow.populateIndexTable(objectTextureIndices.get((modelFiles.get(whichModel))));
		specsWindow.setVisible(true);
	}
	
	public Map<String, Integer> getTextureIndices(File modelFile) {
		return objectTextureIndices.get(modelFile);
	}
}
