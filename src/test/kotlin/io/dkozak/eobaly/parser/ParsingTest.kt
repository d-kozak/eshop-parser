package io.dkozak.eobaly.parser

import io.dkozak.eobaly.service.parseNameFromUrl
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ParsingTest {

    @Test
    fun category() {
        val url = "/produkty/prepravni-boxy-a-vika.htm"
        val expected = "prepravni-boxy-a-vika"
        val actual = parseNameFromUrl(url)
        assertEquals("Could not parse product category name", expected, actual)
    }

    @Test
    fun product() {
        val url = "https://www.eobaly.cz/krabice-balbox-200x200x150-3vvl-hh24b-klopova-fefco-0201.htm"
        val expected = "krabice-balbox-200x200x150-3vvl-hh24b-klopova-fefco-0201"
        val actual = parseNameFromUrl(url)
        assertEquals("Could not parse product name", expected, actual)
    }
}
