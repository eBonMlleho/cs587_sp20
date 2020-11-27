#include<iostream>
#include "RemoteCmdAgentImpl.h"

JNIEXPORT jobject JNICALL 
Java_RemoteCmdAgentImpl_C_1GetLocalTime
  (JNIEnv *env, jobject var1 , jobject var2){
      jclass GetLocalTime = env->GetObjectClass(var2);
      jfieldID valid = env->GetFieldID(GetLocalTime,"valid", "C");
      jfieldID currentTime = env->GetFieldID(GetLocalTime,"time", "I");

      env->SetIntField(var2,valid,'T');
      env->SetIntField(var2,currentTime, time(NULL));
    
    return var2;
  }