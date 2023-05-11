package team.zlg.file.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Table(name = "file")
@Entity
@TableName("file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="bigint(20) comment '文件id'")
    @TableId(type = IdType.AUTO)
    private Long fileId;

    @Column(columnDefinition="varchar(500) comment '时间戳名称'")
    private String timeStampName;

    @Column(columnDefinition="varchar(500) comment '文件url'")
    private String fileUrl;

    @Column(columnDefinition="bigint(10) comment '文件大小'")
    private Long fileSize;

    @Column(columnDefinition="int(1) comment '存储类型 0-本地存储, 1-阿里云存储, 2-FastDFS存储'")
    private Integer storageType;

    //保存文件的 md5 唯一标识，这个唯一标识是文件极速秒传的关键，当检测上传文件的 md5 已存在，则文件已存在于服务器，文件直接返回上传成功。
    @Column(columnDefinition="varchar(32) comment 'md5唯一标识'")
    private String identifier;

    //pointCount 用来保存文件的引用数量，当上传文件在服务器已存在，则 pointCount 加 1，文件删除的时候减 1，此时如果引用数量大于 0，则文件逻辑删除，等于 0 时文件需要彻底物理删除
    @Column(columnDefinition="int(1) comment '引用数量'")
    private Integer pointCount;

    @Column(columnDefinition="varchar(500) comment '文件密钥'")
    private String fileKey;

    @Column(columnDefinition = "varchar(500)  comment '初始化向量'")
    private String IV;

}