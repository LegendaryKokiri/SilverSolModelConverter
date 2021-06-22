package animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Quaternion {	
	private float w, x, y, z;
	private Matrix4f rotationMatrix;
	
	public Quaternion(float w, float x, float y, float z) {
		this.w = w; this.x = x; this.y = y; this.z = z;
		this.rotationMatrix = new Matrix4f();
		
		normalize();
	}
	
	public Quaternion(Vector3f axis, float angle) {
		this.w = (float) Math.cos(Math.toRadians(angle / 2));
		this.x = axis.x * (float) Math.sin(Math.toRadians(angle / 2));
		this.y = axis.y * (float) Math.sin(Math.toRadians(angle / 2));
		this.z = axis.z * (float) Math.sin(Math.toRadians(angle / 2));
		this.rotationMatrix = new Matrix4f();
		
		normalize();
	}
	
	public Quaternion(Matrix4f matrix) {		
		float diagonal = matrix.m00 + matrix.m11 + matrix.m22;
		if(diagonal > 0) {
			float wTimesFour = (float) ((Math.sqrt(diagonal) + 1f) * 2f);
			this.w = wTimesFour * 0.25f;
			this.x = (matrix.m21 - matrix.m12) / wTimesFour;
			this.y = (matrix.m02 - matrix.m20) / wTimesFour;
			this.z = (matrix.m10 - matrix.m01) / wTimesFour;
		} else if(matrix.m00 > matrix.m11 && matrix.m00 > matrix.m22) {
			float xTimesFour = (float) (Math.sqrt(matrix.m00 - matrix.m11 - matrix.m22 + 1f) * 2f);
			this.w = (matrix.m21 - matrix.m12) / xTimesFour;
			this.x = xTimesFour * 0.25f;
			this.y = (matrix.m01 + matrix.m10) / xTimesFour;
			this.z = (matrix.m02 + matrix.m20) / xTimesFour;
		} else if(matrix.m11 > matrix.m22) {
			float yTimesFour = (float) (Math.sqrt(matrix.m11 - matrix.m00 - matrix.m22 + 1f) * 2f);
			this.w = (matrix.m02 - matrix.m20) / yTimesFour;
			this.x = (matrix.m01 + matrix.m10) / yTimesFour;
			this.y = yTimesFour * 0.25f;
			this.z = (matrix.m12 + matrix.m21) / yTimesFour;
		} else {
			float zTimesFour = (float) (Math.sqrt(matrix.m22 - matrix.m00 - matrix.m11 + 1f) * 2f);
			this.w = (matrix.m10 - matrix.m01) / zTimesFour;
			this.x = (matrix.m02 + matrix.m20) / zTimesFour;
			this.y = (matrix.m12 + matrix.m21) / zTimesFour;
			this.z = zTimesFour * 0.25f;
		}
		
		this.rotationMatrix = new Matrix4f();
		
		normalize();
	}
	
	public void normalize() {
		float magnitude = (float) Math.sqrt(w * w + x * x + y * y + z * z);
		w /= magnitude; x /= magnitude; y /= magnitude; z /= magnitude;
		
		calculateRotationMatrix();
	}
	
	private void calculateRotationMatrix() {
		float xy = x * y;
		float xz = x * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;
		float xSquared = x * x;
		float ySquared = y * y;
		float zSquared = z * z;
		
		rotationMatrix.m00 = 1 - 2 * (ySquared + zSquared);
		rotationMatrix.m01 = 2 * (xy - wz);
		rotationMatrix.m02 = 2 * (xz + wy);
		rotationMatrix.m03 = 0;
		rotationMatrix.m10 = 2 * (xy + wz);
		rotationMatrix.m11 = 1 - 2 * (xSquared + zSquared);
		rotationMatrix.m12 = 2 * (yz - wx);
		rotationMatrix.m13 = 0;
		rotationMatrix.m20 = 2 * (xz - wy);
		rotationMatrix.m21 = 2 * (yz + wx);
		rotationMatrix.m22 = 1 - 2 * (xSquared + ySquared);
		rotationMatrix.m23 = 0;
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		rotationMatrix.m33 = 1;
	}
	
	public Matrix4f getRotationMatrix() {
		return rotationMatrix;
	}
	
	public static Quaternion interpolate(Quaternion left, Quaternion right, float blendFactor) {
		float w = 1, x = 0, y = 0, z = 0;
		float dotProduct = left.w * right.w + left.x * right.x + left.y * right.y + left.z * right.z;
		float blendAlternate = 1f - blendFactor;
		if(dotProduct < 0) {
			w = blendAlternate * left.w + blendFactor * -right.w;
			x = blendAlternate * left.x + blendFactor * -right.x;
			y = blendAlternate * left.y + blendFactor * -right.y;
			z = blendAlternate * left.z + blendFactor * -right.z;
		} else {
			w = blendAlternate * left.w + blendFactor * right.w;
			x = blendAlternate * left.x + blendFactor * right.x;
			y = blendAlternate * left.y + blendFactor * right.y;
			z = blendAlternate * left.z + blendFactor * right.z;
		}
		
		Quaternion quaternion = new Quaternion(w, x, y, z);
		return quaternion;
	}
	
	public void printQuaternion() {
		System.out.println("---Quaternion.printQuaternion()---");
		System.out.println("(W, X, Y, Z): (" + w + ", " + x + ", " + y + ", " + z + ")");
	}
}
