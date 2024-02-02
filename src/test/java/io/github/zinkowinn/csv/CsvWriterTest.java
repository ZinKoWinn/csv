package io.github.zinkowinn.csv;

import io.github.zinkowinn.csv.dto.Student;
import io.github.zinkowinn.csv.exceptions.CsvWriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zin Ko Winn
 */

class CsvWriterTest {
    private CsvWriter<Student> csvWriter;

    @BeforeEach
    void setUp() {
        csvWriter = CsvWriter.of(Student.class);
    }

    @Test
    @DisplayName("Write CSV Data to Byte Array")
    void testWriteCSVToByteArray() {
        List<Student> students = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        byte[] actualBytes = csvWriter.writeCSV(students);

        byte[] expectedBytes = "name,age\nJohn,30\nAlice,25\n".getBytes();

        assertEquals(expectedBytes.length, actualBytes.length);
        for (int i = 0; i < expectedBytes.length; i++) {
            assertEquals(expectedBytes[i], actualBytes[i]);
        }
    }

    @Test
    @DisplayName("Write CSV Data to Base64 Encoded String")
    void testWriteCSVToBase64() {
        List<Student> students = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        String actualBase64 = csvWriter.writeCSVToBase64(students);

        String expectedBase64 = "bmFtZSxhZ2UKSm9obiwzMApBbGljZSwyNQo=";

        assertEquals(expectedBase64, actualBase64);
    }

    @Test
    @DisplayName("Write CSV Data to OutputStream")
    void testWriteCSVToOutputStream() throws IOException {
        List<Student> students = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        csvWriter.writeCSV(outputStream, students);

        String actualCSV = outputStream.toString();

        String expectedCSV = "name,age\nJohn,30\nAlice,25\n";

        assertEquals(expectedCSV, actualCSV);
    }

    @Test
    @DisplayName("Write Empty CSV Data")
    void testWriteEmptyCSVData() {
        List<Student> students = new ArrayList<>();

        byte[] actualBytes = csvWriter.writeCSV(students);

        byte[] expectedBytes = "".getBytes();

        assertEquals(expectedBytes.length, actualBytes.length);
    }

    @Test
    @DisplayName("Write CSV Data with Null List")
    void testWriteCSVWithNullList() {
        assertThrows(NullPointerException.class, () -> csvWriter.writeCSV(null));
    }

    @Test
    @DisplayName("Write CSV Data with Null Element")
    void testWriteCSVWithNullElement() {
        List<Student> students = new ArrayList<>();
        students.add(null);

        assertDoesNotThrow(() -> csvWriter.writeCSV(students));
    }

    @Test
    @DisplayName("Write CSV Data to Invalid Output Stream")
    void testWriteCSVToInvalidOutputStream() {
        List<Student> students = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Invalid output stream");
            }
        };

        assertThrows(CsvWriterException.class, () -> csvWriter.writeCSV(outputStream, students));
    }

    @Test
    @DisplayName("Write CSV Data with Custom Configuration")
    void testWriteCSVWithCustomConfiguration() {
        List<Student> students = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        csvWriter.config(config -> config
                .separator(';')
                .quoteChar('\'')
                .escapeChar('\\')
                .lineEnd("\r\n"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        csvWriter.writeCSV(outputStream, students);

        String actualCSV = outputStream.toString();

        String expectedCSV = "'name';'age'\r\n'John';'30'\r\n'Alice';'25'\r\n";

        assertEquals(expectedCSV, actualCSV);
    }

    @Test
    @DisplayName("Write CSV Data with Large Data Sets")
    void testWriteCSVWithLargeDataSets() {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            students.add(new Student("Name" + i, "Age" + i));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        csvWriter.writeCSV(outputStream, students);

        String actualCSV = outputStream.toString();

        assertFalse(actualCSV.isEmpty(), "Expected non-empty CSV data");

        String[] lines = actualCSV.split("\r\n|\r|\n");
        assertEquals(10001, lines.length, "Expected number of lines in CSV matches the number of records");
    }

    @Test
    @DisplayName("Write CSV Data with Null Values")
    void testWriteCSVWithNullValues() {
        List<Student> students = List.of(
                new Student("John", null),
                new Student("Alice", "25")
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        csvWriter.writeCSV(outputStream, students);

        String actualCSV = outputStream.toString();

        String expectedCSV = "name,age\nJohn,\nAlice,25\n";

        assertEquals(expectedCSV, actualCSV);
    }
}
