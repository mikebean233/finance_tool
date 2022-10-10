package com.petersonlabs.personalfinancetool

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.CsvFileSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PersonalFinanceToolApplicationTests {

    @Test
    @CsvFileSource(resources = [""])
    fun contextLoads() {
    }

}
