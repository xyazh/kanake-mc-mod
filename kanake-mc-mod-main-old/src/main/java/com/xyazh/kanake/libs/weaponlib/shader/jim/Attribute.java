package com.xyazh.kanake.libs.weaponlib.shader.jim;

public class Attribute {
	private final int attribID;
	private final String attribName;
	
	public Attribute(String name, int id) {
		this.attribName = name;
		this.attribID = id;
	}
	
	public int getAttributeID() {
		return this.attribID;
	}
	
	public String getAttributeName() {
		return this.attribName;
	}

}
