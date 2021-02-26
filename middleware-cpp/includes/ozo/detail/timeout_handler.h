#pragma once

#include <ozo/asio.h>
#include <ozo/error.h>

namespace ozo::detail {

template <typename Connection, typename Allocator>
struct cancel_io {
    Connection conn_;
    Allocator allocator_;

    cancel_io(Connection&& conn, const Allocator& allocator)
    : conn_(std::forward<Connection>(conn)), allocator_(allocator) {}

    void operator() (error_code) const {
        conn_.cancel();
    }

    using allocator_type = Allocator;

    allocator_type get_allocator() const noexcept { return allocator_;}
};

template <typename Connection, typename Allocator>
cancel_io(Connection&& conn, const Allocator& allocator) -> cancel_io<Connection, Allocator>;

} // namespace ozo::detail
