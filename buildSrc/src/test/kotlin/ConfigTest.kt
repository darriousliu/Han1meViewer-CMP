import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigTest {
    @Test
    fun androidVariantsKeepTheirOwnMode() {
        assertTrue(Config.isReleaseBuild(listOf(":app:assembleRelease")))
        assertFalse(Config.isReleaseBuild(listOf(":app:assembleDebug")))
        assertFalse(Config.isReleaseBuild(listOf(":app:packageDebug")))
    }

    @Test
    fun desktopDistributionsAreReleaseWithoutEnablingObfuscation() {
        for (task in listOf("packageDistributionForCurrentOS", "packageDmg", "packageNsis", "packageZip", "createDistributable")) {
            assertTrue(Config.isReleaseBuild(listOf(":desktopApp:$task")), task)
        }
        assertFalse(Config.isReleaseBuild(listOf(":desktopApp:run")))
        assertFalse(Config.isReleaseBuild(listOf(":desktopApp:hotRun")))
    }

    @Test
    fun xcodeCallbackUsesItsConfiguration() {
        val tasks = listOf(":shared:embedAndSignAppleFrameworkForXcode")
        assertTrue(Config.isReleaseBuild(tasks, xcodeConfiguration = "Release"))
        assertFalse(Config.isReleaseBuild(tasks, xcodeConfiguration = "Debug"))
        assertFalse(Config.isReleaseBuild(tasks))
        assertFalse(Config.isReleaseBuild(listOf(":app:assembleDebug"), xcodeConfiguration = "Release"))
    }

    @Test
    fun explicitModeWorksForCiAndOverridesInference() {
        assertTrue(Config.isReleaseBuild(listOf(":shared:generateBuildKonfig"), explicitRelease = "true"))
        assertFalse(Config.isReleaseBuild(listOf(":desktopApp:packageZip"), explicitRelease = "false"))
        assertFailsWith<IllegalArgumentException> {
            Config.isReleaseBuild(emptyList(), explicitRelease = "typo")
        }
    }

    @Test
    fun releaseAndDebugApplicationIdsStaySeparate() {
        assertEquals("io.github.darriousliu.han1meviewer", Config.App.applicationId(true))
        assertEquals("io.github.darriousliu.han1meviewer.debug", Config.App.applicationId(false))
    }
}
