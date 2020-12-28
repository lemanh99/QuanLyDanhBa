package client;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
	private ArrayList<Person> listPerson = new ArrayList<Person>();
	private ArrayList<String> listGroup;
	private ArrayList<GroupContact> listGroupNew;

	public Data() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Person> getlistPerson() {
		return listPerson;
	}

	public void setlistPerson(ArrayList<Person> listPerson) {
		this.listPerson = listPerson;
	}

	public ArrayList<String> getListGroup() {
		listGroup = new ArrayList<String>();
		for (int i = 0; i < listPerson.size(); i++) {
			boolean check = true;
			for (int j = i - 1; j >= 0; j--) {
				if (listPerson.get(i).getGroup().equals(listPerson.get(j).getGroup())) {
					check = false;
					break;
				}
			}
			if (check) {
				listGroup.add(listPerson.get(i).getGroup());
			}
		}
		return listGroup;
	}

	public void setListGroup(ArrayList<String> listGroup) {
		this.listGroup = listGroup;
	}

	public ArrayList<GroupContact> getListGroupNew() {
		return listGroupNew;
	}

	public void setListGroupNew(ArrayList<GroupContact> listGroupNew) {
		this.listGroupNew = listGroupNew;
	}
	
	public ArrayList<String> getListGroupName() {
		listGroup = new ArrayList<String>();
		for (int i = 0; i < listGroupNew.size(); i++) {
			listGroup.add(listGroupNew.get(i).getName());
		}
		return listGroup;
	}
}
