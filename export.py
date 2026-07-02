#!/usr/bin/env python3
"""
export.py — Exports all necessary source code into a single export.txt
Run from the project root: python export.py
"""

import os
from pathlib import Path

ROOT = Path(__file__).parent

# Files/dirs to completely skip
SKIP_DIRS = {
    "app", "target", ".jdk", ".mvn", ".git", ".github", ".vscode",
    ".idea", "node_modules", "__pycache__", ".mvn",
}

# Static JS/font/image files to skip (too large or binary)
SKIP_FILES = {
    "tailwind.js",       # 400KB bundled Tailwind — not source
    "favicon.ico",
    "favicon.svg",
}

# File extensions considered source code
INCLUDE_EXTENSIONS = {
    ".java", ".html", ".css", ".properties", ".xml", ".yml", ".yaml",
    ".js", ".sql", ".bat", ".sh", ".py", ".md",
}

# Specific top-level files to always include
ALWAYS_INCLUDE = {
    "pom.xml", "run-app.bat", "build.bat", "mvn.bat",
}

# Skip files larger than this (bytes) — avoids dumping minified libraries
MAX_FILE_BYTES = 80_000


def should_include(path: Path) -> bool:
    # Skip any path that contains a skipped directory segment
    for part in path.parts:
        if part in SKIP_DIRS:
            return False

    if path.name in SKIP_FILES:
        return False

    # Always include specific top-level files
    if path.parent == ROOT and path.name in ALWAYS_INCLUDE:
        return True

    # Skip binary/font dirs
    if "fonts" in path.parts:
        return False

    if path.suffix.lower() not in INCLUDE_EXTENSIONS:
        return False

    # Skip oversized files (minified libs, etc.)
    try:
        if path.stat().st_size > MAX_FILE_BYTES:
            return False
    except OSError:
        return False

    return True


def collect_files() -> list[Path]:
    files = []
    for path in sorted(ROOT.rglob("*")):
        if path.is_file() and should_include(path):
            files.append(path)
    return files


def export():
    out_path = ROOT / "export.txt"
    files = collect_files()

    with open(out_path, "w", encoding="utf-8", errors="replace") as out:
        out.write("=" * 80 + "\n")
        out.write("  SMART EXPENSE TRACKER — Source Export\n")
        out.write(f"  {len(files)} files\n")
        out.write("=" * 80 + "\n\n")

        for path in files:
            rel = path.relative_to(ROOT)
            separator = f"\n{'=' * 80}\n FILE: {rel}\n{'=' * 80}\n"
            out.write(separator)
            try:
                content = path.read_text(encoding="utf-8", errors="replace").rstrip()
                out.write(content)
                out.write("\n")
            except Exception as e:
                out.write(f"[ERROR reading file: {e}]\n")

    size_kb = out_path.stat().st_size // 1024
    print(f"Done. {len(files)} files -> export.txt ({size_kb} KB)")
    for f in files:
        print(f"  {f.relative_to(ROOT)}")


if __name__ == "__main__":
    export()
