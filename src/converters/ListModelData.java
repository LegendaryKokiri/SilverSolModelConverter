package converters;

import java.util.List;

public class ListModelData {
 
	private List<Float> vertices;
	private List<Float> textureCoordinates;
	private List<Float> normals;
	private List<Integer> indices;
    private float farthestPoint;
 
    public ListModelData(List<Float> vertices, List<Float> textureCoordinates, List<Float> normals, List<Integer> indices,
            float farthestPoint) {
        this.vertices = vertices;
        this.textureCoordinates = textureCoordinates;
        this.normals = normals;
        this.indices = indices;
        this.farthestPoint = farthestPoint; //The distance from the origin of the point farthest from the origin.
    }
 
    public List<Float> getVertices() {
        return vertices;
    }
 
    public List<Float> getTextureCoordinates() {
        return textureCoordinates;
    }
 
    public List<Float> getNormals() {
        return normals;
    }
 
    public List<Integer> getIndices() {
        return indices;
    }
 
    public float getFurthestPoint() {
        return farthestPoint;
    }
 
}