package modelData;

import animation.Bone;

public class ArmatureData {
	
	private float[] weights; //How the affecting bones are weighted for each vertex
	private int[] weightedVertexCounts; //How many bones affect each vertex
	private int[] weightedVertexData; //Which bones and which weights affect each vertex
	private Bone armature; //This bone will include all of its children, so despite being of the Bone class, it is a full armature.
	
	//VAO Data
	private int[] boneIndices;
	private float[] boneWeights;
	private int[] vboBoneIndices;
	private float[] vboBoneWeights;
	
	public ArmatureData(float[] weights, int[] weightedVertexCounts, int[] weightedVertexData, Bone armature) {
		this.weights = weights;
		this.weightedVertexCounts = weightedVertexCounts;
		this.weightedVertexData = weightedVertexData;
		this.armature = armature;
		
		int arrayLength = weightedVertexCounts.length * 3;
		this.boneIndices = new int[arrayLength];
		this.boneWeights = new float[arrayLength];
		
		//In the COLLADA file, the number of entries of data for vertex is not constant.
		//Instead, only the necessary information is exported.
		//int offset keeps track of where we need to begin parsing for each vertex in order to get the correct data every time.
		int offset = 0;
		
		for(int i = 0; i < weightedVertexCounts.length; i++) {
			int bonesAffectingVertex = weightedVertexCounts[i];
			
			//We want three entries for each bone no matter what.
			for(int j = 0; j < 3; j++) {
				if(j < bonesAffectingVertex) {
					this.boneIndices[i * 3 + j] = weightedVertexData[offset + (j * 2)];
					this.boneWeights[i * 3 + j] = weights[weightedVertexData[offset + (j * 2) + 1]];
				} else {
					this.boneIndices[i * 3 + j] = 0;
					this.boneWeights[i * 3 + j] = 0f;
				}
			}
			
			offset += (bonesAffectingVertex * 2);
		}
	}

	public float[] getWeights() {
		return weights;
	}

	public int[] getWeightedVertexCounts() {
		return weightedVertexCounts;
	}
	
	public int[] getWeightedVertexData() {
		return weightedVertexData;
	}
	
	public Bone getArmature() {
		return armature;
	}

	public int[] getBoneIndices() {
		return boneIndices;
	}

	public float[] getBoneWeights() {
		return boneWeights;
	}
	
	public void calculateVBOData(int[] originalModelIndices, int[] indices) {
//		int arrayLength = originalModelIndices.length * 3;
		int arrayLength = 0;
		for(int index : indices) {
			arrayLength = (index > arrayLength) ? index : arrayLength;
		}
		arrayLength *= 3;
		arrayLength += 3;
		
		vboBoneIndices = new int[arrayLength];
		vboBoneWeights = new float[arrayLength];
		
		System.out.println("Array Length = " + (originalModelIndices.length * 3) + " --> " + arrayLength);
		System.out.println("Bone Indices Length = " + boneIndices.length);
				
		for(int i = 0; i < originalModelIndices.length; i++) {
			if((indices[i] * 3 + 2) >= vboBoneIndices.length) System.out.println("vboBoneIndices out of bounds.");
			if((originalModelIndices[i] * 3 + 2) >= boneIndices.length) System.out.println("boneIndices out of bounds.");
			
			vboBoneIndices[indices[i] * 3] = boneIndices[originalModelIndices[i] * 3];
			vboBoneIndices[indices[i] * 3 + 1] = boneIndices[originalModelIndices[i] * 3 + 1];
			vboBoneIndices[indices[i] * 3 + 2] = boneIndices[originalModelIndices[i] * 3 + 2];
			
			vboBoneWeights[indices[i] * 3] = boneWeights[originalModelIndices[i] * 3];
			vboBoneWeights[indices[i] * 3 + 1] = boneWeights[originalModelIndices[i] * 3 + 1];
			vboBoneWeights[indices[i] * 3 + 2] = boneWeights[originalModelIndices[i] * 3 + 2];
		}
		
	}
	
	public int[] getVboBoneIndices() {
		return vboBoneIndices;
	}

	public float[] getVboBoneWeights() {
		return vboBoneWeights;
	}
}
