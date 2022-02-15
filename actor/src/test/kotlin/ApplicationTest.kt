import akka.actor.typed.ActorSystem
import akka.actor.typed.SpawnProtocol
import com.github.gunkins.client.OrganicResult
import com.github.gunkins.client.RequestInfo
import com.github.gunkins.client.SearchResponse
import com.github.gunkins.client.SerpClient
import com.github.gunkins.client.SerpEngine
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gunkins.appModule
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val QUERY = "pasta carbonara recipe"
private const val RESULTS_PER_ENGINE = 5

fun Application.testModule(client: SerpClient, actorSystem: ActorSystem<SpawnProtocol.Command>) {
    (environment.config as MapApplicationConfig).apply {
        put("results-per-engine", RESULTS_PER_ENGINE.toString())
        put("actor.master.receive.timeout-ms", "300")
        put("actor.spawn.timeout-ms", "300")
    }
    appModule(client, actorSystem)
}

class ApplicationTest {
    private val objectMapper = jacksonObjectMapper()
    private val serpClient = mockk<SerpClient>()
    private val actorSystem: ActorSystem<SpawnProtocol.Command> = ActorSystem.create(SpawnProtocol.create(), "spawn")

    @Test
    fun `Every serp request returns number of links less than the limit`() {
        val engineToResponse = SerpEngine.values().associateWith { generateResponse(it, 3) }
        coEvery { serpClient.search(any(), any()) } answers { engineToResponse.getValue(firstArg()) }

        withTestApplication({ testModule(serpClient, actorSystem) }) {
            handleRequest(HttpMethod.Get, "/search?query=$QUERY").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)

                val expectedResponse = engineToResponse.values.flatMap { it.organicResults }
                val actualResponse: List<OrganicResult> = objectMapper.readValue(content)
                assertThat(actualResponse)
                    .containsExactlyInAnyOrderElementsOf(expectedResponse)
            }
        }

        verifySearchCalledOncePerEngine(QUERY)
    }

    @Test
    fun `Serp requests return number of links more than the limit`() {
        val engineToResponse = SerpEngine.values().associateWith { generateResponse(it, 12) }
        coEvery { serpClient.search(any(), any()) } answers { engineToResponse.getValue(firstArg()) }

        withTestApplication({ testModule(serpClient, actorSystem) }) {
            handleRequest(HttpMethod.Get, "/search?query=$QUERY").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)

                val expectedResponse = engineToResponse.values.flatMap { it.organicResults.take(RESULTS_PER_ENGINE) }
                val actualResponse: List<OrganicResult> = objectMapper.readValue(content)
                assertThat(actualResponse)
                    .containsExactlyInAnyOrderElementsOf(expectedResponse)
            }
        }

        verifySearchCalledOncePerEngine(QUERY)
    }

    @Test
    fun `One of serp requests exceeds timeout limit`() {
        val googleResponse = generateResponse(SerpEngine.GOOGLE, 3)
        val bingResponse = generateResponse(SerpEngine.BING, 5)
        val yandexResponse = generateResponse(SerpEngine.YANDEX, 10)
        coEvery { serpClient.search(SerpEngine.GOOGLE, any()) } returns googleResponse
        coEvery { serpClient.search(SerpEngine.BING, any()) } returns bingResponse
        coEvery { serpClient.search(SerpEngine.YANDEX, any()) } answers {
            Thread.sleep(1000)
            yandexResponse
        }

        withTestApplication({ testModule(serpClient, actorSystem) }) {
            handleRequest(HttpMethod.Get, "/search?query=$QUERY").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)

                val expectedResponse = googleResponse.organicResults + bingResponse.organicResults
                val actualResponse: List<OrganicResult> = objectMapper.readValue(content)
                assertThat(actualResponse)
                    .containsExactlyInAnyOrderElementsOf(expectedResponse)
            }
        }

        verifySearchCalledOncePerEngine(QUERY)
    }

    @Test
    fun `Every serp request exceeds timeout limit`() {
        coEvery { serpClient.search(any(), any()) } answers {
            Thread.sleep(1000)
            generateResponse(firstArg(), 10)
        }

        withTestApplication({ testModule(serpClient, actorSystem) }) {
            handleRequest(HttpMethod.Get, "/search?query=$QUERY").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)

                val actualResponse: List<OrganicResult> = objectMapper.readValue(content)
                assertThat(actualResponse).isEmpty()
            }
        }

        verifySearchCalledOncePerEngine(QUERY)
    }

    private fun generateResponse(engine: SerpEngine, count: Int): SearchResponse {
        val organicResults = (1..count).map {
            OrganicResult(
                "Title$it$engine",
                "https://link$it.${engine.code}"
            )
        }
        return SearchResponse(RequestInfo(success = true), organicResults)
    }

    private fun verifySearchCalledOncePerEngine(query: String) {
        coVerify(exactly = 1) {
            SerpEngine.values().forEach {
                serpClient.search(it, query)
            }
        }
        confirmVerified(serpClient)
    }
}