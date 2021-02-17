//
// Created by rahul on 16/02/21.
//

#ifndef MIDDLEWARE_METRICS_H
#define MIDDLEWARE_METRICS_H

#include <prometheus/counter.h>
#include <prometheus/exposer.h>
#include <prometheus/registry.h>

struct Gauge {
    prometheus::Gauge &msgRead;
    prometheus::Gauge &msgDecoded;
    prometheus::Gauge &msgProcessed;
    prometheus::Gauge &msgEncoded;
    prometheus::Gauge &getByNameTotal;
    prometheus::Gauge &getByNameDecode;
    prometheus::Gauge &getByNameProcess;
    prometheus::Gauge &getByNameEncode;
    prometheus::Gauge &lookupDecode;
    prometheus::Gauge &lookupProcess;
    prometheus::Gauge &lookupEncode;
    prometheus::Gauge &dbConnection;
    prometheus::Gauge &queryExec;
};

struct Gauges {
    prometheus::Family<prometheus::Gauge> &msgRead;
    prometheus::Family<prometheus::Gauge> &msgDecoded;
    prometheus::Family<prometheus::Gauge> &msgProcessed;
    prometheus::Family<prometheus::Gauge> &msgEncoded;
    prometheus::Family<prometheus::Gauge> &getByNameTotal;
    prometheus::Family<prometheus::Gauge> &getByNameDecode;
    prometheus::Family<prometheus::Gauge> &getByNameProcess;
    prometheus::Family<prometheus::Gauge> &getByNameEncode;
    prometheus::Family<prometheus::Gauge> &lookupDecode;
    prometheus::Family<prometheus::Gauge> &lookupProcess;
    prometheus::Family<prometheus::Gauge> &lookupEncode;
    prometheus::Family<prometheus::Gauge> &dbConnection;
    prometheus::Family<prometheus::Gauge> &queryExec;

    Gauge *create(int id) {
        return new Gauge{
                msgRead.Add({{"number", std::to_string(id)}}),
                msgDecoded.Add({{"number", std::to_string(id)}}),
                msgProcessed.Add({{"number", std::to_string(id)}}),
                msgEncoded.Add({{"number", std::to_string(id)}}),
                getByNameTotal.Add({{"number", std::to_string(id)}}),
                getByNameDecode.Add({{"number", std::to_string(id)}}),
                getByNameProcess.Add({{"number", std::to_string(id)}}),
                getByNameEncode.Add({{"number", std::to_string(id)}}),
                lookupDecode.Add({{"number", std::to_string(id)}}),
                lookupProcess.Add({{"number", std::to_string(id)}}),
                lookupEncode.Add({{"number", std::to_string(id)}}),
                dbConnection.Add({{"number", std::to_string(id)}}),
                queryExec.Add({{"number", std::to_string(id)}})
        };
    }
};


#endif //MIDDLEWARE_METRICS_H
