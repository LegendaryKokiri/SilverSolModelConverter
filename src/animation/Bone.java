package animation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Bone {
	
	//Bone identification
	private String name;
	private int index;
	
	//Bone hierarchy
	private Bone parent;
	private List<Bone> children;
	
	//Note: "Bone space" means "in relation to the parent joint."
	
	//In bone space--The offset of the bone from its parent bone when all bones in are in rest position
	private Matrix4f localBindTransformation;
	
	//In model space (note the change from bone space)--The inverse of the local bind transformation
	private Matrix4f inverseBindTransformation;
	
	//In model space--The transformation from the bind position to the position in the current frame of animation
	private Matrix4f transformation;
	
	public Bone() {
		name = "";
		index = -1;
		
		children = new ArrayList<>();
		
		localBindTransformation = new Matrix4f();
		inverseBindTransformation = new Matrix4f();
		
		localBindTransformation = new Matrix4f();
		transformation = new Matrix4f();
	}
	
	public void applyTransformations(Matrix4f parentTransformation) {
		Matrix4f currentModelSpaceTransformation = Matrix4f.mul(parentTransformation, this.localBindTransformation, null);
		
		for(Bone child : children) {
			child.applyTransformations(currentModelSpaceTransformation);
		}
		
		Matrix4f.mul(currentModelSpaceTransformation, inverseBindTransformation, transformation);
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getIndex(String name) {
		int index = -1;
		
		for(Bone child : children) {
			index = child.getIndex(name);			
			if(index != -1) return index;
		}
		
		return (name.equals(this.name)) ? this.index : -1;
	}
	
	public Bone getBone(String name) {
		for(Bone child : children) {
			if(child.getName().equals(name)) return child;
			child.getBone(name);
		}
		
		return (name.equals(this.name)) ? this : null;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Bone getParent() {
		return parent;
	}
	
	public void setParent(Bone parent) {
		this.parent = parent;
	}
	
	public void addChild(Bone child) {
		children.add(child);
	}
	
	public List<Bone> getChildren() {
		return children;
	}
	
	public void calculateInverseBindTransformation(Matrix4f parentBindTransformation) {
		//Convert the bone-space transform to a model-space transform before inversion
		Matrix4f bindTransformation = Matrix4f.mul(parentBindTransformation, localBindTransformation, null);
		Matrix4f.invert(bindTransformation, inverseBindTransformation);
				
		for(Bone child : children) {
			child.calculateInverseBindTransformation(bindTransformation);
		}
	}
	
	public void setLocalBindTransformation(Matrix4f localBindTransformation) {
		this.localBindTransformation = localBindTransformation;
	}
	
	public Matrix4f getLocalBindTransformation() {
		return localBindTransformation;
	}
	
	public void setInverseBindTransformation(Matrix4f inverseBindTransformation) {
		this.inverseBindTransformation = inverseBindTransformation;
	}
	
	public Matrix4f getInverseBindTransformation() {
		return inverseBindTransformation;
	}
	
	public Matrix4f getTransformation() {
		return transformation;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}
}
