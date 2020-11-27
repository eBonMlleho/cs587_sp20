//Author: Zhanghao
//Time: 2/9/2020

#include<stdio.h>
#include <stdlib.h>
#include<string.h>
#include <unistd.h>
#include <netdb.h>
#include<sys/socket.h>
#include <sys/types.h>
#include<arpa/inet.h>
#include <netinet/in.h>
#include <time.h>
#include <pthread.h> 
#include <sys/utsname.h>


//#define TCPPORT 5555
#define UDPPORT 7777
#define MAXLINE 1000 

struct BEACON
{
	int ID;
	int startUpTime;
	int timeInterval;
	int cmdPort;
	char IP[4];
};

void GetLocalOS(char OS[16], int *valid);
void GetLocalTime(int mytime, int *valid);
char* getIPAddress();
//void getPort(int bport, int port);

void *BeaconSender(void *vargp){
	


	//BEACON INFO:
	srand(time(0)); 
	int myID = rand();
	time_t the_time = time(0);
	int my_start_time = (int)the_time;
	int my_time_interval = 6;
	char* qigemingzi = getIPAddress();

	int* bPORT = (int*) vargp;
	int bport = bPORT[0];

	struct BEACON myBeacon = {myID, my_start_time, my_time_interval, bport, *qigemingzi};

	//1. UDP 
    //char message[50] = "Hello from UDP client"; //byte size is important
	//char message[100] = (char*)malloc(sizeof(char)); 
	char message[100];
	char message1[100];
	char message2[100];
	char message3[100];
	//int message[3] = {myBeacon.ID,myBeacon.startUpTime,myBeacon.timeInterval};

	sprintf(message, "%d", myBeacon.ID); 
	sprintf(message1, " %d", myBeacon.startUpTime); 
	sprintf(message2, " %d", myBeacon.timeInterval); 
	sprintf(message3, " %d", myBeacon.cmdPort); 
	
	strcat(message,message1);
	strcat(message,message2);
	strcat(message,message3);
	strcat(message, " ");
	strcat(message, qigemingzi);

	printf("UDP packet contents: %s\n", message);

	

	int sockfdd, n; 
    struct sockaddr_in servaddr_UDP;

	servaddr_UDP.sin_addr.s_addr = inet_addr("127.0.0.1"); 
    servaddr_UDP.sin_port = htons(UDPPORT); 
    servaddr_UDP.sin_family = AF_INET; 

	// create datagram socket 
    //sockfdd = socket(AF_INET, SOCK_DGRAM, 0); 
    // Creating socket file descriptor 
	
    if ( (sockfdd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        printf("UDP socket creation failed\n"); 
        exit(EXIT_FAILURE); 
    } else{
		printf("UDP socket creation success\n");
	}
  


    // connect to server 
    if(connect(sockfdd, (struct sockaddr *)&servaddr_UDP, sizeof(servaddr_UDP)) < 0) { 
        printf("\n Error :UDP Connect Failed \n"); 
        exit(0); 
    } 
	//send datagram

	while(1){

    	sendto(sockfdd, message, MAXLINE, 0, (struct sockaddr*)NULL, sizeof(servaddr_UDP)); 
		sleep(my_time_interval);	
	}

	
    //sendto(sockfdd, (struct BEACON*)&myBeacon, MAXLINE, 0, (struct sockaddr*)NULL, sizeof(servaddr_UDP));
    // close the descriptor 
    close(sockfdd); 
	//2. periodically send message to server 

	//return (void *) bport;
}

int main(){
	
	struct sockaddr_in server;
	char* message;
	// int OS_valid;
	// char OS[16];
	


	srand(time(0));
	int bport = rand()%1000+6000;
	int* bPORT = (int*)malloc(2*sizeof(int));
	bPORT[0] = bport;

	//BeaconSender thread
	pthread_t thread_id;
    pthread_create(&thread_id, NULL, BeaconSender, bPORT); 

	
	//socket file descriptor, use to determine whether socket creation failed or not. 
	//the third field: IPPROTO_IP or 0 - IP protocol 

	//TCP connection
	int sockfd = socket(AF_INET, SOCK_STREAM, 0);
	int new_socket;
	

	if(sockfd == -1){
		printf("socket creation failed...\n");
		return -1;
	}else{
		printf("socket creation success...\n");
	}
	
	
	server.sin_family = AF_INET;
	//server.sin_addr.s_addr = inet_addr("0.0.0.0");
	server.sin_addr.s_addr = INADDR_ANY;
	//server.sin_len = sizeof(server);
	server.sin_port = htons(bport);
	

	if(bind(sockfd, (struct sockaddr *)&server, sizeof(server))<0){
		printf("bind errorrrrrrrrrrrrrrr\n");
	}

	if(listen(sockfd,5)<0){
		printf("listen too much clients\n");
	}


	int len = sizeof(server);
	if( (new_socket= accept(sockfd, (struct sockaddr *)&server, (socklen_t*)&len) )< 0){
		printf("TCP Connection Failed\n");
		return -1;
	}else{
		printf("TCP Connection succes\n");
	}
	
	while(1){
		char* server_reply = malloc(400*sizeof(char));

		if( recv(new_socket, server_reply, 400, 0) < 0){
			printf("\nRecv Failed\n");
		}

		read(sockfd, server_reply, 400);
		//process server_reply
		printf("Server Reply: \n%s\n", server_reply);
		memset(server_reply, 0, 255);
		//printf("i have cleaned my cache !!!!!!!!Server Reply: \n%s\n", server_reply);
		//send feedback back to server
		int OS_valid = 0;
		char OS[16];
		char msg[16];
		char msg1[100];
		if(strcmp(server_reply, "GET OS")){
			GetLocalOS(OS,&OS_valid);
			//printf("Your computer's OS is %s\n", OS);
			strcpy(msg,OS);
			strcat(msg,"\n");
			//msg = "My OS is --- ";
			// strcat(msg, OS);	
			if( send(new_socket, msg, strlen(msg), 0) < 0){
				printf("\nSend Failed\n");
				return -1;
			}else{
				//printf("Send OS Info successfully\n");
				memset(server_reply, 0, 255);
			}
		}

   		 int myTime;
  		 int TIME_valid;

		if(strcmp(server_reply, "GET TIME")){
			GetLocalTime(myTime,&TIME_valid);

			time_t the_time = time(0);
   			int myTime = (int)the_time;
			sprintf(msg1, "%d", myTime); 
			strcat(msg1,"\n");

			if( send(new_socket, msg1, strlen(msg1), 0) < 0){
				printf("\nSend Failed\n");
				return -1;
			}else{
				//printf("Send Time Info successfully\n");
				memset(server_reply, 0, 255);
			}
		}
		
		free(server_reply);
		// message = "Hello from client haha";
		
		// if( send(sockfd, message, strlen(message), 0) < 0){
		// 	printf("\nSend Failed\n");
		// 	return -1;
		// }
	}

		
			

	close(sockfd);
	return 0;
}

void GetLocalOS(char OS[16], int *valid){
		struct utsname name;
		if(uname(&name)) exit(-1);
        strcpy(OS, name.sysname);
		//*valid = 1;
		//printf("Your computer's OS is %s\n", OS);
}

 void GetLocalTime(int mytime, int *valid){
	time_t the_time = time(0);
	//int my_start_time = (int)the_time;
    mytime = (int)the_time;
    //printf("Your computer's UNIX time is %d\n", mytime);
 }


char* getIPAddress(){
	char hostbuffer[256]; 
	char *IPbuffer; 
	struct hostent *host_entry; 
	int hostname; 

	// To retrieve hostname 
	hostname = gethostname(hostbuffer, sizeof(hostbuffer)); 
	if (hostname == -1) { 
		perror("gethostname"); 
		exit(1); 
	} 

	// To retrieve host information 
	host_entry = gethostbyname(hostbuffer); 
	if (host_entry == NULL) { 
		perror("gethostbyname"); 
		exit(1); 
	} 

	// To convert an Internet network 
	// address into ASCII string 
	IPbuffer = inet_ntoa(*((struct in_addr*) 
						host_entry->h_addr_list[0])); 
	if (NULL == IPbuffer) { 
		perror("inet_ntoa"); 
		exit(1); 
	} 

	printf("Hostname: %s\n", hostbuffer); 
	//printf("Host IP: %s\n", IPbuffer); 

	return IPbuffer;
}