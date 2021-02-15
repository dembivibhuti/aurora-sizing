//
// Created by rahul on 02/02/21.
//

#ifndef MIDDLEWARE_REPOSITORY_H
#define MIDDLEWARE_REPOSITORY_H

#include <tao/pq.hpp>
#include <tao/pq/connection_pool.hpp>
#include <vector>

typedef std::shared_ptr<tao::pq::basic_connection_pool<tao::pq::parameter_text_traits>> ConnectionPool;

static const char *const SELECT_OBJ = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, length(mem), mem from objects where nameLower = '";

static const char *const END_QUOTE = "'";

class Repository {
public:
    Security *get_security(std::string &name) {
        const auto conn = pool->connection();
        const auto rs = conn->execute(SELECT_OBJ + name + END_QUOTE);
        //const auto rs = pool->execute(SELECT_OBJ + name + END_QUOTE);
        auto row = rs.at(0);
        Security *security = new Security;
        security->name = row[0].as<std::string>();
        security->type = row[1].as<int>();
        security->lastTransactionId = row[2].as<int>();
        security->timeUpdated = 10001;
        security->updateCount = row[4].as<int>();
        security->dateCreated = row[5].as<short>();
        security->dbIDUpdated = row[6].as<short>();
        security->versionInfo = row[7].as<short>();
        security->blobSize = row[8].as<int>();
        security->blob = row.get(9);
        return security;
    }

    std::vector<std::string>* lookup(std::string &prefix, short &count) {
        std::vector<std::string> *objects = new std::vector<std::string>();
        const auto conn = pool->connection();
        auto query = "select name from objects where nameLower >= '" + prefix +"' order by nameLower LIMIT " + std::to_string(count);
        //const auto rs = pool->execute(query);
        const auto rs = conn->execute(query);
        for( const auto& row : rs ) {
            objects->emplace_back(row[0].as< std::string >());
        }
        return objects;
    }

    static Repository *getInstance();

private:

    Repository() {
        pool = tao::pq::connection_pool::create("postgresql://postgres:postgres@database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com:5432/postgres");
        //pool = tao::pq::connection_pool::create("dbname=rahul");
        const auto conn = pool->connection();
        conn->prepare("get_sec",
                      "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, length(mem), mem from objects where nameLower = $1");
    }

    ConnectionPool pool;
};

Repository *Repository::getInstance() {
    static Repository instance;
    return &instance;
}

#endif //MIDDLEWARE_REPOSITORY_H
