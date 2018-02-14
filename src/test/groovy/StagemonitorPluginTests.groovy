import geb.Browser
import geb.junit4.GebReportingTest
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.junit.Before
import org.junit.Test

class StagemonitorPluginTests extends GebReportingTest {

    @Test
    void testTimingsPageload() {

        def browser = new Browser()
        browser.go "/"
        assert browser.$("h2").text() == "Welcome"

        sleep(10_000) // wait for stagemonitor to report span

        def slurper = new JsonSlurper()
        def result = slurper.parse("http://localhost:9200/stagemonitor-spans-*/_search?q=type:pageload".toURL())

        assert result.hits.total == 1
        assert result.hits.hits[0]._source."timing.unload" >= 0
        assert result.hits.hits[0]._source."timing.redirect" >= 0
        assert result.hits.hits[0]._source."timing.app_cache_lookup" >= 0
        assert result.hits.hits[0]._source."timing.dns_lookup" >= 0
        assert result.hits.hits[0]._source."timing.tcp" >= 0
        assert result.hits.hits[0]._source."timing.ssl" >= 0
        assert result.hits.hits[0]._source."timing.request" >= 0
        assert result.hits.hits[0]._source."timing.response" >= 0
        assert result.hits.hits[0]._source."timing.processing" >= 0
        assert result.hits.hits[0]._source."timing.load" >= 0
        assert result.hits.hits[0]._source."timing.time_to_first_paint" >= 0
        assert result.hits.hits[0]._source."timing.resource" >= 0
        assert result.hits.hits[0]._source."duration_ms" >= 0

    }

    @Before
    void clearSpans() {
        def http = new HTTPBuilder('http://localhost:9200/stagemonitor-spans-*')
        http.request(Method.DELETE, {})
    }

}
