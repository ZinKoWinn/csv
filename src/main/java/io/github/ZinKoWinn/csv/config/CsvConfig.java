package io.github.zinkowinn.csv.config;

import lombok.*;

/**
 * Configuration class for CSV parsing.
 * This class allows customization of various parameters used during CSV parsing.
 *
 * <p>Example usage:
 * <pre>{@code
 * CsvConfig config = CsvConfig.builder()
 *     .separator(',')
 *     .skipLines(1)
 *     .verifyReader(true)
 *     .quoteChar('"')
 *     .escapeChar('\\')
 *     .strictQuotes(false)
 *     .ignoreLeadingWhiteSpace(true)
 *     .ignoreQuotations(false)
 *     .throwsExceptions(true)
 *     .multilineLimit(null)
 *     .orderedResults(true)
 *     .ignoreEmptyLines(false)
 *     .lineEnd("\n")
 *     .hasHeader(true)
 *     .build();
 * }</pre>
 *
 * <p>All parameters are optional, and default values are provided for each parameter.
 * To customize the behavior, use the builder pattern provided by Lombok's {@code @Builder} annotation.
 * After setting the desired parameters, call the {@code build()} method to create an instance of {@code CsvConfig}.
 *
 * <p>Note: The default values are typically chosen to align with common CSV parsing behavior,
 * but they can be adjusted as needed for specific requirements.
 *
 * @author Zin Ko Winn
 * @since 1.0.0
 */

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PACKAGE)
public class CsvConfig {
    private char separator = ',';
    private Integer skipLines = 0;
    private Character quoteChar = '\u0000';
    private Character escapeChar = '\u0000';
    private Integer multilineLimit = null;
    private boolean ignoreLeadingWhiteSpace = false;
    private boolean ignoreQuotations = false;
    private boolean throwsExceptions = true;
    private boolean ignoreEmptyLines = false;
    private boolean orderedResults = true;
    private boolean strictQuotes = false;
    private boolean verifyReader = false;
    private boolean hasHeader = true;

    private String lineEnd = "\n";

    public CsvConfig separator(char separator) {
        this.separator = separator;
        return this;
    }

    public CsvConfig skipLines(Integer skipLines) {
        this.skipLines = skipLines;
        return this;
    }

    public CsvConfig verifyReader(boolean isVerifyReader) {
        this.verifyReader = isVerifyReader;
        return this;
    }

    public CsvConfig quoteChar(Character quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    public CsvConfig escapeChar(Character escapeChar) {
        this.escapeChar = escapeChar;
        return this;
    }

    public CsvConfig strictQuotes(boolean isStrictQuotes) {
        this.strictQuotes = isStrictQuotes;
        return this;
    }

    public CsvConfig ignoreLeadingWhiteSpace(boolean isIgnoreLeadingWhiteSpace) {
        this.ignoreLeadingWhiteSpace = isIgnoreLeadingWhiteSpace;
        return this;
    }

    public CsvConfig ignoreQuotations(boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
        return this;
    }

    public CsvConfig throwsExceptions(boolean isThrowsExceptions) {
        this.throwsExceptions = isThrowsExceptions;
        return this;
    }

    public CsvConfig multilineLimit(Integer multilineLimit) {
        this.multilineLimit = multilineLimit;
        return this;
    }

    public CsvConfig orderedResults(boolean orderedResults) {
        this.orderedResults = orderedResults;
        return this;
    }

    public CsvConfig ignoreEmptyLines(boolean isIgnoreEmptyLines) {
        this.ignoreEmptyLines = isIgnoreEmptyLines;
        return this;
    }

    public CsvConfig lineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

    public CsvConfig hasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        return this;
    }
}
