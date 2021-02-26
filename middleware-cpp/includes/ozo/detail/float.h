#pragma once

#include <cstdint>

namespace ozo::detail {

template <class T>
struct floating_point_integral {};

template <>
struct floating_point_integral<float> {
    using type = std::int32_t;
};

template <>
struct floating_point_integral<double> {
    using type = std::int64_t;
};

template <class T>
using floating_point_integral_t = typename floating_point_integral<T>::type;

template <class T>
struct integral_floating_point {};

template <>
struct integral_floating_point<floating_point_integral_t<float>> {
    using type = float;
};

template <>
struct integral_floating_point<floating_point_integral_t<double>> {
    using type = double;
};

template <class T>
using integral_floating_point_t = typename integral_floating_point<T>::type;

template <class T>
constexpr floating_point_integral_t<T> to_integral(T value) noexcept {
    union {
        T input;
        floating_point_integral_t<T> output;
    } data {value};
    return data.output;
}

template <class T>
constexpr integral_floating_point_t<T> to_floating_point(T value) noexcept {
    union {
        T input;
        integral_floating_point_t<T> output;
    } data {value};
    return data.output;
}

} // namespace ozo::detail
