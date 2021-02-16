//
// Created by rahul on 01/02/21.
//

#ifndef MIDDLEWARE_SECURITY_H
#define MIDDLEWARE_SECURITY_H

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
  int blobSize = 0;
  const char* blob;

    ~Security() {
        // delete[] blob;
    }
};
#endif //MIDDLEWARE_SECURITY_H
