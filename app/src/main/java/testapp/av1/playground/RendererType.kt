package testapp.av1.playground

import android.content.Context
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.RenderersFactory

enum class RendererType (val isUseOpenGL: Boolean){
    DEFAULT(false),
    VPX(false),
    VPX_GL(true)
    ;
}

fun createFactory(context: Context, factoryType: RendererType): RenderersFactory {
    return when(factoryType) {
        RendererType.DEFAULT -> DefaultRenderersFactory(context)

        RendererType.VPX, RendererType.VPX_GL -> VpxRendererFactory(context)
    }
}