package edu.kit.ifv.populationsynthesis.input

import java.io.InputStream

/**
 * Using the resource folder with the class loader is a bit more convenient than to pass a path.
 */

internal fun resourceStream(owner: Class<*>, name: String): InputStream =
    requireNotNull(owner.classLoader.getResourceAsStream(name)) {
        "Resource not found on classpath: $name"
    }

internal inline fun <reified T> parseResource(owner: Class<*>, resourceName: String): List<T> =
    resourceStream(owner, resourceName).use { standardParse<T>(it) }