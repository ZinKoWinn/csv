package io.github.zinkowinn.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zin Ko Winn
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @CsvBindByName(column = "name")
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByName(column = "age")
    @CsvBindByPosition(position = 1)
    private String age;
}
