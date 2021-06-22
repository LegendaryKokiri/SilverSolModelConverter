package toolbox;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

//"M" stands for "Mathematics."
public class M {
	
	public static int min(int number1, int number2) {
		return (number1 < number2) ? number1 : number2;
	}
	
	public static int max(int number1, int number2) {
		return (number1 > number2) ? number1 : number2;
	}
	
	public static Vector3f multiplyVector(Vector3f v, float multiplicand) {
		return new Vector3f(v.x * multiplicand, v.y * multiplicand, v.z * multiplicand);
	}
	
	public static Vector3f multiplyVectorData(Vector3f v1, Vector3f v2) {
		return new Vector3f(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
	}
	
	public static Vector3f divideVector(Vector3f v, float divisor) {
		return new Vector3f(v.x / divisor, v.y / divisor, v.z / divisor);
	}
	
	public static Vector3f divideVectorData(Vector3f v1, Vector3f v2) {
		return new Vector3f(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
	}
	
	public static Matrix4f createMatrix4f(float[] data) {
		Matrix4f matrix = new Matrix4f();
		
		if(data.length != 16) return matrix;
		
		matrix.m00 = data[0]; matrix.m01 = data[1]; matrix.m02 = data[2]; matrix.m03 = data[3];
		matrix.m10 = data[4]; matrix.m11 = data[5]; matrix.m12 = data[6]; matrix.m13 = data[7];
		matrix.m20 = data[8]; matrix.m21 = data[9]; matrix.m22 = data[10]; matrix.m23 = data[11];
		matrix.m30 = data[12]; matrix.m31 = data[13]; matrix.m32 = data[14]; matrix.m33 = data[15];
		
		return matrix;
	}
	
	public static Matrix3f createRotationMatrix(Vector3f rotation) {		
		Matrix3f xMatrix = new Matrix3f();
		Matrix3f yMatrix = new Matrix3f();
		Matrix3f zMatrix = new Matrix3f();
		Matrix3f composite = new Matrix3f();
		
		double rotX = Math.toRadians(rotation.x);
		double rotY = Math.toRadians(rotation.y);
		double rotZ = Math.toRadians(rotation.z);
		
		xMatrix.m00 = 1; xMatrix.m01 = 0; xMatrix.m02 = 0;
		xMatrix.m10 = 0; xMatrix.m11 = (float) Math.cos(rotX); xMatrix.m12 = (float) Math.sin(rotX);
		xMatrix.m20 = 0; xMatrix.m21 = (float) -Math.sin(rotX); xMatrix.m22 = (float) Math.cos(rotX);

		yMatrix.m00 = (float) Math.cos(rotY); yMatrix.m01 = 0; yMatrix.m02 = (float) -Math.sin(rotY);
		yMatrix.m10 = 0; yMatrix.m11 = 1; yMatrix.m12 = 0;
		yMatrix.m20 = (float) Math.sin(rotY); yMatrix.m21 = 0; yMatrix.m22 = (float) Math.cos(rotY);
		
		zMatrix.m00 = (float) Math.cos(rotZ); zMatrix.m01 = (float) Math.sin(rotZ); zMatrix.m02 = 0;
		zMatrix.m10 = (float) -Math.sin(rotZ); zMatrix.m11 = (float) Math.cos(rotZ); zMatrix.m12 = 0;
		zMatrix.m20 = 0; zMatrix.m21 = 0; zMatrix.m22 = 1;
		
		Matrix3f.mul(xMatrix, yMatrix, composite);
		Matrix3f.mul(composite, zMatrix, composite);
		
		return composite;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
		
		Matrix4f.scale(scale, matrix, matrix);
		return matrix;
	}
}
