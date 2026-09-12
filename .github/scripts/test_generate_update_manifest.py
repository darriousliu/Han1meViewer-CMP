import unittest

from generate_update_manifest import generate_manifest


CONFIG = '''
    const val VERSION_NAME = "26.3.3"
    const val VERSION_CODE = 260806
    const val GITHUB_OWNER = "darriousliu"
    const val GITHUB_REPO = "Han1meViewer-CMP"
'''
REPOSITORY = "darriousliu/Han1meViewer-CMP"


class UpdateManifestTest(unittest.TestCase):
    def test_release_asset_uses_app_version_and_exact_tag_page(self):
        manifest = generate_manifest(CONFIG, "v26.3.3", REPOSITORY)
        self.assertEqual(manifest["versionCode"], 260806)
        self.assertEqual(manifest["versionName"], "26.3.3")
        self.assertEqual(manifest["repository"], REPOSITORY)
        self.assertEqual(manifest["downloadUrl"], f"https://github.com/{REPOSITORY}/releases/tag/v26.3.3")
        self.assertFalse(manifest["forceUpdate"])
        self.assertFalse(manifest["isShowAnnouncement"])

    def test_tag_must_match_the_built_app(self):
        with self.assertRaises(ValueError):
            generate_manifest(CONFIG, "v26.3.4", REPOSITORY)

    def test_fork_cannot_accidentally_publish_for_the_parent_repository(self):
        with self.assertRaises(ValueError):
            generate_manifest(CONFIG, "v26.3.3", "someone/another-fork")

    def test_invalid_app_versions_are_rejected(self):
        for invalid in (
            CONFIG.replace('"26.3.3"', '"26.3.3-beta"'),
            CONFIG.replace("260806", "0"),
            CONFIG.replace("VERSION_CODE", "UNKNOWN_CODE"),
        ):
            with self.subTest(config=invalid), self.assertRaises(ValueError):
                generate_manifest(invalid, "v26.3.3", REPOSITORY)


if __name__ == "__main__":
    unittest.main()
