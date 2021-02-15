#include <iostream>
#include <vector>
#include <future>
#include <boost/asio.hpp>
#include "server/secserv.h"


int main(int argc, char *argv[]) {
    int numOfIOContexts = 16, threadsPerContext = 16;
    if (argc == 3) {
        numOfIOContexts = atoi(argv[1]);
        threadsPerContext = atoi(argv[2]);
    }

    std::cout << "I/O contexts          : " << numOfIOContexts << std::endl;
    std::cout << "Threads per context   : " << threadsPerContext << std::endl;
    std::vector<std::future<void>> futures;

    const unsigned short port = 4008;
    std::shared_ptr<boost::asio::io_context> context(new boost::asio::io_context());
    std::shared_ptr<boost::asio::io_context> repo_context(new boost::asio::io_context());
    boost::asio::executor_work_guard<decltype(repo_context->get_executor())> workGuard{repo_context->get_executor()};
    Server server(numOfIOContexts, threadsPerContext, *context, *repo_context, port);
    server.run();

    return 0;
}
