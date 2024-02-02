package io.github.zinkowinn.csv.core.strategy;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.zinkowinn.csv.core.DefaultColumnMapper;
import io.github.zinkowinn.csv.mapper.ColumnMapper;

/**
 * Custom column mapping strategy for CSV beans.
 * This strategy extends the ColumnPositionMappingStrategy provided by OpenCSV
 * to customize header generation based on a provided ColumnMapper.
 *
 * @param <T> The type of the bean being mapped.
 * @author by Zin Ko Winn
 * @since 1.0.0
 */

public class DefaultColumnMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
    private final ColumnMapper<T> columnMapper;
    private final Class<T> type;

    /**
     * Constructs a DefaultColumnMappingStrategy instance.
     *
     * @param type The class type of the bean being mapped.
     */
    public DefaultColumnMappingStrategy(Class<T> type) {
        this.columnMapper = new DefaultColumnMapper<>();
        this.type = type;
        setType(type);
    }

    /**
     * Generates the header based on the provided bean.
     * Overrides the super class method to customize header generation.
     *
     * @param bean The bean instance from which to generate the header.
     * @return An array of header strings.
     * @throws CsvRequiredFieldEmptyException If a required field is found empty.
     */
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] header = super.generateHeader(bean);
        final int numColumns = headerIndex.findMaxIndex();
        if (numColumns == -1) {
            return header;
        }
        header = this.columnMapper.getColumnNames(this.type).toArray(new String[numColumns]);
        return header;
    }
}
