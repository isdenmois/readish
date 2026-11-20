#!/usr/bin/env bash
# Increment versionCode by one and bump the last numeric segment of versionName.
# Suffixes after that number are dropped: "1.2.0-beta" becomes "1.2.1".
# Usage: bump-version.sh [gradle-file]  (default: app/build.gradle.kts)
set -euo pipefail

file="${1:-app/build.gradle.kts}"

if [[ ! -f $file ]]; then
    echo "error: $file not found" >&2
    exit 1
fi

tmp="$(mktemp "${file}.XXXXXX")"
trap 'rm -f "$tmp"' EXIT

bumped_code=0
bumped_name=0
log=()

code_re='^([[:space:]]*versionCode[[:space:]]*=[[:space:]]*)([0-9]+)(.*)$'
name_re='^([[:space:]]*versionName[[:space:]]*=[[:space:]]*")([^"]*)(".*)$'
number_re='^(.*[^0-9])?([0-9]+)([^0-9]*)$'

while IFS= read -r line || [[ -n $line ]]; do
    if [[ $line =~ $code_re ]]; then
        pre=${BASH_REMATCH[1]}
        old=${BASH_REMATCH[2]}
        post=${BASH_REMATCH[3]}
        new=$((10#$old + 1))
        printf '%s%s%s\n' "$pre" "$new" "$post" >>"$tmp"
        log+=("versionCode $old -> $new")
        bumped_code=1
    elif [[ $line =~ $name_re ]]; then
        pre=${BASH_REMATCH[1]}
        old=${BASH_REMATCH[2]}
        post=${BASH_REMATCH[3]}
        if [[ ! $old =~ $number_re ]]; then
            echo "error: no number to bump in versionName \"$old\"" >&2
            exit 1
        fi
        head=${BASH_REMATCH[1]}
        number=${BASH_REMATCH[2]}
        new="${head}$((10#$number + 1))"
        printf '%s%s%s\n' "$pre" "$new" "$post" >>"$tmp"
        log+=("versionName $old -> $new")
        bumped_name=1
    else
        printf '%s\n' "$line" >>"$tmp"
    fi
done <"$file"

if [[ $bumped_code -eq 0 ]]; then
    echo "error: no versionCode line in $file" >&2
    exit 1
fi

if [[ $bumped_name -eq 0 ]]; then
    echo "error: no versionName line in $file" >&2
    exit 1
fi

cat "$tmp" >"$file"
printf '%s\n' "${log[@]}"
