package com.github.gunkins.exchange.config

import com.github.gunkins.exchange.domain.dao.ShareDao
import com.github.gunkins.exchange.domain.dao.UserAccountDao
import com.github.gunkins.exchange.domain.dao.UserShareDao
import com.github.gunkins.exchange.domain.service.InvestApiService
import com.github.gunkins.exchange.domain.service.ShareService
import com.github.gunkins.exchange.domain.service.UserAccountService
import com.github.gunkins.exchange.domain.service.UserShareService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import ru.tinkoff.piapi.core.InvestApi

object DependencyConfig {
    fun mainModule(): DI.Module {
        return DI.Module("MainModule", false) {
            bind<ShareDao>() with singleton { ShareDao() }
            bind<UserAccountDao>() with singleton { UserAccountDao() }
            bind<UserShareDao>() with singleton { UserShareDao() }

            bind<InvestApiService>() with singleton { InvestApiService(instance(), instance()) }

            bind<ShareService>() with singleton { ShareService(instance(), instance()) }
            bind<UserAccountService>() with singleton { UserAccountService(instance()) }
            bind<UserShareService>() with singleton { UserShareService(instance(), instance(), instance()) }
        }
    }

    fun investApiClient(token: String): DI.Module {
        return DI.Module("InvestApiClientModule", false) {
            bind<InvestApi>() with singleton { InvestApi.createReadonly(token) }
        }
    }
}