package client;

import java.io.Serializable;

public class GroupContact implements Serializable{
	private int id;
	private String name;
	
	public GroupContact() {
		super();
	}
	
	public GroupContact(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String groupName) {
		this.name = name;
	}

}
