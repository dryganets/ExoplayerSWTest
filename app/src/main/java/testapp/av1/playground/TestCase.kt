package testapp.av1.playground

data class TestCase(
    val name: String,
    val rendererType: RendererType,
    val assetName: String
) {
    override fun toString(): String {
        return name
    }
}