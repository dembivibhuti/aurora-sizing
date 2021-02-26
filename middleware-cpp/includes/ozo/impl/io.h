#pragma once

#include <ozo/pg/handle.h>
#include <ozo/connection.h>
#include <ozo/error.h>
#include <ozo/io/binary_query.h>
#include <ozo/detail/bind.h>
#include <ozo/impl/result_status.h>
#include <ozo/impl/result.h>

#include <boost/asio/post.hpp>

#include <libpq-fe.h>

namespace ozo::impl {

/**
* Query states. The values similar to PQflush function results.
* This state is used to synchronize async query parameters
* send process with async query result receiving process.
*/
enum class query_state : int {
    error = -1,
    send_finish = 0,
    send_in_progress = 1
};

namespace pq {

template <typename T>
inline auto pq_start_connection(const T&, const std::string& conninfo) {
    static_assert(Connection<T>, "T must be a Connection");
    return ozo::pg::make_safe(PQconnectStart(conninfo.c_str()));
}

} // namespace pq

template <typename T>
inline auto start_connection(T& conn, const std::string& conninfo) {
    static_assert(Connection<T>, "T must be a Connection");
    using pq::pq_start_connection;
    return pq_start_connection(unwrap_connection(conn), conninfo);
}

template <typename T>
inline int connect_poll(T& conn) {
    static_assert(Connection<T>, "T must be a Connection");
    return PQconnectPoll(get_native_handle(conn));
}

template <typename T>
inline int send_query_params(T& conn, const binary_query& q) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    return PQsendQueryParams(get_native_handle(conn),
                q.text(),
                q.params_count(),
                q.types(),
                q.values(),
                q.lengths(),
                q.formats(),
                int(result_format::binary)
            );
}

template <typename T>
inline error_code set_nonblocking(T& conn) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    if (PQsetnonblocking(get_native_handle(conn), 1)) {
        return error::pg_set_nonblocking_failed;
    }
    return {};
}

template <typename T>
inline error_code consume_input(T& conn) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    if (!PQconsumeInput(get_native_handle(conn))) {
        return error::pg_consume_input_failed;
    }
    return {};
}

template <typename T>
inline bool is_busy(T& conn) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    return PQisBusy(get_native_handle(conn));
}

template <typename T>
inline query_state flush_output(T& conn) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    return static_cast<query_state>(PQflush(get_native_handle(conn)));
}

template <typename T>
inline decltype(auto) get_result(T& conn) noexcept {
    static_assert(Connection<T>, "T must be a Connection");
    return ozo::pg::make_safe(PQgetResult(get_native_handle(conn)));
}

template <typename T>
inline ExecStatusType result_status(const T& res) noexcept {
    return PQresultStatus(std::addressof(res));
}

template <typename T>
inline error_code result_error(const T& res) noexcept {
    if (auto s = PQresultErrorField(std::addressof(res), PG_DIAG_SQLSTATE)) {
        return sqlstate::make_error_code(std::strtol(s, nullptr, 36));
    }
    return error::no_sql_state_found;
}

} // namespace ozo::impl
