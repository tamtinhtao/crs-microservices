package vn.edu.crs.courseservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    // ID có thể null khi Client gửi Request tạo mới, nhưng sẽ có giá trị khi trả Response về
    private Long id;

    @NotBlank(message = "Tên môn học không được để trống")
    private String tenMonHoc;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ tối thiểu phải là 1")
    private Integer soTinChi;

    @NotNull(message = "Số chỗ tối đa không được để trống")
    @Min(value = 1, message = "Số chỗ tối đa phải từ 1 trở lên")
    private Integer soChoToiDa;

    //@NotNull(message = "Số chỗ còn lại không được để trống")
    //@Min(value = 0, message = "Số chỗ còn lại không được là số âm")
    private Integer soChoConLai;
}