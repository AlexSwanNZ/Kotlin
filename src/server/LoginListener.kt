package server

import java.net.ServerSocket

/**
 * Listens for connections to the server.
 * Creates a new Login session for each new connection.
 */
class LoginListener(
        /** The game server */
        private val server: KServer):
        Thread() {

    /** Server IP */
    private val ip = server.IP
    /** Server port */
    private val port = server.port
    /** TCP listening socket */
    private val listener = ServerSocket(port)

    /** Listening loop */
    override fun run(){

        println("Connection Listener: Waiting for clients on $ip:$port")
        while(server.online) Login(server, listener.accept()).start()

    }

}