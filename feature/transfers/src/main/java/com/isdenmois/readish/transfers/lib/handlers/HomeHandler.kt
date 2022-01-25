package com.isdenmois.readish.transfers.lib.handlers

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD

class HomeHandler : RouterNanoHTTPD.DefaultHandler() {
    override fun getMimeType() = "text/plain"

    override fun getStatus() = NanoHTTPD.Response.Status.OK

    override fun getText() = "Everything works OK"
}
