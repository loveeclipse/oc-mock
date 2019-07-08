package utils

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import utils.ocWindow.PATH
import utils.ocWindow.TITLE
import utils.ocWindow.WIDTH
import utils.ocWindow.HEIGHT
import javafx.stage.Stage



class ViewLauncher: Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader()
        val rootParent: Parent = loader.load(javaClass.classLoader.getResource(PATH).openStream())
        val scene = Scene(rootParent, WIDTH.toDouble(), HEIGHT.toDouble())
        primaryStage.title = TITLE
        primaryStage.scene = scene
        primaryStage.isResizable = false
        primaryStage.setOnCloseRequest {
            Platform.exit()
            System.exit(0)
        }
        primaryStage.show()
    }
}