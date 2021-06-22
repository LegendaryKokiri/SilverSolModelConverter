package modelData;

import java.io.File;

public class ModelData {
 
	private String name;
	
	//MODEL COMPONENT
	private int[] objectIndices;
	private int[] textureIndices;
    private float[] vertices;
    private float[] textureCoordinates;
    private float[] normals;
    private int[] indices;
    private int[] originalIndices;
    private float farthestPoint;
    
    //ARMATURE COMPONENT
    private boolean hasArmature;
    	private ArmatureData armatureData;
    
    //ANIMATION COMPONENT
    private boolean hasAnimation;
    	private File animationFile;
     
    public ModelData(int[] objectIndices, int[] textureIndices, float[] vertices, float[] textureCoordinates, float[] normals,
    		int[] indices, float farthestPoint) {
    	this.name = "";
    	
    	this.objectIndices = objectIndices;
    	this.textureIndices = textureIndices;
        this.vertices = vertices;
        this.textureCoordinates = textureCoordinates;
        this.normals = normals;
        this.indices = indices;
        this.originalIndices = indices;
        this.farthestPoint = farthestPoint; //The distance from the origin of the point farthest from the origin.        
    }
    
    public ModelData(int[] objectIndices, int[] textureIndices, float[] vertices, float[] textureCoordinates, float[] normals,
    		int[] indices, int[] originalIndices, float farthestPoint) {
    	this.name = "";
    	
    	this.objectIndices = objectIndices;
    	this.textureIndices = textureIndices;
        this.vertices = vertices;
        this.textureCoordinates = textureCoordinates;
        this.normals = normals;
        this.indices = indices;
        this.originalIndices = originalIndices;
        this.farthestPoint = farthestPoint; //The distance from the origin of the point farthest from the origin.        
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public int[] getObjectIndices() {
    	return objectIndices;
    }
    
    public int[] getTextureIndices() {
    	return textureIndices;
    }
 
    public float[] getVertices() {
        return vertices;
    }
 
    public float[] getTextureCoordinates() {
        return textureCoordinates;
    }
 
    public float[] getNormals() {
        return normals;
    }
 
    public int[] getIndices() {
        return indices;
    }
    
    public int[] getOriginalIndices() {
        return originalIndices;
    }
 
    public float getFurthestPoint() {
        return farthestPoint;
    }
    
    public boolean hasArmature() {
    	return hasArmature;
    }
    
    public void addArmatureData(ArmatureData armatureData) {
    	if(armatureData == null) return;
    	this.hasArmature = true;
    	this.armatureData = armatureData;
    }
    
    public ArmatureData getArmatureData() {
    	if(!hasArmature) return null;
		return armatureData;
	}
    
    public boolean hasAnimation() {
    	return hasAnimation;
    }
    
    public void setAnimationFile(File animationFile) {
    	this.animationFile = animationFile;
    	this.hasAnimation = this.animationFile != null;
    }
	
	public File getAnimationFile() {
		return animationFile;
	}
 
}