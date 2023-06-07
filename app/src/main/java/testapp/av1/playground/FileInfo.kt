package testapp.av1.playground

enum class EncoderType {
    AOM_AV1,
    SVT_AV1,
    VP9
}


data class FileInfo(
    val encoderType: EncoderType,
    val resolution: String,
    val assetName: String
) {
    override fun toString(): String {
        return "$encoderType $resolution"
    }
}