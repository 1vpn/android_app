# 1VPN Android App

The 1VPN Android app changes your device's IP address by routing your internet traffic through one of our secure servers using V2Ray/Xray with the VLESS + Reality protocol. It can be used to access region-restricted content, hide your browsing activity from ISPs and network administrators, bypass internet censorship, and protect against IP address-based tracking.

## Download

Google Play: https://play.google.com/store/apps/details?id=com.one.vpnapp

## Acknowledgments

This project is a fork of [v2rayNG](https://github.com/2dust/v2rayNG) by 2dust, licensed under GPL-3.0.

## Local Build

### Setup (once)

- **Android SDK** – use the same path as `sdk.dir` in `V2rayNG/local.properties` (not a fake path). Install **Android 36** platform and **build-tools 36.x** (CI: `platforms;android-36.1`, `build-tools;36.1.0`).
- **NDK** – CI uses **28.2.13676358**; your machine may only have **29.x** (Android Studio often installs one version). **Always** point `NDK_HOME` at a real folder: `ls $ANDROID_HOME/ndk/` and use that name (e.g. `29.0.14206865`). To match CI exactly, install NDK 28.2 in SDK Manager (Side by side).
- **Java 21** – matches CI.

Set env for the native build (`ANDROID_HOME` = your `sdk.dir`):

```bash
export ANDROID_HOME=/home/user/Android/Sdk
export NDK_HOME=$ANDROID_HOME/ndk/$(ls "$ANDROID_HOME/ndk" | head -1)
```
Or set `NDK_HOME` manually after `ls $ANDROID_HOME/ndk/`.

### 1. Submodules

```bash
git submodule update --init --recursive
```

### 2. libhevtun (hev-socks5-tunnel)

```bash
export ANDROID_HOME=/home/user/Android/Sdk
export NDK_HOME=$ANDROID_HOME/ndk/$(ls "$ANDROID_HOME/ndk" | head -1)
bash compile-hevtun.sh
cp -r libs V2rayNG/app/
```

### 3. libv2ray.aar

```bash
cd AndroidLibXrayLite && CURRENT_TAG=$(git describe --tags --abbrev=0) && cd ..
mkdir -p V2rayNG/app/libs
curl -L -o V2rayNG/app/libs/libv2ray.aar "https://github.com/2dust/AndroidLibXrayLite/releases/download/${CURRENT_TAG}/libv2ray.aar"
```

### 4. App

**Debug (install on device):**

```bash
cd V2rayNG
./gradlew :app:install1vpnPlaystoreDebug
```

**Release (same signing pattern as CI):**

```bash
cd V2rayNG
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=/path/to/keystore.jks \
  -Pandroid.injected.signing.store.password=... \
  -Pandroid.injected.signing.key.alias=... \
  -Pandroid.injected.signing.key.password=...
```

CI also runs `./gradlew licenseFdroidReleaseReport` before `assembleRelease`. Per-ABI APKs: `V2rayNG/app/build/outputs/apk/*/release/`.