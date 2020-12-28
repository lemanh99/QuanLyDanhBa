package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadAndWriteExcel {

	public final static String FILE_TO_OUTPUT = "src/Output/DANHSACHLIENHE.xlsx";

	public ReadAndWriteExcel() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Person> ReadFile(File file) {
		ArrayList<Person> listPerson = new ArrayList<Person>();
		try {
			OPCPackage pkg = OPCPackage.open(file);
			XSSFWorkbook myExcelBook = new XSSFWorkbook(pkg);
			XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
			String name, phone, address, email, group;
			int i = 1;
			XSSFRow row = myExcelSheet.getRow(i);
			while (row != null) {
				double stt = row.getCell(Integer.valueOf(0)).getNumericCellValue();
				name = row.getCell(Integer.valueOf(1)).getStringCellValue();
				phone = row.getCell(Integer.valueOf(2)).getStringCellValue();
				address = row.getCell(Integer.valueOf(3)).getStringCellValue();
				email = row.getCell(Integer.valueOf(4)).getStringCellValue();
				group = row.getCell(Integer.valueOf(5)).getStringCellValue();
				if (name.equals(""))
					break;
//				System.out.println("Ket qua"+ stt+name);
				listPerson.add(new Person(name, phone, address, email, group));
				i++;
				row = myExcelSheet.getRow(i);
			}
			System.out.println("Đã đọc file thanh cong");
			myExcelBook.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Đọc file Can bo : " + file + " lỗi" + e);
			return null;
		}
		return listPerson;
	}

	public boolean SaveContacts(ArrayList<Person> listPersons) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet danh bạ");
		int rownum = 0;
		XSSFCell cell;
		XSSFRow row;
		row = sheet.createRow(rownum);
		XSSFCellStyle style = createStyleForTitle(workbook);

		// STT
		cell = row.createCell(0, CellType.STRING);
		cell.setCellValue("STT");
		cell.setCellStyle(style);

		// Tên
		cell = row.createCell(1, CellType.STRING);
		cell.setCellValue("Tên");
		cell.setCellStyle(style);

		// Số điện thoại
		cell = row.createCell(2, CellType.STRING);
		cell.setCellValue("Số điện thoại");
		cell.setCellStyle(style);
		// Địa chỉ
		cell = row.createCell(3, CellType.STRING);
		cell.setCellValue("Địa chỉ");
		cell.setCellStyle(style);

		// Email
		cell = row.createCell(4, CellType.STRING);
		cell.setCellValue("Email");
		cell.setCellStyle(style);

		// Nhóm
		cell = row.createCell(5, CellType.STRING);
		cell.setCellValue("Nhóm");
		cell.setCellStyle(style);

		int stt = 1;
		// Data
		for (Person ps : listPersons) {
			rownum++;
			row = sheet.createRow(rownum);
			// STT (A)
			cell = row.createCell(0, CellType.NUMERIC);
			cell.setCellValue(rownum);

			// Tên liên lạc
			cell = row.createCell(1, CellType.STRING);
			cell.setCellValue(ps.getName());

			// Số điện thoại
			cell = row.createCell(2, CellType.STRING);
			cell.setCellValue(ps.getPhone());

			// Địa chỉ
			cell = row.createCell(3, CellType.STRING);
			cell.setCellValue(ps.getAddress());

			// Email
			cell = row.createCell(4, CellType.STRING);
			cell.setCellValue(ps.getEmail());

			// Nhóm
			cell = row.createCell(5, CellType.STRING);
			cell.setCellValue(ps.getGroup());

			style.setWrapText(true);
			stt++;

		}

		File file = new File(FILE_TO_OUTPUT);
		file.getParentFile().mkdirs();

		FileOutputStream outFile;
		try {
			outFile = new FileOutputStream(file);

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			workbook.write(outFile);
			outFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.println("Đã tạo file: " + file.getAbsolutePath());

		return true;
	}

	private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

}
