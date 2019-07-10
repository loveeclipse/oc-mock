package controller

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.control.Spinner
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import java.time.LocalDateTime
import javafx.scene.control.ButtonType
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Alert

import utils.DiscoveryData.REQUEST_EVENTS_LOCATION
import utils.DiscoveryData.HOST
import utils.DiscoveryData.REQUEST_MISSION_LOCATION
import utils.DiscoveryData.PORT

class OcMockController {
    @FXML lateinit var address: TextField
    @FXML lateinit var dispatchCode: TextField
    @FXML lateinit var dynamic: ComboBox<String>
    @FXML lateinit var primary: RadioButton
    @FXML lateinit var secondary: RadioButton
    @FXML lateinit var hotelBravoChopper: CheckBox
    @FXML lateinit var elimike: CheckBox
    @FXML lateinit var ambulance: CheckBox
    @FXML lateinit var patientsNumber: Spinner<Int>
    @FXML lateinit var notes: TextArea
    @FXML lateinit var confirmButton: Button

    private val radioButtonGroup = ToggleGroup()
    private lateinit var ocCall: LocalDateTime
    private var client = WebClient.create(Vertx.vertx())
    private var isFirst = true
    private lateinit var eventId: String

    fun initialization() {
        dynamic.items.addAll(" ", "Pedone investito", "Ciclista investito", "Motociclista", "Macchina-Moto",
                "Macchina-Macchina")
        hotelBravoChopper.isSelected = true
        radioButtonGroup.toggles.addAll(primary, secondary)
        primary.isSelected = true
        ocCall = getDatetime()
        dispatchCode.promptText = "SC01R"
        captureText(address)
        captureText(dispatchCode)
        captureText(notes)
        confirmButton.setOnMouseClicked {
            when {
                address.text.isEmpty() ->
                    alertMessage()
                isFirst ->
                    client
                            .get(PORT, HOST, REQUEST_EVENTS_LOCATION)
                            .send { findEventsLocation ->
                                if (findEventsLocation.succeeded()) {
                                    val eventLocationResponse = findEventsLocation.result().body()
                                    val eventDocument = getEventData()
                                    client
                                            .postAbs("$eventLocationResponse/events")
                                            .sendJsonObject(eventDocument) { insertOperation ->
                                                when {
                                                    insertOperation.succeeded() -> {
                                                        eventId = insertOperation.result().bodyAsString()
                                                        isFirst = false
                                                        client
                                                                .get(PORT, HOST, REQUEST_MISSION_LOCATION)
                                                                .send { findMissionsLocation ->
                                                                    if (findMissionsLocation.succeeded()) {
                                                                        val missionResponse = findMissionsLocation.result().body()
                                                                        val missionDocument = getMissionData()
                                                                        client
                                                                                .postAbs("$missionResponse/missions")
                                                                                .sendJsonObject(missionDocument) {}
                                                                    }
                                                                }
                                                    }
                                                }
                                            }
                                }
                            }
                else ->
                    client
                            .get(PORT, HOST, REQUEST_EVENTS_LOCATION)
                            .send { findServiceLocation ->
                                if (findServiceLocation.succeeded()) {
                                    val response = findServiceLocation.result().body()
                                    val document = getEventData()
                                    client
                                            .patchAbs("$response/events/$eventId")
                                            .sendJsonObject(document) {}
                                }
                            }
            }
        }
    }

    private fun getDatetime(): LocalDateTime {
        return LocalDateTime.now()
    }

    @FXML private fun captureText(textField: TextField) {
        textField.textProperty().addListener { _, _, newValue ->
            textField.text = newValue
        }
    }

    @FXML private fun captureText(textArea: TextArea) {
        textArea.textProperty().addListener { _, _, newValue ->
            textArea.text = newValue
        }
    }

    private fun alertMessage() {
        val alert = Alert(ERROR, "Inserire indirizzo!", ButtonType.CANCEL)
        alert.showAndWait()
    }

    private fun getEventData(): JsonObject {
        when {
            dispatchCode.text.isNotEmpty() &&
                    dynamic.selectionModel.selectedItem != " " &&
                    !secondary.isSelected &&
                    primary.isSelected &&
                    patientsNumber.value != 0
                    && notes.text.isNotEmpty() ->
                return json {
                    obj(
                            CALL_TIME to ocCall.toString(),
                            ADDRESS to address.text,
                            DISPATCH_CODE to dispatchCode.text,
                            DYNAMIC to dynamic.selectionModel.selectedItem,
                            SECONDARY to secondary.isSelected,
                            PATIENT_NUMBER to patientsNumber.value,
                            NOTES to notes.text
                    )
            }
            else ->
                return json {
                    obj(
                            CALL_TIME to ocCall.toString(),
                            ADDRESS to address.text,
                            SECONDARY to secondary.isSelected
                    )
                }
        }
    }

    private fun getMissionData(): JsonObject {
        return json {
            json { obj(
                    EVENT_ID to eventId,
                    VEHICLE to hotelBravoChopper.text
            ) }
        }
    }

    private companion object {
        private const val CALL_TIME = "callTime"
        private const val ADDRESS = "address"
        private const val DISPATCH_CODE = "dispatchCode"
        private const val DYNAMIC = "dynamic"
        private const val SECONDARY = "secondary"
        private const val PATIENT_NUMBER = "patientsNumber"
        private const val NOTES = "notes"
        private const val EVENT_ID = "eventId"
        private const val VEHICLE = "vehicle"
    }
}