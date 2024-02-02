package io.github.zinkowinn.csv.mapper;

import java.util.List;

/**
 * @author by Zin Ko Winn
 */

@FunctionalInterface
public interface ColumnMapper<T> {
    List<String> getColumnNames(Class<T> clazz);
}
