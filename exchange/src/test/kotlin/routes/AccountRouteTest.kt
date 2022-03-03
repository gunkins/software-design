package routes

import IntegrationTest
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gunkins.exchange.domain.Portfolio
import com.github.gunkins.exchange.domain.Position
import com.github.gunkins.exchange.domain.Share
import com.github.gunkins.exchange.domain.ShareQuantity
import com.github.gunkins.exchange.domain.TopUpBalanceRequest
import com.github.gunkins.exchange.domain.UserForm
import com.github.gunkins.exchange.domain.dao.ShareDao
import com.github.gunkins.exchange.domain.dao.UserAccountDao
import com.github.gunkins.exchange.domain.dao.UserShareDao
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import util.assertAccountsEquals
import util.assertPositionsEquals
import util.assertResponseContainsError
import util.insertAccountAndGet
import util.insertShare
import util.insertUserShare
import util.selectAllUsers
import java.math.BigDecimal
import kotlin.test.assertNotNull

class AccountRouteTest : IntegrationTest() {
    @Test
    fun `Register new account successfully`() {
        val fullName = "User Name"

        withApp {
            handleRequest(HttpMethod.Post, "/user/register") {
                setJsonBody(UserForm(fullName))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val users = selectAllUsers()
                assertEquals(1, users.size)
                assertEquals(fullName, users.single().fullName)
                assertTrue(BigDecimal.ZERO.compareTo(users.single().balance) == 0)
            }
        }
    }

    @Test
    fun `Getting portfolio of account without shares`() {
        val account = insertAccountAndGet("User Name", BigDecimal(1234))

        withApp {
            handleRequest(HttpMethod.Get, "/user/${account.id}/portfolio").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val portfolio = jacksonObjectMapper.readValue<Portfolio>(response.content!!)
                assertAccountsEquals(account, portfolio.account)
                assertEquals(0, portfolio.positions.size)
            }
        }
    }

    @Test
    fun `Getting portfolio of account with several shares`() {
        val account = insertAccountAndGet("User Name", BigDecimal(1234))
        val position1 = Position("BBG004S68758", 20, BigDecimal(44))
        val position2 = Position("BBG012YQ6P43", 9, BigDecimal(9999))
        insertShare(Share(position1.figi, position1.figi, position1.currentPrice, 0))
        insertShare(Share(position2.figi, position2.figi, position2.currentPrice, 0))
        insertUserShare(account.id, position1.figi, position1.quantity)
        insertUserShare(account.id, position2.figi, position2.quantity)

        withApp {
            handleRequest(HttpMethod.Get, "/user/${account.id}/portfolio").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val portfolio = jacksonObjectMapper.readValue<Portfolio>(response.content!!)
                assertAccountsEquals(account, portfolio.account)
                assertEquals(2, portfolio.positions.size)

                val figiToPosition = portfolio.positions.associateBy { it.figi }
                assertPositionsEquals(position1, figiToPosition[position1.figi])
                assertPositionsEquals(position2, figiToPosition[position2.figi])
            }
        }
    }

    @Test
    fun `Getting portfolio of non-existent account produces not found error`() {
        withApp {
            handleRequest(HttpMethod.Get, "/user/10/portfolio").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertResponseContainsError(response.content)
            }
        }
    }

    @Test
    fun `Top up balance successfully`() {
        val account = insertAccountAndGet("User Name", BigDecimal(1234))
        val toAdd = BigDecimal("100.12345")

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/top-up") {
                setJsonBody(TopUpBalanceRequest(toAdd))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val users = selectAllUsers()
                assertEquals(1, users.size)
                val expectedUserAccount = account.copy(balance = account.balance + toAdd)
                assertAccountsEquals(expectedUserAccount, users.single())
            }
        }
    }

    @Test
    fun `Buying shares successfully`() {
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(80), 50)
        insertShare(share)
        val account = insertAccountAndGet("User Name", BigDecimal(1000))
        val toBuy = 10L

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/buy") {
                setJsonBody(ShareQuantity(share.figi, toBuy))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val userShareDao by closestDI().instance<UserShareDao>()
                val userAccountDao by closestDI().instance<UserAccountDao>()
                val shareDao by closestDI().instance<ShareDao>()

                val actualUserShareQuantity = userShareDao.findUserShareQuantity(account.id, share.figi)
                assertEquals(toBuy, actualUserShareQuantity)

                val actualBalance = userAccountDao.find(account.id)?.balance
                val expectedBalance = account.balance - BigDecimal.valueOf(toBuy) * share.price
                assertEquals(0, expectedBalance.compareTo(actualBalance))

                val actualShareQuantity = shareDao.find(share.figi)?.available
                val expectedShareQuantity = share.available - toBuy
                assertEquals(expectedShareQuantity, actualShareQuantity)
            }
        }
    }

    @Test
    fun `Buying shares more than available produces bad request error`() {
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(100), 5)
        insertShare(share)
        val account = insertAccountAndGet("User Name", BigDecimal(1234))

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/buy") {
                setJsonBody(ShareQuantity(share.figi, 10))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertResponseContainsError(response.content)

                val userShareDao by closestDI().instance<UserShareDao>()
                val positions = userShareDao.findUserPositions(account.id)
                assertEquals(0, positions.size)
            }
        }
    }

    @Test
    fun `Buying shares without sufficient funds produces bad request error`() {
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(100), 100)
        insertShare(share)
        val account = insertAccountAndGet("User Name", BigDecimal(10))

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/buy") {
                setJsonBody(ShareQuantity(share.figi, 10))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertResponseContainsError(response.content)

                val userShareDao by closestDI().instance<UserShareDao>()
                val positions = userShareDao.findUserPositions(account.id)
                assertEquals(0, positions.size)
            }
        }
    }

    @Test
    fun `Selling share successfully`() {
        val account = insertAccountAndGet("User Name", BigDecimal(10))
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(100), 5)
        insertShare(share)
        val userShareQuantity = 15L
        insertUserShare(account.id, share.figi, userShareQuantity)
        val toSell = 10L

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/sell") {
                setJsonBody(ShareQuantity(share.figi, 10))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val userShareDao by closestDI().instance<UserShareDao>()
                val userAccountDao by closestDI().instance<UserAccountDao>()
                val shareDao by closestDI().instance<ShareDao>()

                val actualUserShareQuantity = userShareDao.findUserShareQuantity(account.id, share.figi)
                val expectedUserShareQuantity = userShareQuantity - toSell
                assertEquals(expectedUserShareQuantity, actualUserShareQuantity)

                val actualBalance = userAccountDao.find(account.id)?.balance
                val expectedBalance = account.balance + BigDecimal.valueOf(toSell) * share.price
                assertEquals(0, expectedBalance.compareTo(actualBalance))

                val actualShareQuantity = shareDao.find(share.figi)?.available
                val expectedShareQuantity = share.available + toSell
                assertEquals(expectedShareQuantity, actualShareQuantity)
            }
        }
    }

    @Test
    fun `Selling shares more than available produces bad request error`() {
        val account = insertAccountAndGet("User Name", BigDecimal(10))
        val share = Share("BBG004730RP0", "Hugo Boss", BigDecimal(100), 100)
        insertShare(share)
        val userShareQuantity = 5L
        insertUserShare(account.id, share.figi, userShareQuantity)

        withApp {
            handleRequest(HttpMethod.Post, "/user/${account.id}/sell") {
                setJsonBody(ShareQuantity(share.figi, 10))
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertResponseContainsError(response.content)

                val userShareDao by closestDI().instance<UserShareDao>()
                val actualQuantity = userShareDao.findUserShareQuantity(account.id, share.figi)
                assertEquals(userShareQuantity, actualQuantity)
            }
        }
    }
}