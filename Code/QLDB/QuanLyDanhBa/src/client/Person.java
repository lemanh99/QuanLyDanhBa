package client;

import java.io.Serializable;
import java.util.Comparator;

public class Person implements Serializable {
	private int id;
	private String name, address, phone, email, group;
	private GroupContact classify;
	private int idClassify;

	public Person() {

	}
	public Person(String name, String phone,String address, String email, String group) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.group = group;
	}
	
	public Person(int id, String name, String phone, String address, String email, String group) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.group = group;
	}
	
	public Person(String name, String phone, String address, String email, GroupContact classify) {
		super();
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.email = email;
		this.classify = classify;
	}
	public Person(int id, String name, String phone, String address, String email, GroupContact classify) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.email = email;
		this.classify = classify;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public GroupContact getClassify() {
		return classify;
	}

	public void setClassify(GroupContact classify) {
		this.classify = classify;
	}

	public int getIdClassify() {
		return idClassify;
	}

	public void setIdClassify(int idClassify) {
		this.idClassify = idClassify;
	}

	private String getLLastName() {
		String st = getName();
		int i = st.length() - 1;
		while (i > 0) {
			if (st.charAt(i) == ' ') {
				return st.substring(i + 1);
			}
			i--;
		}
		return st;
	}
	
	public static Comparator<Person> PersonNameComparator = new Comparator<Person>() {
		public int compare(Person p1, Person p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getName()).compareTo(sort.generator(p2.getName()));
		}
	};
	public static Comparator<Person> PersonPhoneComparator = new Comparator<Person>() {
		public int compare(Person p1, Person p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getPhone()).compareTo(sort.generator(p2.getPhone()));
		}
	};
	public static Comparator<Person> PersonAddressComparator = new Comparator<Person>() {
		public int compare(Person p1, Person p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getAddress()).compareTo(sort.generator(p2.getAddress()));
		}
	};
	public static Comparator<Person> PersonEmailComparator = new Comparator<Person>() {
		public int compare(Person p1, Person p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getEmail()).compareTo(sort.generator(p2.getEmail()));
		}
	};
	public static Comparator<Person> personGroupComparator = new Comparator<Person>() {
		public int compare(Person p1, Person p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getGroup()).compareTo(sort.generator(p2.getGroup()));
		}
	};
	public static Comparator<String> GroupComparator = new Comparator<String>() {
		public int compare(String p1, String p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1).compareTo(sort.generator(p2));
		}
	};
	public static Comparator<GroupContact> GroupGGComparator = new Comparator<GroupContact>() {
		public int compare(GroupContact p1, GroupContact p2) {
			sortCodeVN sort = new sortCodeVN();
			return sort.generator(p1.getName()).compareTo(sort.generator(p2.getName()));
			//return s.getName().compareTo(p2.getName());
		}
	};
	public String toString() {
		return id + ", " + name + ", " + phone + ", " + idClassify + ", " + address + ", " + email;
	}
}