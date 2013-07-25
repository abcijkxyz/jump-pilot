/*
 *  License: GPL. See LICENSE file for details.
 *  Adapted from JOSM by Stefan Steiniger for use in OpenJUMP [25.July.2013] 
 */
package org.openjump.core.openstreetmap.model;

public class OjOsmRelationMember {
	
	private final String role;
    private final long memberId;
    private final int osmPrimitiveType;
    private boolean idNotFoundInDataset = true;
    
    public OjOsmRelationMember(String role, int type, long id) {
        this.role = role == null?"":role;
        this.osmPrimitiveType = type;
        this.memberId = id;
    }
    
	public String getRole() {
		return role;
	}
	public long getMemberId() {
		return memberId;
	}
	public int getOsmPrimitiveType() {
		return osmPrimitiveType;
	}
    
	/**
	 * TODO: check if this works for roles with empty strings
	 * Does the relation member have a role, such as "inner" or "outer" polygon ring? 
	 * @return
	 */
	public boolean hasRole(){
		if(role != null){
			if(role.equalsIgnoreCase("")){
				System.out.println("OjOsmRelationMember: this role contains an empty - null-length string");
			}
		}
		if((role != null) && (role.length() > 1)){
			return true;
		}
		else{
			return false;
		}
	}
	
    public static int getOsmPrimitiveTypeFromParsedString(String parsedTypeValue){
    	int type = -1;
    	if(parsedTypeValue.equalsIgnoreCase("node")){
    		type = OjOsmPrimitive.OSM_PRIMITIVE_NODE;
    	}
    	else if(parsedTypeValue.equalsIgnoreCase("way")){
    		type =  OjOsmPrimitive.OSM_PRIMITIVE_WAY;
    	}
    	else if(parsedTypeValue.equalsIgnoreCase("relation")){
    		type = OjOsmPrimitive.OSM_PRIMITIVE_RELATION;
    	}
    	return type;
    }
    
    public static boolean isInnerWay(String role){
    	boolean isInner = false;
    	if(role.equalsIgnoreCase("inner")){
    		isInner = true;
    	}
    	return isInner;
    }
    
    public static boolean isOuterWay(String role){
    	boolean isInner = false;
    	if(role.equalsIgnoreCase("outer")){
    		isInner = true;
    	}
    	return isInner;
    }

	public boolean isIdNotFoundInDataset() {
		return idNotFoundInDataset;
	}

	public void setIdNotFoundInDataset(boolean idNotFoundInDataset) {
		this.idNotFoundInDataset = idNotFoundInDataset;
	}
    
    
}
