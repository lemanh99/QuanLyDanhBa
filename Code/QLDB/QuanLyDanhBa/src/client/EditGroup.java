package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog.ModalityType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class EditGroup extends JDialog implements ActionListener {
	private MainContacts mainContacts;
	DataOutputStream dos;
	DataInputStream dis;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	private ArrayList<String> listGroup;
	private ArrayList<GroupContact> listGroupNew;

	private JComboBox<String> cbGroup;
	private JButton btnAdd, btnEdit, btnDelete, btnCancel;

	public EditGroup(MainContacts mainContacts, DataOutputStream dos, DataInputStream dis, ObjectInputStream ois,
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

	private void GUI() {
		JPanel pnInfor = new JPanel(new BorderLayout());
		// Add label
		JPanel pn1 = new JPanel(new BorderLayout());
		pn1.setBorder(new EmptyBorder(10, 10, 10, 10));
		pn1.add(new JLabel("Nhóm"), BorderLayout.CENTER);

		// Add textfield
		JPanel pn2 = new JPanel(new BorderLayout());
		pn2.setBorder(new EmptyBorder(10, 10, 10, 10));
		String[] list = new String[listGroup.size()];
		list = listGroup.toArray(list);
		cbGroup = new JComboBox<String>(list);
		pn2.add(cbGroup, BorderLayout.CENTER);

		pnInfor.add(pn1, BorderLayout.WEST);
		pnInfor.add(pn2, BorderLayout.CENTER);

		JPanel pnControl = new JPanel(new GridLayout(1, 3, 10, 10));
		pnControl.setBorder(new EmptyBorder(10, 10, 10, 10));
		btnAdd = new JButton("Thêm");
		btnAdd.addActionListener(this);
		btnEdit = new JButton("Đổi tên");
		btnEdit.addActionListener(this);
		btnDelete = new JButton("Xóa");
		btnDelete.addActionListener(this);
		btnCancel = new JButton("Trở lại");
		btnCancel.addActionListener(this);
		pnControl.add(btnAdd);
		pnControl.add(btnEdit);
		pnControl.add(btnDelete);
		pnControl.add(btnCancel);

		JPanel pn = new JPanel(new BorderLayout());
		pn.add(pnInfor, BorderLayout.CENTER);
		pn.add(pnControl, BorderLayout.PAGE_END);
		add(pn);
		setTitle("Chỉnh sửa nhóm");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setLocationRelativeTo(null);

	}

	public void display(boolean visible) {
		loadInfor();
		setVisible(visible);
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

	public void setListGroupNew(ArrayList<GroupContact> listGroupNew) {
		this.listGroupNew = listGroupNew;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnAdd) {
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
		if (e.getSource() == btnEdit) {
			try {
				renameGroup();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == btnDelete) {
			try {
				int selected = JOptionPane.showOptionDialog(null,
						"Bạn thực sự muốn xóa nhóm " + cbGroup.getSelectedItem()
								+ "\nTất cả liên hệ thuộc nhóm sẽ bị xóa ra khỏi danh bạ?",
						"Xóa nhóm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (selected == 0) {
					delete();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == btnCancel) {
			setVisible(false);
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

	private void delete() throws IOException {
		String groupName = (String) cbGroup.getSelectedItem();
		int index = cbGroup.getSelectedIndex();
		System.out.println(groupName);
		dos.writeUTF("DELETE_GROUP");
		dos.writeUTF(groupName);
		Object object = null;
		try {
			object = ois.readObject();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mainContacts.getData().setlistPerson((ArrayList<Person>) object);
		Object objectGroup = null;
		try {
			objectGroup = ois.readObject();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mainContacts.getData().setListGroupNew((ArrayList<GroupContact>) objectGroup);
		listGroup.remove(index);
		Collections.sort(listGroup, Person.GroupComparator);
		loadListGroup();
		mainContacts.updateData();
	}

	private void renameGroup() throws IOException {
		String group_name_old = (String) cbGroup.getSelectedItem();
		int index = cbGroup.getSelectedIndex();
		String group_name_new = (String) JOptionPane.showInputDialog(null, "Chỉnh sửa tên nhóm", "Chỉnh sửa tên nhóm",
				JOptionPane.QUESTION_MESSAGE, null, null, group_name_old);
		if (group_name_new != null && group_name_new.trim().length() > 0 && !group_name_old.equals(group_name_new)) {
			try {
				boolean check = false;
				for (int i = 0; i < listGroup.size(); i++) {
					if (listGroup.get(i).equals(group_name_new)) {
						check = true;
						JOptionPane.showMessageDialog(null, "Nhóm đã tồn tại!");
						cbGroup.setSelectedIndex(index);
						break;
					}
				}
				if (!check) {
					dos.writeUTF("RENAME_GROUP");
					dos.writeUTF(group_name_new);
					dos.writeUTF(group_name_old);

					Object object = null;
					try {
						object = ois.readObject();
					} catch (Exception e) {
						// TODO: handle exception
					}
					mainContacts.getData().setlistPerson((ArrayList<Person>) object);
					Object objectGroup = null;
					try {
						objectGroup = ois.readObject();
					} catch (Exception e) {
						// TODO: handle exception
					}
					mainContacts.getData().setListGroupNew((ArrayList<GroupContact>) objectGroup);
					listGroup.set(index, group_name_new);
					Collections.sort(listGroup, Person.GroupComparator);
					loadListGroup();
					mainContacts.updateData();
					cbGroup.setSelectedItem(group_name_new);
				}

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {

				e2.printStackTrace();
			}
		}

	}

	public int indexGroupName(String groupName) {
		for (int i = 0; i < listGroup.size(); i++) {
			if (listGroup.get(i).equals(groupName)) {
				return i;
			}
		}
		return -1;
	}

}
