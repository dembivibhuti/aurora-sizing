#pragma once

#include <ozo/pg/definitions.h>
#include <ozo/io/send.h>
#include <ozo/io/recv.h>

#include <string>

namespace ozo::pg {

class jsonb {
    friend send_impl<jsonb>;
    friend recv_impl<jsonb>;
    friend size_of_impl<jsonb>;

public:
    jsonb() = default;

    jsonb(std::string raw_string) noexcept
        : value(std::move(raw_string)) {}

    std::string raw_string() const & noexcept {
        return value;
    }

    std::string raw_string() && noexcept {
        return std::move(value);
    }

private:
    std::string value;
};

} // namespace ozo::pg

namespace ozo {

template <>
struct size_of_impl<pg::jsonb> {
    static auto apply(const pg::jsonb& v) noexcept {
        return std::size(v.value) + 1;
    }
};

template <>
struct send_impl<pg::jsonb> {
    template <typename OidMap>
    static ostream& apply(ostream& out, const OidMap&, const pg::jsonb& in) {
        const std::int8_t version = 1;
        write(out, version);
        return write(out, in.value);
    }
};

template <>
struct recv_impl<pg::jsonb> {
    template <typename OidMap>
    static istream& apply(istream& in, size_type size, const OidMap&, pg::jsonb& out) {
        if (size < 1) {
            throw std::range_error("data size " + std::to_string(size) + " is too small to read jsonb");
        }
        std::int8_t version;
        read(in, version);
        out.value.resize(static_cast<std::size_t>(size - 1));
        return read(in, out.value);
    }
};

} // namespace ozo

OZO_PG_BIND_TYPE(ozo::pg::jsonb, "jsonb")
