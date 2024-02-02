package io.github.zinkowinn.csv;

import io.github.zinkowinn.csv.core.strategy.DefaultColumnMappingStrategy;
import io.github.zinkowinn.csv.dto.StudentWithNoPositionAnnotation;
import io.github.zinkowinn.csv.exceptions.CsvReaderException;
import io.github.zinkowinn.csv.dto.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zin Ko Winn
 */

class CsvReaderTest {
    private CsvReader<Student> csvReader;

    @BeforeEach
    void setUp() {
        csvReader = CsvReader.of(Student.class)
                .config(c -> c.skipLines(1));
    }

    @Test
    @DisplayName("Read CSV from File")
    void testReadCSVFromFile() {
        String filePath = "csv/student.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData);
    }

    @Test
    @DisplayName("Read CSV with Invalid File Path")
    void testReadCSVWithInvalidFile() {
        String filePath = "";
        assertThrows(CsvReaderException.class, () -> csvReader.readCSV(filePath));
    }

    @Test
    @DisplayName("Read CSV from Byte Array")
    void testReadCSVFromByteArray() {
        byte[] content = "name,age\nJohn,30\nAlice,25\n".getBytes();

        List<Student> actualData = csvReader.readCSV(content);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData);
    }

    @Test
    @DisplayName("Read CSV from Base64 Encoded String")
    void testReadCSVFromBase64() {
        String base64Content = Base64.getEncoder().encodeToString("name,age\nJohn,30\nAlice,25\n".getBytes());

        List<Student> actualData = csvReader.readCSVFromBase64(base64Content);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData);
    }

    @Test
    @DisplayName("Read CSV from Invalid Reader")
    void testReadCSVFromInvalidReader() {
        assertThrows(CsvReaderException.class, () -> csvReader.readCSVFromReader(null));
    }

    @Test
    @DisplayName("Read CSV with Empty Content")
    void testReadCSVWithEmptyContent() {
        byte[] content = "".getBytes();
        csvReader.config(c -> c.skipLines(1).hasHeader(false));
        List<Student> actualData = csvReader.readCSV(content);
        assertEquals(0, actualData.size());
    }

    @Test
    @DisplayName("Read CSV from Invalid Base64 String")
    void testReadCSVFromInvalidBase64() {
        String base64Content = "InvalidBase64Content";
        assertThrows(CsvReaderException.class, () -> csvReader.readCSVFromBase64(base64Content));
    }

    @Test
    @DisplayName("Read CSV from Reader")
    void testReadCSVFromReader() {
        StringReader reader = new StringReader("name,age\nJohn,30\nAlice,25\n");

        List<Student> actualData = csvReader.readCSVFromReader(reader);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData);
    }

    @Test
    @DisplayName("Read CSV with Custom Mapping Strategy")
    void testReadCSVWithCustomMappingStrategy() {
        csvReader.mappingStrategy(new DefaultColumnMappingStrategy<>(Student.class));
        String filePath = "csv/student.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData);
    }

    @Test
    @DisplayName("Read CSV with Custom Mapping Strategy Without Position Annotation")
    void testReadCSVWithCustomMappingStrategyWithNoPositionAnnotation() {
        CsvReader<StudentWithNoPositionAnnotation> reader = CsvReader.of(StudentWithNoPositionAnnotation.class)
                .config(c -> c.skipLines(1))
                .mappingStrategy(new DefaultColumnMappingStrategy<>(StudentWithNoPositionAnnotation.class));
        String filePath = "csv/student.csv";
        assertThrows(CsvReaderException.class, () -> reader.readCSVFromClasspath(filePath));
    }

    @Test
    @DisplayName("Read CSV from Non-Existent File")
    void testReadCSVFromNonExistentFile() {
        String filePath = "non_existent_file.csv";
        assertThrows(IllegalArgumentException.class, () -> csvReader.readCSVFromClasspath(filePath));
    }

    @Test
    @DisplayName("Read CSV File with Header but No Records")
    void testReadCSVFileWithHeaderButNoRecords() {
        String filePath = "csv/empty_student.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        assertTrue(actualData.isEmpty(), "Expected empty list when CSV file has header but no records");
    }

    @Test
    @DisplayName("Read CSV File with Records but No Header")
    void testReadCSVFileWithRecordsButNoHeader() {
        String filePath = "csv/no_header_student.csv";

        csvReader.config(c -> c.skipLines(0).hasHeader(false));
        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData, "Expected data should match when CSV file has records but no header");
    }

    @Test
    @DisplayName("Read CSV File with Different Delimiters")
    void testReadCSVFileWithDifferentDelimiters() {
        String filePath = "csv/semicolon_delimiter_student.csv";

        csvReader.config(c -> c.skipLines(1).separator(';'));
        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "25")
        );

        assertEquals(expectedData, actualData, "Expected data should match when CSV file uses semicolon as delimiter");
    }

    @Test
    @DisplayName("Read CSV File with Missing Fields")
    void testReadCSVFileWithMissingFields() {
        String filePath = "csv/missing_fields_student.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice", "")
        );

        assertEquals(expectedData, actualData, "Expected data should match when CSV file contains missing fields");
    }

    @Test
    @DisplayName("Read CSV File with Escaped Characters")
    void testReadCSVFileWithEscapedCharacters() {
        String filePath = "csv/escaped_characters_student.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        List<Student> expectedData = List.of(
                new Student("John", "30"),
                new Student("Alice, Jr.", "25")
        );

        assertEquals(expectedData, actualData, "Expected data should match when CSV file contains escaped characters");
    }

    @Test
    @DisplayName("Read CSV File with Large Data Sets")
    void testReadCSVFileWithLargeDataSets() {
        String filePath = "csv/large_student_data.csv";

        List<Student> actualData = csvReader.readCSVFromClasspath(filePath);

        assertFalse(actualData.isEmpty(), "Expected non-empty data with large CSV file");
    }
}
