//
// Created by rahul on 08/02/21.
//

#ifndef PERFCLIENT_METRICS_H
#define PERFCLIENT_METRICS_H

class Metrics {
public:
    Metrics() {
        // create an http server running on port 8080
        exposer = new prometheus::Exposer{"127.0.0.1:8080"};
        // create a metrics registry
        // @note it's the users responsibility to keep the object alive
        auto registry = std::make_shared<prometheus::Registry>();
        getObject = &(prometheus::BuildGauge()
                .Name("get_object")
                .Register(*registry));
        exposer->RegisterCollectable(registry);
    }

    prometheus::Gauge &getByNameMetric(int id) {
        getObject->GetName();
        return getObject->Add({{"number", std::to_string(id)}});
    }

private:
    prometheus::Family<prometheus::Gauge> *getObject;
    prometheus::Exposer *exposer;
};

#endif //PERFCLIENT_METRICS_H
