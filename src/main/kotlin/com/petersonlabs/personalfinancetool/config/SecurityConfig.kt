package com.petersonlabs.personalfinancetool.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

@Configuration
class SecurityConfig : SecurityFilterChain {
    override fun matches(request: HttpServletRequest?): Boolean {
        return true
    }

    override fun getFilters(): MutableList<Filter> {
        return mutableListOf()
    }
}
