package net.rrerr.minetrix

import com.okkero.skedule.BukkitDispatcher
import de.msrd0.matrix.client.*
import de.msrd0.matrix.client.listener.EventQueue
import de.msrd0.matrix.client.listener.EventTypes
import de.msrd0.matrix.client.util.DefaultHttpTarget
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.lang.Exception
import java.lang.IllegalStateException
import java.net.URI

class Main : JavaPlugin {
    constructor() : super()

    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) :
        super(loader, description, dataFolder, file)

    var matrixClient : MatrixClient? = null
    var room : Room? = null
    var target : DefaultHttpTarget? = null
    private val matrixEventQueue = EventQueue()
    private val minecraftChatMessageListener = MinecraftChatMessageListener(this)
    private val matrixRoomMessageReceivedListener = MatrixRoomMessageReceivedListener(this)

    companion object {
        private const val SYNC_TIMEOUT = 30_000
    }

    override fun onEnable() {
        logger.info("Starting up Minetrix")
        saveDefaultConfig()

        try {
            setupMatrixClient()
        } catch (e : Exception) {
            logger.severe("got a ${e.message} error while setting up Matrix :-(")
            e.printStackTrace()
            return
        }
        saveConfig()

        if (!(matrixClient == null || room == null)) {
            logger.info("Registering MinecraftChatMessageListener")
            server.pluginManager.registerEvents(minecraftChatMessageListener, this)
        }

        logger.info("Minetrix started up")
    }

    override fun onDisable() {
        try {
            logger.info("Stopping Matrix sync loop")
            matrixClient!!.stopSyncBlocking()
        } catch (e : IllegalStateException) {
            logger.warning("syncBlocking already stopped :thonk:")
        }

        if (matrixEventQueue.isRunning) {
            logger.info("Stopping Matrix event queue")
            matrixEventQueue.stop()
        }
        target = null

        saveConfig()

        logger.info("Minetrix shut down")
    }

    private fun setupMatrixClient() {
        val homeServerDomain : String = config.getString("homeserver.domain")!!
        val homeServer = HomeServer(homeServerDomain, URI("https://$homeServerDomain"))
        val id = MatrixId.fromString(config.getString("username")!!)

        matrixClient = MatrixClient(homeServer, id)
        matrixClient!!.moveTo(matrixEventQueue)
        target = DefaultHttpTarget(homeServer.base, MatrixClient.publicTarget.userAgent)

        if (config.getString("client.token") == null || config.getString("client.device_id") == null) {
            // we need to login first
            val auth = matrixClient!!.auth(LoginType.PASSWORD)
            if (auth == null) {
                logger.severe("Matrix login failed!")
                return
            }
            auth.setProperty("password", config.getString("password")!!)
            val authResult = auth.submit()
            if (!authResult.stream().anyMatch { it.loginType == LoginType.SUCCESS }) {
                logger.severe("Matrix login failed!")
                return
            }

            logger.info("Login to Matrix was successful.  Nice.")
            if (matrixClient!!.token == null) {
                logger.severe("Too bad there was no supported authentication method found.  Sigh.")
                return
            }

            config.set("client.token", matrixClient!!.token)
            config.set("client.device_id", matrixClient!!.deviceId)
        }

        matrixClient!!.userData = MatrixUserData(config.getString("client.token")!!, config.getString("client.device_id")!!)

        logger.info("Registering Matrix roomMessageReceived event listener")
        matrixClient!!.on(EventTypes.ROOM_MESSAGE_RECEIVED, matrixRoomMessageReceivedListener)

        if (!matrixEventQueue.isRunning) {
            logger.info("Starting Matrix event queue")
            matrixEventQueue.start()
        }

        logger.info("Performing initial Matrix sync")
        matrixClient!!.sync()

        val roomId = RoomId.fromString(config.getString("room_id")!!)
        room = matrixClient!!.rooms.find { it.id == roomId }

        if (room == null) {
            logger.severe("Could not find room with id ${config.getString("room_id")}")
            return
        }

        logger.info("Starting Matrix sync loop")
        GlobalScope.launch(BukkitDispatcher(this, async = true)) {
            matrixClient!!.syncBlocking(SYNC_TIMEOUT)
        }
    }
}

