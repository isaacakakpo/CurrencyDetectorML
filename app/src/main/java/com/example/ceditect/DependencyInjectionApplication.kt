package com.example.ceditect

import android.app.Application
import com.example.ceditect.network.MyApi
import com.example.ceditect.network.NetworkConnectionInterceptor
import com.example.ceditect.network.QuotesRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


class DependencyInjectionApplication: Application(),KodeinAware {

    override val kodein = Kodein.lazy {

        import(androidXModule(this@DependencyInjectionApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { QuotesRepository(instance()) }


    }





}