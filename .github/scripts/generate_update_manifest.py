"""Create the CMP update asset from the same Config.App values used by the apps."""

import argparse
import json
from pathlib import Path
import re


def generate_manifest(config: str, tag: str, repository: str) -> dict:
    def constant(name: str, quoted: bool = True):
        value_pattern = r'"([^"\n]+)"' if quoted else r"([0-9]+)\b"
        values = re.findall(rf"\bconst val {name}\s*=\s*{value_pattern}", config)
        if len(values) != 1:
            raise ValueError(f"Expected one literal Config.App.{name}")
        return values[0] if quoted else int(values[0])

    version = constant("VERSION_NAME")
    code = constant("VERSION_CODE", quoted=False)
    configured_repository = f'{constant("GITHUB_OWNER")}/{constant("GITHUB_REPO")}'
    if not re.fullmatch(r"[1-9][0-9]*\.[0-9]+\.[0-9]+", version) or code <= 0:
        raise ValueError("Release version must be numeric major.minor.patch with a positive version code")
    if tag != f"v{version}":
        raise ValueError(f"Tag {tag!r} does not match Config.App.VERSION_NAME ({version})")
    if repository != configured_repository:
        raise ValueError(f"Release repository must match Config.App: {configured_repository}")
    return {
        "schemaVersion": 1,
        "repository": repository,
        "versionName": version,
        "versionCode": code,
        "downloadUrl": f"https://github.com/{repository}/releases/tag/{tag}",
        "updateDescription": f"Han1meViewer CMP {version}，完整更新内容与各平台安装说明见发布页面。",
        "forceUpdate": False,
        "isShowAnnouncement": False,
        "announcement": "",
    }


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--config", type=Path, default=Path("buildSrc/src/main/java/Config.kt"))
    parser.add_argument("--tag", required=True)
    parser.add_argument("--repository", required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    manifest = generate_manifest(args.config.read_text(encoding="utf-8"), args.tag, args.repository)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Created {args.output} for {args.repository} {args.tag}")
