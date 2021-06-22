package converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import modelData.ArmatureData;
import modelData.ModelData;
import modelData.Vertex;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xmlParser.XMLParser;
import animation.Bone;
 
public class DAEFileLoader extends ModelParser {
	
	private static final Matrix4f CORRECTIVE_ROTATION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
	
    public static ModelData loadDAE(File daeFile) throws ParserConfigurationException, SAXException, IOException {
        ListModelData rawData = parseColladaModel(daeFile);
        
        List<Integer> objectIndices = new ArrayList<>();
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Integer> originalIndices = new ArrayList<>();
        
        List<Float> rawPositions = rawData.getVertices();
        for(int i = 0; i < rawPositions.size(); i += 3) {
        	Vector4f positionFour = new Vector4f(rawPositions.get(i), rawPositions.get(i + 1), rawPositions.get(i + 2), 1);
        	Matrix4f.transform(CORRECTIVE_ROTATION, positionFour, positionFour);
        	vertices.add(new Vertex(i / 3, new Vector3f(positionFour.x, positionFour.y, positionFour.z)));        	
        }
        
        List<Float> rawTextureCoordinates = rawData.getTextureCoordinates();
        for(int i = 0; i < rawTextureCoordinates.size(); i += 2) {
        	textures.add(new Vector2f(rawTextureCoordinates.get(i), rawTextureCoordinates.get(i + 1)));
        }
        
        List<Float> rawNormals = rawData.getNormals();
        for(int i = 0; i < rawNormals.size(); i += 3) {
        	Vector4f normalFour = new Vector4f(rawNormals.get(i), rawNormals.get(i + 1), rawNormals.get(i + 2), 0);
        	Matrix4f.transform(CORRECTIVE_ROTATION, normalFour, normalFour);
        	normals.add(new Vector3f(normalFour.x, normalFour.y, normalFour.z));
        }
        
        List<Integer> rawIndices = rawData.getIndices();
        
        //Read in each vertex, one at a time, including its position, texture coordinate, and normal.
        for(int i = 0; i + 2 < rawIndices.size(); i += 3) {
        	int[] vertex = new int[]{rawIndices.get(i), rawIndices.get(i + 1), rawIndices.get(i + 2)};
            ModelParser.processVertex(0, 0, vertex, objectIndices, vertices, indices, ModelParser.DAE_FILE_FORMAT);
            originalIndices.add(rawIndices.get(i));
        }
        
        ModelParser.removeUnusedVertices(vertices);
        
        //TODO: Add object indices
    	//TODO: Add texture indices
        int[] objectIndicesArray = ModelParser.generateObjectIndicesArray(vertices);
        int[] textureIndicesArray = ModelParser.generateTextureIndicesArray(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float farthest = ModelParser.convertDataToArrays(vertices, textures, normals, objectIndicesArray,
        		textureIndicesArray, verticesArray, texturesArray, normalsArray);
        
        int[] indicesArray = VariableConverter.toArrayInteger(indices);
        int[] originalIndicesArray = VariableConverter.toArrayInteger(originalIndices);
        
        System.out.println("---DAEFileLoader.loadDAE()---");
        System.out.println("Length of indices came out to " + indicesArray.length);
        
        ModelData data = new ModelData(objectIndicesArray, textureIndicesArray, verticesArray, texturesArray, normalsArray,
        		indicesArray, originalIndicesArray, farthest);
        data.setName(daeFile.getName().split("\\.")[0]);
        
        return data;
    }
	
    public static ListModelData parseColladaModel(File file) throws ParserConfigurationException, SAXException, IOException {
		NodeList modelList = XMLParser.getMasterNodeList(file);
		List<Node> geometries = XMLParser.getNamedNodes(modelList, "library_geometries");
		
		if(geometries.size() != 1) {
			System.err.println("There's not just one instance of library_geometries! Instead, there's " + geometries.size() + "!");
			System.err.println("Returning null...");
			return null;
		}
		
		NodeList modelData = XMLParser.getNodesWithin(geometries.get(0));
		String geometryID = "";
		
		//Get the name of the geometry ID.
		for(int j = 0; j < modelData.getLength(); j++) {
			Node modelDataNode = modelData.item(j);
			String nodeName = modelDataNode.getNodeName();
			if(nodeName.equals("geometry")) {
				geometryID = XMLParser.getAttribute("id", XMLParser.extractAttributes(modelDataNode));
				break;
			}
		}
		
		//Now we can get the model data.
		Node positionsNode = XMLParser.drillDown(geometries.get(0), "float_array", new String[][]{{"id", geometryID + "-positions-array"}});
		Node normalsNode = XMLParser.drillDown(geometries.get(0), "float_array", new String[][]{{"id", geometryID + "-normals-array"}});
		Node textureCoordinatesNode = XMLParser.drillDown(geometries.get(0), "float_array", new String[][]{{"id", geometryID + "-map-0-array"}});
		Node polylistNode = XMLParser.drillDown(geometries.get(0), "polylist", null);
		
		Node inputVertex = XMLParser.drillDown(polylistNode, "input", new String[][]{{"semantic", "VERTEX"}});
		Node inputTexture = XMLParser.drillDown(polylistNode, "input", new String[][]{{"semantic", "TEXCOORD"}});
		Node inputNormal = XMLParser.drillDown(polylistNode, "input", new String[][]{{"semantic", "NORMAL"}});
		
		int vertexOffset = Integer.parseInt(XMLParser.getAttribute("offset", inputVertex));
		int textureOffset = Integer.parseInt(XMLParser.getAttribute("offset", inputTexture));
		int normalOffset = Integer.parseInt(XMLParser.getAttribute("offset", inputNormal));
		int inputCount = XMLParser.getChildNodes(polylistNode, "input").size();
		
		Node indicesNode = XMLParser.getChildNode(polylistNode, "p");
		
		List<Float> positions = new ArrayList<>();
		String[] stringPositions = XMLParser.extractDataFrom(positionsNode, " ");
		for(String position : stringPositions) {
			positions.add(Float.parseFloat(position));
		}
		
		List<Float> normals = new ArrayList<>();
		String[] stringNormals = XMLParser.extractDataFrom(normalsNode, " ");
		for(String normal : stringNormals) {
			normals.add(Float.parseFloat(normal));
		}
		
		List<Float> textureCoordinates = new ArrayList<>();
		String[] stringTextureCoordinates = XMLParser.extractDataFrom(textureCoordinatesNode, " ");		
		for(String textureCoordinate : stringTextureCoordinates) {
			textureCoordinates.add(Float.parseFloat(textureCoordinate));
		}
		
		String[] stringIndices = XMLParser.extractDataFrom(indicesNode, " ");
		List<Integer> indices = new ArrayList<>();
		for(int i = 0; i < stringIndices.length; i++) {
			int index = i % inputCount;
			if(index == vertexOffset || index == textureOffset || index == normalOffset)
				indices.add(Integer.parseInt(stringIndices[i]));
		}
		
		float furthest = 0;
		for(int i = 0; i + 2 < positions.size(); i += 3) {
			float a = positions.get(i), b = positions.get(i + 1), c = positions.get(i + 2);
			float distance = (a * a) + (b * b) + (c * c);
			furthest = (distance > furthest) ? distance : furthest;
		}
		
		//Square root only at the end as an optimization.
		furthest = (float) Math.sqrt(furthest);
		
		ListModelData listModelData = new ListModelData(positions, textureCoordinates, normals, indices, furthest);
		
		return listModelData;
	}
    
    public static ModelData loadAnimatedDAE(File daeFile, File animationFile) throws ParserConfigurationException, SAXException, IOException {
    	ModelData modelData = loadDAE(daeFile);
    	    	
        ArmatureData armatureData = parseColladaArmature(daeFile);
        armatureData.calculateVBOData(modelData.getOriginalIndices(), modelData.getIndices());
        modelData.addArmatureData(armatureData);
        
        modelData.setAnimationFile(animationFile);
        
        return modelData;
    }
    
    public static ArmatureData parseColladaArmature(File file) throws ParserConfigurationException, SAXException, IOException {
		NodeList modelList = XMLParser.getMasterNodeList(file);
		List<Node> controllersLibrary = XMLParser.getNamedNodes(modelList, "library_controllers");
		List<Node> visualScenesLibrary = XMLParser.getNamedNodes(modelList, "library_visual_scenes");
		
		if(controllersLibrary.size() != 1) {
			System.err.println("There's not just one instance of library_controllers! Instead, there's " + controllersLibrary.size() + "!");
			System.err.println("Returning null...");
			return null;
		}
		
		if(visualScenesLibrary.size() != 1) {
			System.err.println("There's not just one instance of library_visual_scenes! Instead, there's " + visualScenesLibrary.size() + "!");
			System.err.println("Returning null...");
			return null;
		}
		
		//PARSE ARMATURE
		NodeList armatureData = XMLParser.getNodesWithin(controllersLibrary.get(0));
		String armatureID = "";
		
		//Get the name of the armature ID.
		for(int j = 0; j < armatureData.getLength(); j++) {
			Node armatureDataNode = armatureData.item(j);
			String nodeName = armatureDataNode.getNodeName();
			if(nodeName.equals("controller")) {
				armatureID = XMLParser.getAttribute("id", XMLParser.extractAttributes(armatureDataNode));
				break;
			}
		}
		
		//Parse the needed armature information.
		Node weightsNode = XMLParser.drillDown(controllersLibrary.get(0), "float_array", new String[][]{{"id", armatureID + "-weights-array"}});
		Node weightedVertexCountNode = XMLParser.drillDown(controllersLibrary.get(0), "vcount", null);
		Node weightedVertexDataNode = XMLParser.drillDown(controllersLibrary.get(0), "v", null);
		Node jointNamesNode = XMLParser.drillDown(controllersLibrary.get(0), "Name_array", new String[][]{{"id", armatureID + "-joints-array"}});
		Node armatureNode = XMLParser.drillDown(visualScenesLibrary.get(0), "node", new String[][]{{"id", "Root"}});
		
		if(armatureNode == null) {
			System.err.println("Something screwed up while parsing... you may not have named the main bone \"Root.\" Returning null...");
			return null;
		}
		
		String[] stringJointNames = XMLParser.extractDataFrom(jointNamesNode, " ");
		
		List<Bone> orderedBones = new ArrayList<>();
		Map<String, Integer> boneIndices = new HashMap<>();
		
		for(int i = 0; i < stringJointNames.length; i++) {
			String name = stringJointNames[i];
			
			Bone bone = new Bone();
			bone.setIndex(i);
			bone.setName(name);
			
			orderedBones.add(bone);
			boneIndices.put(name, i);
		}
		
		//TODO: Make sure to transpose the bind matrices
		String[] stringWeights = XMLParser.extractDataFrom(weightsNode, " ");
		float[] parsedWeights = new float[stringWeights.length];
		
		for(int i = 0; i < stringWeights.length; i++) {
			parsedWeights[i] = Float.parseFloat(stringWeights[i]);
		}
		
		String[] stringWeightedVertexCount = XMLParser.extractDataFrom(weightedVertexCountNode, " ");
		int[] parsedWeightedVertexCount = new int[stringWeightedVertexCount.length];
		
		for(int i = 0; i < stringWeightedVertexCount.length; i++) {
			parsedWeightedVertexCount[i] = Integer.parseInt(stringWeightedVertexCount[i]);
		}
		
		String[] stringWeightedVertexData = XMLParser.extractDataFrom(weightedVertexDataNode, " ");
		int[] parsedWeightedVertexData = new int[stringWeightedVertexData.length];
		
		for(int i = 0; i < stringWeightedVertexData.length; i++) {
			parsedWeightedVertexData[i] = Integer.parseInt(stringWeightedVertexData[i]);
		}
		
		Bone armature = orderedBones.get(boneIndices.get("Root"));
		addChildrenToBone(armature, armatureNode, orderedBones, boneIndices);
		
		return new ArmatureData(parsedWeights, parsedWeightedVertexCount, parsedWeightedVertexData, armature);
	}
    
    private static void addChildrenToBone(Bone parentBone, Node boneNode, List<Bone> orderedBones, Map<String, Integer> boneIndices) {
		//This function recursively nests bones into their proper hierarchy.
		NodeList rawChildNodes = boneNode.getChildNodes();
		List<Node> childNodes = new ArrayList<>();
		for(int i = 0; i < rawChildNodes.getLength(); i++) {
			Node child = rawChildNodes.item(i);
			if(child.getNodeName().equals("node")) {
				childNodes.add(child);
			}
		}
		
		int numberOfChildren = childNodes.size();
		
		for(int i = 0; i < numberOfChildren; i++) {
			Node childNode = childNodes.get(i);
			Bone childBone = orderedBones.get(boneIndices.get(XMLParser.getAttribute("id", childNode)));
			parentBone.addChild(childBone);
			childBone.setParent(parentBone);
			addChildrenToBone(childBone, childNode, orderedBones, boneIndices);
		}
	}
}
