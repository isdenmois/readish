# Readish

### Build
```Bash
cp .env.defaults .env   # fill in SERVER_URL, KEY_STORE_FILE, KEY_ALIAS, KEY_PASSWORD
task build              # signed release APK -> app/build/outputs/apk/release/
task deploy             # bump version, build, commit "Version Bump", upload
```

`task --list` shows every task (lint, test, version:show, clean:apk, ...).
