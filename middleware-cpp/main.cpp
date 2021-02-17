#include <iostream>
#include "server/secserv.h"

int main(int argc, char *argv[]) {
    int numOfIOContexts = 4, numOfIOThreads = 1, numOfDBThreads = 2, numOfDBContexts = 20;
    unsigned short port = 4008;
    if (argc == 6) {
        port = atoi(argv[1]);
        numOfIOContexts = atoi(argv[2]);
        numOfIOThreads = atoi(argv[3]);
        numOfDBContexts = atoi(argv[4]);
        numOfDBThreads = atoi(argv[5]);
    }

    std::cout << "I/O contexts             : " << numOfIOContexts << std::endl;
    std::cout << "I/O Threads per context  : " << numOfIOThreads << std::endl;
    std::cout << "DB contexts              : " << numOfDBContexts << std::endl;
    std::cout << "DB Threads per context   : " << numOfDBThreads << std::endl;

    Server server(numOfIOContexts, numOfIOThreads, numOfDBContexts, numOfDBThreads, port);
    server.run();

    /*std::vector<std::future<void>> futures;

    auto fut = std::async([numOfIOContexts, numOfIOThreads, port, repo_context, context] {
        Server server(numOfIOContexts, numOfIOThreads, *context, *repo_context, port);
        server.run();
    });
    futures.push_back(std::move(fut));


    for (int i = 0; i < threadsForRepository; i++) {
        auto fut = std::async([repo_context] {
            repo_context->run();
        });
        futures.push_back(std::move(fut));
    }
    std::for_each(futures.begin(), futures.end(), [](std::future<void> &fut) {
        fut.wait();
    });*/

    return 0;
}
