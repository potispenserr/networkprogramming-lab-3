#include <iostream>
#include <string>
#include <winsock2.h>

#pragma comment(lib,"ws2_32.lib")
#pragma warning(disable:4996) 

#define SERVER "127.0.0.1"  // server IP
#define BUFLEN 512  // max length of data to send and receieve
#define PORT 4445  // the port on which to listen for incoming data

int main()
{
    system("title UDP Client for Twitch plays pixel art");

    // initialise winsock
    WSADATA wsa;
    printf("Initialising Winsock...");
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0)
    {
        printf("Failed. Error Code: %d", WSAGetLastError());
        return 1;
    }
    printf("Initialised.\n");

    // create socket
    sockaddr_in client;
    int s, slen = sizeof(client);
    if ((s = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == SOCKET_ERROR)
    {
        printf("socket() failed with error code: %d", WSAGetLastError());
        return 2;
    }

    // setup address structure
    memset((char*)&client, 0, sizeof(client));
    client.sin_family = AF_INET;
    client.sin_port = htons(PORT);
    client.sin_addr.S_un.S_addr = inet_addr(SERVER);

    // start communication
    while (true)
    {
        std::string input;
        std::string message;
        printf("Enter Command: ");
        std::getline(std::cin, input);
        message = input;
        printf("Enter Square Coordinates(X:Y): ");
        std::getline(std::cin, input);
        message = message + ":" + input;
        printf("Enter Color: ");
        std::getline(std::cin, input);
        message = message + ":" + input;

        // send the message
        std::cout << "Message sent was " << message << "\n";
        if (sendto(s, message.c_str(), strlen(message.c_str()), 0, (struct sockaddr*)&client, slen) == SOCKET_ERROR)
        {
            printf("sendto() failed with error code: %d", WSAGetLastError());
            return 3;
        }
        message.clear();
        break;

        // receive a reply and print it
        // clear the answer by filling null, it might have previously received data
        //char answer[BUFLEN];
        //memset(answer, '\0', BUFLEN);
        //// try to receive some data, this is a blocking call
        //if (recvfrom(s, answer, BUFLEN, 0, (struct sockaddr*)&client, &slen) == SOCKET_ERROR)
        //{
        //   printf("recvfrom() failed with error code: %d", WSAGetLastError());
        //    //return 4;
        //}

        //puts(answer);
    }

    closesocket(s);
    WSACleanup();

    return 1;
}