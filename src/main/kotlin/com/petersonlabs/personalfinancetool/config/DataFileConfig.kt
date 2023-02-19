package com.petersonlabs.personalfinancetool.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("finance-tool.data-file-config")
@EnableConfigurationProperties(DataFileConfig::class)
data class DataFileConfig(
    var root: String = ""
)
