package org.http4k.servirtium

import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response

interface InteractionOptions {
    fun modify(request: Request): Request = request
    fun modify(response: Response): Response = response
    fun isBinary(contentType: ContentType): Boolean = false

    companion object {
        object Defaults : InteractionOptions
    }
}
