package trade.paper.app.fragments.home.settings

import trade.paper.app.fragments.home.settings.Licenses.LicenseType.APACHE
import trade.paper.app.fragments.home.settings.Licenses.LicenseType.MIT

object Licenses {
    enum class LicenseType(var type: String){
        APACHE("apache"),
        MIT("mit")
    }


    var licenses = hashMapOf(
           "MPAndroidChart" to License(
                   name = "MPAndroidChart",
                   licenseType = APACHE,
                   hasCopyright = true,
                   copyrightYears = "2019",
                   copyrightOwner = "Philipp Jahoda"
           ),
            "RxAndroid" to License(
                    name = "RxAndroid",
                    licenseType = APACHE,
                    hasCopyright = true,
                    copyrightYears = "2015",
                    copyrightOwner = "The RxAndroid authors"
            ),
            "RxKotlin" to License(
                    name = "RxKotlin",
                    licenseType = APACHE,
                    hasCopyright = true,
                    copyrightYears = "2012",
                    copyrightOwner = "Netflix"
            ),
            "OkHttp Logging Interceptor" to License(
                    name = "OkHttp Logging Interceptor",
                    licenseType = APACHE,
                    hasCopyright = false,
                    copyrightYears = null,
                    copyrightOwner = null
            ),
            "Hawk 2.0" to License(
                    name = "Hawk 2.0",
                    licenseType = APACHE,
                    hasCopyright = true,
                    copyrightYears = "2016",
                    copyrightOwner = "Orhan Obut"
            ),
            "OkHttp 3" to License(
                    name = "OkHttp 3",
                    licenseType = APACHE,
                    hasCopyright = false,
                    copyrightYears = null,
                    copyrightOwner = null
            ),
            "Gson Converter" to License(
                    name = "Gson Converter",
                    licenseType = APACHE,
                    hasCopyright = false,
                    copyrightYears = null,
                    copyrightOwner = null
            ),
            "QRGenerator" to License(
                    name = "QRGenerator",
                    licenseType = MIT,
                    hasCopyright = false,
                    copyrightYears = null,
                    copyrightOwner = null
            )


    )

    class License(
            var name: String,
            var licenseType: LicenseType,
            var hasCopyright: Boolean,
            var copyrightYears: String?,
            var copyrightOwner: String?
    )
}