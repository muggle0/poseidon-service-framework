package com.muggle.psf.admin.entity;

import com.muggle.psf.common.base.BaseBean;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author muggle
 * @since 2023-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="PsfSecret对象", description="")
public class PsfSecret extends BaseBean {

    private static final long serialVersionUID = 1L;

    private String appId;

    private String nonce;

    private String appSecret;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String creator;

    private String updater;


}
