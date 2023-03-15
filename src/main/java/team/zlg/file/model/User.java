package team.zlg.file.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "user")
@Entity
@TableName("user")
@ApiModel("用户")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint(20) comment '用户id'")
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("用户id")
    private Long userId;

    @Column(columnDefinition = "varchar(30) comment '用户名'")
    @ApiModelProperty("用户名")
    private String username;

    @Column(columnDefinition = "varchar(35) comment '密码'")
    @ApiModelProperty("密码")
    private String password;

    @Column(columnDefinition = "varchar(15) comment '手机号码'")
    @ApiModelProperty("手机号码")
    private String telephone;

    @Column(columnDefinition = "varchar(20) comment '盐值'")
    @ApiModelProperty("盐值")
    private String salt;

    @Column(columnDefinition = "varchar(30) comment '注册时间'")
    @ApiModelProperty("注册时间")
    private String registerTime;

}