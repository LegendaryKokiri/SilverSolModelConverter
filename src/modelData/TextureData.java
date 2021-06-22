package modelData;

public class TextureData {

    private String[] texturePaths;
    private int atlas;

    public TextureData() {
    	this.texturePaths = new String[0];
    	this.atlas = 1;
    }
    
	public String[] getTexturePaths() {
    	return texturePaths;
    }
    
    public void setTexturePaths(String[] paths) {
    	this.texturePaths = paths;
    }

	public int getAtlas() {
		return atlas;
	}

	public void setAtlas(int atlas) {
		this.atlas = atlas;
	}
    
}
