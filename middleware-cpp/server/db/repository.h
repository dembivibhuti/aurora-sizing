//
// Created by rahul on 02/02/21.
//

#ifndef MIDDLEWARE_REPOSITORY_H
#define MIDDLEWARE_REPOSITORY_H

#include <tao/pq.hpp>
#include <tao/pq/connection_pool.hpp>
#include <vector>

typedef std::shared_ptr<tao::pq::basic_connection_pool<tao::pq::parameter_text_traits>> ConnectionPool;

static const char *const SELECT_OBJ = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = '";

static const char *const END_QUOTE = "'";

namespace tao::pq {
    class bytea  // NOLINT
    {
    private:
        std::size_t m_size;
        unsigned char *m_data;

    public:
        explicit bytea(const char *value)
                : m_size(0), m_data(PQunescapeBytea((unsigned char *) value, &m_size))  //NOLINT
        {
            if (m_data == nullptr) {
                throw std::bad_alloc();  // LCOV_EXCL_LINE
            }
        }

        ~bytea() {
            PQfreemem(m_data);
        }

        bytea(const bytea &) = delete;

        auto operator=(const bytea &) -> bytea & = delete;

        [[nodiscard]] auto size() const noexcept {
            return m_size;
        }

        [[nodiscard]] auto data() const noexcept -> const unsigned char * {
            return m_data;
        }

        [[nodiscard]] auto operator[](const std::size_t idx) const noexcept -> unsigned char {
            return m_data[idx];
        }
    };

    template<>
    struct result_traits<bytea> {
        [[nodiscard]] static auto from(const char *value) {
            return bytea(value);
        }
    };

}  // namespace tao::pq

class Repository {
public:
    Security *get_security(std::string &name, Gauge *gauge) {
        auto start = std::chrono::steady_clock::now();

        const auto conn = pool->connection();
        auto end = std::chrono::steady_clock::now();
        const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
        gauge->dbConnection.Set(count);

        start = std::chrono::steady_clock::now();

        conn->prepare("get_sec",
                      "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = $1");

        const auto rs = conn->execute("get_sec", name);
        if (rs.size() > 0) {
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
            auto blob = row[8].as<tao::pq::bytea>();
            security->blobSize = blob.size();
            security->blob = blob.data();


            end = std::chrono::steady_clock::now();
            const long count1 = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
            gauge->queryExec.Set(count1);

            return security;
        }

        return nullptr;
    }

    std::vector<std::string> *lookup(std::string &prefix, short &count) {
        std::vector<std::string> *objects = new std::vector<std::string>();
        const auto conn = pool->connection();
        conn->prepare("lookup",
                      "select name from objects where nameLower >= $1 order by nameLower LIMIT $2");

        /*auto query = "select name from objects where nameLower >= '" + prefix + "' order by nameLower LIMIT " +
                     std::to_string(count);*/
        //const auto rs = pool->execute(query);
        const auto rs = conn->execute("lookup", prefix, count);
        for (const auto &row : rs) {
            objects->emplace_back(row[0].as<std::string>());
        }
        return objects;
    }

    static Repository *getInstance();

private:

    Repository() {
        pool = tao::pq::connection_pool::create(
                "postgresql://postgres:postgres@database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com:5432/postgres");
        //pool = tao::pq::connection_pool::create("dbname=rahul");
        /* const auto conn = pool->connection();
         conn->prepare("get_sec",
                       "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = $1");
 */
    }

    ConnectionPool pool;
};

Repository *Repository::getInstance() {
    static Repository instance;
    return &instance;
}

#endif //MIDDLEWARE_REPOSITORY_H
