import javafx.application.Application.launch
import utils.ViewLauncher

object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ViewLauncher::class.java, *args)
        }
}