package com.petersonlabs.personalfinancetool

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PersonalFinanceToolApplication

fun main(args: Array<String>) {
    runApplication<PersonalFinanceToolApplication>(*args)
}
