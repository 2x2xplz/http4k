package org.http4k.testing

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.traffic.ReadWriteStream
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError

class ExtensionTests {
    private val request = Request(GET, "/")
    private val response = Response(OK)
    private val stream = ReadWriteStream.Memory(mutableListOf(request to response))
    private val http = stream.replayingMatchingContent()

    @Test
    fun `replayingMatchingContent replays matching content ok`() {
        assertThat(http(request), equalTo(response))
    }

    @Test
    fun `replayingMatchingContent ignores headers which were not in stream`() {
        assertThat(http(request.header("foo", "bar")), equalTo(response))
    }

    @Test
    fun `replayingMatchingContent blows up with non-matching request`() {
        assertThat({ http(Request(GET, "w")) }, throws(has(
            AssertionFailedError::getLocalizedMessage,
            containsSubstring("Unexpected request received for Interaction 0 ==>")
        )))
    }

    @Test
    fun `replayingMatchingContent blows up when more requests than interactions`() {
        assertThat(http(request), equalTo(response))
        assertThat({ http(request) }, throws(has(
            AssertionFailedError::getLocalizedMessage,
            containsSubstring("Unexpected request received for Interaction 0 ==>")
        )))
    }

}
