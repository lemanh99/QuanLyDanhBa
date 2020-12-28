package client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.ode.nonstiff.DormandPrince853FieldIntegrator;

public class MainContacts extends JFrame implements ActionListener, KeyListener, MouseListener {
	public Socket socket;
	static int DEFAULT_PORT = 5500;
	static String DEFAULT_IP = "localhost";
	public DataInputStream dis;
	public DataOutputStream dos;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public static final int width = 600;
	public static final int height = 400;
	private int typeSearch = 0, typeSort = 0;

	private JMenuItem addItem, editItem, deleteItem, editGroupMenuItem, exitItem, exportFile, importFile, editIP,
			editPort;
	private JTextField txtSearch;
	private JComboBox<String> cbSearchType, cbSortType;
	private JLabel lbStatus;
	private JButton btnAdd, btnEdit, btnDelete, btnExit;
	private JTable tbData;

	String[] columnNames = { "Tên", "Số điện thoại", "Địa chỉ", "Email", "Nhóm" };
	private Data data = new Data();
	private ArrayList<Person> listPerson = new ArrayList<Person>();
	private ArrayList<Person> listPersonExport = new ArrayList<Person>();
	private ArrayList<Person> dataSearch = new ArrayList<Person>();
	private AddPerson addPerson;
	private EditPerson editPerson;
	private EditGroup editGroup;

	public MainContacts() {
		// TODO Auto-generated constructor stub
		GUI();
		try {
			socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Kết nối lỗi");
			lbStatus.setText("Lỗi kết nối server");
			e.printStackTrace();
		}
		try {
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.dis = new DataInputStream(socket.getInputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			read();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void GUI() {

		JMenuBar menuBar = new JMenuBar();
		JMenu editMenu = new JMenu("Menu");
		JMenu file = new JMenu("File");

		addItem = new JMenuItem("Thêm liên lạc");
		addItem.addActionListener(this);
		editItem = new JMenuItem("Chỉnh sửa liên lạc");
		editItem.addActionListener(this);
		deleteItem = new JMenuItem("Xóa liên lạc");
		deleteItem.addActionListener(this);
		exitItem = new JMenuItem("Thoát");
		exitItem.addActionListener(this);
		editGroupMenuItem = new JMenuItem("Chỉnh sửa group");
		editGroupMenuItem.addActionListener(this);
		JMenu reconnect = new JMenu("Thay đổi kết nối");
		editIP = new JMenuItem("Địa chỉ IP");
		editIP.addActionListener(this);
		editPort = new JMenuItem("Cổng Port");
		editPort.addActionListener(this);
		reconnect.add(editIP);
		reconnect.add(editPort);
		exportFile = new JMenuItem("Xuất danh bạ");
		exportFile.addActionListener(this);

		importFile = new JMenuItem("Nhập từ file");
		importFile.addActionListener(this);

		editMenu.add(addItem);
		editMenu.add(editItem);
		editMenu.add(deleteItem);
		editMenu.add(editGroupMenuItem);
		editMenu.add(reconnect);
		editMenu.add(exitItem);

		file.add(importFile);
		file.add(exportFile);

		menuBar.add(editMenu);
		menuBar.add(file);
		setJMenuBar(menuBar);

		// Tim kiem
		JPanel pnSearch = new JPanel(new BorderLayout(5, 5));
		pnSearch.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JLabel lbSearch = new JLabel("Tìm kiếm: ");
		txtSearch = new JTextField(20);
		txtSearch.addKeyListener(this);
		cbSearchType = new JComboBox<String>(columnNames);
		cbSearchType.addActionListener(this);
		pnSearch.add(lbSearch, BorderLayout.WEST);
		pnSearch.add(txtSearch, BorderLayout.CENTER);
		pnSearch.add(cbSearchType, BorderLayout.EAST);

		// Data contact
		JPanel pnSort = new JPanel(new BorderLayout());
		JPanel pnSor1 = new JPanel(new GridLayout(1, 2));
		pnSor1.setBorder(new EmptyBorder(20, 10, 2, 2));
		pnSor1.add(new JLabel("Sắp xếp theo:"));
		cbSortType = new JComboBox<String>(columnNames);
		cbSortType.addActionListener(this);
		pnSor1.add(cbSortType);
		pnSort.add(pnSor1, BorderLayout.EAST);

		JPanel pnTable = new JPanel(new BorderLayout());
		tbData = new JTable();
		tbData.setCellSelectionEnabled(false);
		tbData.setRowSelectionAllowed(true);
		tbData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbData.addMouseListener(this);
		JScrollPane scTable = new JScrollPane(tbData);
		scTable.setPreferredSize(new Dimension(width - 10, height - 130));
		pnTable.add(scTable, BorderLayout.CENTER);
		JPanel pnContacts = new JPanel(new BorderLayout());
		pnContacts.add(pnSort, BorderLayout.PAGE_START);
		pnContacts.add(pnTable, BorderLayout.CENTER);

		// Control
		JPanel pnView = new JPanel(new BorderLayout());
		pnView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		lbStatus = new JLabel();
		JPanel pnButton = new JPanel();
		btnAdd = new JButton("Thêm");
		btnAdd.addActionListener(this);
		btnEdit = new JButton("Sửa");
		btnEdit.addActionListener(this);
		btnDelete = new JButton("Xóa");
		btnDelete.addActionListener(this);
		btnExit = new JButton("Thoát");
		btnExit.addActionListener(this);
		pnButton.add(btnAdd);
		pnButton.add(btnEdit);
		pnButton.add(btnDelete);
		pnButton.add(btnExit);
		searchStatus(0, "", "");
		pnView.add(lbStatus, BorderLayout.CENTER);
		pnView.add(pnButton, BorderLayout.EAST);

		JPanel pnMain = new JPanel(new BorderLayout());
		pnMain.add(pnSearch, BorderLayout.PAGE_START);
		pnMain.add(pnContacts, BorderLayout.CENTER);
		pnMain.add(pnView, BorderLayout.PAGE_END);

		add(pnMain);
		setVisible(true);
		setTitle("Quản lý danh bạ");
		setSize(width, height);
		setLocation(700, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainContacts();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == editGroupMenuItem) {
			editGroupContacts();
		}
		if (e.getSource() == btnAdd || e.getSource() == addItem) {
			addContact();
		}
		if (e.getSource() == btnEdit || e.getSource() == editItem) {
			editContact();
		}
		if (e.getSource() == btnDelete || e.getSource() == deleteItem) {
			delete();

		}
		if (e.getSource() == cbSearchType) {
			resetSearch();
		}
		if (e.getSource() == cbSortType) {
			sort();
		}
		if (e.getSource() == btnExit || e.getSource() == exitItem) {
			int selected = JOptionPane.showOptionDialog(null, "Bạn có chắc muốn thoát", "Chú ý",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (selected == 0) {
				System.exit(0);
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
		if (e.getSource() == exportFile) {
			ReadAndWriteExcel rb = new ReadAndWriteExcel();
			boolean check = rb.SaveContacts(this.listPersonExport);
			if (check) {
				JOptionPane.showMessageDialog(null, "Xuất danh bạ thành công!");
			} else {
				JOptionPane.showMessageDialog(null, "Xuất danh bạ thất bại!");
			}
		}
		if (e.getSource() == importFile) {
			readFileExcel();
		}
		if (e.getSource() == editIP || e.getSource() == editPort) {
			String ip_address = null;
			String port = null;

			if (e.getSource() == editIP) {
				ip_address = (String) JOptionPane.showInputDialog(null, "Nhập địa chỉ IP", "Thay đổi kết nối",
						JOptionPane.QUESTION_MESSAGE, null, null, DEFAULT_IP);
			} else {
				port = (String) JOptionPane.showInputDialog(null, "Nhập cổng mới", "Thay đổi kết nối",
						JOptionPane.QUESTION_MESSAGE, null, null, DEFAULT_PORT);
			}
			if (ip_address != null || port != null) {
				try {
					if (e.getSource() == editIP)
						socket = new Socket(ip_address, DEFAULT_PORT);
					else
						socket = new Socket(DEFAULT_IP, Integer.parseInt(port));
					this.dos = new DataOutputStream(socket.getOutputStream());
					this.dis = new DataInputStream(socket.getInputStream());
					this.ois = new ObjectInputStream(socket.getInputStream());
					this.oos = new ObjectOutputStream(socket.getOutputStream());
					lbStatus.setText("");
					txtSearch.setText("");
					txtSearch.setBackground(Color.WHITE);
					txtSearch.requestFocus();
					read();
				} catch (Exception e9) {
					// TODO: handle exception
					System.out.println("Kết nối lỗi");
					lbStatus.setText("Lỗi kết nối server");
					JOptionPane.showMessageDialog(null, "Vui lòng thử lại!");
					e9.printStackTrace();
				}
				if (e.getSource() == editIP)
					DEFAULT_IP = ip_address;
				else
					DEFAULT_PORT = Integer.parseInt(port);

			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		updateTable(search(typeSearch));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	// Cập nhật bảng
	public void updateTable(ArrayList<Person> list) {
		int size = list.size();
		this.listPersonExport = list;
		String data[][] = new String[size][5];
		for (int i = 0; i < size; i++) {
			Person person = list.get(i);
			data[i][0] = person.getName();
			data[i][1] = person.getPhone();
			data[i][2] = person.getAddress();
			data[i][3] = person.getEmail();
			data[i][4] = person.getGroup();
		}

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			};
		};
		tbData.setModel(tableModel);
	}

	// Cập nhật contact
	public void updateData() {
		switch (typeSort) {
		case 0:
			Collections.sort(data.getlistPerson(), Person.PersonNameComparator);
			break;
		case 1:
			Collections.sort(data.getlistPerson(), Person.PersonPhoneComparator);
			break;
		case 2:
			Collections.sort(data.getlistPerson(), Person.PersonAddressComparator);
			break;
		case 3:
			Collections.sort(data.getlistPerson(), Person.PersonEmailComparator);
			break;
		case 4:
			Collections.sort(data.getlistPerson(), Person.personGroupComparator);
			break;
		default:
			Collections.sort(data.getlistPerson(), Person.PersonNameComparator);
			break;
		}

		if (dataSearch.size() > 0) {
			updateTable(search(typeSearch));
		} else {
			updateTable(this.data.getlistPerson());
		}
	}

	// Đọc table contact
	public void read() {
		try {
			dos.writeUTF("READ");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Lỗi không thể kết nối");
			System.exit(1);
		}
		Object object = null;
		try {
			object = ois.readObject();
		} catch (Exception e) {
			// TODO: handle exception
		}
		listPerson = (ArrayList<Person>) object;
		data.setlistPerson((ArrayList<Person>) object);
		Object objectGroup = null;
		try {
			objectGroup = ois.readObject();
		} catch (Exception e) {
			// TODO: handle exception
		}
		data.setListGroupNew((ArrayList<GroupContact>) objectGroup);
		updateTable(data.getlistPerson());
	}

	// tim vi tri chon trong bang
	private int findIndexSelectContact() {
		int index = tbData.getSelectedRow();
		if (dataSearch.size() > 0) {
			for (int i = 0; i < data.getlistPerson().size(); i++) {
				if (dataSearch.get(index) == data.getlistPerson().get(i)) {
					System.out.println("index of data: " + i);
					return i;
				}
			}
		}
		return index;
	}

	// Chỉnh sửa nhóm - thêm xóa
	private void editGroupContacts() {
		// TODO Auto-generated method stub
		if (editGroup == null) {
			editGroup = new EditGroup(this, dos, dis, ois, oos);
		}
		editGroup.display(true);
		txtSearch.requestFocus();

	}

	// Thêm danh bạ
	private void addContact() {
		// TODO Auto-generated method stub
		if (addPerson == null) {
			addPerson = new AddPerson(this, dis, dos, ois, oos);
		}
		addPerson.display(true);
		read();
		txtSearch.requestFocus();

	}

	// Chỉnh sửa danh bạ
	public void editContact() {
		int index = findIndexSelectContact();
		if (index >= 0) {
			if (editPerson == null) {
				editPerson = new EditPerson(this, dos, dis, ois, oos);
			}
			editPerson.setIndexSelected(index);
			editPerson.display(true);
		} else {
			JOptionPane.showMessageDialog(null, "Chọn một người rồi sửa!");
		}
		txtSearch.requestFocus();
	}

	private void detailPerson() {

		int index = findIndexSelectContact();
		if (index >= 0) {
			DetailPerson editPerson = new DetailPerson(this, dos, dis, ois, oos);
			editPerson.setIndexSelected(index);
			editPerson.display(true);
		} else {
			JOptionPane.showMessageDialog(null, "Chọn một người rồi sửa!");
		}
		txtSearch.requestFocus();
	}

	// Xoa
	private void delete() {
		int index = findIndexSelectContact();
		if (index >= 0) {
			int selected = JOptionPane.showOptionDialog(null,
					"Bạn thực sự muốn xóa " + data.getlistPerson().get(index).getName() + " ra khỏi danh bạ?", "Xóa",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (selected == 0) {
				try {
					dos.writeUTF("DELETE");
					dos.writeInt(data.getlistPerson().get(index).getId());
				} catch (Exception e) {
					// TODO: handle exception
				}
				data.getlistPerson().remove(index);
				updateData();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Chọn một người muốn xóa");
		}
		txtSearch.requestFocus();
	}

	// Tìm kiếm
	private ArrayList<Person> search(int typeSearch) {
		int size = data.getlistPerson().size();
		dataSearch.clear();
		String textFind = txtSearch.getText().trim().toLowerCase(); // Tên tìm kiếm
		if (textFind.length() == 0) {
			searchStatus(0, "", "");
			return this.data.getlistPerson();
		}
		String textType = "";
		for (int i = 0; i < size; i++) {
			Person p = data.getlistPerson().get(i);
			String text = "";
			if (typeSearch == 0) {
				text = p.getName();
				textType = " người có tên phù hợp với \"";
			} else if (typeSearch == 1) {
				text = p.getPhone();
				textType = " người có số điện thoại phù hợp với \"";
			} else if (typeSearch == 2) {
				text = p.getAddress();
				textType = " người có địa chỉ phù hợp với \"";
			} else if (typeSearch == 3) {

				text = p.getEmail();
				textType = " người có email phù hợp với \"";
			} else if (typeSearch == 4) {
				text = p.getGroup();
				textType = " người có nhóm phù hợp với \"";
			}
			text = text.trim().toLowerCase();
			// Tìm chuỗi giống nhau
			if (text.indexOf(textFind) >= 0) {
				dataSearch.add(p);
			}
		}
		searchStatus(dataSearch.size(), textType + textFind + "\".", textFind);
		return dataSearch;
	}

	private void searchStatus(int count, String status, String textFind) {
		txtSearch.setBackground(Color.WHITE);
		if (textFind.length() == 0) {
			lbStatus.setText("Nhập vào thông tin tìm kiếm !");
		} else if (count > 0 && textFind.length() > 0) {
			lbStatus.setText("Tìm thấy " + count + status);
		} else if (count == 0 && textFind.length() > 0) {
			txtSearch.setBackground(Color.RED);
			lbStatus.setText("Không tìm thấy" + status);
		}
	}

	private void resetSearch() {
		typeSearch = cbSearchType.getSelectedIndex();
//		System.out.println(typeSearch);
		txtSearch.setText("");
		txtSearch.requestFocus();
		searchStatus(0, "", "");
		updateData();
	}

	private void sort() {
		typeSort = cbSortType.getSelectedIndex();
		updateData();
	}

	private void readFileExcel() {
		ArrayList<String> listST = new ArrayList<String>();
		for (Person ps : this.listPerson) {
			listST.add(ps.getName().toLowerCase() + ps.getPhone());
		}
		ReadAndWriteExcel rw = new ReadAndWriteExcel();
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel file", "xls", "xlsx");
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int resultat = fc.showDialog(null, "Thêm danh sách từ file");
		if (resultat == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			for (File file : files) {
				try {
					ArrayList<Person> persons = rw.ReadFile(file);
					for (Person person : persons) {
						if (listST.contains(person.getName().toLowerCase() + person.getPhone()) == false) {
							int index = indexGroupName(person.getGroup());
							if (index == -1) {
								dos.writeUTF("ADD_GROUP");
								dos.writeUTF(person.getGroup());
								ArrayList<GroupContact> listGroupNew = null;
								try {
									listGroupNew = (ArrayList<GroupContact>) ois.readObject();
								} catch (ClassNotFoundException e3) {
									// TODO: handle exception
									e3.printStackTrace();

								}
								this.getData().setListGroupNew(listGroupNew);
							}
							;
							person.setClassify(this.getData().getListGroupNew().get(indexGroupName(person.getGroup())));
							try {
								dos.writeUTF("ADD");
								oos.writeObject(person);
							} catch (Exception e4) {
								e4.getStackTrace();
							}
						}
					}
				} catch (Exception e2) {
					e2.getStackTrace();
				}
			}
			read();
			JOptionPane.showMessageDialog(null, "Thêm danh bạ từ file thành công !!");
		}
	}

	public int indexGroupName(String groupName) {
		for (int i = 0; i < this.getData().getListGroupNew().size(); i++) {
			if (this.getData().getListGroupNew().get(i).getName().equals(groupName)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getClickCount() == 2) {
			detailPerson();
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
