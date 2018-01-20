package server

import java.net.InetAddress

/** Class for storing connection data */
data class Connection(val IP: InetAddress, val port: Int)