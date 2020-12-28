package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AddPerson extends JDialog implements ActionListener, KeyListener {
	private MainContacts mainContacts;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
//	private Information infor;
	
	private ArrayList<String> listGroup;
	private ArrayList<GroupContact> listGroupNew;
	private JTextField txtName, txtPhone, txtAddress, txtEmail;
	private JComboBox<String> cbGroup;
	private JButton btnGroupNew, btnAdd, btnRetype, btnCancel;
	
	
	public AddPerson(MainContacts mainContacts, DataInputStream dis,DataOutputStream dos, ObjectInputStream ois, ObjectOutputStream oos) {
		// TODO Auto-generated constructor stub
		this.mainContacts = mainContacts;
		this.dis = dis;
		this.dos = dos;
		this.oos = oos;
		this.ois = ois;
		this.listGroup = this.mainContacts.getData().getListGroup();
		GUI();
		pack();
		
	}
	public void GUI() {
		JPanel pnInfor = new JPanel(new BorderLayout());
		// Add label
		JPanel pn1 = new JPanel(new GridLayout(5, 1, 5, 5));
		pn1.setBorder(new EmptyBorder(10, 10, 10, 10));
		pn1.add(new JLabel("Tên"));
		pn1.add(new JLabel("Số điện thoại"));
		pn1.add(new JLabel("Địa chỉ"));
		pn1.add(new JLabel("Email"));
		pn1.add(new JLabel("Nhóm"));

		// Add textfield
		JPanel pn2 = new JPanel(new GridLayout(5, 1, 5, 5));
		pn2.setBorder(new EmptyBorder(10, 10, 10, 10));
		txtName = new JTextField(20);
		txtName.addKeyListener(this);
		txtPhone = new JTextField(20);
		txtPhone.addKeyListener(this);
		txtAddress = new JTextField(20);
		txtAddress.addKeyListener(this);
		txtEmail = new JTextField(20);
		txtEmail.addKeyListener(this);
		String[] list = new String[listGroup.size()];
		list = listGroup.toArray(list);
		cbGroup = new JComboBox<String>(list);
		btnGroupNew = new JButton("Nhóm mới");
		btnGroupNew.addActionListener(this);
		btnGroupNew.setMargin(new Insets(5, 2, 5, 2));
		JPanel pnGroup = new JPanel(new BorderLayout(5, 5));
		pnGroup.add(cbGroup, BorderLayout.CENTER);
		pnGroup.add(btnGroupNew, BorderLayout.EAST);
		pn2.add(txtName);
		pn2.add(txtPhone);
		pn2.add(txtAddress);
		pn2.add(txtEmail);
		pn2.add(pnGroup);
		
		pnInfor.add(pn1, BorderLayout.WEST);
		pnInfor.add(pn2, BorderLayout.CENTER);
		
		
//		infor = new Information(this.mainContacts.getData().getListGroup(), dos, ois, this.mainContacts);
		
		
		JPanel pnControl = new JPanel(new GridLayout(1, 3, 10, 10));
		pnControl.setBorder(new EmptyBorder(10, 10, 10, 10));
		btnAdd = new JButton("Thêm");
		btnAdd.addActionListener(this);
		btnRetype = new JButton("Nhập lại");
		btnRetype.addActionListener(this);
		btnCancel = new JButton("Hủy");
		btnCancel.addActionListener(this);
		pnControl.add(btnAdd);
		pnControl.add(btnRetype);
		pnControl.add(btnCancel);
		
		JPanel pn = new JPanel(new BorderLayout());
		pn.add(pnInfor, BorderLayout.CENTER);
		pn.add(pnControl, BorderLayout.PAGE_END);
		add(pn);
		setTitle("Thêm liên hệ");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==btnAdd) {
			add();
		}
		if(e.getSource()==btnRetype) {
			retypeData();
		}
		if(e.getSource()==btnCancel) {
			cancel();
		}
		if (e.getSource() == btnGroupNew) {
			String groupName = JOptionPane.showInputDialog("Nhập nhóm mới");
			
			if (groupName != null && groupName.trim().length() > 0) {
				try {
					boolean check = false;
					for (int i = 0; i < listGroup.size(); i++) {
						if (listGroup.get(i).equals(groupName)) {
							check = true;
							System.out.println("Nhóm đã tồn tại");
							break;
						}
						if (!check) {
							addGroup(groupName);
						}
					}
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {

					e2.printStackTrace();
				}
			}
		}
	}
	public Person getInfor() {
		if (checkTextField(txtName) && checkTextField(txtPhone) && checkTextField(txtAddress)
				&& checkTextField(txtEmail) && checkComboBox(cbGroup)) {
			Person p = new Person(txtName.getText().trim().toString(), txtPhone.getText().trim().toString(),
					txtAddress.getText().trim().toString(), txtEmail.getText().trim().toString(),
					listGroupNew.get(indexGroupName(cbGroup.getSelectedItem().toString())));
			return p;
		} else {
			return null;
		}
	}


	// Kiểm tra thông tin nhập của textfield
	private boolean checkTextField(JTextField txt) {
		if (txt.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Chưa nhập dữ liệu", "Nhập dữ liệu", JOptionPane.OK_OPTION);
			txt.requestFocus();
			return false;
		}
		// Kiểm tra ô nhập số điện thoại
		if (txt.equals(txtPhone)) {
			try {
				Double.parseDouble(txt.getText());
				if (txt.getText().length() < 10) {
					JOptionPane.showMessageDialog(null, "Điện thoại phải có lớn hơn 10 chữ số", "Nhập số điện thoại",
							JOptionPane.OK_OPTION);
					txt.requestFocus();
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(null, "Điện thoại phải là số", "Nhập số điện thoại",
						JOptionPane.OK_OPTION);
				txt.requestFocus();
				return false;
			}
		}
		// Kiểm tra ô nhập emali
		if (txt.equals(txtEmail)) {
			// regular expressions
			String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
			Pattern regex = Pattern.compile(emailPattern);
			Matcher matcher = regex.matcher(txt.getText());
			if (matcher.find()) {
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Gmail sai định dạng", "Nhập gmail", JOptionPane.OK_OPTION);
				txt.requestFocus();
				return false;
			}
		}
		return true;
	}

	private boolean checkComboBox(JComboBox<String> cb) {
		if (listGroup.size() == 0) {
			cb.requestFocus();
			return false;
		}
		return true;
	}
	
	public int indexGroupName(String groupName) {
		for (int i = 0; i < listGroup.size(); i++) {
			if (listGroup.get(i).equals(groupName)) {
				return i;
			}
		}
		return -1;
	}
	
	//Gui len server va cap nhat table
	private void add() {
		Person p = getInfor();
		
		clearInput();
		if(p!=null) {
			try {
				dos.writeUTF("ADD");
				oos.writeObject(p);
			} catch (Exception e) {
				// TODO: handle exception
			}
			setVisible(false);
			p = new Person(p.getName(), p.getPhone(), p.getAddress(), p.getEmail(), p.getClassify().getName());
		}
	}
	private void retypeData() {
		clearInput();
	}
	private void cancel() {
		clearInput();
		setVisible(false);
	}
	
	private void clearInput() {
		txtName.setText("");;
		txtPhone.setText("");
		txtAddress.setText("");
		txtEmail.setText("");
		txtName.requestFocus();
	}
	public void display(boolean visible) {
		loadInfor();
		setVisible(visible);
	}
	
	public void setListGroupNew(ArrayList<GroupContact> listGroupNew) {
		this.listGroupNew = listGroupNew;
	}
	private void loadInfor() {
		setListGroupNew(mainContacts.getData().getListGroupNew());
		loadListGroupNew();
	}
	public void loadListGroup() {
		String[] list = new String[listGroup.size()];
		list = listGroup.toArray(list);
		cbGroup.setModel(new DefaultComboBoxModel<String>(list));
	}

	public void loadListGroupNew() {
		listGroup = new ArrayList<>();
		for (int i = 0; i < listGroupNew.size(); i++) {
			listGroup.add(listGroupNew.get(i).getName());
		}
		loadListGroup();
	}
	
	private void addGroup(String groupName) throws UnknownHostException, IOException {
		int index = indexGroupName(groupName);
		if (index == -1) {
			dos.writeUTF("ADD_GROUP");
			dos.writeUTF(groupName);
			try {
				listGroupNew = (ArrayList<GroupContact>) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO: handle exception
				e.printStackTrace();
				
			}
			listGroup.add(groupName);
			Collections.sort(listGroup, Person.GroupComparator);
			index = indexGroupName(groupName);
			loadListGroup();
			////
			mainContacts.getData().setListGroupNew(listGroupNew);
			cbGroup.setSelectedIndex(index);
		}
	}
}
