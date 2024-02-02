package io.github.zinkowinn.csv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zin Ko Winn
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithNoPositionAnnotation {
    private String name;
    private String age;
}
