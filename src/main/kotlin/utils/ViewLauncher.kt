package utils

import controller.OcMockController
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import utils.OcWindow.PATH
import utils.OcWindow.TITLE
import utils.OcWindow.WIDTH
import utils.OcWindow.HEIGHT
import javafx.stage.Stage

class ViewLauncher : Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader()
        val rootParent: Parent = loader.load(javaClass.classLoader.getResource(PATH).openStream())
        val controller = loader.getController<OcMockController>()
        val scene = Scene(rootParent, WIDTH.toDouble(), HEIGHT.toDouble())
        primaryStage.title = TITLE
        primaryStage.scene = scene
        primaryStage.isResizable = true
        primaryStage.onShown = EventHandler {
            controller.initialization()
        }
        primaryStage.setOnCloseRequest {
            Platform.exit()
            System.exit(0)
        }
        primaryStage.show()
    }
}