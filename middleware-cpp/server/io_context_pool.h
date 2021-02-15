//
// Created by rahul on 21/01/21.
//

#ifndef MIDDLEWARE_SERVER_IO_CONTEXT_POOL_H_
#define MIDDLEWARE_SERVER_IO_CONTEXT_POOL_H_

#include <vector>
#include <list>
#include <boost/asio.hpp>

class IOContextPool {
public:
    explicit IOContextPool(size_t poolSize, size_t threadsPerContext) : threadsPerContext(threadsPerContext), nextIOContext(0) {
        if (poolSize == 0)
            throw std::runtime_error("IO Context Pool size is 0");
        for (std::size_t i = 0; i < poolSize; ++i) {
            IOContextPtr ioContextPtr(new boost::asio::io_context);
            ioContexts.push_back(ioContextPtr);
            work.push_back(boost::asio::make_work_guard(*ioContextPtr));
        }
    }

    void run() {
        std::vector<std::future<void>> futures;

        for (int i = 0; i < ioContexts.size(); i++) {
            for (int j = 0; j < threadsPerContext; ++j) {
                auto future = std::async([this, i] {
                    this->ioContexts[i]->run();
                });
                futures.push_back(std::move(future));
            }
        }

        std::for_each(futures.begin(), futures.end(), [](std::future<void> &fut) {
            fut.wait();
        });
    }

    boost::asio::io_context &getIOContext() {
        boost::asio::io_context &context = *ioContexts[nextIOContext];
        ++nextIOContext;
        if (nextIOContext == ioContexts.size())
            nextIOContext = 0;
        return context;
    }

private:
    typedef std::shared_ptr<boost::asio::io_context> IOContextPtr;
    typedef boost::asio::executor_work_guard<boost::asio::io_context::executor_type> ioContextWork;
    std::vector<IOContextPtr> ioContexts;
    std::list<ioContextWork> work;
    std::size_t nextIOContext;
    std::size_t threadsPerContext;
};

#endif //MIDDLEWARE_SERVER_IO_CONTEXT_POOL_H_
