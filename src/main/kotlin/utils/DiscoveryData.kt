package utils

object DiscoveryData {

    const val PORT = 5150
    const val HOST = "localhost"

    private const val DISCOVERY_BASE_PATH = "/discovery"
    private const val DISCOVERY_GET_SERVICE = "$DISCOVERY_BASE_PATH/discover"
    const val REQUEST_EVENTS_LOCATION = "$DISCOVERY_GET_SERVICE/events-service"
    const val REQUEST_MISSION_LOCATION = "$DISCOVERY_GET_SERVICE/missions-service"
}