package social.aceinteract.entity

data class WebfingerData(
    val subject: String,
    val links: Map<String, String>,
)