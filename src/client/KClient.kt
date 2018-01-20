package client

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.effect.Glow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.stage.Stage
import java.io.FileInputStream

/**
 * KClient: JavaFX GUI for a simple Kotlin-based chat program.
 * Implements:
 *      - Graphic display of an image, text and basic geometry
 *      - User input and basic console display
 *
 * @author Alex Swan
 * @version 2018.01.08.0
 * @since 2018.01.08
 */
class KClient: Application(){

    /** Command line arguments */
    //private val args = args
    /** Version of the program */
    private val version = "2018.01.06.0"
    /** Name of the program */
    private val title = "Swan Client"
    /** Connection manager */
    private var net: Connection? = null

    /** Width of the client window */
    private val width: Double = 590.0
    /** Height of the client window */
    private val height: Double = 613.0
    /** Number of lines to show in the chat screen */
    private val textLines = 8
    /** Height of the chat screen in the window */
    private val textHeight = 145.0

    /** TextArea to be used for user input */
    private val ta = TextArea()
    /** ScrollPane to contain the TextArea in the client GUI */
    private val sp = ScrollPane()
    /** TextField to display the client output */
    private val tf = TextField()
    /** VBox to contain nodes in the application */
    private val vb = VBox()

    /** Background image to display */
    private val back = ImageView(Image(FileInputStream(
            "res/grass.jpg")))

    /** Array to store the client text output */
    private val chatArray = Array(textLines, {""})
    /** Index for tracking */
    private var chatIdx = 0

    /** Sets up and starts the GUI
     * @param ps The (Primary) Stage for displaying the application */
    override fun start(ps: Stage?) {

        //Create a KeyEvent Handler to detect and handle 'ENTER' commands
        val keh = EventHandler<KeyEvent> { e ->
            if(e?.code == KeyCode.ENTER) {

                //Detects commands beginning with the forward slash delimiter
                if (tf.text.startsWith("/"))
                    execute(tf.text.substring(1))
                else net?.sendMessage(tf.text)

                tf.clear(); e.consume()

            }
        }

        //Set TextField parameters
        tf.apply{
            promptText = "Console/Chat:"
            addEventFilter(KeyEvent.KEY_PRESSED, keh)
        }

        //Set TextArea parameters
        ta.apply{
            prefWidth = this@KClient.width
            prefHeight = textHeight
            isWrapText = true
            isEditable = false
        }

        //Set ScrollPane parameters
        sp.apply{
            content = ta
            isFitToWidth = true
            prefWidth(this@KClient.width)
        }

        //Creates a line
        val line = Line().apply{
            startX = 0.0
            startY = 0.0
            endX = 600.0
            endY = 450.0
        }

        //Creates another line
        val line2 = Line().apply{
            startX = 0.0
            startY = 450.0
            endX = 600.0
            endY = 0.0
        }

        //Creates text
        val text = Text(50.0, 50.0, "TEXT").apply{
            effect = Glow(0.8)
        }

        //Sets Pane parameters
        val p = Pane().apply{
            prefWidth = 610.0
            prefHeight = 440.0
        }

        //Adds chat nodes to VBox
        vb.children.addAll(sp, tf)
        //Adds nodes to pane
        p.children.addAll(back, line, line2, text)
        //Arranges the VBox and Pane in a BorderPane layout
        val bp = BorderPane().apply{
            bottom = vb
            center = p
        }

        //Shows the GUI
        ps?.apply{
            title = this@KClient.title
            scene = Scene(bp, this@KClient.width, this@KClient.height)
            isResizable = false
            show()
        }

        //Call startup commands
        intro()

    }

    /** Displays introduction information to the user */
    private fun intro(){
        write("Welcome to $title. " +
                "To connect to the server type '/connect'")
        write("For more commands, type '/help'")
    }

    /** Writes text to the TextArea output
     * @param string A String to write */
    fun write(string: String){
        chatArray[chatIdx] = string
        chatIdx = ++chatIdx % textLines
        refreshChat()
    }

    /** Displays the updated text output */
    private fun refreshChat(){
        val sb = StringBuilder()
        sb.append(chatArray[chatIdx])
        for(i in 1 until textLines) sb.append("\n")
                .append(chatArray[(chatIdx + i) % textLines])
        ta.apply{
            text = sb.toString()
            scrollTop = Double.MAX_VALUE
        }
    }

    /** Clears the chat of any text */
    private fun clearChat(){
        for(i in 0 until textLines) chatArray[i] = ""
        chatIdx = 0
        refreshChat()
    }

    /** Writes help information to the TextArea output */
    private fun writeHelp(){
        write("\t/connect -> connects to the server\t\t" +
                "/quit, /exit -> exit $title")
        write("\t/clear -> clears the text area\t\t\t" +
                "/version -> $title version")
    }

    /** Executes user commands
     * @param string The command represented as a String */
    private fun execute(string: String){
        when(string){
            "quit"      -> System.exit(0)
            "exit"      -> System.exit(0)
            "version"   -> write("$title version: $version")
            "clear"     -> clearChat()
            "help"      -> writeHelp()
            "connect"   -> connect()
            "disconnect"-> disconnect()
            else        -> write("Error: command \"$string\"not recognised.")
        }
    }

    /** Launches a connection to the server */
    private fun connect(){
        if(net != null && net!!.active){
            write("You are already connected")
            return
        }
        net = Connection(this)
        net?.start()
        when (net?.active) {
            true -> write("Successfully connected to ${net?.getAddress()}")
            false ->{
                write("Connection failure.")
                net = null
            }
        }
    }

    private fun disconnect(){
        net = net?.disconnect()
    }

    /** Object for launching Application */
    companion object {
        /** Launches the KClient
         * @param args Launch arguments (unused) */
        @JvmStatic fun main(args: Array<String>) {
            launch(KClient::class.java)
        }
    }
}