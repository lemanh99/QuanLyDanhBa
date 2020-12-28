package server;

import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import client.GroupContact;
import client.Person;

public class Server {
	final static int DEFAULT_PORT = 5500;
	Socket socket;

	public Server() {
		try {
			ServerSocket server = new ServerSocket(DEFAULT_PORT);
			System.out.println("Server started at " + InetAddress.getLocalHost().getHostAddress() + ":" + DEFAULT_PORT);

//			System.out.println("Listening...");
			socket = server.accept();
			System.out.println("Có 1 kết nối đến");
//			System.out.println("Connected to " + s.getInetAddress().getHostAddress());
			new Process(this, socket).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}

class Process extends Thread {
	Server server;
	Socket socket;
	ResultSet rs = null;
	ResultSet rs_group = null;
	PreparedStatement preSt = null;
	DataInputStream dis;
	DataOutputStream dos;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	public Process(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			ArrayList<Person> listPerson = new ArrayList<Person>();
			ArrayList<GroupContact> listGroupContact = new ArrayList<GroupContact>();
			Connection conn = null;
			Statement st = null;
			// Connection database
			try {
				String dbURL = "jdbc:mysql://localhost:3306/contacts?useUnicode=true&characterEncoding=UTF-8";
				String username = "root";
				String password = "root";
				conn = (Connection) DriverManager.getConnection(dbURL, username, password);
				if (conn != null) {
					System.out.println("Kết nối database thành công");
					conn.setCharacterEncoding("utf-8");
				}
				st = (Statement) conn.createStatement();

			} catch (Exception e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					String s = dis.readUTF();
					if (s.equals("READ")) {
						listPerson = null;
						listPerson = new ArrayList<Person>();
						String sql_contact = "select * from person left join groupcontacts on person.groupid = groupcontacts.groupid order by fullname";
						String sql_group = "select * from groupcontacts order by namegroup";
						System.out.println("Đọc dữ liệu thành công");
						rs = st.executeQuery(sql_contact);
						while (rs.next()) {
							listPerson.add(new Person(rs.getInt("PersonID"), rs.getString("FullName"),
									rs.getString("PhoneNumber"), rs.getString("Address"), rs.getString("Email"),
									rs.getString("NameGroup")));
						}
						System.out.println("So luong" + listPerson.size());
						oos.writeObject(listPerson);
						rs_group = st.executeQuery(sql_group);
						while (rs_group.next()) {
							listGroupContact
									.add(new GroupContact(rs_group.getInt("GroupID"), rs_group.getString("NameGroup")));
						}
						oos.writeObject(listGroupContact);
					} else if (s.equals("ADD")) {
						Object object = ois.readObject();
						System.out.println("add person: " + object);
						Person person = (Person) object;
						String insert = "INSERT INTO person ( FullName, PhoneNumber, Address, Email, GroupID) VALUES (?,?,?,?,?)";
						conn.setCharacterEncoding("utf-8");
						preSt = (PreparedStatement) conn.prepareStatement(insert);
						preSt.setString(1, person.getName());
						preSt.setString(2, person.getPhone());
						preSt.setString(3, person.getAddress());
						preSt.setString(4, person.getEmail());
						preSt.setInt(5, person.getClassify().getId());
						preSt.executeUpdate();

					} else if (s.equals("ADD_GROUP")) {
						String insert = "INSERT INTO groupcontacts (NameGroup) VALUES (?)";
						preSt = (PreparedStatement) conn.prepareStatement(insert);
						preSt.setString(1, dis.readUTF());
						int check = preSt.executeUpdate();
						if (check > 0) {
							listGroupContact = new ArrayList<>();
							String sql_group = "select * from groupcontacts order by NameGroup";
							rs_group = st.executeQuery(sql_group);
							while (rs_group.next()) {
								listGroupContact.add(
										new GroupContact(rs_group.getInt("GroupID"), rs_group.getString("NameGroup")));
							}
							oos.writeObject(listGroupContact);
						}
					} else if (s.equals("DELETE_GROUP")) {
						String name_group = dis.readUTF();
						System.out.println(name_group);

						String delete_person = "delete person from person left join groupcontacts on person.groupid = groupcontacts.groupid where groupcontacts.NameGroup = ?";
						preSt = (PreparedStatement) conn.prepareStatement(delete_person);
						preSt.setString(1, name_group);
						preSt.executeUpdate();

						String delete_group = "DELETE FROM groupcontacts WHERE NameGroup = ?";
						preSt = (PreparedStatement) conn.prepareStatement(delete_group);
						preSt.setString(1, name_group);
						preSt.executeUpdate();

						listPerson = new ArrayList<Person>();
						listGroupContact = new ArrayList<GroupContact>();
						String sql_contact = "select * from person left join groupcontacts on person.groupid = groupcontacts.groupid order by fullname";
						String sql_group = "select * from groupcontacts order by namegroup";
						System.out.println("Đọc dữ liệu thành công");
						rs = st.executeQuery(sql_contact);
						while (rs.next()) {
							listPerson.add(new Person(rs.getInt("PersonID"), rs.getString("FullName"),
									rs.getString("PhoneNumber"), rs.getString("Address"), rs.getString("Email"),
									rs.getString("NameGroup")));
						}
						oos.writeObject(listPerson);
						rs_group = st.executeQuery(sql_group);
						while (rs_group.next()) {
							listGroupContact
									.add(new GroupContact(rs_group.getInt("GroupID"), rs_group.getString("NameGroup")));
						}
						oos.writeObject(listGroupContact);

					} else if (s.equals("RENAME_GROUP")) {
						String name_group_new = dis.readUTF();
						System.out.println(name_group_new);
						String name_group_old = dis.readUTF();
						System.out.println(name_group_old);

						String rename_group = "update groupcontacts set NameGroup = ? where NameGroup = ?";
						preSt = (PreparedStatement) conn.prepareStatement(rename_group);
						preSt.setString(1, name_group_new);
						preSt.setString(2, name_group_old);
						preSt.executeUpdate();

						listPerson = new ArrayList<Person>();
						listGroupContact = new ArrayList<GroupContact>();
						String sql_contact = "select * from person left join groupcontacts on person.groupid = groupcontacts.groupid order by fullname";
						String sql_group = "select * from groupcontacts order by namegroup";
						System.out.println("Đọc dữ liệu thành công");
						rs = st.executeQuery(sql_contact);
						while (rs.next()) {
							listPerson.add(new Person(rs.getInt("PersonID"), rs.getString("FullName"),
									rs.getString("PhoneNumber"), rs.getString("Address"), rs.getString("Email"),
									rs.getString("NameGroup")));
						}
						oos.writeObject(listPerson);
						rs_group = st.executeQuery(sql_group);
						while (rs_group.next()) {
							listGroupContact
									.add(new GroupContact(rs_group.getInt("GroupID"), rs_group.getString("NameGroup")));
						}
						oos.writeObject(listGroupContact);

					} else if (s.equals("EDIT")) {
						Object object = ois.readObject();
						Person person = (Person) object;
						String insert = "UPDATE person SET FullName = ?, PhoneNumber = ?, Address = ?, Email = , GroupID = ?? WHERE PersonID = ?";
						conn.setCharacterEncoding("utf-8");
						preSt = (PreparedStatement) conn.prepareStatement(insert);
						preSt.setString(1, person.getName());
						preSt.setString(2, person.getPhone());
						preSt.setString(3, person.getAddress());
						preSt.setString(4, person.getEmail());
						preSt.setInt(5, person.getClassify().getId());
						preSt.setInt(6, person.getId());
						preSt.executeUpdate();

					} else if (s.equals("DELETE")) {
						int id = dis.readInt();
						System.out.println("Đã xóa" + id);
						String delete = "DELETE FROM person WHERE PersonID =" + id;
						preSt = (PreparedStatement) conn.prepareStatement(delete);
						preSt.executeUpdate();
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}