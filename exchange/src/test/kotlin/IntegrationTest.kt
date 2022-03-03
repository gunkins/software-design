import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.gunkins.exchange.appModule
import com.github.gunkins.exchange.config.DependencyConfig
import com.github.gunkins.exchange.domain.dao.Shares
import com.github.gunkins.exchange.domain.dao.UserAccounts
import com.github.gunkins.exchange.domain.dao.UserShares
import com.github.gunkins.exchange.domain.service.InvestApiService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import ru.tinkoff.piapi.core.InvestApi

abstract class IntegrationTest {

    @AfterEach
    fun afterEach() {
        clearAllMocks()
        transaction {
            db.dialect.allTablesNames().forEach {
                exec("truncate table $it cascade")
            }
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:12").apply {
            waitingFor(Wait.defaultWaitStrategy())
            start()
        }

        val investApi = mockk<InvestApi>()
        val investApiService = mockk<InvestApiService>(relaxed = true)
        val jacksonObjectMapper = jacksonObjectMapper()

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Database.connect(
                container.jdbcUrl,
                driver = "org.postgresql.Driver",
                user = container.username,
                password = container.password
            )
            transaction {
                SchemaUtils.create(Shares, UserAccounts, UserShares)
            }
        }

        fun <R> withApp(test: TestApplicationEngine.() -> R) = withTestApplication({ testModule() }) {
            test.invoke(this)
        }

        private fun Application.testModule() {
            val di = DI {
                import(DependencyConfig.mainModule())
                bind<InvestApi>() with singleton { investApi }
                bind<InvestApiService>(overrides = true) with singleton { investApiService }
            }
            appModule(di)
        }

        fun <T> TestApplicationRequest.setJsonBody(body: T) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(jacksonObjectMapper.writeValueAsString(body))
        }
    }
}