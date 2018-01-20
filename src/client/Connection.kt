package client

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class Connection(
        /** KClient GUI */
        private val gui: KClient) : Thread(){

    /** IP address of the game server */
    private val serverIP = "localhost"
    /** Port of the game server */
    private val serverPort = 61673
    /** The server Socket */
    private val server = Socket(serverIP, serverPort)
    /** OutputStream to the server */
    private val outp = DataOutputStream(server.getOutputStream())
    /** InputStream from the server */
    private val inp = DataInputStream(server.getInputStream())
    /** Active connection */
    var active = true

    /** Print input to the client */
    override fun run(){

        while(active){
            println("Waiting")
            gui.write(inp.readUTF())
        }

    }

    /** Send a message to the server */
    fun sendMessage(msg: String){
        outp.writeUTF(msg.trim())
    }

    /** Disconnect */
    fun disconnect(): Connection?{
        server.close()
        active = false
        return null
    }

    /** Gets a String of the server address */
    fun getAddress(): String {
        return "$serverIP:$serverPort"
    }

}