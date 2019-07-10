package controller

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import utils.DiscoveryData.PORT
import utils.DiscoveryData.HOST
import utils.DiscoveryData.REQUEST_EVENTS_LOCATION
import utils.DiscoveryData.REQUEST_MISSION_LOCATION

object RequestManager {

    private var client = WebClient.create(Vertx.vertx())
    private lateinit var eventId: String
    private var isFirst = true

    fun createMissionAndEvent(missionData: JsonObject, eventData: JsonObject) {
        client
                .get(PORT, HOST, REQUEST_EVENTS_LOCATION)
                .send { findEventsLocation ->
                    if (findEventsLocation.succeeded()) {
                        val eventLocationResponse = findEventsLocation.result().body()
                        client
                                .postAbs("$eventLocationResponse/events")
                                .sendJsonObject(eventData) { insertOperation ->
                                    when {
                                        insertOperation.succeeded() -> {
                                            eventId = insertOperation.result().bodyAsString()
                                            isFirst = false
                                            client
                                                    .get(PORT, HOST, REQUEST_MISSION_LOCATION)
                                                    .send { findMissionsLocation ->
                                                        if (findMissionsLocation.succeeded()) {
                                                            val missionResponse = findMissionsLocation.result().body()
                                                            client
                                                                    .postAbs("$missionResponse/missions")
                                                                    .sendJsonObject(missionData) {}
                                                        }
                                                    }
                                        }
                                    }
                                }
                    }
                }
    }

    fun updateMission(eventData: JsonObject) {
        client
                .get(PORT, HOST, REQUEST_EVENTS_LOCATION)
                .send { findServiceLocation ->
                    if (findServiceLocation.succeeded()) {
                        val response = findServiceLocation.result().body()
                        client
                                .patchAbs("$response/events/eventId")
                                .sendJsonObject(eventData) {}
                    }
                }
    }

    fun getIsFirst(): Boolean {
        return isFirst
    }

    fun getEventId(): String {
        return eventId
    }
}