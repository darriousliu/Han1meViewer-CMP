#ifndef CHINO_H
#define CHINO_H

#include <jni.h>
#include <cstdint>

#define MY_NR_OPENAT 56
#define MY_NR_READ   63
#define MY_NR_LSEEK  62
#define MY_NR_CLOSE  57

#define XOR_KEY 0x66

// CMP 首次发布证书的 SHA-256；私钥保存在本机 .secrets/android-release/。
#define EXPECTED_SIG_HASH "04aefa693c7df32e743a855645595d922732f1b33ccabb53f0f9ebae32216ded"

extern "C" {
JNIEXPORT jboolean JNICALL
Java_io_github_darriousliu_han1meviewer_util_SignatureCheckKt_svc(
        JNIEnv *env, jclass thiz);

JNIEXPORT jstring JNICALL
Java_io_github_darriousliu_han1meviewer_util_SignatureCheckKt_getString(
        JNIEnv *env,
        jclass thiz);
}

#endif // CHINO_H