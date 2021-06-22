package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import modelData.ArmatureData;
import modelData.ModelData;
import modelData.TextureData;

import org.xml.sax.SAXException;

import animation.Bone;
import converters.DAEFileLoader;
import converters.OBJFileLoader;

public class WindowListener implements ActionListener {

	private WindowPanel panel;
	private JFileChooser fileChooser;
	private FileNameExtensionFilter modelFilter;
	private FileNameExtensionFilter animationFilter;
	
	private List<ModelData> modelData;
	private List<TextureData> textureData;
	
	public WindowListener(WindowPanel panel) {
		this.panel = panel;
		
		fileChooser = new JFileChooser();
		modelFilter = new FileNameExtensionFilter("Compatible Model Formats", "obj", "dae");
		animationFilter = new FileNameExtensionFilter("Compatible Animation Formats", "ani");
		fileChooser.setFileFilter(modelFilter);
		fileChooser.setMultiSelectionEnabled(true);
		
		modelData = new ArrayList<>();
		textureData = new ArrayList<>();
	}
	
	@Override
	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals("Load Models")) loadModels();
		if(action.getActionCommand().equals("Load Animations")) loadAnimations();
		if(action.getActionCommand().equals("Prepare Texture Data")) prepareTextureData();
		if(action.getActionCommand().equals("Convert Files")) convertFiles();
		
		if(action.getActionCommand().equals("Model File Up")) moveModelFileUp();
		if(action.getActionCommand().equals("Model File Down")) moveModelFileDown();
		if(action.getActionCommand().equals("Animation File Up")) moveAnimationFileUp();
		if(action.getActionCommand().equals("Animation File Down")) moveAnimationFileDown();
		
		if(action.getActionCommand().equals("Delete Model File")) deleteModelFile();
		if(action.getActionCommand().equals("Delete Animation File")) panel.deleteAnimationFile();
	}
	
	private void loadModels() {
		fileChooser.setDialogTitle("Choose the model files you want to convert.");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(modelFilter);
		
		int modelLoadApproved = fileChooser.showOpenDialog(panel);
		if(modelLoadApproved == JFileChooser.APPROVE_OPTION) {
			File[] files = fileChooser.getSelectedFiles();
			panel.populateModelFiles(files, -1);
			generateTextureData(files);
		}
	}
	
	private void generateTextureData(File[] files) {
		for(File modelFile : files) {
			String extension = modelFile.getName().split("\\.")[1];
			if(extension.equals("obj")) {
				panel.putObjectTextureIndexMap(modelFile, OBJFileLoader.getObjectTextureIndexMap(modelFile));
			}
			textureData.add(new TextureData());
		}
	}
	
	private void loadAnimations() {
		fileChooser.setDialogTitle("Choose the animation files you want to include in conversion.");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(animationFilter);
		
		int animationLoadApproved = fileChooser.showOpenDialog(panel);
		
		if(animationLoadApproved == JFileChooser.APPROVE_OPTION) {
			panel.populateAnimationFiles(fileChooser.getSelectedFiles(), panel.getSelectedTableRow());
		}
	}
	
	private void prepareTextureData() {
		panel.promptForTextureData(textureData.get(panel.getSelectedTableRow()));
	}
	
	private void convertFiles() {
		List<File> modelFiles = panel.getModelFiles();
		List<File> animationFiles = panel.getAnimationFiles();
		
		for(int i = 0; i < modelFiles.size(); i++) {
			try {
				convertFile(modelFiles.get(i), animationFiles.get(i));
			} catch (ParserConfigurationException | SAXException
					| IOException e) {
				e.printStackTrace();
			}
		}
		
		fileChooser.setDialogTitle("Choose the directory where you want to save the files.");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int saveApproved = fileChooser.showSaveDialog(panel);
		if(saveApproved == JFileChooser.APPROVE_OPTION) {
			for(int i = 0; i < modelData.size(); i++) {
				ModelData mData = modelData.get(i);
				TextureData tData = textureData.get(i);
				System.out.println("Attempting to write file for " + mData.getName() + "...");
				saveFile(mData, tData);
			}
		}
	}
	
	private void convertFile(File modelFile, File animationFile) throws ParserConfigurationException, SAXException, IOException {
		String extension = modelFile.getName().split("\\.")[1];
		ModelData data = null;
		
		if(extension.equals("obj")) {
			data = OBJFileLoader.loadOBJ(modelFile, panel.getTextureIndices(modelFile));
		} else if(extension.equals("dae")) {
			data = (animationFile != null) ?
					DAEFileLoader.loadAnimatedDAE(modelFile, animationFile) : DAEFileLoader.loadDAE(modelFile);
		} else {
			return;
		}
		
		if(data != null) modelData.add(data);
	}
	
	private void saveFile(ModelData modelData, TextureData textureData) {
		try {
			FileWriter writer = new FileWriter(fileChooser.getSelectedFile() + "/" + modelData.getName() + ".ssm");
			
			writeModelData(writer, modelData);
			writeTextureData(writer, textureData);
			if(modelData.hasArmature()) {
				writeArmatureData(writer, modelData);
				if(modelData.hasAnimation()) {
					writeAnimationData(writer, modelData);
				}
			}
			
			System.out.println("Successfully wrote the file!");
			
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Failed to write the file.");
		}
	}
	
	private void writeModelData(FileWriter writer, ModelData modelData) throws IOException {
		writer.write("MODEL\n");
		if(panel.objectIndicesChecked())
			writer.write("ObjectIndices: " + generateStringFromArray(modelData.getObjectIndices()) + "\n");
		if(panel.textureIndicesChecked())
			writer.write("TextureIndices: " + generateStringFromArray(modelData.getTextureIndices()) + "\n");
		if(panel.positionsChecked())
			writer.write("Vertices: " + generateStringFromArray(modelData.getVertices()) + "\n");
		if(panel.textureCoordinatesChecked())
			writer.write("TextureCoordinates: " + generateStringFromArray(modelData.getTextureCoordinates()) + "\n");
		if(panel.normalsChecked())
			writer.write("Normals: " + generateStringFromArray(modelData.getNormals()) + "\n");
		if(panel.indicesChecked())
			writer.write("Indices: " + generateStringFromArray(modelData.getIndices()) + "\n");
	}
	
	private void writeTextureData(FileWriter writer, TextureData textureData) throws IOException {
		writer.write("TEXTURE\n");
		writer.write("AtlasDimension: " + textureData.getAtlas() + "\n");
		for(String texturePath : textureData.getTexturePaths()) {
			writer.write("Texture: " + texturePath + "\n");
		}
	}
	
	private void writeArmatureData(FileWriter writer, ModelData modelData) throws IOException {
		ArmatureData armatureData = modelData.getArmatureData();
		writer.write("ARMATURE\n");
		writer.write("Bone Names: " + generateStringFromArmature(armatureData.getArmature(), "", 0) + "\n");
		writer.write("Bone Indices: " + generateStringFromArmatureIndices(armatureData.getArmature(), "") + "\n");
		writer.write("Bone VBO Indices: " + generateStringFromArray(armatureData.getVboBoneIndices()) + "\n");
		writer.write("Bone Weights: " + generateStringFromArray(armatureData.getVboBoneWeights()) + "\n");
	}
	
	private void writeAnimationData(FileWriter writer, ModelData modelData) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(modelData.getAnimationFile()));
		String line = "";
		while((line = reader.readLine()) != null) {
			writer.write(line + "\n");
		}
		reader.close();
	}
	
	private String generateStringFromArray(int[] array) {
		String string = "";
		
		for(int i = 0; i < array.length; i++) {
			string += Integer.toString(array[i]);
			if(i + 1 < array.length) string += " ";
		}
		
		return string;
	}
	
	private String generateStringFromArray(float[] array) {
		String string = "";
		
		for(int i = 0; i < array.length; i++) {
			string += Float.toString(array[i]);
			if(i + 1 < array.length) string += " ";
		}
		
		return string;
	}
	
	private String generateStringFromArmature(Bone armature, String string, int childCount) {
		if(string.equals("")) string += armature.getName();
		else string += " " + armature.getName() + "|" + armature.getParent().getName();
		
		for(Bone child : armature.getChildren()) {
			string = generateStringFromArmature(child, string, ++childCount);
		}
		
		return string;
	}
	
	private String generateStringFromArmatureIndices(Bone armature, String string) {
		if(string.equals("")) string += Integer.toString(armature.getIndex());
		else string += " " + Integer.toString(armature.getIndex());
		
		for(Bone child : armature.getChildren()) {
			string = generateStringFromArmatureIndices(child, string);
		}
		
		return string;
	}
	
	private void moveModelFileUp() {
		int fileIndex = panel.getSelectedTableRow();
		panel.swapModelFilePositions(fileIndex, fileIndex - 1);
		panel.moveTableSelectionUp();
	}
	
	private void moveModelFileDown() {
		int fileIndex = panel.getSelectedTableRow();
		panel.swapModelFilePositions(fileIndex, fileIndex + 1);
		panel.moveTableSelectionDown();
	}

	private void moveAnimationFileUp() {
		int fileIndex = panel.getSelectedTableRow();
		panel.swapAnimationFilePositions(fileIndex, fileIndex - 1);
		panel.moveTableSelectionUp();
	}

	private void moveAnimationFileDown() {
		int fileIndex = panel.getSelectedTableRow();
		panel.swapAnimationFilePositions(fileIndex, fileIndex + 1);
		panel.moveTableSelectionDown();
	}

	private void deleteModelFile() {
		textureData.remove(panel.getSelectedTableRow());
		panel.deleteModelFile();
	}
}
