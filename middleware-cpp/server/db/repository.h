//
// Created by rahul on 02/02/21.
//

#ifndef MIDDLEWARE_REPOSITORY_H
#define MIDDLEWARE_REPOSITORY_H

/*#include <tao/pq.hpp>
#include <tao/pq/connection_pool.hpp>*/
#include <vector>

#include <ozo/request.h>
#include <ozo/connection_info.h>
#include <ozo/shortcuts.h>
#include <boost/asio.hpp>
#include <boost/asio/io_service.hpp>
#include <boost/asio/spawn.hpp>
#include <ozo/pg/types/bytea.h>
#include <ozo/connection_pool.h>
#include "repository.h"


//typedef std::shared_ptr<tao::pq::basic_connection_pool<tao::pq::parameter_text_traits>> ConnectionPool;

static const char *const GET_SEC = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = $1";

auto get_connection_pool() {
    //const ozo::connection_info connection_info("dbname=rahul");
    const ozo::connection_info connection_info("postgresql://postgres:postgres@database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com:5432/postgres");
    ozo::connection_pool_config connection_pool_config;
    connection_pool_config.capacity = 10000;
    connection_pool_config.queue_capacity = 128;
    connection_pool_config.idle_timeout = std::chrono::seconds(60);
    connection_pool_config.lifespan = std::chrono::hours(24);
    ozo::connection_pool pool(connection_info, connection_pool_config);
    std::cout << "Pool" << std::endl;
    return pool;
}

static ozo::connection_pool pool = get_connection_pool();

void printPoolStats() {
    std::cout << "---------------------------------------" << std::endl;
    std::cout << "Size " << pool.stats().size << std::endl;
    std::cout << "Available " << pool.stats().available << std::endl;
    std::cout << "Used " << pool.stats().used << std::endl;
    std::cout << "Queue size " << pool.stats().queue_size << std::endl;
    std::cout << "---------------------------------------" << std::endl;
}

class Repository {
public:

    template<typename T>
    void get_security_async(boost::asio::io_context &dbContext,
                            std::string &name,
                            T handler, boost::shared_ptr<Connection> connPtr, void *ptr) {
        //printPoolStats();
        using namespace ozo::literals;
        //const auto query = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = '"_SQL + name + "'";

        const auto query =
                "select name, typeId, lastTransaction, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = "_SQL
                + name + ""_SQL;

        std::shared_ptr<ozo::rows_of<std::string, int, int64_t, int64_t, int, int, int, ozo::pg::bytea>> rows(
                new ozo::rows_of<std::string, int, int64_t, int64_t, int, int, int, ozo::pg::bytea>);
        ozo::request(pool[dbContext], query, ozo::into(*(rows.get())),
                     [rows, &handler, connPtr, ptr](ozo::error_code ec, auto conn) {
                         if (ec) {
                             std::cerr << ec.message();
                             std::cerr << " | " << ozo::error_message(conn);
                             if (!ozo::is_null_recursive(conn)) {
                                 std::cerr << " | " << ozo::get_error_context(conn);
                             }
                             return;
                         };

                         assert(ozo::connection_good(conn));
                         if ((*rows.get()).size() > 0) {
                             auto &row = (*rows.get()).front();
                             Security *security = new Security;
                             security->name = std::get<0>(row);
                             security->type = std::get<1>(row);
                             security->lastTransactionId = std::get<2>(row);
                             security->timeUpdated = 10001;
                             security->updateCount = std::get<3>(row);
                             security->dateCreated = std::get<4>(row);
                             security->dbIDUpdated = std::get<5>(row);
                             security->versionInfo = (short) std::get<6>(row);
                             std::vector<char> blob = std::get<7>(row);
                             security->blobSize = blob.size();
                             security->blob = blob.data();
                             handler(ptr, connPtr, security);
                         } else {
                             handler(ptr, connPtr, nullptr);
                         }

                     });
    }

    template<typename T>
    void lookup_async(boost::asio::io_context &dbContext,
                      std::string &prefix, short &count,
                      T handler, boost::shared_ptr<Connection> connPtr,
                      void *ptr) {

        using namespace ozo::literals;
        //const auto query = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = '"_SQL + name + "'";
        const auto query = "select name from objects where nameLower >='"_SQL
                           + prefix + "' LIMIT "_SQL + count;

        std::shared_ptr<ozo::rows_of<std::string>> rows(new ozo::rows_of<std::string>);
        ozo::request(pool[dbContext], query, ozo::into(*(rows.get())),
                     [rows, &handler, connPtr, ptr](ozo::error_code ec, auto conn) {
                         if (ec) {
                             std::cerr << ec.message();
                             std::cerr << " | " << ozo::error_message(conn);
                             if (!ozo::is_null_recursive(conn)) {
                                 std::cerr << " | " << ozo::get_error_context(conn);
                             }
                             return;
                         };
                         assert(ozo::connection_good(conn));
                         std::vector<std::string> *objects = new std::vector<std::string>();
                         for (auto &value: *rows.get()) {
                             objects->emplace_back(std::get<0>(value));
                         }
                         handler(ptr, connPtr, objects);
                     });
    }

    static Repository *getInstance();

private:

    Repository() {
    }
};


Repository *Repository::getInstance() {
    static Repository instance;
    return &instance;
}

#endif //MIDDLEWARE_REPOSITORY_H
