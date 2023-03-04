import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val selectorManager = SelectorManager()
    val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 12345)

    while (true) {
        println("listen")
        val socket = serverSocket.accept()
        handleSocket(socket)
    }
}

suspend fun handleSocket(socket: Socket) {
    println("accept")
    val input = socket.openReadChannel()

    try {
        while (true) {
            val line = input.readUTF8Line() ?: break
            println("Received: $line")
        }
    } catch (ex: Exception) {
        println("Exception: ${ex.message}")
    } finally {
        println("close")
        socket.close()
    }
}

