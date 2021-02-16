//
// Created by rahul on 02/02/21.
//

#ifndef CLIENT_SECURITY_H
#define CLIENT_SECURITY_H
const size_t SEC_NAME_SIZE = 32;

struct Security {
    std::string name;
    int type;
    int32_t lastTransactionId;
    int updateCount;
    int timeUpdated;
    short dateCreated;
    short dbIDUpdated;
    short versionInfo;
    int blobSize;
    char *blob;

    ~Security() {
        delete[] blob;
    }
};

#endif //CLIENT_SECURITY_H
