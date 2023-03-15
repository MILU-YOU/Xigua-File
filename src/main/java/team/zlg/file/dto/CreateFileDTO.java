package team.zlg.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "创建文件DTO",required = true)
@Data
public class CreateFileDTO {
    @Schema(description="文件名")
    private String fileName;
    @Schema(description="文件路径")
    private String filePath;
}
