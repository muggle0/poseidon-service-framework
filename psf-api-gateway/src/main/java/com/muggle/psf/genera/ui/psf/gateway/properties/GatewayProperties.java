package com.muggle.psf.genera.ui.psf.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * Date 2023/6/5
 * Created by muggle
 */
@ConfigurationProperties(prefix = "gateway.api")
@Configuration
@Data
public class GatewayProperties implements Serializable {

    private static final long serialVersionUID = -3884197454846021539L;

    private String openurl;

    private List<String> excludeurls;

    private String signature;

    private String
}
