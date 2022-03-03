package util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gunkins.exchange.domain.Account
import com.github.gunkins.exchange.domain.Position
import com.github.gunkins.exchange.domain.Share
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertNotNull

private val objectMapper = jacksonObjectMapper()

fun assertSharesEquals(expected: Share, actual: Share?) {
    assertNotNull(actual)
    assertEquals(expected.figi, actual.figi)
    assertEquals(expected.name, actual.name)
    assertEquals(expected.available, actual.available)
    assertTrue(expected.price.compareTo(actual.price) == 0)
}

fun assertAccountsEquals(expected: Account, actual: Account) {
    assertEquals(expected.id, actual.id)
    assertEquals(expected.fullName, actual.fullName)
    assertTrue(expected.balance.compareTo(actual.balance) == 0)
}

fun assertPositionsEquals(expected: Position, actual: Position?) {
    assertNotNull(actual)
    assertEquals(expected.figi, actual.figi)
    assertEquals(expected.quantity, actual.quantity)
    assertTrue(expected.currentPrice.compareTo(actual.currentPrice) == 0)
}

fun assertResponseContainsError(content: String?) {
    assertNotNull(content)
    assertNotNull(objectMapper.readValue<Map<String, Any>>(content)["error"])
}