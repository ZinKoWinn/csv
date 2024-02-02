package io.github.zinkowinn.csv.core;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import io.github.zinkowinn.csv.mapper.ColumnMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of ColumnMapper interface.
 * This class provides functionality to retrieve column names from bean fields
 * based on OpenCSV annotations.
 *
 * @param <T> The type of the bean for which column names are retrieved.
 * @author by Zin Ko Winn
 * @since 1.0.0
 */

public class DefaultColumnMapper<T> implements ColumnMapper<T> {
    /**
     * Retrieves column names from bean fields based on OpenCSV annotations.
     *
     * @param clazz The class type of the bean.
     * @return A list of column names extracted from bean fields.
     */
    @Override
    public List<String> getColumnNames(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(CsvIgnore.class))
                .map(field -> {
                    if (field.isAnnotationPresent(CsvBindByName.class)) {
                        CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                        return annotation.column();
                    } else if (field.isAnnotationPresent(CsvCustomBindByName.class)) {
                        CsvCustomBindByName annotation = field.getAnnotation(CsvCustomBindByName.class);
                        return annotation.column();
                    } else {
                        return field.getName();
                    }
                })
                .collect(Collectors.toList());
    }
}
