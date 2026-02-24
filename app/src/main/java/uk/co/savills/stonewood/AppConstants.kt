
@file:Suppress("InvalidMethodName")

package uk.co.savills.stonewood


const val PHOTO_DIRECTORY = "photos"
const val DATA_BACKUP_DIRECTORY = "dataBackup"

val APP_CENTER_KEY
    get() = when (BuildConfig.BUILD_TYPE) {
        "release" -> "8414de61-9061-4034-a716-44bf1165d185"
        "staging" -> "77447808-8580-4cb5-b775-7c4e9715ef28"
        "development" -> "39dcb040-4560-4fd6-b318-41fe5090edfa"
        else -> throw IllegalStateException("${BuildConfig.BUILD_TYPE} build not configured")
    }

val BASE_ADDRESS
    get() = when (BuildConfig.BUILD_TYPE) {
        "release" -> "https://api.surveys.savillsmdc.co.uk"
        "staging" -> "https://api.stonewood.thumbmunkeys.com"
        "development" -> "https://api.dev.stonewood.thumbmunkeys.com"
        "debug" -> "https://api.stonewood.thumbmunkeys.com"
        else -> throw IllegalStateException("${BuildConfig.BUILD_TYPE} build not configured")
    }

val FORGOT_PASSWORD_ADDRESS
    get() = when (BuildConfig.BUILD_TYPE) {
        "release" -> "https://surveys.savillsmdc.co.uk/forgetpassword"
        "staging" -> "https://stonewood.thumbmunkeys.com/forgetpassword"
        "development" -> "https://dev.stonewood.thumbmunkeys.com/forgetpassword"
        "debug" -> "https://dev.stonewood.thumbmunkeys.com/forgetpassword"
        else -> throw IllegalStateException("${BuildConfig.BUILD_TYPE} build not configured")
    }

val APP_STORE_ADDRESS
    get() = when (BuildConfig.BUILD_TYPE) {
        "release" -> "https://play.google.com/store/apps/details?id=uk.co.savills.stonewood"
        "staging" -> "https://appcenter.ms/orgs/Stonewood/apps/android-staging/distribute/releases"
        "development" -> "https://appcenter.ms/orgs/Stonewood/apps/android-development/distribute/releases"
        else -> throw IllegalStateException("${BuildConfig.BUILD_TYPE} build not configured")
    }
