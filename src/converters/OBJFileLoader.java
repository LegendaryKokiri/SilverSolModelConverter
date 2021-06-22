package converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelData.ModelData;
import modelData.Vertex;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
 
public class OBJFileLoader extends ModelParser {
	
	public static Map<String, Integer> getObjectTextureIndexMap(File objFile) {
		FileReader isr = null;
		
		try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
		
		Map<String, Integer> textureAtlasIndices = new HashMap<>();
		
		String line;
		BufferedReader reader = new BufferedReader(isr);
		
		try {
			while((line = reader.readLine()) != null) {
				if(line.startsWith("o ")) {
					String objectName = line.split("\\s+")[1];
			    	textureAtlasIndices.put(objectName, 0);
			    }
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return textureAtlasIndices;
	}
	
    public static ModelData loadOBJ(File objFile, Map<String, Integer> textureIndices) {
        FileReader isr1 = null, isr2 = null;
                
        try {
            isr1 = new FileReader(objFile);
            isr2 = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
        
        String line;
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2f> textureCoordinates = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Integer> originalIndices = new ArrayList<>();
        List<Integer> objectIndices = new ArrayList<>();
        
        try {
        	//Start by reading in all of the values for vertices, texture coordinates, and normals.
        	//In the OBJ file format, there are only as many vertices and normals as are in the mesh.
        	//These values are only repeated implicitly in the indices.
        	//The same cannot be said for texture coordinates.
        	BufferedReader reader1 = new BufferedReader(isr1);
        	while((line = reader1.readLine()) != null) {
	            if (line.startsWith("v ")) {
	                String[] currentLine = line.split(" ");
	                Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    vertices.add(new Vertex(vertices.size(), vertex));
                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    textureCoordinates.add(new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2])));
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    normals.add(new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3])));
                }
            }
        	
        	//Now take in all of the indices three groups at a time. (One full face at a time.)
        	String currentObject = "";
        	int objectIndex = -1;
        	int textureIndex = 0;
        	
        	BufferedReader reader2 = new BufferedReader(isr2);
        	while((line = reader2.readLine()) != null) {
        		if(line.startsWith("o ")) {
        			objectIndex++;
        			currentObject = line.split("\\s+")[1];
        			textureIndex = textureIndices.get(currentObject);
        		} else if(line.startsWith("f ")) {
	                String[] currentLine = line.split(" ");
	                String[] vertex1 = currentLine[1].split("/");
	                String[] vertex2 = currentLine[2].split("/");
	                String[] vertex3 = currentLine[3].split("/");
	                
	                //We subtract one because OBJ files start numbering from 1 instead of from 0.
	                int[] intVertex1 = parseRow(vertex1);
	                int[] intVertex2 = parseRow(vertex2);
	                int[] intVertex3 = parseRow(vertex3);
	                
	                ModelParser.processVertex(objectIndex, textureIndex, intVertex1, objectIndices, vertices, indices, ModelParser.OBJ_FILE_FORMAT);
	                ModelParser.processVertex(objectIndex, textureIndex, intVertex2, objectIndices, vertices, indices, ModelParser.OBJ_FILE_FORMAT);
	                ModelParser.processVertex(objectIndex, textureIndex, intVertex3, objectIndices, vertices, indices, ModelParser.OBJ_FILE_FORMAT);
	                
	                originalIndices.add(intVertex1[0]);
	                originalIndices.add(intVertex2[0]);
	                originalIndices.add(intVertex3[0]);
	            }
        	}
        	
        	reader1.close();
        	reader2.close();
        } catch (IOException e) {
            System.err.println("Error reading the file");
        }
        
        ModelParser.removeUnusedVertices(vertices);
        
        int[] objectIndicesArray = ModelParser.generateObjectIndicesArray(vertices);
        int[] textureIndicesArray = ModelParser.generateTextureIndicesArray(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] textureCoordinatesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        
        if(textureCoordinates.isEmpty()) textureCoordinates.add(new Vector2f(0, 0));
        if(normals.isEmpty()) normals.add(new Vector3f(0, 0, 0));
        
        float farthest = ModelParser.convertDataToArrays(vertices, textureCoordinates, normals, objectIndicesArray, 
        		textureIndicesArray, verticesArray, textureCoordinatesArray, normalsArray);
        
        int[] indicesArray = VariableConverter.toArrayInteger(indices);
        int[] originalIndicesArray = VariableConverter.toArrayInteger(originalIndices);
        
        ModelData data = new ModelData(objectIndicesArray, textureIndicesArray, verticesArray, textureCoordinatesArray,
        		normalsArray, indicesArray, originalIndicesArray, farthest);
        data.setName(objFile.getName().split("\\.")[0]);
        
        return data;
    }
    
    private static int[] parseRow(String[] vertex) {
    	int position = vertex[0].isEmpty() ? 0 : Integer.parseInt(vertex[0]) - 1;
    	int textureCoordinate = vertex[1].isEmpty() ? 0 : Integer.parseInt(vertex[1]) - 1;
    	int normal = vertex[2].isEmpty() ? 0 : Integer.parseInt(vertex[2]) - 1;
    	return new int[]{position, textureCoordinate, normal};
    }
}
