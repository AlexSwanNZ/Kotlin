package server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Login handler for KServer. Allows the client a max number of login attempts
 * and times out the connection after specified time
 *
 * @param server The game server
 * @param client The client Socket
 */
class Login(private val server: KServer, private val client: Socket): Thread(){

    /** Max number of login attempts */
    private val maxAttempts = 3
    /** Timeout in ms */
    private val timeout = 10000
    /** Active state of login thread */
    private var active = true
    /** Last client activity time */
    private var activeTime = System.currentTimeMillis()
    /** Connection object */
    private val connection = Connection(client.inetAddress, client.localPort)
    /** Client inward TCP channel */
    private val inp = DataInputStream(client.getInputStream())
    /** Client outward TCP channel */
    private val outp = DataOutputStream(client.getOutputStream())

    /** Handle login requests */
    override fun run(){

        //Don't start a new login process for already connected users. Necessary??
        if(server.connections.contains(connection)) return

        server.connections.add(connection)
        println("New connection: ${client.localSocketAddress}")
        outp.writeUTF("Msg: Welcome to ${server.title}")

        active()
        Timeout(this).start()

        var attempts = 0
        while(server.online && active && attempts < maxAttempts){

            outp.writeUTF("Msg: Enter your username")
            val uName = inp.readUTF()

            if(!active()) break

            outp.writeUTF("Msg: Enter your password")
            val pWord = inp.readUTF()

            if(!active()) break

            if(uName == "alex" && pWord == "alex"){
                outp.writeUTF("LIN: Login success")
                server.login(client, uName)
                break
            }else outp.writeUTF("Msg: Invalid credentials")

            if(++attempts == maxAttempts){
                outp.writeUTF("Msg: You have exceeded the maximum allowed login attempts.")
                outp.writeUTF("Dis: You have been disconnected.")
                server.connections.remove(connection)
            }
        }
    }

    /** Check that the user has not timed out */
    private fun active(): Boolean{
        activeTime = System.currentTimeMillis()
        return active
    }

    /** Thread for detecting client timeout */
    inner class Timeout(private var login: Login): Thread(){
        override fun run(){
            while(server.online){
                Thread.sleep(1000)
                if(System.currentTimeMillis() > activeTime * timeout) break
            }
            outp.writeUTF("Msg: The connection has timed out.")
            outp.writeUTF("Dis: You have been disconnected.")
            server.connections.remove(connection)
            login.active = false
        }
    }

}