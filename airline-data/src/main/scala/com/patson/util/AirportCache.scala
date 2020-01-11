package com.patson.util

import java.util.concurrent.TimeUnit

import com.patson.data.AirportSource
import com.patson.model._


object AirportCache {
  import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}

  val detailedCache: LoadingCache[Int, Option[Airport]] = CacheBuilder.newBuilder.maximumSize(2000).expireAfterAccess(10, TimeUnit.MINUTES).build(new DetailedLoader())
  val simpleCache: LoadingCache[Int, Option[Airport]] = CacheBuilder.newBuilder.maximumSize(2000).expireAfterAccess(10, TimeUnit.MINUTES).build(new SimpleLoader())

  def getAirport(airportId : Int, fullLoad : Boolean = false) : Option[Airport] = {
    if (fullLoad) {
      detailedCache.get(airportId)
    } else {
      simpleCache.get(airportId)
    }
  }

  def invalidateAirport(airportId : Int) = {
    detailedCache.invalidate(airportId)
    simpleCache.invalidate(airportId)
  }

  class DetailedLoader extends CacheLoader[Int, Option[Airport]] {
    override def load(airportId: Int) = {
      AirportSource.loadAirportById(airportId, true)
    }
  }

  class SimpleLoader extends CacheLoader[Int, Option[Airport]] {
    override def load(airportId: Int) = {
      AirportSource.loadAirportById(airportId, false)
    }
  }

}



