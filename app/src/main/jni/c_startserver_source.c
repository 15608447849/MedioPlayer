//
// Created by lzp on 2017/1/18.
//
#include <com_wos_play_rootdir_model_monitor_soexcute_RunJniHelper.h>
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/resource.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <pthread.h>
#include <android/log.h>
#define  TAG    "PingGG_Jin_log"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

//---------------------------------------//
void thread(char* srvname) {
    while(1){
        check_and_restart_service(srvname);
        sleep(4);
    }
}

/**
 * 执行命令
 */
void ExecuteCommandWithPopen(char* command, char* out_result,
                             int resultBufferSize) {
    FILE * fp;
    out_result[resultBufferSize - 1] = '\0';
    fp = popen(command, "r");
    if (fp) {
        fgets(out_result, resultBufferSize - 1, fp);
        out_result[resultBufferSize - 1] = '\0';
        pclose(fp);
    } else {
        exit(0);
    }
}

/**
 * 检测服务，如果不存在服务则启动.
 * 通过am命令启动一个laucher服务,由laucher服务负责进行主服务的检测,laucher服务在检测后自动退出
 */
void check_and_restart_service(char* service) {
    LOGI("当前所在的进程pid=",getpid());
    char cmdline[200];
    sprintf(cmdline, "am startservice --user 0 -n %s", service);
    char tmp[200];
    sprintf(tmp, "cmd=%s", cmdline);
    ExecuteCommandWithPopen(cmdline, tmp, 200);
}

/**
 * jstring 转 String
 */
char* jstringTostring(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "utf-8");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
                                        "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid,
                                                           strencode);
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
    return rtn;
}
//jni call - main mother>>>
JNIEXPORT void JNICALL Java_com_wos_play_rootdir_model_1monitor_soexcute_RunJniHelper_startMservice
  (JNIEnv *env, jobject thiz, jstring cchrptr_ProcessName, jstring sdpath)
  {

  char * rtn = jstringTostring(env, cchrptr_ProcessName); // 得到进程名称
  char * sd = jstringTostring(env, sdpath);
  LOGI("服务名:%s \n sdcard路径:%s", rtn,sd);

  int ret;
  pthread_t id;
  struct rlimit r;
  //char* a ;
  //    a = rtn;
  int pid = fork();
  int mpid = getpid();

      LOGI("fork() 返回值 : %d\n 当前进程 pid - %d", pid,mpid);

      if (pid < 0) {
          LOGI("fork() 错误 ,退出程序", pid);
          exit(0);
      }
      else if(pid != 0){

      }else{
              //第一个子进程
              LOGI("第一个子进程 pid=%d", getpid());
              LOGI(" setsid=%d", setsid());
              umask(0); //使用umask修改文件的屏蔽字，为文件赋予跟多的权限，因为继承来的文件可能某些权限被屏蔽，从而失去某些功能，如读写

              //第二个子进程
              int pidz = fork();
              if(pidz == 0){

              FILE  *fp;
              sprintf(sd,"%s/pidfile",sd);
              fp=fopen(sd,"a");
              //打开文件 没有创建
              if( fp == NULL){
              LOGI("%s文件还未创建!",sd);
              ftruncate(fp, 0);
              lseek(fp, 0, SEEK_SET);
              }
              fclose(fp);//关闭文件流

              fp=fopen(sd,"rw"); //读写

              if(fp>0){
              char buff1[6];
              int p = 0;
              memset(buff1,0,sizeof(buff1));
              fseek(fp,0,SEEK_SET);
              fgets(buff1,6,fp);  //读取一行
              LOGI("读取的进程号：%s",buff1);
              if(strlen(buff1)>1){ // 有值

              kill(atoi(buff1), SIGTERM);
              LOGI("杀死进程，pid=%d",atoi(buff1));
              }
              }
              fclose(fp);

              fp=fopen(sd,"w");//写
              char buff[100];
              int k = 3;
              if(fp>0){
              sprintf(buff,"%lu",getpid());
              fprintf(fp,"%s\n",buff); // 把进程号写入文件
              LOGI("写入中...");
              }
              fflush(fp);
              fclose(fp);

              chdir("/");//修改进程工作目录为根目录，chdir(“/”).
              //关闭不需要的从父进程继承过来的文件描述符。
              if (r.rlim_max == RLIM_INFINITY) {
              r.rlim_max = 1024;
              }
              int i;
              for (i = 0; i < r.rlim_max; i++) {
              close(i);
              }
              umask(0);

              ret = pthread_create(&id, NULL, (void *) thread, rtn);
              if (ret != 0) {
              LOGI("Create pthread error!");
              exit(1);
              }
              int stdfd = open ("/dev/null", O_RDWR);
              dup2(stdfd, STDOUT_FILENO);
              dup2(stdfd, STDERR_FILENO);
              }else{
              exit(0);
              }
      }
  }