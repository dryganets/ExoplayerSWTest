package testapp.av1.playground

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.video.VideoRendererEventListener
import java.util.ArrayList

private const val MIN_FRAME_DROP_TO_NOTIFY = 1

open class SingleSoftwareDecoderFactory(context: Context, val rendererClassName: String)
    : DefaultRenderersFactory(context) {
    final override fun buildVideoRenderers(
        context: Context,
        extensionRendererMode: Int,
        mediaCodecSelector: MediaCodecSelector,
        drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?,
        playClearSamplesWithoutKeys: Boolean,
        enableDecoderFallback: Boolean,
        eventHandler: Handler,
        eventListener: VideoRendererEventListener,
        allowedVideoJoiningTimeMs: Long,
        out: ArrayList<Renderer>
    ) {
        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            val clazz =
                Class.forName(rendererClassName)
            val constructor = clazz.getConstructor(
                Long::class.javaPrimitiveType,
                Handler::class.java,
                VideoRendererEventListener::class.java,
                Int::class.javaPrimitiveType
            )
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            val renderer = constructor.newInstance(
                allowedVideoJoiningTimeMs,
                eventHandler,
                eventListener,
                MIN_FRAME_DROP_TO_NOTIFY
            ) as Renderer
            out.add(renderer)
            Log.i(TAG, "Loaded SoftwareRenderer $rendererClassName")
        } catch (e: ClassNotFoundException) {
            // Expected if the app was built without the extension.
        } catch (e: Exception) {
            // The extension is present, but instantiation failed.
            throw RuntimeException("Error instantiating SoftwareRenderer $rendererClassName", e)
        }
    }

    companion object {
        private const val TAG = "SoftwareRendererFactory"
    }
}

class Dav1dRenderersFactory(context: Context) : SingleSoftwareDecoderFactory(
    context = context,
    rendererClassName = "com.google.android.exoplayer2.ext.av1Dav1d.LibDav1dVideoRenderer")

class Gav1RenderersFactory(context: Context) : SingleSoftwareDecoderFactory(
    context = context,
    rendererClassName = "com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer")

class VpxRendererFactory(context: Context) : SingleSoftwareDecoderFactory(
    context = context,
    rendererClassName = "com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer"
)
