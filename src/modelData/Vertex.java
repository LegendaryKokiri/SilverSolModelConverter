package modelData;
 
import org.lwjgl.util.vector.Vector3f;
 
public class Vertex {
     
    private static final int NO_INDEX = -1;
     
    private Vector3f position;
    private int objectIndex = NO_INDEX;
    private int textureIndex = NO_INDEX;
    private int textureCoordinatesIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private Vertex duplicateVertex = null;
    private int index;
    private float length;
     
    public Vertex(int index, Vector3f position){
        this.index = index;
        this.position = position;
        this.length = position.length();
    }
     
    public int getIndex(){
        return index;
    }
     
    public float getLength(){
        return length;
    }
     
    public boolean isSet(){
        return textureCoordinatesIndex != NO_INDEX && normalIndex != NO_INDEX;
    }
     
    public boolean indicesMatch(int objectIndex, int textureCoordinateIndex, int normalIndex){
        return (this.objectIndex == objectIndex && this.textureCoordinatesIndex == textureCoordinateIndex && this.normalIndex == normalIndex);
    }
    
    public void setObjectIndex(int objectIndex) {
    	this.objectIndex = objectIndex;
    }
    
    public void setTextureIndex(int textureIndex) {
    	this.textureIndex = textureIndex;
    }

	public void setTextureCoordinatesIndex(int textureCoordinatesIndex) {
        this.textureCoordinatesIndex = textureCoordinatesIndex;
    }
     
    public void setNormalIndex(int normalIndex) {
        this.normalIndex = normalIndex;
    }
 
    public Vector3f getPosition() {
        return position;
    }
    
    public int getObjectIndex() {
    	return objectIndex;
    }
    
    public int getTextureIndex() {
    	return textureIndex;
    }
 
    public int getTextureCoordinatesIndex() {
        return textureCoordinatesIndex;
    }
 
    public int getNormalIndex() {
        return normalIndex;
    }
 
    public Vertex getDuplicateVertex() {
        return duplicateVertex;
    }
 
    public void setDuplicateVertex(Vertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }
 
}