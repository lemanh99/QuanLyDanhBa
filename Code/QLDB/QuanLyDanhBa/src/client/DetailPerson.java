package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog.ModalityType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class DetailPerson extends JDialog implements ActionListener {
	private MainContacts mainContacts;
	DataOutputStream dos;
	DataInputStream dis;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private int indexSelected;

	private JLabel lbName, lbPhone, lbAddress, lbEmail, lbGroup;
	private JButton btnEdit, btnCancel;

	public DetailPerson(MainContacts mainContacts, DataOutputStream dos, DataInputStream dis, ObjectInputStream ois,
			ObjectOutputStream oos) {
		super();
		this.mainContacts = mainContacts;
		this.dos = dos;
		this.dis = dis;
		this.ois = ois;
		this.oos = oos;
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
		JPanel pnInfor = new JPanel(new BorderLayout());
		// Add label
		JPanel pn1 = new JPanel(new GridLayout(5, 1, 5, 5));
		pn1.setBorder(new EmptyBorder(10, 10, 10, 10));
		pn1.add(new JLabel("Tên :"));
		pn1.add(new JLabel("Số điện thoại: "));
		pn1.add(new JLabel("Địa chỉ: "));
		pn1.add(new JLabel("Email:"));
		pn1.add(new JLabel("Nhóm: "));

		// Add textfield
		JPanel pn2 = new JPanel(new GridLayout(5, 1, 5, 5));
		pn2.setBorder(new EmptyBorder(10, 10, 10, 10));
		lbName = new JLabel();
		lbPhone = new JLabel();
		lbAddress = new JLabel();
		lbEmail = new JLabel();
		lbGroup = new JLabel();
		pn2.add(lbName);
		pn2.add(lbPhone);
		pn2.add(lbAddress);
		pn2.add(lbEmail);
		pn2.add(lbGroup);

		pnInfor.add(pn1, BorderLayout.WEST);
		pnInfor.add(pn2, BorderLayout.CENTER);

		JPanel pnControl = new JPanel(new GridLayout(1, 3, 10, 10));
		pnControl.setBorder(new EmptyBorder(10, 10, 10, 10));
		btnEdit = new JButton("Chỉnh sửa");
		btnEdit.addActionListener(this);
		btnCancel = new JButton("Trở lại");
		btnCancel.addActionListener(this);
		pnControl.add(btnEdit);
		pnControl.add(btnCancel);

		JPanel pn = new JPanel(new BorderLayout());
		pn.add(pnInfor, BorderLayout.CENTER);
		pn.add(pnControl, BorderLayout.PAGE_END);
		add(pn);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Chi tiết liên hệ");
		setLocationRelativeTo(null);
	}

	private void loadInfor() {
		Person p = mainContacts.getData().getlistPerson().get(indexSelected);
		lbName.setText(p.getName());
		lbPhone.setText(p.getPhone());
		lbAddress.setText(p.getAddress());
		lbEmail.setText(p.getEmail());
		lbGroup.setText(p.getGroup());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnEdit) {
			setVisible(false);
			this.mainContacts.editContact();
		}
		if (e.getSource() == btnCancel) {
			setVisible(false);
		}

	}

	public void display(boolean visible) {
		loadInfor();
		setVisible(visible);
	}
}
