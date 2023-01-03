#ifndef __GLOBAL_VAR__
#define __GLOBAL_VAR__

#include "string.h"

enum GlobalState{
    WORKING,
    IDLE,
    ERROR
};

extern GlobalState globalState;
extern int lux;
extern int hum;
extern int tmp;

extern String modules[];
extern String devices[];

#endif