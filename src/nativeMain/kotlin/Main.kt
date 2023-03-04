import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import platform.osx.timeout

fun main() = runBlocking {
    val selectorManager = SelectorManager()
    val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 12345) {
        timeout(1000)
    }

    while (true) {
        println("listen")
        val socket = serverSocket.accept()
        handleSocket(socket)
    }
}

suspend fun handleSocket(socket: Socket) {
    println("accept")
    val input = socket.openReadChannel()
    val output = socket.openWriteChannel(autoFlush = true)

    try {
        while (true) {
            try {
                withTimeout(1000) {
                    val line = input.readUTF8Line()
                    println("Received: $line")
                    output.writeStringUtf8("Hello Client! I received: \"$line\" from you.\n")
                }
            } catch (e: TimeoutCancellationException) {
                println("Timeout: ${e.message}")
                break
            }
        }
    } catch (ex: Exception) {
        println("Exception: ${ex.message}")
    } finally {
        println("close")
        output.close()
        socket.close()
    }
}

