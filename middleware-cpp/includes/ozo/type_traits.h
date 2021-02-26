#pragma once

#include <ozo/detail/float.h>
#include <ozo/core/strong_typedef.h>
#include <ozo/core/nullable.h>
#include <ozo/core/unwrap.h>
#include <ozo/core/concept.h>
#include <ozo/optional.h>

#include <libpq-fe.h>

#include <boost/hana/at_key.hpp>
#include <boost/hana/insert.hpp>
#include <boost/hana/string.hpp>
#include <boost/hana/map.hpp>
#include <boost/hana/pair.hpp>
#include <boost/hana/type.hpp>
#include <boost/hana/not_equal.hpp>

#include <memory>
#include <string>
#include <string_view>
#include <vector>
#include <type_traits>

/**
 * @defgroup group-type_system Type system
 * @brief Database-related type system of the library.
 */

/**
 * @defgroup group-type_system-types Types
 * @ingroup group-type_system
 * @brief Database-related type system types.
 */

/**
 * @defgroup group-type_system-concepts Concepts
 * @ingroup group-type_system
 * @brief Database-related type system concepts.
 */

/**
 * @defgroup group-type_system-constants Constants
 * @ingroup group-type_system
 * @brief Database-related type system constants.
 */

/**
 * @defgroup group-type_system-functions Functions
 * @ingroup group-type_system
 * @brief Database-related type system functions.
 */

/**
 * @defgroup group-type_system-mapping Mapping
 * @ingroup group-type_system
 * @brief Types mapping C++ to PostgreSQL
 */
namespace ozo {

namespace hana = boost::hana;
using namespace hana::literals;

namespace fusion = boost::fusion;
/**
 * @brief PostgreSQL OID type - object identifier
 * @ingroup group-type_system-types
 */
using oid_t = ::Oid;

/**
 * @brief OID constant type.
 *
 * This type is like `std::bool_constant` - used to enable type level
 * resolving of constants. All `BuiltIn` types uses this template
 * specialization for their OID constants.
 *
 * @ingroup group-type_system-types
 * @tparam Oid --- OID value
 */
template <oid_t Oid>
struct oid_constant : std::integral_constant<oid_t, Oid> {};

/**
 * @brief Type for non initialized OID
 * @ingroup group-type_system-types
 */
using null_oid_t = oid_constant<0>;

/**
 * @brief Constant for empty OID
 * @ingroup group-type_system-constants
 */
inline constexpr null_oid_t null_oid;

template <typename T>
struct is_array : std::false_type {};

/**
 * @brief %Array concept represents PostgreSQL array.
 *
 * `%Array` is a Range or Container with fixed or dynamic size.
 * For the type or template which supposed to represent an array `ozo::is_array<>`
 * should be specialized as `std::true_type`.
 *
 * @par Requirements
 *
 * `#include <ozo/io/array.h>` should be included for arrays support.
 *
 * @par Concrete models
 *
 * @ref group-ext-std-vector, @ref group-ext-std-list, @ref group-ext-std-array
 *
 * @par Example
 *
 * The `std::vector<>` is a dynamic size array and its definition may look like this.
 *
 * @code
namespace ozo {
template <typename ...Ts>
struct is_array<std::vector<Ts...>> : std::true_type {};
} // namespace ozo
 * @endcode
 *
 * For the `std::array<>` the definition is slightely different. The `std::array<>`
 * is a fixed-size container, so it should be pointed via `ozo::fit_array_size_impl`
 * specialization.
 *
 * @code
namespace ozo {
template <typename T, std::size_t size>
struct is_array<std::array<T, size>> : std::true_type {};

template <typename T, std::size_t S>
struct fit_array_size_impl<std::array<T, S>> {
    static void apply(const std::array<T, S>& array, size_type size) {
        if (size != static_cast<size_type>(array.size())) {
            throw system_error(error::bad_array_size);
        }
    }
};
} // namespace ozo
 * @endcode
 *
 * @concept{Array}
 * @ingroup group-type_system-concepts
 */
//! @cond
template <typename T>
inline constexpr auto Array = is_array<std::decay_t<T>>::value;
//! @endcond

namespace definitions {
template <typename T>
struct type;

template <typename T>
struct array;
} // namespace definitions

namespace detail {

template <typename Traits, typename = std::void_t<>>
struct is_type_traits_defined : std::false_type {};

template <typename T>
struct is_type_traits_defined<T, Require<
    !std::is_void_v<typename T::name>
>> : std::true_type {};

template <typename T>
inline auto get_type_traits(const T&) {
    using type = unwrap_type<T>;
    if constexpr (Array<type>) {
        using value_type = unwrap_type<typename type::value_type>;
        if constexpr (is_type_traits_defined<definitions::array<value_type>>::value) {
            return definitions::array<value_type>{};
        } else {
            return;
        }
    } else {
        if constexpr (is_type_traits_defined<definitions::type<type>>::value) {
            return definitions::type<type>{};
        } else {
            return;
        }
    }
}

} // namespace detail

/**
 * @brief Type traits template forward declaration.
 * @ingroup group-type_system-types
 *
 * Type traits contains information
 * related to it's representation in the database. There are two different
 * kind of traits - built-in types with constant OIDs and custom types with
 * database depent OIDs. The functions below describe neccesary traits.
 * For built-in types traits will be defined there. For custom types user
 * must define traits.
 *
 * @tparam T --- type to examine
 */
#ifdef OZO_DOCUMENTATION
template <typename T>
struct type_traits {
    using name = <implementation defined>; //!< `boost::hana::string` with name of the type in a database
    using oid = <implementation defined>; //!< `ozo::oid_constant` with Oid of the built-in type or null_oid_t for non built-in type
    using size = <implementation defined>; //!< `ozo::size_constant` with size of the type object in bytes or `ozo::dynamic_size` in other case
};
#endif

template <typename T>
using type_traits = decltype(detail::get_type_traits(std::declval<T>()));

/**
 * @brief Condition indicates if type has corresponding type traits for PostgreSQL
 *
 * @concept{HasDefinition}
 * @ingroup group-core-concepts
 */
//! @cond
template <typename T>
inline constexpr auto HasDefinition = !std::is_void_v<type_traits<T>>;
//! @endcond

template <typename T>
struct is_composite : std::bool_constant<
    HanaStruct<T> || FusionAdaptedStruct<T>
> {};

/**
 * @brief %Composite concept represents PostgreSQL composite types.
 *
 * Representation means that you can obtain PostgreSQL composite into the type.
 *
 * @par Definition
 *
 * Any `Boost.Hana` or `Boost.Fusion` adapted structure or class is a composite.
 * Any `HanaSequence` or `FusionSequence` may be represented as a composite if
 * `ozo::is_composite<>` is specialized for this type as `std::true_type`.
 *
 * @par Requirements
 *
 * `#include <ozo/io/composite.h>` should be included for the composite support.
 *
 * @par Concrete models
 *
 * @ref group-ext-std-tuple, @ref group-ext-std-pair, @ref group-ext-boost-tuple .
 *
 * @concept{Composite}
 * @ingroup group-type_system-concepts
 */
//! @cond
template <typename T>
inline constexpr auto Composite = is_composite<std::decay_t<T>>::value;
//! @endcond

/**
 * @brief PostgreSQL size type
 * @ingroup group-type_system-types
 */
using size_type = std::int32_t;

/**
 * @brief Object size constant type
 *
 * This type is like `std::bool_constant` - used to enable type level
 * resolving of constants. All `BuiltIn` types uses this template
 * specialization for their size constants.
 *
 * @tparam n --- size in bytes
 * @ingroup group-type_system-types
 */
template <size_type n>
struct size_constant : std::integral_constant<size_type, n> {};

inline constexpr size_constant<-1> null_state_size;

/**
* Helpers to make size trait constant
*     bytes - makes fixed size trait
*     dynamic_size - makes dynamic size trait
*/
template <size_type n>
using bytes = size_constant<n>;
struct dynamic_size : size_constant<-1> {};

template <typename T, typename = std::void_t<>>
struct is_built_in : std::false_type {};

template <typename T>
struct is_built_in<T, std::enable_if_t<
    !std::is_void_v<typename type_traits<T>::oid>>
> : std::true_type {};

/**
 * @brief Condition indicates if the specified type is built-in for PG
 * @ingroup group-type_system-concepts
 * @tparam T --- type to check
 * @hideinitializer
 */
template <typename T>
inline constexpr bool BuiltIn = is_built_in<std::decay_t<T>>::value;

template <typename T>
struct is_dynamic_size : std::is_same<typename type_traits<T>::size, dynamic_size> {};

/**
 * @brief Condition indicates if the specified type is has dynamic size
 * @ingroup group-type_system-concepts
 * @tparam T --- type to check
 * @hideinitializer
 */
template <typename T>
inline constexpr auto DynamicSize = is_dynamic_size<std::decay_t<T>>::value;

/**
 * @brief Condition indicates if the specified type is has fixed size
 * @ingroup group-type_system-concepts
 * @tparam T --- type to check
 * @hideinitializer
 */
template <typename T>
inline constexpr auto StaticSize = !DynamicSize<T>;

/**
* @brief Function returns type name in Postgre SQL.
* @tparam T --- type
* @return `const char*` --- name of the type
* @ingroup group-type_system-functions
*/
template <typename T>
constexpr auto type_name() noexcept {
    static_assert(!std::is_void_v<type_traits<T>>,
        "no type_traits found for the type");
    constexpr auto name = typename type_traits<T>::name{};
    constexpr char const* retval = hana::to<char const*>(name);
    return retval;
}

/**
* @brief Function returns type name in Postgre SQL.
* @param T --- type
* @return `const char*` --- name of the type
* @ingroup group-type_system-functions
*/
template <typename T>
constexpr auto type_name(const T&) noexcept {return type_name<T>();}

namespace detail {

template <typename Name, typename Oid = void, typename Size = dynamic_size>
struct type_definition {
    using name = Name;
    using oid = Oid;
    using size = Size;
};

template <typename Type, typename Oid>
using array_definition = type_definition<
    decltype(typename type_traits<Type>::name{} + "[]"_s),
    Oid
>;

} // namespace detail
} // namespace ozo

#define OZO_PG_DEFINE_TYPE_(Type, Name, OidType, Size) \
    namespace ozo::definitions {\
    template <>\
    struct type<Type> : detail::type_definition<decltype(Name##_s), OidType, Size>{\
        static_assert(std::is_same_v<Size, dynamic_size>\
            || Size::value == null_state_size\
            || static_cast<size_type>(sizeof(Type)) == Size::value,\
            #Type " type size does not match to declared size");\
    };\
    }

#define OZO_PG_DEFINE_TYPE(Type, Name, Oid, Size) \
    OZO_PG_DEFINE_TYPE_(Type, Name, oid_constant<Oid>, Size)

#define OZO_PG_DEFINE_TYPE_ARRAY_(Type, OidType) \
    namespace ozo::definitions {\
    template <>\
    struct array<Type> : detail::array_definition<Type, OidType>{};\
    }

#define OZO_PG_DEFINE_TYPE_AND_ARRAY_(Type, Name, OidType, ArrayOidType, Size) \
    OZO_PG_DEFINE_TYPE_(Type, Name, OidType, Size) \
    OZO_PG_DEFINE_TYPE_ARRAY_(Type, ArrayOidType)

/**
 * @brief [[DEPRECATED]] Helper macro to define type mapping
 *
 * In general type mapping is provided via `ozo::definitions::type` and
 * `ozo::definitions::array` specialization.
 * To reduce the boilerplate code the macro exists.
 *
 * @note This macro is deprecated, use #OZO_PG_BIND_TYPE instead.
 * This macro should be called in the global namespace only.
 *
 * @param Type --- C++ type to be mapped to database type
 * @param Name --- string with name of database type
 * @param Oid --- oid for built-in type and `void` for custom type
 * @param ArrayOid --- oid for an array of built-in type and `void` for custom type
 * @param Size --- `bytes<N>` for fixed-size type (like integer, bigint and so on),
 * there N - size of the type in database, `dynamic_type` for dynamic size types (like `text`
 * `bytea` and so on)
 * @ingroup group-type_system-mapping
 */
#ifdef OZO_DOCUMENTATION
#define OZO_PG_DEFINE_TYPE_AND_ARRAY(Type, Name, Oid, ArrayOid, Size)
#else
#define OZO_PG_DEFINE_TYPE_AND_ARRAY(Type, Name, Oid, ArrayOid, Size) \
    OZO_PG_DEFINE_TYPE_AND_ARRAY_(Type, Name, oid_constant<Oid>, oid_constant<ArrayOid>, Size)
#endif

/**
 * @brief Helper macro to define custom type mapping
 *
 * In general type mapping is provided via `ozo::definitions::type` and
 * `ozo::definitions::array` specialization.
 *
 * @note This macro can be called in the global namespace only
 *
 * @param Type --- C++ type to be mapped to database type
 * @param Name --- string with name of database type
 * @param Size --- optional parameter, `bytes<N>` for fixed-size type (like integer, bigint and so on),
 * there N - size of the type in database, `dynamic_type` for dynamic size types (like `text`
 * `bytea` and so on), if parameter not pointed size type will be evaluated basing on C++ type size.
 *
 * ### Example
 *
 * Definition of user defined composite type may look like this:
 * @code
BOOST_FUSION_DEFINE_STRUCT((smtp), message,
    (std::int64_t, id)
    (std::string, from)
    (std::vector<std::string>, to)
    (std::optional<std::string>, subject)
    (std::optional<std::string>, text)
);

//...

OZO_PG_DEFINE_CUSTOM_TYPE(smtp::message, "code.message")
 * @endcode
 * @sa OZO_PG_BIND_TYPE
 * @ingroup group-type_system-mapping
 */
#ifdef OZO_DOCUMENTATION
#define OZO_PG_DEFINE_CUSTOM_TYPE(Type, Name [, Size])
#else
#define OZO_PG_DEFINE_CUSTOM_TYPE_IMPL_3(Type, Name, Size) \
    OZO_PG_DEFINE_TYPE_AND_ARRAY_(Type, Name, void, void, Size)

#define OZO_PG_DEFINE_CUSTOM_TYPE_IMPL_2(Type, Name) \
    OZO_PG_DEFINE_CUSTOM_TYPE_IMPL_3(Type, Name, dynamic_size)

#define OZO_PG_DEFINE_CUSTOM_TYPE(...)\
    BOOST_PP_OVERLOAD(OZO_PG_DEFINE_CUSTOM_TYPE_IMPL_,__VA_ARGS__)(__VA_ARGS__)
#endif

namespace ozo {

namespace detail {

template <typename ... T>
constexpr decltype(auto) register_types() noexcept {
    return hana::make_map(
        hana::make_pair(hana::type_c<T>, null_oid()) ...
    );
}

} // namespace detail

/**
 * @brief OidMap implementation type.
 * @ingroup group-type_system-types
 *
 * This type implements OidMap concept based on `boost::hana::map`.
 */
template <typename ...Ts>
struct oid_map_t {
    using impl_type = decltype(detail::register_types<Ts...>());

    impl_type impl;
};


template <typename T>
struct is_oid_map : std::false_type {};

template <typename ...Ts>
struct is_oid_map<oid_map_t<Ts...>> : std::true_type {};

/**
 * @brief Map of C++ types to corresponding PostgreSQL types OIDs
 *
 * @ingroup group-type_system-concepts
 * `OidMap` is needed to store information about C++ types and corresponding
 * custom database types' OIDs. For PostgreSQL built-in types no mapping is needed since their
 * `OID`s are defined in PostgreSQL sources. For custom types their `OID`s are defined
 * in a database.
 *
 * ###OidMap Definition
 *
 * `OidMap` `map` is an object for which these next statements are valid:
 *
 @code
 oid_t oid;
 //...
 set_type_oid<T>(map, oid);
 @endcode
 * Sets oid for type T in the map.
 *
 @code
 oid_t oid = type_oid<T>(map);
 @endcode
 * Returns oid value for type T from the map.
 *
@code
bool res = empty(map);
@endcode
 * Returns true if map has no types OIDs.
 *
 * @hideinitializer
 * @sa oid_map_t, register_types(), set_type_oid(), type_oid(), accepts_oid()
 */
template <typename T>
inline constexpr auto OidMap = is_oid_map<std::decay_t<T>>::value;

/**
 * @brief Provides #OidMap implementation for user-defined types.
 *
 * @ingroup group-type_system-functions
 * This function have to be used to provide information about custom types are being used
 * within requests for a `ConnectionSource`.
 *
 * ###Example
 *
 * @code
// User defined type
struct custom_type;

//...

// Providing type information and corresponding database type
OZO_PG_DEFINE_CUSTOM_TYPE(custom_type, "code.custom_type")

//...
// Creating ConnectionSource for futher requests to a database
const ozo::connection_info conn_source("...", register_types<custom_type>());
 * @endcode
 *
 * @return `oid_map_t` object.
 */
template <typename ...Ts>
constexpr oid_map_t<Ts...> register_types() noexcept {
    return {};
}

/**
 * @brief Type alias for empty #OidMap
 * @ingroup group-type_system-types
 */
using empty_oid_map = std::decay_t<decltype(register_types<>())>;

inline constexpr empty_oid_map empty_oid_map_c;

/**
* Function sets oid for a type in #OidMap.
* @ingroup group-type_system-functions
* @tparam T --- type to set oid for.
* @param map --- #OidMap to modify.
* @param oid --- OID to set.
*/
template <typename T, typename ...Ts>
inline void set_type_oid(oid_map_t<Ts...>& map, oid_t oid) noexcept {
    static_assert(!is_built_in<T>::value, "type must not be built-in");
    map.impl[hana::type_c<T>] = oid;
}

/**
* @brief Function returns oid for a type from #OidMap.
* @ingroup group-type_system-functions
* @tparam T --- type to get OID for.
* @param map --- #OidMap to get OID from.
* @return oid_t --- OID of the type
*/
#ifdef OZO_DOCUMENTATION
template <typename T, typename OidMap>
oid_t type_oid(const OidMap& map) noexcept;
#else
template <typename T, typename ...Ts>
inline auto type_oid(const oid_map_t<Ts...>& map) noexcept
        -> Require<!BuiltIn<T>, oid_t> {
    constexpr auto key = hana::type_c<unwrap_type<T>>;
    static_assert(decltype(hana::find(map.impl, key) != hana::nothing)::value,
        "type OID for T can not be found in the OidMap, it should be registered via register_type()");
    return map.impl[key];
}

template <typename T, typename OidMap>
constexpr auto type_oid(const OidMap&) noexcept
        -> Require<BuiltIn<T>, oid_t> {
    static_assert(ozo::OidMap<OidMap>, "map should model OidMap concept");
    return typename type_traits<T>::oid();
}
#endif

/**
* @brief Function returns oid for type from #OidMap.
* @ingroup group-type_system-functions
* @param map --- #OidMap to get OID from.
* @param v --- object for which type's OID will return.
* @return oid_t --- OID of the type
*/
#ifdef OZO_DOCUMENTATION
template <typename T, typename OidMap>
oid_t type_oid(const OidMap& map, const T& v) noexcept
#else
template <typename T, typename OidMap>
inline auto type_oid(const OidMap& map, const T&) noexcept{
    static_assert(ozo::OidMap<OidMap>, "map should model OidMap concept");
    return type_oid<std::decay_t<T>>(map);
}
#endif

/**
* Function returns true if type can be obtained from DB response with
* specified OID.
* @ingroup group-type_system-functions
* @tparam T --- type to examine
* @param map --- #OidMap to get type OID from
* @param oid --- OID to check for compatibility
*/
template <typename T, typename OidMap>
inline bool accepts_oid(const OidMap& map, oid_t oid) noexcept {
    static_assert(ozo::OidMap<OidMap>, "map should model OidMap concept");
    return type_oid<T>(map) == oid;
}

/**
* Function returns true if type can be obtained from DB response with
* specified OID.
* @ingroup group-type_system-functions
* @param map --- #OidMap to get type OID from
* @param const T& --- type to examine
* @param oid --- OID to check for compatibility
*/
template <typename T, typename OidMap>
inline bool accepts_oid(const OidMap& map, const T& , oid_t oid) noexcept {
    return accepts_oid<std::decay_t<T>>(map, oid);
}

/**
 * Checks if #OidMap contains no items.
 * @ingroup group-type_system-functions
 *
 * ### Example
 *
 * @code
static_assert(empty(ozo::empty_oid_map{}));
 * @endcode
 * @param map --- #OidMap to check
 * @return `true` --- if map contains no items
 * @return `false` --- if contains items.
 */
template <typename ...Ts>
inline constexpr bool empty(const oid_map_t<Ts...>& map) noexcept {
    return hana::length(map.impl) == hana::size_c<0>;
}

} // namespace ozo
