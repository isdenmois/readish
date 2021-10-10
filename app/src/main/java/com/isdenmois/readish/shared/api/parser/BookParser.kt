package com.isdenmois.readish.shared.api.parser

interface IBookParser {
    fun parse(): BookFile?
}

object BookParser {
    fun getParser(path: String): IBookParser {
        val lowPath = path.lowercase()

        if (lowPath.endsWith(".epub")) {
            return EPUBParser(path)
        }

        if (lowPath.endsWith(".fb2") || lowPath.endsWith(".fb2.zip")) {
            return FB2Parser(path)
        }

        return UnknownCoverParser()
    }
}

class UnknownCoverParser : IBookParser {
    override fun parse(): BookFile? {
        return null
    }
}
