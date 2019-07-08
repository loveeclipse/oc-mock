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
import utils.DiscoveryData.DISCOVERY_GET_SERVICE
import utils.DiscoveryData.HOST
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
                            .get(PORT, HOST, DISCOVERY_GET_SERVICE)
                            .send { findServiceLocation ->
                                if (findServiceLocation.succeeded()) {
                                    val response = findServiceLocation.result().body()
                                    val document = getData()
                                    client
                                            .patchAbs(response.toString())
                                            .sendJsonObject(document) { insertOperation ->
                                                when {
                                                    insertOperation.succeeded() ->
                                                        isFirst = false
                                                }
                                            }
                                }
                            }
                else ->
                    client
                            .get(PORT, HOST, DISCOVERY_GET_SERVICE)
                            .send { findServiceLocation ->
                                if (findServiceLocation.succeeded()) {
                                    val response = findServiceLocation.result().body()
                                    val document = getData()
                                    client
                                            .postAbs(response.toString())
                                            .sendJsonObject(document) { insertOperation ->
                                                when {
                                                    insertOperation.succeeded() ->
                                                        println("OK")
                                                    else ->
                                                        println("Error: ${insertOperation.cause()}")
                                                }
                                            }
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

    private fun getData(): JsonObject {
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
            dispatchCode.text.isEmpty() &&
                    dynamic.selectionModel.selectedItem == " " &&
                    !secondary.isSelected &&
                    primary.isSelected &&
                    patientsNumber.value == 0
                    && notes.text.isEmpty() ->
                return json {
                    obj(
                            CALL_TIME to ocCall.toString(),
                            ADDRESS to address.text,
                            SECONDARY to secondary.isSelected
                    )
                }
            dispatchCode.text.isNotEmpty() &&
                    dynamic.selectionModel.selectedItem != " " &&
                    !secondary.isSelected &&
                    primary.isSelected &&
                    patientsNumber.value != 0
                    && notes.text.isEmpty() ->
                return json {
                    obj(
                            CALL_TIME to ocCall.toString(),
                            ADDRESS to address.text,
                            DISPATCH_CODE to dispatchCode.text,
                            DYNAMIC to dynamic.selectionModel.selectedItem,
                            SECONDARY to secondary.isSelected,
                            PATIENT_NUMBER to patientsNumber.value
                    )
                }
            else ->
                return json {
                    obj(
                            CALL_TIME to ocCall.toString(),
                            ADDRESS to address.text
                    )
                }
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
    }
}