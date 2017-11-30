#include <jni.h>
#include <string>
//
// Created by Development_Android on 2017/1/11.
//
int longLenth(long l){
    int lenth = 0;
    while (l){
        l = l/10;
        lenth++;
    }
    return lenth;
}

extern "C"
jlong
Java_com_bjghhnt_app_treatmentdevice_utils_JniUtils_getSupperPassword(
        JNIEnv *env,
        jobject /* this */,
        jint times) {

    long password = 19467166;
    long enpsw;
    if (times%2 == 0){
        enpsw = password << times;
    } else{
        enpsw = password >> times;
    }
    int lenth = longLenth(enpsw);



//    std::string text = "Text From C++";
//    return env->NewStringUTF(text.c_str());
    return enpsw;
}

