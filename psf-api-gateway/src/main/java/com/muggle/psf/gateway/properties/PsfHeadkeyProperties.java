package com.muggle.psf.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * Created by muggle
 */
@ConfigurationProperties(prefix = "gateway.api.header")
@Configuration
@Data
public class PsfHeadkeyProperties implements Serializable {

    private static final long serialVersionUID = -3884197454846021539L;

    private String openurl;

    private List<String> excludeurls;

    private String signature;

    private String nonce;

    private String appid;

    private String appsecret;

    private String timestamp;
}
