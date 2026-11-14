package io.github.bbsk.c5d

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class C5dApplication {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<C5dApplication>(*args)
		}
	}
}
