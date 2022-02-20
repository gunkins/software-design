package routes

import IntegrationTest
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gunkins.exchange.domain.Share
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import util.assertResponseContainsError
import util.assertSharesEquals
import util.insertShare
import java.math.BigDecimal

class ShareRouteTest : IntegrationTest() {
    @Test
    fun `Get all shares`() {
        val shares = listOf(
            Share("BBG004S68758", "Башнефть", BigDecimal("22.3"), 120),
            Share("BBG012YQ6P43", "Циан", BigDecimal("4"), 5000),
            Share("BBG000BN56Q9", "Детский мир", BigDecimal("399"), 99),
        )

        shares.forEach { insertShare(it) }

        withApp {
            handleRequest(HttpMethod.Get, "/share/all").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val responseShares = jacksonObjectMapper.readValue<List<Share>>(response.content!!)
                    .associateBy { it.figi }
                assertEquals(3, responseShares.size)

                shares.forEach { expected ->
                    assertSharesEquals(expected, responseShares[expected.figi])
                }
            }
        }
    }

    @Test
    fun `Get existent share by figi`() {
        val share = Share("BBG004S68758", "Башнефть", BigDecimal("22.3"), 120)
        insertShare(share)

        withApp {
            handleRequest(HttpMethod.Get, "/share/by-figi?figi=${share.figi}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val responseShare = jacksonObjectMapper.readValue<Share>(response.content!!)
                assertSharesEquals(share, responseShare)
            }
        }
    }

    @Test
    fun `Get non-existent share by figi`() {
        withApp {
            handleRequest(HttpMethod.Get, "/share/by-figi?figi=BBG004S68758").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertResponseContainsError(response.content)
            }
        }
    }
}