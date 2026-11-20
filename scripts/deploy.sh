#!/usr/bin/env bash
# Deploy: bump the version, build the signed release APK, commit the bump, upload.
# Any failure after the bump restores the working tree to its pre-deploy state,
# so re-running deploy produces the same version instead of skipping one.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

if [[ -n "$(git status --porcelain)" ]]; then
    echo "error: working tree must be clean before deploy" >&2
    git status --short >&2
    exit 1
fi

./scripts/bump-version.sh

committed=0
rollback() {
    local rc=$?
    (( rc == 0 )) && return 0
    if [[ $committed -eq 1 ]]; then
        git reset --quiet --hard HEAD~1
    else
        git checkout --quiet -- app/build.gradle
    fi
    echo "deploy failed; restored the pre-deploy tree" >&2
    return 0
}
trap rollback EXIT

task build
git commit --quiet -m 'Version Bump' -- app/build.gradle
committed=1
task upload

echo "deployed $(git log -1 --pretty='%h %s')"
