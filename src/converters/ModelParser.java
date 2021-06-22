package converters;

import java.util.List;

import modelData.Vertex;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ModelParser {
	
	public static final int OBJ_FILE_FORMAT = 0;
	public static final int DAE_FILE_FORMAT = 1;
	
	//int[] vertex consists of the indices of one vertex, one texture coordinate, and one normal.
	public static void processVertex(int objectIndex, int textureIndex, int[] vertexData, List<Integer> objectIndices,
			List<Vertex> vertices, List<Integer> indices, int fileFormat) {
		//Take the vertex in our list that corresponds to the index.
        int index = vertexData[0];
        Vertex currentVertex = vertices.get(index);
        
        //Also take note of the relevant texture and normal index for the given vertex.
        int textureCoordinatesIndex = 0, normalIndex = 0;
        if(fileFormat == OBJ_FILE_FORMAT) {
        	textureCoordinatesIndex = vertexData[1];
        	normalIndex = vertexData[2];
        } else if(fileFormat == DAE_FILE_FORMAT) {
        	textureCoordinatesIndex = vertexData[2];
        	normalIndex = vertexData[1];
        }
        
        //If we haven't assigned the current vertex a texture and normal index, we can do that now and then update the indices list.
        if (!currentVertex.isSet()) {
        	currentVertex.setObjectIndex(objectIndex);
        	currentVertex.setTextureIndex(textureIndex);
            currentVertex.setTextureCoordinatesIndex(textureCoordinatesIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            handleSetVertex(objectIndex, textureIndex, currentVertex, textureCoordinatesIndex, normalIndex, objectIndices, vertices, indices);
        }
    }
 
	private static void handleSetVertex(int objectIndex, int textureIndex, Vertex previousVertex, int textureCoordinateIndex,
            int normalIndex, List<Integer> objectIndices, List<Vertex> vertices, List<Integer> indices) {
    	//If we are here, we've found a vertex that's already been assigned a texture coordinate and a normal (indices pointed to it more than once).
        if (previousVertex.indicesMatch(objectIndex, textureCoordinateIndex, normalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
            	//If the vertex in question has already been assigned a duplicate vertex, we need to assign a new duplicate to the duplicate.
                handleSetVertex(objectIndex, textureIndex, anotherVertex, textureCoordinateIndex, normalIndex, objectIndices, vertices, indices);
            } else {
            	//If the vertex in question has not already been assigned a duplicate vertex, we will make one.
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setObjectIndex(objectIndex);
                duplicateVertex.setTextureIndex(textureIndex);
                duplicateVertex.setTextureCoordinatesIndex(textureCoordinateIndex);
                duplicateVertex.setNormalIndex(normalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());                
            }
        }
    }
 
	public static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
            List<Vector3f> normals, int[] objectIndicesArray, int[] textureIndicesArray, float[] verticesArray,
            float[] texturesArray, float[] normalsArray) {
        float farthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            
            if (currentVertex.getLength() > farthestPoint) {
                farthestPoint = currentVertex.getLength();
            }
            
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoordinates = textures.get(currentVertex.getTextureCoordinatesIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            objectIndicesArray[i] = currentVertex.getObjectIndex();
            textureIndicesArray[i] = currentVertex.getTextureIndex();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoordinates.x;
            texturesArray[i * 2 + 1] = 1 - textureCoordinates.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        
        return farthestPoint;
    }
     
    public static void removeUnusedVertices(List<Vertex> vertices){
        for(Vertex vertex:vertices){
            if(!vertex.isSet()){
                vertex.setTextureCoordinatesIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }
    
    public static int[] generateObjectIndicesArray(List<Vertex> vertices) {
    	int[] objectIndicesArray = new int[vertices.size()];
    	for(int i = 0; i < vertices.size(); i++) {
    		objectIndicesArray[vertices.get(i).getIndex()] = vertices.get(i).getObjectIndex();
    	}
    	return objectIndicesArray;
    }
    
    public static int[] generateTextureIndicesArray(List<Vertex> vertices) {
    	int[] textureIndicesArray = new int[vertices.size()];
    	for(int i = 0; i < vertices.size(); i++) {
    		textureIndicesArray[vertices.get(i).getIndex()] = vertices.get(i).getTextureIndex();
    	}
    	return textureIndicesArray;
    }
}
