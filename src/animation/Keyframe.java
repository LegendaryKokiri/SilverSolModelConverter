package animation;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Keyframe {
	//The frame at which the keyframe is perfectly realized
	private int frame;
	
	//The position and rotation of the bone at the specified frame
	private Vector3f position;
	private Quaternion quaternion;
	private Vector3f scale;
	
	public Keyframe(int frame, Matrix4f rotationKey, Vector3f positionKey, Vector3f scalingKey) {
		this.frame = frame;
		
		this.quaternion = new Quaternion(rotationKey);
		this.position = positionKey;
	}
	
	public Keyframe(int frame, Matrix4f transformation) {
		this.frame = frame;
		
		this.quaternion = new Quaternion(transformation);
		this.position = new Vector3f(transformation.m30, transformation.m31, transformation.m32);
		//TODO: Add real scaling key.
	}
	
	public Keyframe(int frame, Vector3f position, Quaternion quaternion, Vector3f scale) {
		this.frame = frame;
		
		this.position = position;
		this.quaternion = quaternion;
		this.scale = scale;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Quaternion getQuaternion() {
		return quaternion;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public static int getCurrentKeyframeIndex(List<Keyframe> keyframes, int currentFrame, int animationFrameLength) {	
				
		for(int i = 1; i < keyframes.size(); i++) {
			if(i == keyframes.size() - 1 && currentFrame > keyframes.get(i).frame) {
				return 0;
			} else if(keyframes.get(i - 1).getFrame() < currentFrame && currentFrame < keyframes.get(i).getFrame()) {
				return i;
			}
		}
		
		return 0;
	}
	
	public static float getProximityTo(Keyframe previous, Keyframe current, int currentFrame, int animationFrameLength) {
		if(currentFrame == current.frame) {
			return 1f;
		} else if(current.frame > previous.frame) {
			return 1f - ((float) (current.frame - currentFrame) / (float) (current.frame - previous.frame));
		} else {
			return 1f - ((float) (animationFrameLength - currentFrame) / (float) (animationFrameLength - previous.frame));
		}
		
		
	}
}
