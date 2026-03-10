# Local build

Run steps 1–4 from repo root.

## Setup (once)

- **Android SDK** – set `sdk.dir` in `V2rayNG/local.properties` (e.g. `/home/user/Android/Sdk`).
- **Android NDK** – Android Studio → File → Settings → Languages & Frameworks → Android SDK → SDK Tools → enable **NDK (Side by side)** → Apply. Then set `NDK_HOME` to the installed version:
  ```bash
  export ANDROID_HOME=/home/user/Android/Sdk   # or your sdk.dir path
  ls $ANDROID_HOME/ndk/                         # e.g. 29.0.14206865
  export NDK_HOME=$ANDROID_HOME/ndk/29.0.14206865   # use the folder name from ls
  ```
- **Go** – for step 4. Install: `sudo apt install golang-go` (or from https://go.dev/dl/).
- **Java 17+** – for Gradle.

## 1. Submodules

```bash
git submodule update --init --recursive
```

## 2. libtun2socks

```bash
export ANDROID_HOME=/home/user/Android/Sdk
export NDK_HOME=$ANDROID_HOME/ndk/29.0.14206865
bash compile-tun2socks.sh
cp -r libs V2rayNG/app/
```

## 3. libv2ray.aar

```bash
cd AndroidLibXrayLite && CURRENT_TAG=$(git describe --tags --abbrev=0) && cd ..
mkdir -p V2rayNG/app/libs
curl -L -o V2rayNG/app/libs/libv2ray.aar "https://github.com/2dust/AndroidLibXrayLite/releases/download/${CURRENT_TAG}/libv2ray.aar"
```

## 4. libhysteria2

```bash
export ANDROID_HOME=/home/user/Android/Sdk
export NDK_HOME=$ANDROID_HOME/ndk/29.0.14206865
bash libhysteria2.sh
cp -r hysteria/libs/* V2rayNG/app/libs/
```

## 5. App

**Debug (install on device):**
```bash
cd V2rayNG
./gradlew :app:install1vpnPlaystoreDebug
```

**Prod (release APK):**
```bash
cd V2rayNG
./gradlew :app:assemble1vpnPlaystoreRelease
```
APKs: `V2rayNG/app/build/outputs/apk/1vpnPlaystore/release/`. Signing: configure in `app/build.gradle.kts` or pass `-Pandroid.injected.signing.*` (see CI workflow).
