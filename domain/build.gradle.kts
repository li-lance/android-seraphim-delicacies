plugins {
    alias(libs.plugins.seraphim.android.library)
}
android {
    namespace = "com.seraphim.delicacies.domain"
}
dependencies {
    implementation(project(":shared"))
    implementation(project(":utils"))
    implementation(libs.kotlinx.coroutines.core)
//    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.core)
//    implementation(libs.koin.android)
}