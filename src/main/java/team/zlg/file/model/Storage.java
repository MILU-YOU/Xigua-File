package team.zlg.file.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "storage")
@Entity
@TableName("storage")
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long storageId;

    @Column(columnDefinition="bigint(20)")
    private Long userId;

    @Column(columnDefinition="bigint(20)")
    private Long storageSize;
}
