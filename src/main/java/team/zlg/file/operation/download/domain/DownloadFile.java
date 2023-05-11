package team.zlg.file.operation.download.domain;

import lombok.Data;

@Data
public class DownloadFile {
    private String fileUrl;
    private String timeStampName;
    private String key;
    private String iv;
}
