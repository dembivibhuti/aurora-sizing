#pragma once

#include <ozo/type_traits.h>
#include <ozo/io/send.h>
#include <ozo/io/recv.h>
#include <ozo/io/size_of.h>
#include <boost/hana/adapt_struct.hpp>
#include <boost/hana/members.hpp>
#include <boost/hana/size.hpp>
#include <boost/hana/fold.hpp>
#include <boost/fusion/adapted/struct/adapt_struct.hpp>
#include <boost/fusion/include/fold.hpp>

namespace ozo::detail {

struct pg_composite {
    BOOST_HANA_DEFINE_STRUCT(pg_composite,
        (std::int32_t, count)
    );
};

} // namespace ozo::detail

namespace ozo {

namespace detail {

constexpr auto size_of(const pg_composite&) noexcept {
    return size_constant<sizeof(std::int32_t)>{};
}

template <typename T>
constexpr auto fields_number(const T& v) -> Require<FusionSequence<T>&&!HanaStruct<T>, size_type> {
    return size_type(fusion::size(v));
}

template <typename T>
constexpr auto fields_number(const T& v) -> Require<HanaStruct<T>, size_type> {
    return size_type(hana::value(hana::size(hana::members(v))));
}

template <typename T>
constexpr auto data_size(const T& v) -> Require<FusionSequence<T>&&!HanaStruct<T>, size_type> {
    return fusion::fold(v, size_type(0),
        [&] (auto r, const auto& item) { return r + frame_size(item); });
}

template <typename T>
constexpr auto data_size(const T& v)  -> Require<HanaStruct<T>, size_type>{
    return hana::unpack(hana::members(v),
        [&] (const auto& ...x) { return (frame_size(x) + ... + 0); });
}

template <typename T>
struct size_of_composite {
    static constexpr auto apply(const T& v) {
        using ozo::size_of;
        constexpr const auto header_size = size_of(detail::pg_composite{});
        return header_size +  data_size(v);
    }
};

template <typename T, typename Func>
constexpr auto for_each_member(T&& v, Func&& f) -> Require<FusionSequence<T>&&!HanaStruct<T>> {
    fusion::for_each(std::forward<T>(v), std::forward<Func>(f));
}

template <typename T, typename Func>
constexpr auto for_each_member(T&& v, Func&& f)  -> Require<HanaStruct<T>>{
    hana::for_each(hana::members(std::forward<T>(v)), std::forward<Func>(f));
}

template <typename T>
struct size_of_impl_dispatcher<T, Require<Composite<T>>> { using type = size_of_composite<std::decay_t<T>>; };

template <typename T>
struct send_composite_impl {
    template <typename OidMap>
    static ostream& apply(ostream& out, const OidMap& oid_map, const T& in) {
        write(out, pg_composite{fields_number(in)});
        for_each_member(in, [&] (const auto& v) {
            send_frame(out, oid_map, v);
        });
        return out;
    }
};

template <typename T>
struct send_impl_dispatcher<T, Require<Composite<T>>> { using type = send_composite_impl<std::decay_t<T>>; };

template <typename T>
inline Require<Composite<T>> read_and_verify_header(istream& in, const T& v) {
    pg_composite header;
    read(in, header);
    if (header.count != fields_number(v)) {
        throw system_error(error::bad_composite_size,
            "incoming composite fields count " + std::to_string(header.count)
            + " does not match fields count " + std::to_string(fields_number(v))
            + " of type " + boost::core::demangle(typeid(v).name()));
    }
}

template <typename T>
struct recv_fusion_adapted_composite_impl {
    template <typename OidMap>
    static istream& apply(istream& in, size_type, const OidMap& oid_map, T& out) {
        read_and_verify_header(in, out);
        fusion::for_each(out, [&] (auto& v) {
            recv_frame(in, oid_map, v);
        });
        return in;
    }
};

template <typename T>
struct recv_hana_adapted_composite_impl {
    template <typename OidMap>
    static istream& apply(istream& in, size_type, const OidMap& oid_map, T& out) {
        read_and_verify_header(in, out);
        hana::for_each(hana::keys(out), [&in, &out, &oid_map](auto key) {
            recv_frame(in, oid_map, hana::at_key(out, key));
        });
        return in;
    }
};

template <typename T>
struct recv_impl_dispatcher<T, Require<Composite<T>&&FusionSequence<T>>> {
    using type = recv_fusion_adapted_composite_impl<std::decay_t<T>>;
};

template <typename T>
struct recv_impl_dispatcher<T, Require<Composite<T>&&!FusionSequence<T>>> {
    using type = recv_hana_adapted_composite_impl<std::decay_t<T>>;
};

} // namespace detail
} //namespace ozo
