package com.isdenmois.readish.transfers.lib

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse

object FixedResponse {
    private val JSON = "application/json"

    fun success() = newFixedLengthResponse(
        NanoHTTPD.Response.Status.OK,
        JSON,
        "{ \"OK\": true }",
    )

    fun badRequest(error: String) = newFixedLengthResponse(
        NanoHTTPD.Response.Status.BAD_REQUEST,
        JSON,
        "{ \"message\": \"$error\" }",
    )
}
