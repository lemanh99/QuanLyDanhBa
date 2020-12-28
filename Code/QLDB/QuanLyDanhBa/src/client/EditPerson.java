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

public class EditPerson extends JDialog implements ActionListener, KeyListener {
	private MainContacts mainContacts;
	DataOutputStream dos;
	DataInputStream dis;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	private ArrayList<String> listGroup;
	private ArrayList<GroupContact> listGroupNew;
	private JTextField txtName, txtPhone, txtAddress, txtEmail;
	private JComboBox<String> cbGroup;
	JButton btnGroupNew, btnSave, btnCancel;

	private int indexSelected;
	private int idPerson;

	public EditPerson(MainContacts mainContacts, DataOutputStream dos, DataInputStream dis, ObjectInputStream ois,
			ObjectOutputStream oos) {
		super();
		this.mainContacts = mainContacts;
		this.dos = dos;
		this.dis = dis;
		this.ois = ois;
		this.oos = oos;
		this.listGroup = this.mainContacts.getData().getListGroup();
		GUI();
		pack();
	}

	public int getIndexSelected() {
		return indexSelected;
	}

	public void setIndexSelected(int indexSelected) {
		this.indexSelected = indexSelected;
	}

	private void GUI() {
//		infor = new Information(mainContacts.getData().getListGroupName(), dos, ois, mainContacts);
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

		JPanel pnControl = new JPanel(new GridLayout(1, 3, 10, 10));
		pnControl.setBorder(new EmptyBorder(10, 10, 10, 10));
		btnSave = new JButton("Lưu");
		btnSave.addActionListener(this);
		btnCancel = new JButton("Hủy");
		btnCancel.addActionListener(this);
		pnControl.add(btnSave);
		pnControl.add(btnCancel);

		JPanel pn = new JPanel(new BorderLayout());
		pn.add(pnInfor, BorderLayout.CENTER);
		pn.add(pnControl, BorderLayout.PAGE_END);
		add(pn);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Chỉnh sửa liên hệ");
		setLocationRelativeTo(null);
	}

	public void display(boolean visible) {
		loadInfor();
		setVisible(visible);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
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
		if (e.getSource() == btnSave) {
			editPerson();
		}
		if (e.getSource() == btnCancel) {
			cancel();
		}
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

	private void loadInfor() {
		Person p = mainContacts.getData().getlistPerson().get(indexSelected);
		idPerson = p.getId();
		this.listGroupNew = mainContacts.getData().getListGroupNew();
		loadListGroupNew();
		txtName.setText(p.getName());
		txtPhone.setText(p.getPhone());
		txtAddress.setText(p.getAddress());
		txtEmail.setText(p.getEmail());
		cbGroup.setSelectedIndex(indexGroupName(p.getGroup()));
	}

	public int indexGroupName(String groupName) {
		for (int i = 0; i < listGroup.size(); i++) {
			if (listGroup.get(i).equals(groupName)) {
				return i;
			}
		}
		return -1;
	}

	public void loadListGroupNew() {
		listGroup = new ArrayList<>();
		for (int i = 0; i < listGroupNew.size(); i++) {
			listGroup.add(listGroupNew.get(i).getName());
		}
		loadListGroup();
	}

	public void loadListGroup() {
		String[] list = new String[listGroup.size()];
		list = listGroup.toArray(list);
		cbGroup.setModel(new DefaultComboBoxModel<String>(list));
	}

	private void editPerson() {
		Person p = getInfor();
		p = new Person(idPerson, p.getName(), p.getPhone(), p.getAddress(), p.getEmail(), p.getClassify());
		if (p != null) {
			try {
				dos.writeUTF("EDIT");
				oos.writeObject(p);
			} catch (Exception e) {
				// TODO: handle exception
			}
			p = new Person(idPerson, p.getName(), p.getPhone(), p.getAddress(), p.getEmail(),
					p.getClassify().getName());
			clearInput();
			setVisible(false);

			mainContacts.getData().getlistPerson().set(indexSelected, p);
			mainContacts.updateData();
		}

	}
	private void addGroup(String groupName) throws UnknownHostException, IOException {
		int index = indexGroupName(groupName);
		if (index == -1) {
			dos.writeUTF("ADD_GROUP");
			dos.writeUTF(groupName);
			try {
				listGroupNew = (ArrayList<GroupContact>) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				
			}
			listGroup.add(groupName);
			Collections.sort(listGroup, Person.GroupComparator);
//			Collections.sort(listGroupNew, Person.GroupGGComparator);
			index = indexGroupName(groupName);
			loadListGroup();
			////
			mainContacts.getData().setListGroupNew(listGroupNew);
			cbGroup.setSelectedIndex(index);
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

					JOptionPane.showMessageDialog(null, "Điện thoại phải có hơn 10 chữ số", "Nhập số điện thoại",
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
	

	private void cancel() {
		clearInput();
		setVisible(false);
	}

	private void clearInput() {
		txtName.setText("");
		txtPhone.setText("");
		txtAddress.setText("");
		txtEmail.setText("");
		txtName.requestFocus();
	}

}
