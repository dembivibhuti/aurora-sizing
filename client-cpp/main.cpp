#include <iostream>
#include "client/client.h"
#include <thread>
#include <prometheus/counter.h>
#include <prometheus/exposer.h>
#include <prometheus/registry.h>
#include <chrono>

struct Gauges {
    prometheus::Family<prometheus::Gauge> &getByName;
    prometheus::Family<prometheus::Gauge> &lookup;
    prometheus::Family<prometheus::Counter> &clients;
    prometheus::Family<prometheus::Counter> &getByNameCount;

    prometheus::Gauge &getByNameMetric(int id) {
        return getByName.Add({{"number", std::to_string(id)}});
    }

    prometheus::Gauge &getLookupMetric(int id) {
        return lookup.Add({{"number", std::to_string(id)}});
    }

    prometheus::Counter &getClientCounter(int id) {
        return clients.Add({{"number", std::to_string(id)}});
    }

    prometheus::Counter &getByNameCounter(int id) {
        return getByNameCount.Add({{"number", std::to_string(id)}});
    }
};

std::string getRandomSecNamePrefix() {
    return std::to_string(rand() % (999 - 100 + 1) + 100);
}

void work(int id, Gauges &gauges, const std::string &host, const std::string &port) {
    auto &get_obj_ctr = gauges.getByNameMetric(id);
    auto &lookup_ctr = gauges.getLookupMetric(id);
    auto &client_ctr = gauges.getClientCounter(1);
    auto &get_by_name_ctr = gauges.getByNameCounter(1);
    //auto &get_obj_ctr = metrics->getGetByNameGauge().Add({{"number", std::to_string(id)}});
    // Create number of clients specified
    boost::asio::io_service service;

    Client client(host.c_str(), port.c_str(), service);
    client.attach();
    client_ctr.Increment();
    std::string pattern = getRandomSecNamePrefix();
    // Lookup
    while (true) {
        auto start = std::chrono::steady_clock::now();
        std::vector<std::string> *names = client.lookup(pattern, 75);
        auto end = std::chrono::steady_clock::now();
        const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
        lookup_ctr.Set(count);

        if (names->size() < 75) {
            pattern = getRandomSecNamePrefix();
            //std::cout << "Got Less than 75 Recs in Lookup. Starting over  " << pattern <<  std::endl;
            delete names;
            continue;
        }

        for (int i = 0; i < 400; i++) {
            for (const auto &name: *names) {
                auto start = std::chrono::steady_clock::now();
                Security *pSecurity = client.getSecurity(name);
                auto end = std::chrono::steady_clock::now();
                get_by_name_ctr.Increment();
                const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
                get_obj_ctr.Set(count);
                delete pSecurity;
            }
        }
        pattern = names->back();
        delete names;
    }
}


int main(int argc, char *argv[]) {
    if (argc != 4) {
        std::cerr << "Usage " << std::endl;
        std::cerr << "client <server host> <server port> <number_of_instances>" << std::endl;
        return -1;
    }
    std::string host = argv[1];
    std::string port = argv[2];
    int instances = std::stoi(argv[3]);
    // create an http server running on port 8080
    prometheus::Exposer exposer{boost::asio::ip::host_name() + ":8080"};
    //prometheus::Exposer exposer{"127.0.0.1:8080"};
    // create a metrics registry
    // @note it's the users responsibility to keep the object alive
    auto registry = std::make_shared<prometheus::Registry>();
    auto &getByName = prometheus::BuildGauge()
            .Name("get_object")
            .Register(*registry);
    auto &lookup = prometheus::BuildGauge()
            .Name("lookup_by_name")
            .Register(*registry);

    auto &client_counter = prometheus::BuildCounter()
            .Name("client_count")
            .Register(*registry);

    auto &getByNameCount = prometheus::BuildCounter()
            .Name("get_by_name_count")
            .Register(*registry);

    exposer.RegisterCollectable(registry);

    Gauges gauges{getByName, lookup, client_counter, getByNameCount};

    std::vector<std::thread> clients;
    for (int i = 0; i < instances; i++) {
        clients.push_back(std::thread(work, i, std::ref(gauges), host, port));
    }
    for (auto &client : clients) {
        client.join();
    }
    return 0;
}
