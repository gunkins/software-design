package routes

import IntegrationTest
import com.github.gunkins.exchange.domain.InvestApiShare
import com.github.gunkins.exchange.domain.Share
import com.github.gunkins.exchange.domain.ShareQuantity
import com.github.gunkins.exchange.domain.dao.ShareDao
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import util.assertResponseContainsError
import util.assertSharesEquals
import util.insertShare
import java.math.BigDecimal
import java.util.*

class AdminRouteTest : IntegrationTest() {
    @Test
    fun `Adding share with invest api price`() {
        val share = Share("BBG004730RP0", "Gazprom", BigDecimal(190), 20)

        every { investApiService.getShare(any()) } returns InvestApiShare(share.name, "RUB")
        every { investApiService.getSharePrice(any()) } returns share.price

        withApp {
            handleRequest(HttpMethod.Post, "/admin/add-share") {
                setJsonBody(ShareQuantity(share.figi, share.available))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val shareDao by closestDI().instance<ShareDao>()
                val shares = shareDao.findAll()
                assertEquals(1, shares.size)
                assertSharesEquals(share, shares.single())
            }
        }

        verify(exactly = 1) {
            investApiService.subscribeOnAddedFigiPriceUpdate()
            investApiService.subscribeOnPriceUpdate(share.figi)
            investApiService.getShare(share.figi)
            investApiService.getSharePrice(share.figi)
        }
        confirmVerified(investApiService)
    }

    @Test
    fun `Adding euro share produces bad request`() {
        val figi = "BBG000BD0GG5"
        val share = Share(figi, "Hugo Boss", BigDecimal(100), 5)

        every { investApiService.getShare(any()) } returns InvestApiShare(share.name, "EUR")
        every { investApiService.getSharePrice(any()) } returns share.price

        withApp {
            handleRequest(HttpMethod.Post, "/admin/add-share") {
                setJsonBody(ShareQuantity(share.figi, share.available))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertResponseContainsError(response.content)

                val shareDao by closestDI().instance<ShareDao>()
                val shares = shareDao.findAll()
                assertTrue(shares.isEmpty())
            }
        }

        verify(exactly = 1) {
            investApiService.getShare(figi)
        }
    }

    @Test
    fun `Adding quantity to existent share`() {
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(100), 5)
        insertShare(share)
        val toAdd = 10L

        withApp {
            handleRequest(HttpMethod.Post, "/admin/add-quantity") {
                setJsonBody(ShareQuantity(share.figi, toAdd))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val shareDao by closestDI().instance<ShareDao>()
                val shares = shareDao.findAll()
                val expectedShare = share.copy(available = share.available + toAdd)
                assertEquals(1, shares.size)
                assertSharesEquals(expectedShare, shares.single())
            }
        }
    }

    @Test
    fun `Adding quantity to non-existent share and getting not found`() {
        withApp {
            handleRequest(HttpMethod.Post, "/admin/add-quantity") {
                setJsonBody(ShareQuantity("BBG000BD0GG5", 10))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertResponseContainsError(response.content)
            }
        }
    }
}