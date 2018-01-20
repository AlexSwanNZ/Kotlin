package server

import java.net.*
import java.io.*

/**
 * KServer: A Kotlin-based TCP game server
 *
 * @author Alex Swan
 * @version 2018.01.08.0
 * @since 2018.01.08
 */
class KServer constructor(
        /** Server IP */
        val IP: String = "localhost",
        /** Port for server to listen on */
        val port: Int = 61673): Thread() {

    /** Name of the program */
    val title = "Swan Server"
    /** Listener for new connections */
    private val listener = LoginListener(this)
    /** A set of active client connections */
    val connections = HashSet<Connection>()
    /** Is the server online */
    val online = true

    /** Runs the server on a continuous loop accepting and interacting with clients */
    override fun run() {

        listener.start()

        while(online){
            Thread.sleep(1000)
        }

    }

    fun login(conn: Socket, uName: String){
        println("$uName logged in")
    }

    fun fatalError(msg: String){
        println("Fatal error: $msg")
        println("Shutting down")
        System.exit(-1)
    }

    companion object{
        /** Launches the server
         * @param args Launch arguments (unused) */
        @JvmStatic fun main(args : Array<String>) {
            KServer().start()
        }
    }

}