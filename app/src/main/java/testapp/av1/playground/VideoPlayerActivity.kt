package testapp.av1.playground

import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoDecoderGLSurfaceView
import java.io.PrintWriter
import java.io.StringWriter

import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicInteger

class VideoPlayerActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "VideoPlayer"
        const val ARG_RENDERER_TYPE = "renderer_type"
        const val ARG_ASSET_NAME = "asset_name"
    }

    private lateinit var player: SimpleExoPlayer
    private lateinit var surfaceView: SurfaceView
    private lateinit var glSurfaceView: VideoDecoderGLSurfaceView
    private lateinit var fpsView: TextView

    private val handler = Handler()

    private var useOpenGL = false

    private var frameDrops = AtomicInteger(0)


    private fun updateFPS() {
        fpsView.post {
            fpsView.text = "Drops: " + frameDrops.get()
        }
    }

    private val analyticsListener = object: AnalyticsListener {
        override fun onDroppedVideoFrames(
            eventTime: AnalyticsListener.EventTime,
            droppedFrames: Int,
            elapsedMs: Long
        ) {
            frameDrops.addAndGet(droppedFrames)
            updateFPS()
        }

        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: ExoPlaybackException
        ) {
            Log.e(TAG, "Playback Error: ", error.cause)
            // Create an AlertDialog to show.
            val alertDialog: AlertDialog = AlertDialog.Builder(this@VideoPlayerActivity).create()
            val error = error?.cause ?: error
            val stringWriter = StringWriter()
            with(PrintWriter(stringWriter)) {
                error.printStackTrace(this)
                alertDialog.setMessage(stringWriter.toString())
                alertDialog.show()
            }
        }

        override fun onSurfaceSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            width: Int,
            height: Int
        ) {
            if(useOpenGL) {
                glSurfaceView.holder.setFixedSize(width, height)
            } else {
                surfaceView.holder.setFixedSize(width, height)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        var rendererType: RendererType = RendererType.DEFAULT
        var assetName : String? = null
        this.intent.extras?.let {
            assetName = it.getString(ARG_ASSET_NAME, null)
            rendererType = RendererType.values()[it.getInt(ARG_RENDERER_TYPE, RendererType.DEFAULT.ordinal)]
        }

        if (assetName == null) {
            throw IllegalArgumentException("Asset name not set")
        }

        surfaceView = findViewById(R.id.surface)
        glSurfaceView = findViewById(R.id.glSurface)
        fpsView = findViewById(R.id.fps_view)

        updateFPS()

        player = SimpleExoPlayer.Builder( this, createFactory(this, rendererType))
            .build()
        player.addAnalyticsListener(analyticsListener)
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL

        useOpenGL = rendererType.isUseOpenGL
        if (useOpenGL) {
            glSurfaceView.visibility = View.VISIBLE
            glSurfaceView.keepScreenOn = true
            player.setVideoDecoderOutputBufferRenderer(glSurfaceView.videoDecoderOutputBufferRenderer)
        } else {
            surfaceView.visibility = View.VISIBLE
            surfaceView.keepScreenOn = true
            player.setVideoSurfaceHolder(surfaceView.holder)
        }

        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "yourApplicationName"))
        val dataSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse("file:///android_asset/${assetName}"))
        player.prepare(dataSource)
        handler.postDelayed({
            finish()
        }, 60 * 1000);
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}