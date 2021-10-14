package ru.sazhina;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilesTest {

    @Test
    void parseTXT() throws Exception {
        String parsed;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("txt.txt")) {
            assert is != null;
            parsed = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(parsed).contains("Test file .txt");
            assertThat(parsed).doesNotContain("Dummy check");
        }
    }

    @Test
    void parsePDF() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("pdf.pdf")) {
            assert is != null;
            PDF parsed = new PDF(is);
            assertThat(parsed.text).contains("Test file .pdf");
        }
    }

    @Test
    void parseCSV() throws Exception {
        URL url = getClass().getClassLoader().getResource("csv.csv");
        assert url != null;
        CSVReader parsed = new CSVReader(new FileReader(new File(url.toURI())));

            List<String[]> strings = parsed.readAll();

            assertThat(strings).contains(
                new String[] {"CSV1", "11"},
                new String[] {"CSV2", "22"},
                new String[] {"CSV3", "33"}
        );
    }

    @Test
    void parseXLS() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("xls.xls")) {
            assert is != null;
            XLS parsed = new XLS(is);
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                    .isEqualTo("TEST");
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                    .isEqualTo("test");
            assertThat(parsed.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("NewTest");
        }
    }

    @Test
    void parseZIP() throws Exception {
        ZipFile zipFile = new ZipFile("./src/test/resources/zip.zip");

        if (zipFile.isEncrypted()) {
            zipFile.setPassword("12345");
        }
        zipFile.extractAll("./src/test/resources/extracted");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("txt.txt")) {
            assert is != null;
            String parsed = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(parsed).contains("Test file .txt");
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("xls.xls")) {
            assert is != null;
            XLS parsed = new XLS(is);
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                    .isEqualTo("TEST");
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                    .isEqualTo("test");
        }
    }

    @Test
    void parseDOCX() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("docx.docx")) {
            assert is != null;
            XWPFDocument file = new XWPFDocument(is);
            XWPFWordExtractor extractor = new XWPFWordExtractor(file);
            String text = extractor.getText();
            assertThat(text.contains("Test file .docx"));
        }
    }
}