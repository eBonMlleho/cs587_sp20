//Author: Zhanghao Wen
//------------------------------------------C Language----------------------------------------------

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <time.h>
#include <pthread.h> 
#include <sys/utsname.h>



#define _POSIX_C_SOURCE 200809L

typedef struct{
	int time;
	char valid;
}GET_LOCAL_TIME;

typedef struct{
	char OS[16];
	char valid;
}GET_LOCAL_OS;



int receive_one_byte(int client_socket, char *cur_char);
int receiveFully(int client_socket, char *buffer, int length);
void GetLocalTime(GET_LOCAL_TIME *ds);
void GetLocalOS(GET_LOCAL_OS *ds);
void *TCPReceiver(void *vargp);



int main(){

//----------------------------------C TCP Connection as Server------------------------------
	struct sockaddr_in server;
	char* message;
	int sockfd, new_socket;

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if(sockfd == -1){
		printf("C socket creation failed...\n");
		return -1;
	}else{
		//printf("C socket creation success...\n");
	}
	
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = htonl(INADDR_ANY);
	server.sin_port = htons(6666);
	
	if(bind(sockfd, (struct sockaddr *)&server, sizeof(server))<0){
		printf("C socket bind error\n");
	}

	if(listen(sockfd,5)<0){
		printf("C listen too much clients\n");
	}



//------------------------------TCP data communication--------------------------------
	int count = 0, len = sizeof(server);

	while(1){

		printf("accepting.....\n");

		if( (new_socket = accept(sockfd, (struct sockaddr *)&server, (socklen_t*)&len) )< 0){
			printf("\nC TCP Connection Failed\n");
			return -1;
		}else{
			count++;
			printf("The %d TCP Connection is successful!\n", count);
		}
		
		int* socket = (int*)malloc(2*sizeof(int));
		socket[0] = new_socket;
		socket[1] = count;

		pthread_t thread_id;
		pthread_create(&thread_id, NULL, TCPReceiver, socket);

	}

	close(sockfd);
	return 0;
}




void *TCPReceiver(void *vargp){

	int* socket = (int*)vargp;
	int new_socket = socket[0];
	int count = socket[1];
	//------------------------------receive message from client-------------------------------
	char CmdID[100];
	receiveFully(new_socket, CmdID, 100);
	printf("TCP Connection %d   CmdID  : %s\n", count, CmdID);

	char CmdLength[4];
	receiveFully(new_socket, CmdLength, 4);
	int cmdLen = (CmdLength[0]<<24) + (CmdLength[1]<<16) + (CmdLength[2]<<8) + CmdLength[3];
	printf("TCP Connection %d CmdLength: %d\n", count, cmdLen);

	//this is the part that receive the third part which is further instruction in other app (valid)
	//note: the value in *cmdBuffer cannot be changed easily
	char* CmdBuffer = malloc(cmdLen * sizeof(char));
	receiveFully(new_socket, CmdBuffer, cmdLen);

	
	unsigned char bytes[cmdLen];
	//--------------------------------------Get Local Time----------------------------------------
	if(strcmp(CmdID, "GetLocalTime")==0){

		GET_LOCAL_TIME time;
		GetLocalTime(&time);
		
		bytes[0] = ((unsigned)time.time >> 24) & 0xFF;
		bytes[1] = ((unsigned)time.time >> 16) & 0xFF;
		bytes[2] = ((unsigned)time.time >> 8) & 0xFF;
		bytes[3] = (unsigned)time.time & 0xFF;
		bytes[4] = time.valid;

		printf("TCP Connection %d Current time is: %d\n", count, time.time);
		//printf("%d ---bytes: %x %x %x %x valid: %c\n", count, bytes[0], bytes[1], bytes[2], bytes[3], bytes[4]);


	//--------------------------------------Get Local OS----------------------------------------
	}else if(strcmp(CmdID, "GetLocalOS")==0){

		GET_LOCAL_OS os;
		GetLocalOS(&os);

		sprintf(bytes, "%s", os.OS);
		bytes[sizeof(os.OS)] = os.valid;

		printf("TCP Connection %d Current OS is: %s\n", count, bytes);

	}	
	

	
	//--------------------------------------send message to client------------------------------------------
	//send CmdID
	if( send(new_socket, CmdID, sizeof(CmdID), 0) < 0 ){
		printf("\nSend Failed\n");
	}
	//send CmdLength
	if( send(new_socket, CmdLength, sizeof(CmdLength), 0) < 0 ){
		printf("\nSend Failed\n");
	}
	//send the true gig
	if( send(new_socket, bytes, sizeof(bytes), 0) < 0 ){
		printf("\nSend Failed\n");
	}

	memset(bytes, 0, 50);
}



int receive_one_byte(int client_socket, char *cur_char)
{
    ssize_t bytes_received = 0;
	while (bytes_received != 1)
	{
		bytes_received = recv(client_socket, cur_char, 1, 0);
	} 
	
	return 1;
}



int receiveFully(int client_socket, char *buffer, int length)
{
	char *cur_char = buffer;
	ssize_t bytes_received = 0;
	while (bytes_received != length)
	{
	    receive_one_byte(client_socket, cur_char);
	    cur_char++;
	    bytes_received++;
	}
	
	return 1;
}



void GetLocalTime(GET_LOCAL_TIME *ds){

	time_t mytime = time(NULL);

    /*Convert integer time to readable string
    *
    char * time_str = ctime(&mytime);
	time_str[strlen(time_str)-1] = '\0';
	*/

	ds->time = mytime;
	ds->valid = '1';
}



void GetLocalOS(GET_LOCAL_OS *ds){
	struct utsname uts;

	memset(ds->OS, 0, 20);

	if (uname(&uts) < 0)
		perror("uname() error");
	else {
		/*
		printf("Sysname:  %s\n", uts.sysname);
		printf("Nodename: %s\n", uts.nodename);
		printf("Release:  %s\n", uts.release);
		printf("Version:  %s\n", uts.version);
		printf("Machine:  %s\n", uts.machine);
		*/	
		int i;
		for(i=0; i<16; i++){
			ds->OS[i] = uts.sysname[i];
		}
		ds->valid = '1';
	}
}
