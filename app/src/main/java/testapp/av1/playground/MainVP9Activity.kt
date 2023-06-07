package testapp.av1.playground

class MainVP9Activity : TestCaseListActivity() {

    override val renderers = listOf(
        RendererType.DEFAULT,
        RendererType.VPX,
        RendererType.VPX_GL
    )

    override val files = listOf(
        FileInfo(EncoderType.VP9, "aac 656x1216 mp4", "vp9_aac_656x1216.mp4")
    )
}
