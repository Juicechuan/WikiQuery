package edu.wiki.query;
import java.util.ArrayList;


public class IdAndOffset {
	int id;
	String offset;
	public IdAndOffset(String s){
		String[] string = s.split("=");
		this.id = Integer.parseInt(string[0].trim());
		this.offset = string[1].trim();
		
	}
	
	public ArrayList<Integer> charToArray(String s){
		String[] string = s.split(",");
		ArrayList<Integer> offset = new ArrayList<Integer>(); 
		for(int i = 0; i < string.length; i++){
			string[i] = string[i].trim();
			offset.add(i, Integer.parseInt(string[i]));
		}
		return offset;
	}
	
	public String getOffset(){
		return this.offset;
	}
	
	public int getId(){
		return this.id;
	}
	
	
	
	public String toTakeShellOff(String s){
		return s.substring(1, s.length()-1);
	}
}
