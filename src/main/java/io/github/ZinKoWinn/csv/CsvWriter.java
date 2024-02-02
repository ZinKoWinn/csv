package io.github.zinkowinn.csv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.zinkowinn.csv.config.CsvConfig;
import io.github.zinkowinn.csv.core.DefaultDataTransformer;
import io.github.zinkowinn.csv.core.strategy.DefaultColumnMappingStrategy;
import io.github.zinkowinn.csv.exceptions.CsvWriterException;
import io.github.zinkowinn.csv.mapper.DataTransformer;
import lombok.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * CsvWriter is a utility class to write data from Java objects into CSV files.
 *
 * @param <T> The type of Java object to be written into CSV.
 * @author Zin Ko Winn
 * @since 1.0.0
 */

@Data
public class CsvWriter<T> {

    private CsvConfig config;
    private DataTransformer<T> dataTransformer;
    private MappingStrategy<T> mappingStrategy;
    private Class<T> type;

    /**
     * Private constructor to initialize CsvWriter with the target class type.
     *
     * @param type The class type of the objects to be written into CSV.
     */
    private CsvWriter(Class<T> type) {
        this.type = type;
        this.config = new CsvConfig();
        this.dataTransformer = new DefaultDataTransformer<>();
        this.mappingStrategy = new DefaultColumnMappingStrategy<>(type);
    }

    /**
     * Factory method to create an instance of CsvWriter with the specified class type.
     *
     * @param <T>  The type of Java object to be written into CSV.
     * @param type The class type of the objects to be written into CSV.
     * @return An instance of CsvWriter configured to handle objects of type T.
     */
    public static <T> CsvWriter<T> of(Class<T> type) {
        return new CsvWriter<>(type);
    }

    /**
     * Configures CsvWriter with custom settings using a builder function.
     *
     * @param builder A unary operator function to modify the CsvConfig settings.
     * @return The CsvWriter instance with updated configuration.
     */
    public CsvWriter<T> config(UnaryOperator<CsvConfig> builder) {
        this.config = builder.apply(this.config);
        return this;
    }

    /**
     * Sets a custom data transformer for CsvWriter.
     *
     * @param transformer The data transformer to be used.
     * @return The CsvWriter instance with the specified data transformer.
     */
    public CsvWriter<T> transform(DataTransformer<T> transformer) {
        this.dataTransformer = transformer;
        return this;
    }

    /**
     * Sets a custom mapping strategy for CsvWriter.
     *
     * @param mappingStrategy The mapping strategy to be used.
     * @return The CsvWriter instance with the specified mapping strategy.
     */
    public CsvWriter<T> mappingStrategy(MappingStrategy<T> mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
        return this;
    }

    /**
     * Writes CSV data to a file.
     *
     * @param filePath The path to the CSV file.
     * @param data     The list of Java objects to be written into CSV.
     * @throws CsvWriterException If an error occurs during CSV writing.
     */
    public void writeCSV(String filePath, List<T> data) {
        try (Writer writer = new FileWriter(filePath)) {
            writeData(writer, data);
        } catch (IOException e) {
            throw new CsvWriterException("Error writing CSV file: " + filePath, e);
        }
    }

    /**
     * Writes CSV data to an output stream.
     *
     * @param outputStream The output stream to write CSV data to.
     * @param data         The list of Java objects to be written into CSV.
     * @throws CsvWriterException If an error occurs during CSV writing.
     */
    public void writeCSV(OutputStream outputStream, List<T> data) {
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writeData(writer, data);
        } catch (IOException e) {
            throw new CsvWriterException("Error writing CSV to output stream", e);
        }
    }

    /**
     * Writes CSV data to a byte array.
     *
     * @param data The list of Java objects to be written into CSV.
     * @return A byte array containing the CSV data.
     * @throws CsvWriterException If an error occurs during CSV writing.
     */
    public byte[] writeCSV(List<T> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

            writeData(writer, data);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new CsvWriterException("Error writing CSV data to byte array", e);
        }
    }

    /**
     * Writes CSV data to a Base64 encoded string.
     *
     * @param data The list of Java objects to be written into CSV.
     * @return A Base64 encoded string containing the CSV data.
     * @throws CsvWriterException If an error occurs during CSV writing.
     */
    public String writeCSVToBase64(List<T> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

            writeData(writer, data);

            byte[] byteArray = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);

        } catch (IOException e) {
            throw new CsvWriterException("Error writing CSV data to Base64", e);
        }
    }

    /**
     * Writes CSV data using the provided writer.
     *
     * @param writer The writer to write CSV data to.
     * @param data   The list of Java objects to be written into CSV.
     */
    private void writeData(Writer writer, List<T> data) {
        try (CSVWriter csvWriter = new CSVWriter(writer,
                config.getSeparator(),
                config.getQuoteChar(),
                config.getEscapeChar(),
                config.getLineEnd())) {

            StatefulBeanToCsv<T> statefulBeanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                    .withSeparator(config.getSeparator())
                    .withQuotechar(config.getQuoteChar())
                    .withEscapechar(config.getEscapeChar())
                    .withLineEnd(config.getLineEnd())
                    .withMappingStrategy(this.mappingStrategy)
                    .build();

            List<T> transformedData = data.stream()
                    .map(dataTransformer::transform)
                    .collect(Collectors.toList());

            statefulBeanToCsv.write(transformedData);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            throw new CsvWriterException("Error writing CSV data", e);
        }
    }
}
