package server

import java.net.*;
import java.io.*;

/**
 * KServer: A simple Kotlin-based TCP server that accepts connections from
 * clients and sends and receives a simple message
 *
 * @author Alex Swan
 * @version 2018.01.08.0
 * @since 2018.01.08
 */
class KServer constructor(IP: String = "localhost", port: Int = 61673) : Thread() {

    /** Server IP */
    private val IP = IP
    /** Port for server to listen on */
    private val port = port
    /** ServerSocket that accepts connections */
    private val listener = ServerSocket(port)

    /** Runs the server on a continuous loop accepting and interacting with clients */
    override fun run() {
        while(true){
            try{

                println("Waiting for clients on $IP:$port")
                val server = listener.accept()
                println("Connected to ${server.remoteSocketAddress}")
                val inp = DataInputStream(server.getInputStream())
                println("Client says: ${inp.readUTF()}")
                val outp = DataOutputStream(server.getOutputStream())
                outp.writeUTF("Thanks for connecting to ${server.localSocketAddress}")
                server.close()

            }catch(e: IOException){
                e.printStackTrace()
                println("Failure.")
                break
            }
        }
    }

    /** Launches the server
     * @param args Launch arguments (unused) */
    fun main(args : Array<String>) {
        KServer().start()
    }

}