package controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.control.Spinner
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup

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

    fun initialization() {
        dynamic.items.addAll(" ", "Pedone investito", "Ciclista investito", "Motociclista", "Macchina-Moto", "Macchina-Macchina")
        hotelBravoChopper.isSelected = true
        radioButtonGroup.toggles.addAll(primary, secondary)
        primary.isSelected = true
    }
}