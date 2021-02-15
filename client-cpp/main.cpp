#include <iostream>
#include "client/client.h"
#include <thread>
#include <sys/wait.h>

int main(int argc, char *argv[]) {

    for(int i=0;i<5;i++)
        Client client("localhost","4008");
    /*for(int i=0;i<5;i++) // loop will run n times (n=5)
    {
        if(fork() == 0)
        {

        }
    }
    wait(0);*/
    return 0;
}
