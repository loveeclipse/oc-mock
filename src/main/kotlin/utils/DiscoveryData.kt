package utils

object DiscoveryData {

    const val PORT = 5150
    const val HOST = "localhost"

    private const val DISCOVERY_BASE_PATH = "/discovery"
    const val DISCOVERY_GET_SERVICE = "$DISCOVERY_BASE_PATH/discover/events-service"
}