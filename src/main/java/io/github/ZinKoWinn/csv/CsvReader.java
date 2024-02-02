package io.github.zinkowinn.csv;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.zinkowinn.csv.config.CsvConfig;
import io.github.zinkowinn.csv.core.DefaultDataTransformer;
import io.github.zinkowinn.csv.exceptions.CsvReaderException;
import io.github.zinkowinn.csv.mapper.DataTransformer;
import io.github.zinkowinn.csv.utils.ReflectionUtils;
import lombok.Data;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * CsvReader is a utility class to read data from CSV files and transform them into Java objects.
 * It provides various configuration options to customize CSV parsing and transformation.
 *
 * @param <T> The type of Java object to which each CSV record will be mapped.
 * @author Zin Ko Winn
 * @since 1.0.0
 */

@Data
public class CsvReader<T> {
    private static final String EMPTY_FILE_MESSAGE = "The CSV file is empty.";
    private static final String CSV_HEADER_MISMATCH_MESSAGE = "CSV headers do not match the expected headers.";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found: ";
    private static final String FILE_READ_ERROR_MESSAGE = "Error reading CSV file: ";
    private static final String BASE64_READ_ERROR_MESSAGE = "Error reading CSV from Base64 content";
    private static final String FILE_PATH_EMPTY_ERROR_MESSAGE = "File path cannot be null or empty";
    public static final String CSV_READER_NULL_ERROR_MESSAGE = "The CSV reader object is null";

    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class);

    private static final List<Class<? extends Annotation>> POSITION_ANNOTATIONS = Arrays.asList(
            CsvBindByPosition.class,
            CsvBindAndSplitByPosition.class,
            CsvBindAndJoinByPosition.class,
            CsvCustomBindByPosition.class
    );

    private static final List<Class<? extends Annotation>> NAME_ANNOTATIONS = Arrays.asList(
            CsvBindByName.class,
            CsvBindAndSplitByName.class,
            CsvBindAndJoinByName.class,
            CsvCustomBindByName.class
    );

    private CsvConfig config;
    private DataTransformer<T> dataTransformer;
    private MappingStrategy<T> mappingStrategy;
    private Class<T> type;

    /**
     * Private constructor to initialize CsvReader with the target class type.
     *
     * @param type The class type of the objects to which CSV records will be mapped.
     */
    private CsvReader(Class<T> type) {
        this.type = type;
        this.config = new CsvConfig();
        this.dataTransformer = new DefaultDataTransformer<>();
        this.mappingStrategy = null;
    }

    /**
     * Factory method to create an instance of CsvReader with the specified class type.
     *
     * @param <T>  The type of Java object to which each CSV record will be mapped.
     * @param type The class type of the objects to which CSV records will be mapped.
     * @return An instance of CsvReader configured to handle objects of type T.
     */
    public static <T> CsvReader<T> of(Class<T> type) {
        return new CsvReader<>(type);
    }

    /**
     * Configures CsvReader with custom settings using a builder function.
     *
     * @param builder A unary operator function to modify the CsvConfig settings.
     * @return The CsvReader instance with updated configuration.
     */
    public CsvReader<T> config(UnaryOperator<CsvConfig> builder) {
        this.config = builder.apply(this.config);
        return this;
    }

    /**
     * Configures CsvReader with a provided CsvConfig object.
     *
     * @param config The CsvConfig object containing custom settings.
     * @return The CsvReader instance with updated configuration.
     */
    public CsvReader<T> config(CsvConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Sets a custom data transformer for CsvReader.
     *
     * @param transformer The data transformer to be used.
     * @return The CsvReader instance with the specified data transformer.
     */
    public CsvReader<T> transform(DataTransformer<T> transformer) {
        this.dataTransformer = transformer;
        return this;
    }

    /**
     * Sets a custom mapping strategy for CsvReader.
     *
     * @param mappingStrategy The mapping strategy to be used.
     * @return The CsvReader instance with the specified mapping strategy.
     */
    public CsvReader<T> mappingStrategy(MappingStrategy<T> mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
        return this;
    }

    /**
     * Reads CSV data from a file and returns a list of mapped objects.
     *
     * @param filePath The path to the CSV file.
     * @return A list of Java objects mapped from the CSV records.
     * @throws CsvReaderException If an error occurs during CSV reading or mapping.
     */
    public List<T> readCSV(String filePath) {
        this.validateCsvFile(filePath);

        try (Reader reader = new FileReader(filePath)) {
            return readCSVFromReader(reader);
        } catch (IOException e) {
            throw new CsvReaderException(FILE_READ_ERROR_MESSAGE + filePath, e);
        }
    }

    /**
     * Reads CSV data from a byte array and returns a list of mapped objects.
     *
     * @param content The byte array containing CSV data.
     * @return A list of Java objects mapped from the CSV records.
     * @throws CsvReaderException If an error occurs during CSV reading or mapping.
     */
    public List<T> readCSV(byte[] content) {
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(content))) {
            return readCSVFromReader(reader);
        } catch (IOException e) {
            throw new CsvReaderException("Error reading CSV from byte array", e);
        }
    }

    /**
     * Reads CSV data from a Base64 encoded string and returns a list of mapped objects.
     *
     * @param base64Content The Base64 encoded string containing CSV data.
     * @return A list of Java objects mapped from the CSV records.
     * @throws CsvReaderException If an error occurs during CSV reading or mapping.
     */
    public List<T> readCSVFromBase64(String base64Content) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(decodedBytes))) {
                return readCSVFromReader(reader);
            }
        } catch (IOException e) {
            throw new CsvReaderException(BASE64_READ_ERROR_MESSAGE, e);
        }
    }


    /**
     * Reads CSV data from a file in the classpath and returns a list of mapped objects.
     *
     * @param resourcePath The path to the CSV file within the classpath.
     * @return A list of Java objects mapped from the CSV records.
     * @throws CsvReaderException If an error occurs during CSV reading or mapping.
     */
    public List<T> readCSVFromClasspath(String resourcePath) {
        this.validateCsvFile(resourcePath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException(RESOURCE_NOT_FOUND_MESSAGE + resourcePath);
            }

            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                return readCSVFromReader(reader);
            }
        } catch (IOException e) {
            throw new CsvReaderException(FILE_READ_ERROR_MESSAGE + resourcePath, e);
        }
    }

    /**
     * Reads CSV data from a Reader object and returns a list of mapped objects.
     *
     * @param reader The Reader object providing CSV data.
     * @return A list of Java objects mapped from the CSV records.
     */
    public List<T> readCSVFromReader(Reader reader) {
        if (reader == null) {
            throw new CsvReaderException(CSV_READER_NULL_ERROR_MESSAGE);
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.validateCsvHeader(bufferedReader, config.isHasHeader());
            this.checkPositionAnnotations(this.mappingStrategy);
            return transformData(parseCSV(bufferedReader, type));
        } catch (IOException e) {
            throw new CsvReaderException(FILE_READ_ERROR_MESSAGE, e);
        }
    }

    /**
     * Parses CSV data from a Reader object using the specified class type.
     *
     * @param reader The Reader object providing CSV data.
     * @param clazz  The class type to which CSV records will be mapped.
     * @return A list of Java objects mapped from the CSV records.
     */
    private List<T> parseCSV(Reader reader, Class<T> clazz) {
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(new BufferedReader(reader))
                .withType(clazz)
                .withMappingStrategy(this.mappingStrategy)
                .withSkipLines(columnMappingAnnotationPresent() ? config.getSkipLines() : 0)
                .withSeparator(config.getSeparator())
                .withIgnoreLeadingWhiteSpace(config.isIgnoreLeadingWhiteSpace())
                .withOrderedResults(config.isOrderedResults())
                .withThrowExceptions(config.isThrowsExceptions())
                .withIgnoreEmptyLine(config.isIgnoreEmptyLines())
                .withStrictQuotes(config.isStrictQuotes())
                .build();
        return csvToBean.parse();
    }

    /**
     * Transforms a list of objects using the configured data transformer.
     *
     * @param data The list of objects to transform.
     * @return A transformed list of objects.
     */
    private List<T> transformData(List<T> data) {
        return data.stream()
                .map(dataTransformer::transform)
                .collect(Collectors.toList());
    }

    /**
     * Validates the CSV file path.
     *
     * @param filePath The path to the CSV file.
     * @throws IllegalArgumentException If the file path is null or empty.
     */
    private void validateCsvFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new CsvReaderException(FILE_PATH_EMPTY_ERROR_MESSAGE);
        }
    }

    /**
     * Validates the header of the CSV file.
     *
     * @throws CsvReaderException       If an error occurs during CSV reading or mapping.
     * @throws IllegalArgumentException If the headers do not match the expected headers.
     * @implNote If a specific field is not required in the CSV, it should be annotated with '@CsvIgnore' to exclude it from header validation.
     */
    private void validateCsvHeader(BufferedReader reader, boolean hasHeader) {
        if (hasHeader) {
            try {
                reader.mark(1);
                String header = reader.readLine();
                if (header == null) {
                    throw new CsvReaderException(EMPTY_FILE_MESSAGE);
                }
                String[] actualHeaders = header.split(Pattern.quote(String.valueOf(config.getSeparator())));
                String[] expectedHeaders = generateExpectedHeaders();

                logger.info("Actual Header : {}", Arrays.toString(actualHeaders));
                logger.info("Expected Header : {}\n", Arrays.toString(expectedHeaders));

                if (actualHeaders.length != expectedHeaders.length || !compareHeaders(actualHeaders, expectedHeaders)) {
                    throw new CsvReaderException(CSV_HEADER_MISMATCH_MESSAGE);
                }
                reader.reset();
            } catch (IOException | CsvRequiredFieldEmptyException e) {
                throw new CsvReaderException(FILE_READ_ERROR_MESSAGE, e);
            }
        }
    }

    /**
     * Generates the expected headers based on the mapping strategy.
     *
     * @return The expected headers as an array.
     */
    private String[] generateExpectedHeaders() throws CsvRequiredFieldEmptyException {
        String[] headers;
        if (columnMappingAnnotationPresent() && mappingStrategy != null) {
            headers = this.mappingStrategy.generateHeader(ReflectionUtils.newInstanceOf(type));
        } else {
            headers = Arrays.stream(type.getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        }
        return headers;
    }

    /**
     * Compares the actual headers with the expected headers.
     *
     * @param actualHeaders   The actual headers read from the CSV file.
     * @param expectedHeaders The expected headers based on the mapping strategy.
     * @return True if the headers match, false otherwise.
     */
    private boolean compareHeaders(String[] actualHeaders, String[] expectedHeaders) {
        return Arrays.equals(actualHeaders, expectedHeaders);
    }

    /**
     * Checks if the provided mapping strategy requires position-based annotations and throws an exception
     * if the annotations are not present.
     *
     * @param mappingStrategy The mapping strategy to be checked.
     * @throws CsvReaderException If the mapping strategy is a ColumnPositionMappingStrategy
     *                            and the required position-based annotations are not present.
     */
    private void checkPositionAnnotations(MappingStrategy<T> mappingStrategy) throws CsvReaderException {
        if (mappingStrategy instanceof ColumnPositionMappingStrategy<?> && !positionAnnotationsPresent()) {
            throw new CsvReaderException("When using ColumnPositionMappingStrategy, " +
                    "CsvBindByPosition, CsvBindAndSplitByPosition, CsvBindAndJoinByPosition, " +
                    "or CsvCustomBindByPosition annotations must be used.");
        }
    }


    /**
     * Determines whether the current mapping strategy can be used based on the presence of annotations
     * in the bean class.
     *
     * @return {@code true} if the mapping strategy can be used, otherwise {@code false}.
     */
    private boolean columnMappingAnnotationPresent() {
        return positionAnnotationsPresent() || nameAnnotationsPresent();
    }

    /**
     * Checks if annotations related to position-based mapping are present in the bean class.
     *
     * @return {@code true} if position-based mapping annotations are present, otherwise {@code false}.
     */
    private boolean positionAnnotationsPresent() {
        return hasAnnotationPresent(POSITION_ANNOTATIONS);
    }


    /**
     * Checks if annotations related to name-based mapping are present in the bean class.
     *
     * @return {@code true} if name-based mapping annotations are present, otherwise {@code false}.
     */
    private boolean nameAnnotationsPresent() {
        return hasAnnotationPresent(NAME_ANNOTATIONS);
    }

    /**
     * Checks if any of the specified annotations are present in the bean class.
     *
     * @param annotations The list of annotation classes to check.
     * @return {@code true} if any of the specified annotations are present in the bean class, otherwise {@code false}.
     */
    private boolean hasAnnotationPresent(List<Class<? extends Annotation>> annotations) {
        return annotations.stream()
                .anyMatch(this::hasAnyAnnotation);
    }

    /**
     * Checks if the specified annotation is present in any of the fields of the bean class.
     *
     * @param annotation The annotation class to check.
     * @return {@code true} if the specified annotation is present in any of the fields of the bean class, otherwise {@code false}.
     */
    private boolean hasAnyAnnotation(Class<? extends Annotation> annotation) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(CsvIgnore.class))
                .anyMatch(field -> field.isAnnotationPresent(annotation));
    }
}
