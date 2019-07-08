import javafx.application.Application.launch
import utils.ViewLuncher

object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ViewLuncher::class.java, *args)
        }
}